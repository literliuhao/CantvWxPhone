package com.cantv.wechatphoto.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.target.Target;
import com.cantv.wechatphoto.R;
import com.cantv.wechatphoto.utils.greendao.PhotoBean;
import com.cantv.wechatphoto.view.GridViewTV;
import com.cantv.wechatphoto.view.RoundCornerDrawable;
import com.cantv.wechatphoto.view.RoundCornerImageView;

import java.util.List;

import cn.can.tvlib.imageloader.GlideLoadTask;
import cn.can.tvlib.imageloader.transformation.GlideCircleTransform;
import cn.can.tvlib.imageloader.transformation.GlideRotateTransformation;

public class GridAdapter extends BaseAdapter {

    private List<PhotoBean> photoLists;
    private Context mContext;
    private int qrCodeSize;
    private int photoWidth;
    private int photoHeight;
    private int headWidth;
    private int headHeight;

    public GridAdapter(Context context, int resource, List<PhotoBean> photoList) {
        this.mContext = context;
        this.photoLists = photoList;
        qrCodeSize = context.getResources().getDimensionPixelSize(R.dimen.dimen_265);
        photoWidth = context.getResources().getDimensionPixelSize(R.dimen.dimen_504);
        photoHeight = context.getResources().getDimensionPixelSize(R.dimen.dimen_380);
        headWidth = context.getResources().getDimensionPixelSize(R.dimen.dimen_70);
        headHeight = context.getResources().getDimensionPixelSize(R.dimen.dimen_70);
    }

    @Override
    public PhotoBean getItem(int position) {
        return photoLists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return photoLists.size();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.grid_item, parent, false);
            holder = new ViewHolder();
            holder.bgIv = (ImageView) convertView.findViewById(R.id.iv_bg);
            holder.imgHead = (ImageView) convertView.findViewById(R.id.img_head);
            holder.tipsTv = (TextView) convertView.findViewById(R.id.tv_tips);
            holder.txtName = (TextView) convertView.findViewById(R.id.txt_Name);
            holder.txtTime = (TextView) convertView.findViewById(R.id.txt_time);
            holder.image = (RoundCornerImageView) convertView.findViewById(R.id.id_imgView);
            holder.deleteView = (ViewGroup) convertView.findViewById(R.id.rl_delete);
            holder.deleteView.setBackground(new RoundCornerDrawable(parent.getResources().getColor(R.color
                    .black_per40)));
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        // 解决getview多次调用问题，二维码占位图重叠bug
        if (((GridViewTV) parent).isOnMeasure) {
            return convertView;
        }

        if (position == 0) {
            Log.d("getView", "============" + position);
            holder.tipsTv.setVisibility(View.VISIBLE);
            holder.tipsTv.setText(getItem(position).getWxname());
            holder.imgHead.setVisibility(View.INVISIBLE);
            holder.txtName.setVisibility(View.INVISIBLE);
            holder.txtTime.setVisibility(View.INVISIBLE);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.image.getLayoutParams();
            layoutParams.width = qrCodeSize;
            layoutParams.height = qrCodeSize;
            layoutParams.topMargin = parent.getContext().getResources().getDimensionPixelSize(R.dimen.dimen_39);
            layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            holder.image.setLayoutParams(layoutParams);
            holder.image.setScaleType(ImageView.ScaleType.FIT_XY);
            holder.bgIv.setBackground(new RoundCornerDrawable(BitmapFactory.decodeResource(parent.getResources(), R
                    .drawable.bg_share_phone_item)));
        } else {
            Log.d("getView", "============" + position);
            holder.tipsTv.setVisibility(View.INVISIBLE);
            holder.bgIv.setBackground(null);
            holder.txtName.setText(getItem(position).getWxname());
            holder.txtName.setVisibility(View.VISIBLE);
            holder.txtTime.setText(getItem(position).getUploadtime());
            holder.txtTime.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.image.getLayoutParams();
            layoutParams.width = photoWidth;
            layoutParams.height = photoHeight;
            layoutParams.topMargin = parent.getContext().getResources().getDimensionPixelSize(R.dimen.dimen_15);
            holder.image.setLayoutParams(layoutParams);
            holder.image.setBackgroundResource(R.drawable.pager_background);
//            holder.image.setImageDrawable(mContext.getResources().getDrawable(R.drawable.pager_background,null));
            holder.image.setScaleType(ImageView.ScaleType.CENTER_CROP);

            RelativeLayout.LayoutParams headLayoutParams = (RelativeLayout.LayoutParams) holder.imgHead
                    .getLayoutParams();
            headLayoutParams.width = headWidth;
            headLayoutParams.height = headHeight;
            holder.imgHead.setLayoutParams(headLayoutParams);
            holder.imgHead.setScaleType(ImageView.ScaleType.CENTER_CROP);


            GlideLoadTask.Builder builder = new GlideLoadTask.Builder();
            builder.bitmapTransformation(new GlideCircleTransform(mContext)).view(holder.imgHead).placeholder(R
                    .drawable.default_image).url(getItem(position).getWxheadimgurl()).cacheInMemory(true).start(mContext);
            holder.imgHead.setVisibility(View.VISIBLE);
        }


        GlideLoadTask.Builder builder = new GlideLoadTask.Builder();
        builder.url(getItem(position).getPhotourl()).view(holder.image).placeholder(0).errorHolder(0)
                .bitmapTransformation(new GlideRotateTransformation(mContext, photoLists.get(position).getDirection()
                )).cacheInMemory(true).successCallback(new GlideLoadTask.SuccessCallback() {
            @Override
            public boolean onSuccess(GlideDrawable glideDrawable, String s, Target<GlideDrawable> target, boolean b,
                                     boolean b1) {
                holder.image.setBackgroundResource(0);
                return false;
            }
        }).failCallback(new GlideLoadTask.FailCallback() {
            @Override
            public boolean onFail(Exception e, String s, Target<GlideDrawable> target, boolean b) {
                holder.image.setBackgroundResource(R.drawable.bg_photo_list_item_loading_err);
                return false;
            }
        }).start(mContext);

        return convertView;
    }

    public static class ViewHolder {
        ImageView bgIv;
        ImageView imgHead;
        TextView tipsTv;
        TextView txtName;
        TextView txtTime;
        RoundCornerImageView image;
        ViewGroup deleteView;
    }

}