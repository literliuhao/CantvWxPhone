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

import com.cantv.wechatphoto.App;
import com.cantv.wechatphoto.R;
import com.cantv.wechatphoto.adapter.ImageAdapter;
import com.cantv.wechatphoto.entity.HelperBean;
import com.cantv.wechatphoto.interfaces.IPositionListener;
import com.cantv.wechatphoto.utils.ToastUtils;
import com.cantv.wechatphoto.utils.greendao.PhotoBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuhao on 2016/6/8.
 */
public class PagerActivity extends Activity implements IPositionListener {
    private ViewPager mViewPager;
    private TextView textCurrect;
    private TextView textTotal;
    private RelativeLayout mBottomMask;
    private int totalCount;
    private int mPosition;
    private ImageAdapter imageAdapter;
    private Runnable mHideBottomMaskRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setBackgroundResource(android.R.color.black);
        setContentView(R.layout.pager_view);
        textCurrect = (TextView) findViewById(R.id.txt_pager_currectNumber);
        textTotal = (TextView) findViewById(R.id.txt_pager_totalnumber);
        mBottomMask = (RelativeLayout) findViewById(R.id.rl_bottom_mask);
        Intent gridIntent = getIntent();
        final int mPosition = gridIntent.getIntExtra("position", 0);
        List<PhotoBean> mPhotoLists = new ArrayList<>();
        mPhotoLists.addAll(HelperBean.photoList);
        imageAdapter = new ImageAdapter(this, mPhotoLists);
        imageAdapter.addListenerPosition(this);
        mViewPager = (ViewPager) findViewById(R.id.id_viewpager);
        mViewPager.setAdapter(imageAdapter);
        mViewPager.setCurrentItem(mPosition);
//		mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
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
    public void onPosition(int position) {
        this.mPosition = position;
        if (position == 0 && mBottomMask.getVisibility() == View.VISIBLE) {
            mHideBottomMaskRunnable.run();
        }
        textCurrect.setText(String.valueOf(position + 1));
    }

    @Override
    public void onTotalSize(int total) {
        Log.i("PagerActivity", total + " ");
        this.totalCount = total;
        textTotal.setText("/" + totalCount);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT: // 左
                if (mPosition == 0) {
                    ToastUtils.showMessage(App.getAppContext(), getString(R.string.reach_first_page), Toast.LENGTH_LONG);
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT: // 右
                if (mPosition == totalCount - 1) {
                    ToastUtils.showMessage(App.getAppContext(), getString(R.string.reach_last_page), Toast.LENGTH_LONG);
                }
                break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
}
