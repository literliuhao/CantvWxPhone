package com.cantv.wechatphoto.push;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.cantv.wechatphoto.SampleApplicationLike;
import com.cantv.wechatphoto.activity.GridViewActivity;
import com.cantv.wechatphoto.activity.QRCodePushActivity;
import com.cantv.wechatphoto.push.domain.IPushMessage;
import com.cantv.wechatphoto.push.domain.PushCANProgramMsg;
import com.cantv.wechatphoto.push.domain.PushPhotoProgramMsg;
import com.cantv.wechatphoto.push.domain.PushSohuProgramMsg;
import com.cantv.wechatphoto.push.domain.PushSuccessProgramMsg;
import com.cantv.wechatphoto.push.domain.PushYokuProgramMsg;
import com.cantv.wechatphoto.push.domainvideo.CanVideo;
import com.cantv.wechatphoto.push.domainvideo.SohuVideo;
import com.cantv.wechatphoto.push.domainvideo.YokuVideo;
import com.cantv.wechatphoto.utils.ToastUtils;
import com.cantv.wechatphoto.utils.greendao.DaoOpenHelper;
import com.cantv.wechatphoto.utils.greendao.PhotoBean;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

public class PushMsgHandler {

    public static int PUSH_TYPE_PUSH_YOUKU_PROGRAM = 1;// 播优酷节目
    public static int PUSH_TYPE_PUSH_SOHU_PROGRAM = 2;// 播搜狐节目
    public static int PUSH_TYPE_PUSH_CAN_PROGRAM = 3;// 播CIBN节目
    public static int PUSH_TYPE_PUSH_WECHAT_PHOTO = 4;// 发送微信照片
    public static int PUSH_TYPE_REGISTER_SUCCESS = 5;// 登陆成功
    public static final String KEY_CLIENT_ID = "clientId";// 个推clientId
    public static final String KEY_MOVIE_DETAIL = "moviedetail";
    public static final String KEY_SPECIFIED_PLAY_INDEX = "specifiedPlayIndex";
    public static final String KEY_FREE_PLAY_EPISODE = "freePlayEpisode";
    public static final String KEY_FREE_PLAY_TIME = "freePlayTime";
    public static final String KEY_CHARGE_TYPE = "chargeType";
    public static final String KEY_PLAY_TYPE = "playType";// 取值为PlayConstants中"PLAY_TYPE"，其他非法
    public static final String KEY_PROGRAM_ID = "programId";
    public static final String KEY_VIDEO = "videoInfo";// 进播放器默认播放的剧集数据
    public static final String KEY_VIDEO_INFO = "videoInfo";

    public static final String ACTION_START_CAN_PLAYER = "cn.cibntv.ott.canplayer";
    public static final String ACTION_START_YOUKU_PLAYER = "cn.cibntv.ott.sohuplayer";
    public static final String ACTION_START_SOHU_PLAYER = "cn.cibntv.ott.yokuplayer";
    public static final String ACTION_CLOSE_QRCODE_PAGE = "com.cantv.wechatphoto.ACTION_CLOSE_QRCODE_PAGE";
    public static final String ACTION_START_PHOTO = "com.cibn.ott.action.WECHAT_PHOTO";
    public static final String ACTION_REGISTER_SUCCESS = "com.cantv.wechatphoto.action.REGISTER_SUCCESS";

    private static final String TAG = "PushMsgHandler";
    private static DaoOpenHelper mPhotoHelper;

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
        IPushMessage pushMsg;
        int type;
        try {
            JsonObject msgJs = new JsonParser().parse(msgStr).getAsJsonObject();
            type = msgJs.get("type").getAsInt();
            JsonObject dataJs = msgJs.get("data").getAsJsonObject();
            if (type == PUSH_TYPE_PUSH_YOUKU_PROGRAM) {
                Log.i(TAG, "received push message. " + "【优酷节目】推送消息");
                pushMsg = new PushYokuProgramMsg().parse(dataJs);
            } else if (type == PUSH_TYPE_PUSH_SOHU_PROGRAM) {
                Log.i(TAG, "received push message. " + "【搜狐节目】推送消息");
                pushMsg = new PushSohuProgramMsg().parse(dataJs);
            } else if (type == PUSH_TYPE_PUSH_CAN_PROGRAM) {
                Log.i(TAG, "received push message. " + "【CIBN节目】推送消息");
                pushMsg = new PushCANProgramMsg().parse(dataJs);
            } else if (type == PUSH_TYPE_REGISTER_SUCCESS) {
                Log.i(TAG, "received push message. " + "【登陆成功】推送消息");
                pushMsg = new PushSuccessProgramMsg().parse(dataJs);
            } else if (type == PUSH_TYPE_PUSH_WECHAT_PHOTO) {
                Log.i(TAG, "received push message. " + "【微信相册】推送消息");
                pushMsg = new PushPhotoProgramMsg().parse(dataJs);
            } else {
                Log.w(TAG, "Failed to solve PushMessage [Unknown push type : " + type + "].");
                return;
            }
        } catch (Exception e) {
            Log.w(TAG, "Failed to solve PushMessage [Json parse error].", e);
            return;
        }
        if (null == pushMsg) {
            Log.w(TAG, "Failed to solve pushMessage [pushMessage == NULL].");
            return;
        }
        if (!pushMsg.isLegal()) {
            Log.w(TAG, "Failed to solve pushMessage [Illegal pushMessage]. " + pushMsg.toString());
            return;
        }

        Intent intent = null;
        if (type == PUSH_TYPE_PUSH_YOUKU_PROGRAM) {
            closeIndexPage(context);
            PushYokuProgramMsg msg = (PushYokuProgramMsg) pushMsg;
            if (null != msg) {
                YokuVideo yokuVideo = msg.getVideo();
                if (null != yokuVideo) {
                    String videoId = msg.getVideo().getVideoid();
                    if (null == videoId) {
                        ToastUtils.showMessage(context, "数据获取异常，请按遥控返回键");
                        return;
                    }
                    intent = new Intent("com.cantv.action.YOKUPLAYER");
                    intent.putExtra(KEY_VIDEO_INFO, msgStr);
                } else {
                    Log.i(TAG, "Video. " + "【数据获取异常，请按遥控返回键】");
                    ToastUtils.showMessage(context, "数据获取异常，请按遥控返回键");
                    return;
                }
            } else {
                Log.i(TAG, "PushYokuProgramMsg. " + "【数据获取异常，请按遥控返回键】");
                ToastUtils.showMessage(context, "数据获取异常，请按遥控返回键");
                return;
            }
        } else if (type == PUSH_TYPE_PUSH_SOHU_PROGRAM) {
            closeIndexPage(context);
            PushSohuProgramMsg msg = (PushSohuProgramMsg) pushMsg;
            if (null != msg) {
                SohuVideo sohuVideo = msg.getVideo();
                if (null != sohuVideo) {
                    String videoId = sohuVideo.getSohu().getVid();
                    if (null == videoId) {
                        ToastUtils.showMessage(context, "数据获取异常，请按遥控返回键");
                        return;
                    }
                    intent = new Intent("com.cantv.action.SOHUPLAYER");
                    intent.putExtra(KEY_VIDEO_INFO, msgStr);
                } else {
                    Log.i(TAG, "Video. " + "【数据获取异常，请按遥控返回键】");
                    ToastUtils.showMessage(context, "数据获取异常，请按遥控返回键");
                    return;
                }
            } else {
                Log.i(TAG, "PushSohuProgramMsg. " + "【数据获取异常，请按遥控返回键】");
                ToastUtils.showMessage(context, "数据获取异常，请按遥控返回键");
                return;
            }
        } else if (type == PUSH_TYPE_PUSH_CAN_PROGRAM) {
            closeIndexPage(context);
            PushCANProgramMsg msg = (PushCANProgramMsg) pushMsg;
            if (null != msg) {
                CanVideo canVideo = msg.getVideo();
                if (null != canVideo) {
                    String videoId = canVideo.getVideoid();
                    if (null == videoId) {
                        ToastUtils.showMessage(context, "数据获取异常，请按遥控返回键");
                        return;
                    }
                    intent = new Intent("com.cantv.action.CANPLAYER");
                    intent.putExtra(KEY_VIDEO_INFO, msgStr);
                } else {
                    Log.i(TAG, "Video. " + "【数据获取异常，请按遥控返回键】");
                    ToastUtils.showMessage(context, "数据获取异常，请按遥控返回键");
                    return;
                }
            } else {
                Log.i(TAG, "PushCANProgramMsg. " + "【数据获取异常，请按遥控返回键】");
                ToastUtils.showMessage(context, "数据获取异常，请按遥控返回键");
                return;
            }
        }

        if (intent != null) {
            try {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (type == PUSH_TYPE_PUSH_WECHAT_PHOTO) {
            //写入本地数据库
            mPhotoHelper = DaoOpenHelper.getInstance(context.getApplicationContext());
            long number = mPhotoHelper.queryAllUserCount();
            long queryExpiredUserCount = mPhotoHelper.queryExpiredUserCount();

            PushPhotoProgramMsg msg = (PushPhotoProgramMsg) pushMsg;
            List<PhotoBean> photoList = msg.getPhotoList();

            Log.i(TAG, "【写入数据库】");
            mPhotoHelper.photoBatchInsertWithTransaction(photoList);
            //通知GridView刷新页面
            refreshNumber(context, number);
            if (queryExpiredUserCount == 0 && isTopTask(context)) {
                MobclickAgent.onEvent(SampleApplicationLike.getAppContext(), "First_Upload");
                closeIndexPage(context);
                startGridView(context);
            }
        }

        if (type == PUSH_TYPE_REGISTER_SUCCESS) {
            PushSuccessProgramMsg msg = (PushSuccessProgramMsg) pushMsg;
            Intent registerIntent = new Intent();
            registerIntent.putExtra("openid", msg.getOpenid());
            registerIntent.putExtra("userName", msg.getUserName());
            registerIntent.putExtra("userPicUrl", msg.getUserPicUrl());
            registerIntent.setAction(ACTION_REGISTER_SUCCESS);
            context.sendBroadcast(registerIntent);
        }
    }

    public static boolean isTopTask(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        String topActClassName = am.getRunningTasks(1).get(0).topActivity.getClassName();
        boolean topPageIsPhotoPage = topActClassName.equals(QRCodePushActivity.class.getName());
        return topPageIsPhotoPage;
    }

    public static void startGridView(Context context) {
        Log.i(TAG, "【开启列表页面】");
        Intent intent = new Intent(context, GridViewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void refreshNumber(Context context, long number) {
        Log.i(TAG, "【更新相册照片】");
        Intent intent = new Intent();
        intent.setAction(ACTION_START_PHOTO);
        intent.putExtra("number", number);
        try {
            context.sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void closeIndexPage(Context context) {
        Log.i(TAG, "【关闭首页】");
        Intent closeQRCodePageIntent = new Intent(ACTION_CLOSE_QRCODE_PAGE);
        context.sendBroadcast(closeQRCodePageIntent);
    }
}
