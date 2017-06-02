package com.cantv.wechatphoto.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.target.Target;
import com.cantv.wechatphoto.R;
import com.cantv.wechatphoto.interfaces.IPositionListener;
import com.cantv.wechatphoto.utils.Utils;
import com.cantv.wechatphoto.utils.greendao.PhotoBean;
import com.cantv.wechatphoto.view.ZoomImageView;

import java.util.List;

import cn.can.tvlib.imageloader.GlideLoadTask;
import cn.can.tvlib.imageloader.transformation.GlideRotateTransformation;

public class ImageAdapter extends PagerAdapter {
    private List<PhotoBean> photoLists;
    private int photoSize;
    private View[] mImageViews;
    private Context mContext;
    private IPositionListener listener;
    private int[] realResolution;

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        listener.onPosition(position);
        super.setPrimaryItem(container, position, object);
    }

    public ImageAdapter(Context context, List<PhotoBean> photoList, Activity activity) {
        this.mContext = context;
        this.photoLists = photoList;
        this.photoSize = photoList.size();
        this.mImageViews = new View[photoSize];
        realResolution = Utils.getRealResolution(activity);
        Log.d("ImageAdapter", "realResolution:width:" + realResolution[0] + ",height" + realResolution[1]);
    }

    public void addListenerPosition(IPositionListener listener) {
        this.listener = listener;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = null;
        final ZoomImageView imageView;
        if (position == 0) {
            view = View.inflate(container.getContext(), R.layout.layout_share_qrcode_item, null);
            imageView = (ZoomImageView) view.findViewById(R.id.iv_qrcode);
        } else {
            imageView = new ZoomImageView(mContext);
            imageView.setBackgroundResource(R.drawable.bg_photo_fullscreen_loading);
            imageView.setPosition(position);
            imageView.setBean(photoLists.get(position));
            view = imageView;
        }
        imageView.setFocusable(false);
        ViewPager.LayoutParams vl = new ViewPager.LayoutParams();
        vl.width = ViewPager.LayoutParams.MATCH_PARENT;
        vl.height = ViewPager.LayoutParams.MATCH_PARENT;
        view.setLayoutParams(vl);
        Log.d("ImageAdapter", photoLists.get(position).getPhotourl());
        GlideLoadTask.Builder builder = new GlideLoadTask.Builder();
        builder.view(imageView).url(photoLists.get(position).getPhotourl()).placeholder(0)
                .errorHolder(R.drawable.errorholder).bitmapTransformation(new GlideRotateTransformation(mContext,
                photoLists.get(position).getDirection())).cacheInMemory(true).successCallback(new GlideLoadTask
                .SuccessCallback() {
            @Override
            public boolean onSuccess(GlideDrawable glideDrawable, String s, Target<GlideDrawable> target, boolean b,
                                     boolean b1) {
                imageView.setFocusable(true);
                imageView.setBackgroundResource(R.drawable.transparent_image);
                return false;
            }
        }).failCallback(new GlideLoadTask.FailCallback() {
            @Override
            public boolean onFail(Exception e, String s, Target<GlideDrawable> target, boolean b) {
                imageView.setBackgroundResource(R.drawable.bg_photo_fullscreen_loading_err);
                return false;
            }
        }).start(mContext);
        container.addView(view);
        mImageViews[position] = view;
        return view;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        listener.onTotalSize(photoSize);
        return photoSize;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mImageViews[position]);
        if (mImageViews[position] != null) {
            mImageViews[position] = null;
        }
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }
}