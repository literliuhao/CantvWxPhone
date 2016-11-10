package com.cantv.wechatphoto;


import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.cantv.wechatphoto.push.PushManager;
import com.cantv.wechatphoto.push.PushManager.onClientIdUpdateListener;
import com.cantv.wechatphoto.upgrade.UpgradeManager;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.BuglyStrategy;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.beta.UpgradeInfo;
import com.tencent.bugly.beta.upgrade.UpgradeListener;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class App extends Application {

	private static String mClientId = "";
	public static RequestQueue gReqQueue = null;
	public static final String BUGLY_KEY = "b40705f2a9";
	private static Context mContext;

	@Override
	public void onCreate() {
		super.onCreate();
		mContext = getApplicationContext();
		// 初始化个推服务
		PushManager.getInstance(mContext).init();
		initBugly();
	}

	public static RequestQueue getVolleyRequestQueues() {
		if (gReqQueue == null) {
			gReqQueue = Volley.newRequestQueue(mContext);
		}
		return gReqQueue;
	}

	public static Context getAppContext(){
		return mContext;
	}
	/**
	 * 获取PushManager clientId
	 */
	public static String getPushManangerClientId() {
		mClientId = PushManager.getInstance(mContext).getClientId();
		if (TextUtils.isEmpty(mClientId)) {
			PushManager.getInstance(mContext).setOnClientIdUpdateListener(new onClientIdUpdateListener() {
				@Override
				public void onUpdate(String clientId) {
					mClientId = clientId;
				}
			});
		}
		return mClientId;
	}

	private void initBugly() {
		Beta.autoInit = true;
		Beta.autoCheckUpgrade = false;
		Beta.storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
		Beta.upgradeListener = new UpgradeListener() {
			@Override
			public void onUpgrade(int ret, UpgradeInfo strategy, boolean isManual, boolean
					isSilence) {
				if (strategy != null) {
					UpgradeManager.getIntance(mContext).init();
				}
			}
		};

		BuglyStrategy strategy = new BuglyStrategy();
		strategy.setAppChannel("cantv");
        String processName = getProcessName(android.os.Process.myPid());
        String packageName = mContext.getPackageName();
        strategy.setUploadProcess(processName == null || processName.equals(packageName));
		Bugly.init(this, BUGLY_KEY, false, strategy);
	}

    /**
     * 获取进程号对应的进程名
     *
     * @param pid 进程号
     * @return 进程名
     */
    private static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }
	
}
