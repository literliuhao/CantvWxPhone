package com.cantv.wechatphoto.push;

import java.util.List;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.cantv.wechatphoto.activity.BasePlayerActivity;
import com.cantv.wechatphoto.activity.GridViewActivity;
import com.cantv.wechatphoto.activity.QRCodePushActivity;
import com.cantv.wechatphoto.push.domain.IPushMessage;
import com.cantv.wechatphoto.push.domain.PushCANProgramMsg;
import com.cantv.wechatphoto.push.domain.PushPhotoProgramMsg;
import com.cantv.wechatphoto.push.domain.PushSohuProgramMsg;
import com.cantv.wechatphoto.push.domain.PushSuccessProgramMsg;
import com.cantv.wechatphoto.push.domain.PushYokuProgramMsg;
import com.cantv.wechatphoto.push.domainvideo.IVideo;
import com.cantv.wechatphoto.push.domainvideo.PlayConstants;
import com.cantv.wechatphoto.utils.greendao.DaoOpenHelper;
import com.cantv.wechatphoto.utils.greendao.PhotoBean;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class PushMsgHandler {

	public static int PUSH_TYPE_PUSH_YOUKU_PROGRAM = 1;// 播优酷节目
	public static int PUSH_TYPE_PUSH_SOHU_PROGRAM = 2;// 播搜狐节目
	public static int PUSH_TYPE_PUSH_CAN_PROGRAM = 3;// 播CIBN节目
	public static int PUSH_TYPE_PUSH_WECHAT_PHOTO = 4;// 发送微信照片
	public static int PUSH_TYPE_REGISTER_SUCCESS = 5;//登陆成功
	public static final String KEY_CLIENT_ID = "clientId";// 个推clientId
	public static final String KEY_MOVIE_DETAIL = "moviedetail";
	public static final String KEY_SPECIFIED_PLAY_INDEX = "specifiedPlayIndex";
	public static final String KEY_FREE_PLAY_EPISODE = "freePlayEpisode";
	public static final String KEY_FREE_PLAY_TIME = "freePlayTime";
	public static final String KEY_CHARGE_TYPE = "chargeType";
	public static final String KEY_PLAY_TYPE = "playType";// 取值为PlayConstants中"PLAY_TYPE"，其他非法
	public static final String KEY_PROGRAM_ID = "programId";
	public static final String KEY_VIDEO = "videoInfo";// 进播放器默认播放的剧集数据

	public static final String ACTION_START_CAN_PLAYER = "cn.cibntv.ott.canplayer";
	public static final String ACTION_START_YOUKU_PLAYER = "cn.cibntv.ott.sohuplayer";
	public static final String ACTION_START_SOHU_PLAYER = "cn.cibntv.ott.yokuplayer";
	public static final String ACTION_CLOSE_QRCODE_PAGE = "com.cantv.wechatphoto.ACTION_CLOSE_QRCODE_PAGE";
															
	private static final String TAG = "PushMsgHandler";
	private static DaoOpenHelper mPhotoHelper;
	private static long queryExpiredUserCount;
	
	/**
	 * 解析推送消息，并执行相应意图操作
	 * 
	 * @param msgStr
	 */
	public static void solveMessage(Context context, String msgStr) {
		if (TextUtils.isEmpty(msgStr)) {
			Log.w(TAG, "Failed to solve PushMessage[Empty msg string].");
			return;
		}
		Log.i(TAG, "received push message. " + msgStr);
		IPushMessage pushMsg = null;
		int type = -1;
		try {
			JsonObject msgJs = new JsonParser().parse(msgStr).getAsJsonObject();
			type = msgJs.get("type").getAsInt();
			JsonObject dataJs = msgJs.get("data").getAsJsonObject();
			if (type == PUSH_TYPE_PUSH_YOUKU_PROGRAM) {
				pushMsg = new PushYokuProgramMsg().parse(dataJs);
			} else if (type == PUSH_TYPE_PUSH_SOHU_PROGRAM) {
				pushMsg = new PushSohuProgramMsg().parse(dataJs);
			} else if (type == PUSH_TYPE_PUSH_CAN_PROGRAM) {
				pushMsg = new PushCANProgramMsg().parse(dataJs);
			}else if(type == PUSH_TYPE_REGISTER_SUCCESS){
				pushMsg = new PushSuccessProgramMsg().parse(dataJs);
			}else if(type == PUSH_TYPE_PUSH_WECHAT_PHOTO){
				Log.i(TAG, "received push message. " + "收到推送");
				
				mPhotoHelper = DaoOpenHelper.getInstance(context.getApplicationContext());
				long number = mPhotoHelper.queryAllUserCount();
				queryExpiredUserCount = mPhotoHelper.queryExpiredUserCount();
				pushMsg = new PushPhotoProgramMsg().parse(dataJs);
				PushPhotoProgramMsg msg = (PushPhotoProgramMsg) pushMsg;
				List<PhotoBean> photoList = msg.getPhotoList();
				mPhotoHelper.photoBatchInsertWithTransaction(photoList);
				
				Intent intent = new Intent();
				intent.setAction("com.cibn.ott.action.WECHAT_PHOTO");
				intent.putExtra("number", number);
				context.sendBroadcast(intent);
			}else {
				Log.w(TAG, "Failed to solve PushMessage [Unknown push type : " + type + "].");
				return;
			}
		} catch (Exception e) {
			Log.w(TAG, "Failed to solve PushMessage [Json parse error].", e);
			return;
		}

		if (pushMsg == null) {
			Log.w(TAG, "Failed to solve pushMessage [pushMessage == NULL].");
			return;
		}
		if (!pushMsg.isLegal()) {
			Log.w(TAG, "Failed to solve pushMessage [Illegal pushMessage]. " + pushMsg.toString());
			return;	
		}

		Intent intent = null;
		if (type == PUSH_TYPE_PUSH_YOUKU_PROGRAM) {
			//closeOnShowProgramPage(context, YokuPlayerActivity.class);
			PushYokuProgramMsg msg = (PushYokuProgramMsg) pushMsg;
			intent = new Intent("cn.cibntv.ott.yokuplayer");
			
			/*Bundle bundle = new Bundle();
			bundle.putInt(BasePlayerActivity.KEY_PLAY_TYPE, PlayConstants.PLAY_TYPE.PUSH);
			bundle.putString(BasePlayerActivity.KEY_PROGRAM_ID, msg.getProgramId());
			bundle.putString(BasePlayerActivity.KEY_CHARGE_TYPE, String.valueOf(msg.getChargeType()));
			bundle.putInt(BasePlayerActivity.KEY_SPECIFIED_PLAY_INDEX, msg.getPlayIndex());
			bundle.putInt(BasePlayerActivity.KEY_FREE_PLAY_EPISODE, msg.getFreePlayEpisode());
			bundle.putInt(BasePlayerActivity.KEY_FREE_PLAY_TIME, msg.getFreePlayTime() * 60000);
			bundle.putParcelable(BasePlayerActivity.KEY_VIDEO, msg.getVideo());
			intent.putExtra(BasePlayerActivity.KEY_BUNDLE, bundle);*/
			
			intent.putExtra(BasePlayerActivity.KEY_PLAY_TYPE, PlayConstants.PLAY_TYPE.PUSH);
			intent.putExtra(BasePlayerActivity.KEY_PROGRAM_ID, msg.getProgramId());
			intent.putExtra(BasePlayerActivity.KEY_CHARGE_TYPE, String.valueOf(msg.getChargeType()));
			intent.putExtra(BasePlayerActivity.KEY_SPECIFIED_PLAY_INDEX, msg.getPlayIndex());
			intent.putExtra(BasePlayerActivity.KEY_FREE_PLAY_EPISODE, msg.getFreePlayEpisode());
			intent.putExtra(BasePlayerActivity.KEY_FREE_PLAY_TIME, msg.getFreePlayTime() * 60000);
			intent.putExtra(BasePlayerActivity.KEY_VIDEO, msg.getVideo());

		} else if (type == PUSH_TYPE_PUSH_SOHU_PROGRAM) {
			//closeOnShowProgramPage(context, SohuPlayerActivity.class);
			PushSohuProgramMsg msg = (PushSohuProgramMsg) pushMsg;
			intent = new Intent("cn.cibntv.ott.sohuplayer");
			intent.putExtra(BasePlayerActivity.KEY_PLAY_TYPE, PlayConstants.PLAY_TYPE.PUSH);
			intent.putExtra(BasePlayerActivity.KEY_PROGRAM_ID, msg.getProgramId());
			intent.putExtra(BasePlayerActivity.KEY_CHARGE_TYPE, String.valueOf(msg.getChargeType()));
			intent.putExtra(BasePlayerActivity.KEY_SPECIFIED_PLAY_INDEX, msg.getPlayIndex());
			intent.putExtra(BasePlayerActivity.KEY_FREE_PLAY_EPISODE, msg.getFreePlayEpisode());
			intent.putExtra(BasePlayerActivity.KEY_FREE_PLAY_TIME, msg.getFreePlayTime() * 60000);
			intent.putExtra(BasePlayerActivity.KEY_VIDEO, msg.getVideo());

		} else if (type == PUSH_TYPE_PUSH_CAN_PROGRAM) {
			//closeOnShowProgramPage(context, CanPlayerActivity.class);
			PushCANProgramMsg msg = (PushCANProgramMsg) pushMsg;
			intent = new Intent("cn.cibntv.ott.canplayer");
			intent.putExtra(BasePlayerActivity.KEY_PLAY_TYPE, PlayConstants.PLAY_TYPE.PUSH);
			intent.putExtra(BasePlayerActivity.KEY_PROGRAM_ID, msg.getProgramId());
			intent.putExtra(BasePlayerActivity.KEY_CHARGE_TYPE, String.valueOf(msg.getChargeType()));
			intent.putExtra(BasePlayerActivity.KEY_SPECIFIED_PLAY_INDEX, msg.getPlayIndex());
			intent.putExtra(BasePlayerActivity.KEY_FREE_PLAY_EPISODE, msg.getFreePlayEpisode());
			intent.putExtra(BasePlayerActivity.KEY_FREE_PLAY_TIME, msg.getFreePlayTime() * 60000);
			intent.putExtra(BasePlayerActivity.KEY_VIDEO, msg.getVideo());
		}

		if (intent != null) {
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
			context.startActivity(intent);
			Log.i("shen", "视频投屏成功...");
		}
		
		Intent intentPhoto = null;
		if(type == PUSH_TYPE_PUSH_WECHAT_PHOTO){
			closeQrcodePage(context);
			intentPhoto = new Intent(context, GridViewActivity.class);
			
		}
		if(intentPhoto != null){
			intentPhoto.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
			ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			String topActClassName = am.getRunningTasks(1).get(0).topActivity.getClassName();
			boolean topPageIsPhotoPage = topActClassName.equals(QRCodePushActivity.class.getName());
			if(queryExpiredUserCount == 0 && topPageIsPhotoPage){
				context.startActivity(intentPhoto);
			}
		}
		
		if(type == PUSH_TYPE_REGISTER_SUCCESS){
			PushSuccessProgramMsg msg = (PushSuccessProgramMsg) pushMsg;
			Intent registerIntent = new Intent();
			registerIntent.putExtra("openid", msg.getOpenid());
			registerIntent.putExtra("userName", msg.getUserName());
			registerIntent.putExtra("userPicUrl", msg.getUserPicUrl());
			registerIntent.setAction("com.cantv.wechatphoto.action.REGISTER_SUCCESS");
			context.sendBroadcast(registerIntent);
		}
	}

	@SuppressWarnings("deprecation")
	private static void closeOnShowProgramPage(Context context,
			Class<? extends BasePlayerActivity<? extends IVideo>> clazz) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		String topActClassName = am.getRunningTasks(1).get(0).topActivity.getClassName();

		/*boolean topPageIsPlayerPage = topActClassName.equals(YokuPlayerActivity.class.getName())
				|| topActClassName.equals(SohuPlayerActivity.class.getName())
				|| topActClassName.equals(CanPlayerActivity.class.getName())
				|| topActClassName.equals(LivePlayerActivity.class.getName());
		if (topPageIsPlayerPage) {
			String cmpActClassName = clazz.getName();
			// 如果当前正在播该节目类型，则不用关闭，直接播放即可
			if (!cmpActClassName.equals(topActClassName)) {
				Intent closePlayerPageIntent = new Intent(BasePlayerActivity.ACTION_CLOSE_ONSHOW_PLAYER);
				context.sendBroadcast(closePlayerPageIntent);
			}
		}*/

		Intent closeQRCodePageIntent = new Intent(ACTION_CLOSE_QRCODE_PAGE);
		context.sendBroadcast(closeQRCodePageIntent);
	}
	
	
	private static void closeQrcodePage(Context context) {

		Intent closeQRCodePageIntent = new Intent(ACTION_CLOSE_QRCODE_PAGE);
		context.sendBroadcast(closeQRCodePageIntent);
	}
}
