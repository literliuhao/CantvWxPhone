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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cantv.wechatphoto.R;
import com.cantv.wechatphoto.SampleApplicationLike;
import com.cantv.wechatphoto.adapter.GridAdapter;
import com.cantv.wechatphoto.bridge.EffectNoDrawBridge;
import com.cantv.wechatphoto.interfaces.IDBInteraction;
import com.cantv.wechatphoto.interfaces.IPhotoListener;
import com.cantv.wechatphoto.model.PushDataModelImpl;
import com.cantv.wechatphoto.model.QRManager;
import com.cantv.wechatphoto.push.PushManager;
import com.cantv.wechatphoto.push.PushManager.onClientIdUpdateListener;
import com.cantv.wechatphoto.receiver.DataReceiver;
import com.cantv.wechatphoto.utils.PreferencesUtils;
import com.cantv.wechatphoto.utils.ToastUtils;
import com.cantv.wechatphoto.utils.greendao.DaoOpenHelper;
import com.cantv.wechatphoto.utils.greendao.PhotoBean;
import com.cantv.wechatphoto.view.PopView;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.can.tvlib.imageloader.ImageLoader;

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
    private final int DELAYED_TIME = 1000;
    private final int TIMER_PERIOD = 5000;
    private final float SCALE = 1.1f;
    private final String TAG = GridViewActivity.class.getSimpleName();
    private PushManager mPushManager;
    private Rect mRect;
    private Boolean isFocusDelay = false;
    private List<PhotoBean> picList;
    private Timer timer;
    private String clientId;
    private Boolean isLoaded = false;

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
        clientId = mPushManager.getClientId();
        // 校验clientId，如果获取不到clientId无法加载二维码与获取推送消息。
        if (TextUtils.isEmpty(clientId)) {
            ToastUtils.showMessageLong(this, "设备ID获取失败，请稍候重试。");
            finish();
        }
        picList = new ArrayList<>();

        mPushManager.setOnClientIdUpdateListener(this);

        textEmpty = (TextView) findViewById(R.id.txt_empty);
        currentNumber = (TextView) findViewById(R.id.txt_number);
        totalNumber = (TextView) findViewById(R.id.txt_totalnumber);
        gridAdapter = new GridAdapter(this, R.layout.grid_item, picList);
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
        picList.add(0, bean);
        dataModel.getDBData(getApplicationContext(), GridViewActivity.this);
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);

        pullQRUrl();
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
        //应用每次关闭时候清空CODE_URL防止过期
        PreferencesUtils.putString(this, QRManager.getInstance(this).CODE_URL, "");
//        picList.clear();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ImageLoader.getInstance().clearMemoryCache(GridViewActivity.this);
            }
        });
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
                    popView.setFocusView(view, SCALE);
                    break;
            }
        }
    };

    @Override
    public void onSuccess(List<PhotoBean> photoList) {
        if (null == photoList) {
            mHandler.sendEmptyMessage(SHOW_EMPTY);
        } else {
            picList.addAll(photoList);
            totalNumber.setText("/" + picList.size());
            mHandler.sendEmptyMessage(UPDATE_DATA);
        }
    }

    @Override
    public void onError() {
    }

    @Override
    public void updateData(List<PhotoBean> photoList) {
        // 增量更新
        if (null != picList && picList.size() >= 1 && null != photoList) {
            if (null != layoutDelete && layoutDelete.getVisibility() == View.VISIBLE) {
                layoutDelete.setVisibility(View.INVISIBLE);
                isLock = false;
            }
            MobclickAgent.onEvent(SampleApplicationLike.getAppContext(), "Successful_Upload");
            picList.addAll(1, photoList);
            totalNumber.setText("/" + picList.size());
            mHandler.removeMessages(UPDATE_DATA);
            mHandler.sendEmptyMessageDelayed(UPDATE_DATA, DELAYED_TIME);
        }
    }

    public void refreshNumber(int position, int total) {
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
                        int i = picList.size();
                        if (i % 3 == 1 && currentPosition > i - 5 && currentPosition < i - 1 && currentPosition % 3 != 0) {
                            isFocusDelay = true;
                        }
                        layoutDelete.setVisibility(View.GONE);
                        PhotoBean photoBean = picList.get(currentPosition - 1);
                        DaoOpenHelper daoHelper = DaoOpenHelper.getInstance(getApplicationContext());
                        photoBean.setBack1("expired");
                        daoHelper.updatePhoto(photoBean);
                        picList.remove(currentPosition - 1);
                        refreshNumber(currentPosition, picList.size());
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
                popView.setFocusView(view, lastView, SCALE);
                Log.i(TAG, "onItemSelected FOCUS_VIEW");
            } else {
                Message msg = mHandler.obtainMessage(FOCUS_VIEW);
                msg.obj = view;
                mHandler.sendMessageDelayed(msg, DELAYED_TIME);
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
            popView.setFocusView(newView, SCALE);
            Log.i(TAG, "onLayoutChange FOCUS_VIEW");
        } else if (isFocusDelay) {
            Message msg = mHandler.obtainMessage(FOCUS_VIEW);
            msg.obj = gridView.getSelectedView();
            mHandler.sendMessageDelayed(msg, 20);
            Log.i(TAG, "onLayoutChange FOCUS_VIEW  layoutScroll set Focus");
        }
    }

    @Override
    public void onUpdate(String clientId) {
//        getWXQRCode(clientId);
    }

    /**
     * 获取微信推送二维码
     *
     * @param
     */
    public void getWXQRCode(String clientId) {
        QRManager.getInstance(this).getWXQRCode(clientId, new QRCodePushActivity.OnRequestFinishCallback<String>() {
            @Override
            public void onSuccess(String codeURL, String... extras) {
                isLoaded = true;
                drawQRCode(codeURL);
            }

            @Override
            public void onFail(Throwable e) {
                Log.w(TAG, "Failed to getWXQRCode.", e);
            }
        });
    }

    private void pullQRUrl() {
        timer = new Timer();
        timer.schedule(new PoolTask(), 0, TIMER_PERIOD);
    }

    private void drawQRCode(String codeURL) {
        if (picList != null && picList.size() > 0) {
            picList.get(0).setPhotourl(codeURL);
        }
        //通过观察后发现由于异步原因可能导致刷新不及时，因此手动再次触发刷新
        GridViewActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (null != gridAdapter) gridAdapter.notifyDataSetChanged();
            }
        });

    }

    class PoolTask extends TimerTask {
        @Override
        public void run() {
            if (!isLoaded) {
                Log.i(TAG, "run...");
                getWXQRCode(clientId);
            } else {
                Log.i(TAG, "run...cancel");
                timer.cancel();
            }
        }
    }
}
