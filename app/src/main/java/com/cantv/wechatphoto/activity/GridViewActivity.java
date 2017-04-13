package com.cantv.wechatphoto.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnLayoutChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.cantv.wechatphoto.GetDataUtils;
import com.cantv.wechatphoto.R;
import com.cantv.wechatphoto.SampleApplicationLike;
import com.cantv.wechatphoto.adapter.GridAdapter;
import com.cantv.wechatphoto.bridge.EffectNoDrawBridge;
import com.cantv.wechatphoto.entity.HelperBean;
import com.cantv.wechatphoto.interfaces.IDBInteraction;
import com.cantv.wechatphoto.interfaces.IPhotoListener;
import com.cantv.wechatphoto.model.PushDataModelImpl;
import com.cantv.wechatphoto.push.PushManager;
import com.cantv.wechatphoto.push.PushManager.onClientIdUpdateListener;
import com.cantv.wechatphoto.receiver.DataReceiver;
import com.cantv.wechatphoto.utils.FakeX509TrustManager;
import com.cantv.wechatphoto.utils.NetWorkUtils;
import com.cantv.wechatphoto.utils.PreferencesUtils;
import com.cantv.wechatphoto.utils.ToastUtils;
import com.cantv.wechatphoto.utils.greendao.DaoOpenHelper;
import com.cantv.wechatphoto.utils.greendao.PhotoBean;
import com.cantv.wechatphoto.utils.imageloader.ImageInfo;
import com.cantv.wechatphoto.utils.imageloader.ImageLoader;
import com.cantv.wechatphoto.utils.volley.VolleyCallback;
import com.cantv.wechatphoto.utils.volley.VolleyRequest;
import com.cantv.wechatphoto.view.PopView;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

/**
 * 相册GridView
 */
public class GridViewActivity extends Activity implements IPhotoListener, IDBInteraction, OnKeyListener, OnItemSelectedListener, OnItemClickListener, OnLayoutChangeListener, onClientIdUpdateListener {
    private PopView popView;
    private GridView gridView;
    private GridAdapter gridAdapter;
    private TextView textEmpty;
    private TextView currentNumber;
    private TextView totalNumber;
    private RelativeLayout layoutDelete;
    private DataReceiver dataReceiver;
    private IntentFilter intentFilter;
    private PushDataModelImpl dataModel;
    private Boolean isLock = false;
    private View lastView = null;
    private int currentPosition;
    private final int SHOW_EMPTY = 0x000000;
    private final int UPDATE_DATA = 0x000001;
    private final int FOCUS_VIEW = 0x000002;
    private final int ANIMATION_TIME = 260;
    private final String TAG = GridViewActivity.class.getSimpleName();
    private PushManager mPushManager;
    private String mQrCodeUrl = "";
    private String qrTicket = "";
    private Rect mRect;
    private Boolean isFocusDelay = false;

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("GridViewActivity", "onCreate");
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setBackgroundResource(R.drawable.bg_share_phone_page);
        setContentView(R.layout.grid_view);
        dataModel = new PushDataModelImpl();
        dataReceiver = new DataReceiver();
        intentFilter = new IntentFilter("com.cibn.ott.action.WECHAT_PHOTO");
        dataReceiver.setDBInteractionListener(this);
        mPushManager = PushManager.getInstance(this);
        String clientId = mPushManager.getClientId();
        // 校验clientId，如果获取不到clientId无法加载二维码与获取推送消息。
        if (TextUtils.isEmpty(clientId)) {
            ToastUtils.showMessageLong(this, "设备ID获取失败，请稍候重试。");
            finish();
        }

        getWexinPushQRCode(clientId);
        mPushManager.setOnClientIdUpdateListener(this);

        textEmpty = (TextView) findViewById(R.id.txt_empty);
        currentNumber = (TextView) findViewById(R.id.txt_number);
        totalNumber = (TextView) findViewById(R.id.txt_totalnumber);
        gridAdapter = new GridAdapter(this, R.layout.grid_item, HelperBean.photoList);
        popView = (PopView) findViewById(R.id.popView_id);
        popView.setEffectBridge(new EffectNoDrawBridge());
        EffectNoDrawBridge bridget = (EffectNoDrawBridge) popView.getEffectBridge();
        bridget.setTranDurAnimTime(ANIMATION_TIME);
        popView.setUpRectResource(R.drawable.frame_wx_share_item_focus);
        Resources r = getResources();
        int left = (int) r.getDimension(R.dimen.dimen_f_25);
        int top = (int) r.getDimension(R.dimen.dimen_f_12);
        int Right = (int) r.getDimension(R.dimen.dimen_f_25);
        int bottom = (int) r.getDimension(R.dimen.dimen_f_25);
        mRect = new Rect(left, top, Right, bottom);
        popView.setDrawUpRectPadding(mRect);
        gridView = (GridView) findViewById(R.id.gridView);
        gridView.setAdapter(gridAdapter);
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        gridView.setOnKeyListener(this);
        gridView.setOnItemSelectedListener(this);
        gridView.setOnItemClickListener(this);
        gridView.addOnLayoutChangeListener(this);
        //将数据源中的第一个位置占位，等待二维码数据
        PhotoBean bean = new PhotoBean();
        bean.setWxname("扫一扫：影片、照片投屏看");
        bean.setPhotourl("");
        HelperBean.photoList.add(0, bean);
        dataModel.getDBData(getApplicationContext(), GridViewActivity.this);

        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;  // 屏幕宽度（像素）
        int height = metric.heightPixels;  // 屏幕高度（像素）

        Log.e(TAG + "  DisplayMetrics", "width = " + width);
        Log.e(TAG + "  DisplayMetrics", "height =" + height);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("GridViewActivity", "onStart");
        registerReceiver(dataReceiver, intentFilter);
        gridAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i("GridViewActivity", "onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("GridViewActivity", "onResume");
        MobclickAgent.onResume(this);
        MobclickAgent.onEvent(SampleApplicationLike.getAppContext(), "Album_List_Page");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("GridViewActivity", "onStop");

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("GridViewActivity", "onPause");
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("GridViewActivity", "onDestroy");
        GridViewActivity.this.unregisterReceiver(dataReceiver);
        HelperBean.photoList.clear();
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                ImageLoader.getInstance().clearMemCache();
            }
        });
        System.gc();
    }

    public boolean showDelete() {
        View view = gridView.getSelectedView();
        if (null != view) {
            if (gridView.getPositionForView(view) == 0) {
                return true;
            }
            layoutDelete = (RelativeLayout) view.findViewById(R.id.rl_delete);
            if (null != layoutDelete) {
                if (layoutDelete.getVisibility() != View.VISIBLE) {
                    isLock = true;
                    layoutDelete.setVisibility(View.VISIBLE);
                } else {
                    isLock = false;
                    layoutDelete.setVisibility(View.INVISIBLE);
                }
            }
        }
        return false;
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_EMPTY:
                    textEmpty.setVisibility(View.VISIBLE);
                    break;
                case UPDATE_DATA:
                    textEmpty.setVisibility(View.GONE);
                    gridAdapter.notifyDataSetChanged();
                    break;
                case FOCUS_VIEW:
                    View view = (View) msg.obj;
                    Log.i(TAG, "handleMessage FOCUS_VIEW");
                    Log.i(TAG, view.getWidth() + " " + view.getMeasuredWidth());
                    popView.setFocusView(view, 1.1f);
                    break;
            }
        }
    };

    @Override
    public void onSuccess(List<PhotoBean> photoList) {
        if (null == photoList) {
            mHandler.sendEmptyMessage(SHOW_EMPTY);
        } else {
            HelperBean.photoList.addAll(photoList);
            totalNumber.setText("/" + HelperBean.photoList.size());
            mHandler.sendEmptyMessage(UPDATE_DATA);
        }
    }

    @Override
    public void onError() {
    }

    @Override
    public void updateData(List<PhotoBean> photoList) {
        // 增量更新
        if (null != HelperBean.photoList && HelperBean.photoList.size() >= 1 && null != photoList) {
            if (null != layoutDelete && layoutDelete.getVisibility() == View.VISIBLE) {
                layoutDelete.setVisibility(View.INVISIBLE);
                isLock = false;

            }
            MobclickAgent.onEvent(SampleApplicationLike.getAppContext(), "Successful_Upload");
            HelperBean.photoList.addAll(1, photoList);
            totalNumber.setText("/" + HelperBean.photoList.size());
            gridAdapter.notifyDataSetChanged();
        }
    }

    public void refrshNumber(int position, int total) {
        currentNumber.setText(String.valueOf(position));
        totalNumber.setText("/" + total);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        Log.i("GridViewActivity", "点击在屏幕上的位置" + v.getVerticalScrollbarPosition());
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == 8 || keyCode == KeyEvent.KEYCODE_MENU) {
                showDelete();

            } else if (KeyEvent.KEYCODE_BACK == keyCode) {
                if (isLock) {
                    showDelete();
                    return true;
                } else {
                    GridViewActivity.this.finish();
                }
            } else {
                if (isLock) {
                    if (KeyEvent.KEYCODE_ENTER == keyCode || KeyEvent.KEYCODE_DPAD_CENTER == keyCode) { // 遥控器OK
                        isLock = false;
                        //解决删除后GridView滚动后焦点错位的问题
                        int i = HelperBean.photoList.size();
                        if (i % 3 == 1 && currentPosition > i - 5 && currentPosition < i - 1 && currentPosition % 3 != 0) {
                            isFocusDelay = true;
                        }
                        layoutDelete.setVisibility(View.GONE);
                        PhotoBean photoBean = HelperBean.photoList.get(currentPosition - 1);
                        DaoOpenHelper daoHelper = DaoOpenHelper.getInstance(getApplicationContext());
                        photoBean.setBack1("expired");
                        daoHelper.updatePhoto(photoBean);
                        HelperBean.photoList.remove(currentPosition - 1);
                        refrshNumber(currentPosition, HelperBean.photoList.size());
                        MobclickAgent.onEvent(SampleApplicationLike.getAppContext(), "Delete_photo");
                        gridAdapter.notifyDataSetChanged();
                    }
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (lastView != view) {
            currentPosition = position;
            currentNumber.setText(String.valueOf(++currentPosition));
            view.bringToFront();
            if (lastView != null) {
                mHandler.removeMessages(FOCUS_VIEW);
                popView.setFocusView(view, lastView, 1.1f);
                Log.i(TAG, "onItemSelected FOCUS_VIEW");
            } else {
                Message msg = mHandler.obtainMessage(FOCUS_VIEW);
                msg.obj = view;
                mHandler.sendMessageDelayed(msg, 500);
            }
            lastView = view;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent();
        intent.putExtra("position", position);
        intent.setClass(GridViewActivity.this, PagerActivity.class);
        startActivity(intent);
    }

    @Override
    public void onLayoutChange(View view, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        if (gridView.getChildCount() > 0 && gridView.getChildAt(0) == view) {
            gridView.setSelection(0);
            View newView = gridView.getChildAt(0);
            newView.bringToFront();
            popView.setFocusView(newView, 1.1f);
            Log.i(TAG, "onLayoutChange FOCUS_VIEW");
        } else if (isFocusDelay) {
            Message msg = mHandler.obtainMessage(FOCUS_VIEW);
            msg.obj = gridView.getSelectedView();
            mHandler.sendMessageDelayed(msg, 20);
            Log.i(TAG, "onLayoutChange FOCUS_VIEW  layoutScorll set Focus");
        }
    }

    @Override
    public void onUpdate(String clientId) {
        getWexinPushQRCode(clientId);
    }

    public interface OnRequestFinishCallback<T> {
        void onSuccess(T t, String... extras);

        void onFail(Throwable e);
    }

    /**
     * 获取微信推送二维码
     *
     * @param
     */
    public void getWexinPushQRCode(String clientId) {
        if (TextUtils.isEmpty(clientId)) {
            return;
        }
        String mac = NetWorkUtils.getEthernetMac();
        GetDataUtils.getIntance().requestWexinPushQRCode(mac, clientId, "getWexinPushQRCode", new VolleyCallback<String>() {
            @Override
            public void onSuccess(String response) {
                if (TextUtils.isEmpty(response)) {
                    response = "";
                }
                try {
                    JsonObject respJs = new JsonParser().parse(response).getAsJsonObject();
                    int status = respJs.get("status").getAsInt();
                    Log.w(TAG, "getWexinPushQRCode, status = " + status);
                    if (status == 200) {
                        JsonObject dataJs = respJs.get("data").getAsJsonObject();
                        qrTicket = dataJs.get("qrTicket").getAsString();
                        mQrCodeUrl = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=" + qrTicket;
                        PreferencesUtils.putString(getApplicationContext(), "MQRCODEURL", mQrCodeUrl);
                        if (HelperBean.photoList != null && HelperBean.photoList.size() > 0) {
                            HelperBean.photoList.get(0).setPhotourl(mQrCodeUrl);
                        }
                        if (gridView != null && gridView.getChildAt(0) != null) {
                            ImageView img = (ImageView) gridView.getChildAt(0).findViewById(R.id.id_imgView);
                            ImageInfo imgInfo = new ImageInfo.Builder().url(mQrCodeUrl).isSkipMemoryCache(true).imgView(img).build();
                            FakeX509TrustManager.allowAllSSL();
                            ImageLoader.getInstance().loadImage(GridViewActivity.this, imgInfo);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(VolleyError error) {
                Log.w(TAG, "failed to getWexinPushQRCode: " + error.getClass().getSimpleName());
            }
        });
    }

    public void cancelAllRequest() {
        VolleyRequest.stopRequest("getWexinPushQRCode");
    }
}
