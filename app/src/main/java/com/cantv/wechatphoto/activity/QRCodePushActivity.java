package com.cantv.wechatphoto.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.cantv.wechatphoto.R;
import com.cantv.wechatphoto.SampleApplicationLike;
import com.cantv.wechatphoto.interfaces.ICallBack;
import com.cantv.wechatphoto.model.QRManager;
import com.cantv.wechatphoto.push.PushManager;
import com.cantv.wechatphoto.push.PushManager.onClientIdUpdateListener;
import com.cantv.wechatphoto.utils.FakeX509TrustManager;
import com.cantv.wechatphoto.utils.FileUtils;
import com.cantv.wechatphoto.utils.NetWorkUtils;
import com.cantv.wechatphoto.utils.greendao.DaoOpenHelper;
import com.cantv.wechatphoto.utils.volley.VolleyRequest;
import com.cantv.wechatphoto.view.ConfirmDialog;
import com.tencent.bugly.beta.Beta;
import com.umeng.analytics.MobclickAgent;

import cn.can.tvlib.imageloader.ImageLoader;

public class QRCodePushActivity extends Activity {

    private final String TAG = "QRCodePushActivity";

    private static final String ACTION_CLOSE_QRCODE_PAGE = "com.cantv.wechatphoto.ACTION_CLOSE_QRCODE_PAGE";
    private static final String ACTION_REGISTER_SUCCEED = "com.cantv.wechatphoto.action.REGISTER_SUCCESS";

    private ImageView mQRCodeIv;
    private ImageView mSucceedHint;

    private PushManager mPushManager;
    private BroadcastReceiver mClosePageReceiver;
    private IntentFilter mClosePageIntentFilter;
    private BroadcastReceiver mRegisterSucceedReceiver;
    private IntentFilter mRegisterSucceedIntentFilter;
    private ConfirmDialog mConfirmDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 进行判断，选择打开界面
        DaoOpenHelper pushDaoOpenHelper = DaoOpenHelper.getInstance(getApplicationContext());
        long count = pushDaoOpenHelper.queryExpiredUserCount();
        //Bugly检查更新
        Beta.checkUpgrade(false, true);
        if (count >= 1) {
            QRCodePushActivity.this.finish();
            Intent intent = new Intent(getApplicationContext(), GridViewActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return;
        }

//        this.getWindow().setBackgroundDrawableResource(R.drawable.new_bg_qr_push);
        setContentView(R.layout.activity_qr_push_play);
        mQRCodeIv = (ImageView) findViewById(R.id.iv_qrcode);
        mSucceedHint = (ImageView) findViewById(R.id.iv_succeed_hint);
        mPushManager = PushManager.getInstance(this);
        String clientId = mPushManager.getClientId();
        if (!TextUtils.isEmpty(clientId)) {
            loadQRCode(clientId);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!NetWorkUtils.isNetworkAvailable(QRCodePushActivity.this)) {
            mConfirmDialog = new ConfirmDialog(QRCodePushActivity.this);
            mConfirmDialog.setQuestion(getString(R.string.wechat_start_network_error));
            mConfirmDialog.setOnClickableListener(new ConfirmDialog.OnClickableListener() {
                @Override
                public void onConfirmClickable() {
                    mConfirmDialog.hide();
                }
            });
            mConfirmDialog.show();
        } else {
            mConfirmDialog = new ConfirmDialog(QRCodePushActivity.this);
            FileUtils.getInstance(QRCodePushActivity.this).readSDCard(new ICallBack() {
                @Override
                public void result(int sourceid) {
                    mConfirmDialog.setQuestion(getString(sourceid));
                    mConfirmDialog.show();
                }

                @Override
                public void done() {
                    Log.i("done", "存储空间正常...");
                }
            });
        }
        Log.i(TAG, "waiting for geTui-clientId to getWexinPushQRCode.");
        mPushManager.setOnClientIdUpdateListener(new onClientIdUpdateListener() {
            @Override
            public void onUpdate(String clientId) {
                loadQRCode(clientId);
            }
        });

        if (mClosePageReceiver == null) {
            mClosePageReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Log.i(TAG, "received close qrcodePage broadcast.");
                    finish();
                }
            };
            mClosePageIntentFilter = new IntentFilter(ACTION_CLOSE_QRCODE_PAGE);
        }
        registerReceiver(mClosePageReceiver, mClosePageIntentFilter);

        if (mRegisterSucceedReceiver == null) {
            mRegisterSucceedReceiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    Log.i(TAG, "received close qrcodePage broadcast.");
                    mSucceedHint.setVisibility(View.VISIBLE);
                }
            };
            mRegisterSucceedIntentFilter = new IntentFilter(ACTION_REGISTER_SUCCEED);
        }
        registerReceiver(mRegisterSucceedReceiver, mRegisterSucceedIntentFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        MobclickAgent.enableEncrypt(true);
        MobclickAgent.onEvent(SampleApplicationLike.getAppContext(), "Tutorial_Page");
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        if (mClosePageReceiver != null) {
            unregisterReceiver(mClosePageReceiver);
            mClosePageReceiver = null;
            mClosePageIntentFilter = null;
        }
        if (mRegisterSucceedReceiver != null) {
            unregisterReceiver(mRegisterSucceedReceiver);
            mRegisterSucceedReceiver = null;
            mRegisterSucceedIntentFilter = null;
        }
        mPushManager = null;
        if (null != mSucceedHint) {
            mSucceedHint.setVisibility(View.INVISIBLE);
        }
        super.onDestroy();
    }

    private void loadQRCode(String clientId) {
        QRManager.getInstance(this).getWXQRCode(clientId, new OnRequestFinishCallback<String>() {
            @Override
            public void onSuccess(String t, String... extras) {
                mPushManager.removeClientIdUpdateListener();
                int cornerDp = (int) getResources().getDimension(R.dimen.dimen_30);
                FakeX509TrustManager.allowAllSSL();
                ImageLoader.getInstance().load(QRCodePushActivity.this,mQRCodeIv,t,R.drawable.wechat_cibn,R.drawable.errorholder);

            }

            @Override
            public void onFail(Throwable e) {
                Log.w(TAG, "Failed to getWXQRCode.", e);
            }
        });
    }

    public interface OnRequestFinishCallback<T> {

        void onSuccess(T t, String... extras);

        void onFail(Throwable e);
    }

    public void cancelAllRequest() {
        VolleyRequest.stopRequest("getWexinPushQRCode");
    }

}
