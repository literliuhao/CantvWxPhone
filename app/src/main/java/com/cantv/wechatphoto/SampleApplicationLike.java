package com.cantv.wechatphoto;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Build;
import android.support.multidex.MultiDex;
import android.text.TextUtils;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.cantv.wechatphoto.activity.QRCodePushActivity;
import com.cantv.wechatphoto.push.PushManager;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.tinker.loader.app.DefaultApplicationLike;

/**
 * 自定义ApplicationLike类.
 *
 * 注意：这个类是Application的代理类，以前所有在Application的实现必须要全部拷贝到这里<br/>
 *
 * @author wenjiewu
 * @since 2016/11/7
 */
public class SampleApplicationLike extends DefaultApplicationLike {

    public static final String TAG = "Tinker.SampleApplicationLike";
    private static String mClientId = "";
    public static RequestQueue gReqQueue = null;
    private static Context mContext;

    public static RequestQueue getVolleyRequestQueues() {
        if (gReqQueue == null) {
            gReqQueue = Volley.newRequestQueue(mContext);
        }
        return gReqQueue;
    }

    public static Context getAppContext(){
        return mContext;
    }


    public SampleApplicationLike(Application application, int tinkerFlags,
            boolean tinkerLoadVerifyFlag, long applicationStartElapsedTime,
            long applicationStartMillisTime, Intent tinkerResultIntent, Resources[] resources,
            ClassLoader[] classLoader, AssetManager[] assetManager) {
        super(application, tinkerFlags, tinkerLoadVerifyFlag, applicationStartElapsedTime,
                applicationStartMillisTime, tinkerResultIntent, resources, classLoader,
                assetManager);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplication();
        // TODO: 这里进行Bugly初始化
        // 设置开发设备
        Bugly.setIsDevelopmentDevice(getApplication(), true);
        // 这里实现SDK初始化，appId替换成你的在Bugly平台申请的appId
        Bugly.init(getApplication(), QRCodePushActivity.BUGLY_KEY, true);


        // 初始化个推服务
        PushManager.getInstance(mContext).init();
    }


    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onBaseContextAttached(Context base) {
        super.onBaseContextAttached(base);
        // you must install multiDex whatever tinker is installed!
        MultiDex.install(base);

        // TODO: 安装tinker
        Beta.installTinker(this);
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void registerActivityLifecycleCallback(Application.ActivityLifecycleCallbacks callbacks) {
        getApplication().registerActivityLifecycleCallbacks(callbacks);
    }
    /**
     * 获取PushManager clientId
     */
    public static String getPushManangerClientId() {
        mClientId = PushManager.getInstance(mContext).getClientId();
        if (TextUtils.isEmpty(mClientId)) {
            PushManager.getInstance(mContext).setOnClientIdUpdateListener(new PushManager.onClientIdUpdateListener() {
                @Override
                public void onUpdate(String clientId) {
                    mClientId = clientId;
                }
            });
        }
        return mClientId;
    }

}
