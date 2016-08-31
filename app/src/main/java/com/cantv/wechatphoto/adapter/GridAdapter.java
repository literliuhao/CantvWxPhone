package com.cantv.wechatphoto.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cantv.wechatphoto.R;
import com.cantv.wechatphoto.utils.greendao.PhotoBean;
import com.cantv.wechatphoto.utils.imageloader.ImageInfo;
import com.cantv.wechatphoto.utils.imageloader.ImageInfo.OnLoadFinishListener;
import com.cantv.wechatphoto.utils.imageloader.ImageLoader;
import com.cantv.wechatphoto.view.CircleImageView;
import com.cantv.wechatphoto.view.RoundCornerDrawable;
import com.cantv.wechatphoto.view.RoundCornerImageView;

public class GridAdapter extends BaseAdapter {

	private List<PhotoBean> photoLists;
	private Context mContext;
	private int qrCodeSize;
	private int photoWidth;
	private int photoHeight;

	public GridAdapter(Context context, int resource, List<PhotoBean> photoList) {
		this.mContext = context;
		this.photoLists = photoList;
		qrCodeSize = context.getResources().getDimensionPixelSize(R.dimen.dimen_265px);
		photoWidth = context.getResources().getDimensionPixelSize(R.dimen.px_504);
		photoHeight = context.getResources().getDimensionPixelSize(R.dimen.px_380);
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

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.grid_item, parent, false);
			holder = new ViewHolder();
			holder.bgIv = (ImageView) convertView.findViewById(R.id.iv_bg);
			holder.imgHead = (CircleImageView) convertView.findViewById(R.id.img_head);
			holder.tipsTv = (TextView) convertView.findViewById(R.id.tv_tips);
			holder.txtName = (TextView) convertView.findViewById(R.id.txt_Name);
			holder.txtTime = (TextView) convertView.findViewById(R.id.txt_time);
			holder.image = (RoundCornerImageView) convertView.findViewById(R.id.id_imgView);
			holder.deleteView = (ViewGroup) convertView.findViewById(R.id.rl_delete);
			holder.deleteView
					.setBackground(new RoundCornerDrawable(parent.getResources().getColor(R.color.black_per40)));
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (position == 0) {
			holder.tipsTv.setVisibility(View.VISIBLE);
			holder.tipsTv.setText(getItem(position).getWxname());
			holder.imgHead.setVisibility(View.INVISIBLE);
			holder.txtName.setVisibility(View.INVISIBLE);
			holder.txtTime.setVisibility(View.INVISIBLE);
			RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.image.getLayoutParams();
			layoutParams.width = qrCodeSize;
			layoutParams.height = qrCodeSize;
			layoutParams.topMargin = parent.getContext().getResources().getDimensionPixelSize(R.dimen.dimen_39px);
			layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
			holder.image.setLayoutParams(layoutParams);
			holder.image.setScaleType(ImageView.ScaleType.FIT_XY);
			holder.bgIv.setBackground(new RoundCornerDrawable(
					BitmapFactory.decodeResource(parent.getResources(), R.drawable.bg_share_phone_item)));
		} else {
			holder.tipsTv.setVisibility(View.INVISIBLE);
			holder.bgIv.setBackground(null);
			holder.txtName.setText(getItem(position).getWxname());
			holder.txtName.setVisibility(View.VISIBLE);
			holder.txtTime.setText(getItem(position).getUploadtime());
			holder.txtTime.setVisibility(View.VISIBLE);
			RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.image.getLayoutParams();
			layoutParams.width = photoWidth;
			layoutParams.height = photoHeight;
			layoutParams.topMargin = parent.getContext().getResources().getDimensionPixelSize(R.dimen.dimen_15px);
			holder.image.setLayoutParams(layoutParams);
			holder.image.setBackgroundResource(R.drawable.pager_background);
			holder.image.setScaleType(ImageView.ScaleType.CENTER_CROP);
			ImageInfo img1 = new ImageInfo.Builder().url(getItem(position).getWxheadimgurl())
					.placeHolder(R.drawable.default_image).errorHolder(R.drawable.default_image).imgView(holder.imgHead)
					.build();
			ImageLoader.getInstance().getLoader().loadImgByFitCenter(mContext, img1);
			holder.imgHead.setVisibility(View.VISIBLE);
		}
		ImageInfo img = new ImageInfo.Builder().url(getItem(position).getPhotourl()).placeHolder(0).errorHolder(0)
				.loadListener(new OnLoadFinishListener() {

					@Override
					public void onSuccess() {
					}

					@Override
					public void onFail() {
						holder.image.setBackgroundResource(R.drawable.bg_photo_list_item_loading_err);
					}
				}).imgView(holder.image).build();
		ImageLoader.getInstance().loadImage(mContext, img);
		return convertView;
	}

	public static class ViewHolder {
		ImageView bgIv;
		CircleImageView imgHead;
		TextView tipsTv;
		TextView txtName;
		TextView txtTime;
		RoundCornerImageView image;
		ViewGroup deleteView;
	}

}