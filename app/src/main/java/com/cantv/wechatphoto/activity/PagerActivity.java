package com.cantv.wechatphoto.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cantv.wechatphoto.R;
import com.cantv.wechatphoto.SampleApplicationLike;
import com.cantv.wechatphoto.adapter.ImageAdapter;
import com.cantv.wechatphoto.interfaces.IPhotoListener;
import com.cantv.wechatphoto.interfaces.IPositionListener;
import com.cantv.wechatphoto.model.PushDataModelImpl;
import com.cantv.wechatphoto.push.PushManager;
import com.cantv.wechatphoto.utils.PreferencesUtils;
import com.cantv.wechatphoto.utils.ToastUtils;
import com.cantv.wechatphoto.utils.greendao.PhotoBean;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuhao on 2016/6/8.
 */
public class PagerActivity extends Activity implements IPositionListener, IPhotoListener {
    private ViewPager mViewPager;
    private TextView textCurrect;
    private TextView textTotal;
    private RelativeLayout mBottomMask;
    private int totalCount;
    private int mPosition;
    private ImageAdapter imageAdapter;
    private Runnable mHideBottomMaskRunnable;
    private PushDataModelImpl dataModel;
    List<PhotoBean> mPhotoLists;
    private String mQrCodeUrl = "";
    private PushManager mPushManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("PagerActivity", "onCreate");
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setBackgroundResource(android.R.color.black);
        setContentView(R.layout.pager_view);
        textCurrect = (TextView) findViewById(R.id.txt_pager_currectNumber);
        textTotal = (TextView) findViewById(R.id.txt_pager_totalnumber);
        mBottomMask = (RelativeLayout) findViewById(R.id.rl_bottom_mask);
        mPushManager = PushManager.getInstance(this);
        String clientId = mPushManager.getClientId();
        Intent gridIntent = getIntent();
        final int mPosition = gridIntent.getIntExtra("position", 0);
        mPhotoLists = new ArrayList<>();
        //修改打开图片然后回到launcher内存被清理后回到微信相册，图片无法显示的bug
        dataModel = new PushDataModelImpl();
        dataModel.getDBData(getApplicationContext(), PagerActivity.this);
        imageAdapter.addListenerPosition(this);
        mViewPager = (ViewPager) findViewById(R.id.id_viewpager);
        mViewPager.setAdapter(imageAdapter);
        mViewPager.setCurrentItem(mPosition);
        //mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        mBottomMask.postDelayed(new Runnable() {
            public void run() {
                if (mPosition != 0) {
                    Animation am = AnimationUtils.loadAnimation(PagerActivity.this, R.anim.bottom_in);
                    am.setFillAfter(true);
                    am.setAnimationListener(new AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            mBottomMask.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                        }
                    });
                    mBottomMask.startAnimation(am);
                }
            }
        }, 500);
        mHideBottomMaskRunnable = new Runnable() {
            @Override
            public void run() {
                if (mBottomMask.getVisibility() == View.VISIBLE) {
                    Animation am = AnimationUtils.loadAnimation(PagerActivity.this, R.anim.bottom_out);
                    am.setFillAfter(true);
                    am.setAnimationListener(new AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            mBottomMask.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                        }
                    });
                    mBottomMask.startAnimation(am);
                }
            }
        };
        mBottomMask.postDelayed(mHideBottomMaskRunnable, 3500);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("PagerActivity", "onResume");
        MobclickAgent.onResume(this);
        MobclickAgent.onEvent(SampleApplicationLike.getAppContext(), "Photo_Detail_Page");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("PagerActivity", "onPause");
        MobclickAgent.onPause(this);
    }

    @Override
    public void onPosition(int position) {
        this.mPosition = position;
        if (position == 0 && mBottomMask.getVisibility() == View.VISIBLE) {
            mHideBottomMaskRunnable.run();
        }
        textCurrect.setText(String.valueOf(position + 1));
    }

    @Override
    public void onTotalSize(int total) {
        Log.d("PagerActivity", total + " ");
        this.totalCount = total;
        textTotal.setText("/" + totalCount);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT: // 左
                if (mPosition == 0) {
                    ToastUtils.showMessage(SampleApplicationLike.getAppContext(), getString(R.string.reach_first_page), Toast.LENGTH_LONG);
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT: // 右
                if (mPosition == totalCount - 1) {
                    ToastUtils.showMessage(SampleApplicationLike.getAppContext(), getString(R.string.reach_last_page), Toast.LENGTH_LONG);
                }
                break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onSuccess(List<PhotoBean> photoList) {
        //添加PagerActivity第一个二维码item
        PhotoBean bean = new PhotoBean();
        bean.setWxname(getString(R.string.scan) + "：" + getString(R.string.watch_for_video_add_picture));
        mQrCodeUrl = PreferencesUtils.getString(getApplicationContext(), "MQRCODEURL");
        bean.setPhotourl(mQrCodeUrl);
        photoList.add(0, bean);
        mPhotoLists.addAll(photoList);
        imageAdapter = new ImageAdapter(this, mPhotoLists);
        Log.d("PagerActivity", "mPhotoLists.size() " + mPhotoLists.size());
    }

    @Override
    public void onError() {

    }

}
