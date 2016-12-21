package com.cantv.wechatphoto.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.cantv.wechatphoto.R;
import com.cantv.wechatphoto.interfaces.IPositionListener;
import com.cantv.wechatphoto.utils.greendao.PhotoBean;
import com.cantv.wechatphoto.utils.imageloader.ImageInfo;
import com.cantv.wechatphoto.utils.imageloader.ImageInfo.OnLoadFinishListener;
import com.cantv.wechatphoto.utils.imageloader.ImageLoader;
import com.cantv.wechatphoto.view.ZoomImageView;

import java.util.List;

public class ImageAdapter extends PagerAdapter {
    private List<PhotoBean> photoLists;
    private int photoSize;
    private View[] mImageViews;
    private Context mContext;
    private IPositionListener listener;

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        listener.onPosition(position);
        super.setPrimaryItem(container, position, object);
    }

    public ImageAdapter(Context context, List<PhotoBean> photoList) {
        this.mContext = context;
        this.photoLists = photoList;
        this.photoSize = photoList.size();
        this.mImageViews = new View[photoSize];
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
        ImageInfo img = new ImageInfo.Builder().url(photoLists.get(position).getPhotourl()).placeHolder(0).rotation(photoLists.get(position).getDirection()).errorHolder(0).loadListener(new OnLoadFinishListener() {

            @Override
            public void onSuccess() {
                imageView.setFocusable(true);
                imageView.setBackgroundResource(R.drawable.transparent_image);
            }

            @Override
            public void onFail() {
                imageView.setBackgroundResource(R.drawable.bg_photo_fullscreen_loading_err);
            }
        }).imgView(imageView).build();
        ImageLoader.getInstance().loadImage(mContext, img);
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