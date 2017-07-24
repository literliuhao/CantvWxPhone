package com.cantv.wechatphoto;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Environment;
import android.support.multidex.MultiDex;
import android.text.TextUtils;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.cantv.wechatphoto.push.PushManager;
import com.cantv.wechatphoto.upgrade.BuglyUpgradeListener;
import com.cantv.wechatphoto.upgrade.WechatUpgradeListener;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.BuglyStrategy;
import com.tencent.bugly.beta.Beta;
import com.tencent.tinker.loader.app.DefaultApplicationLike;

import cn.can.tvlib.upgrade.Upgrade;
import cn.can.tvlib.upgrade.UpgradeListener;

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
    public static final String BUGLY_KEY = "b40705f2a9";
    //自有升级接口
    public static final String UPGRADE_SN = "App10009";
    public static final String UPGRADE_CHANNEL_NO = "all_channel";

    public static RequestQueue getVolleyRequestQueues() {
        if (gReqQueue == null) {
            gReqQueue = Volley.newRequestQueue(mContext);
        }
        return gReqQueue;
    }

    public static Context getAppContext() {
        return mContext;
    }


    public SampleApplicationLike(Application application, int tinkerFlags,
                                 boolean tinkerLoadVerifyFlag, long applicationStartElapsedTime,
                                 long applicationStartMillisTime, Intent tinkerResultIntent, Resources[] resources,
                                 ClassLoader[] classLoader, AssetManager[] assetManager) {
        super(application, tinkerFlags, tinkerLoadVerifyFlag, applicationStartElapsedTime, applicationStartMillisTime,
              tinkerResultIntent, resources, classLoader, assetManager);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplication();
        // TODO: 这里进行Bugly初始化
        initBugly();
        initUpgrade();
        // 初始化个推服务
        PushManager.getInstance(mContext).init();
    }

    /**
     *
     */
    private void initUpgrade() {
        Upgrade.init(mContext, UPGRADE_SN, UPGRADE_CHANNEL_NO, BuildConfig.DEBUG);
        Upgrade.setUpgradeListener(new WechatUpgradeListener(mContext.getApplicationContext()));
        Upgrade.checkUpdate();
    }

    private void initBugly() {
        /**
         * Beta高级设置
         * true表示app启动自动初始化升级模块；
         * false不好自动初始化
         * 开发者如果担心sdk初始化影响app启动速度，可以设置为false
         * 在后面某个时刻手动调用
         */
        Beta.autoInit = true;
        /**
         * true表示初始化时自动检查升级.
         * false表示不会自动检查升级，需要手动调用Beta.checkUpgrade()方法
         */
        Beta.autoCheckUpgrade = false;
        /**
         * 设置sd卡的Download为更新资源保存目录;.
         * 后续更新资源会保存在此目录，需要在manifest中添加WRITE_EXTERNAL_STORAGE权限;
         */
        Beta.storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        Beta.upgradeListener = new BuglyUpgradeListener();
        BuglyStrategy strategy = new BuglyStrategy();
        strategy.setAppChannel("cantv");
        strategy.setUploadProcess(true);
        //设置开发设备
        Bugly.setIsDevelopmentDevice(getApplication(), true);
        // 这里实现SDK初始化，appId替换成你的在Bugly平台申请的appId
        Bugly.init(mContext, BUGLY_KEY, BuildConfig.DEBUG, strategy);    //1.6.0版本这句取消注释
        /**
         * 已经接入Bugly用户改用上面的初始化方法,不影响原有的crash上报功能;.
         * init方法会自动检测更新，不需要再手动调用Beta.checkUpgrade(),如需增加自动检查时机可以使用Beta.checkUpgrade(false,false);
         * 参数1：applicationContext
         * 参数2：appId
         * 参数3：是否开启debug
         */
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
