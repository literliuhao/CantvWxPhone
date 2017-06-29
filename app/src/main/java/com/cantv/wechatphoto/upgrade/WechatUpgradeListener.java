package com.cantv.wechatphoto.upgrade;

import android.content.Context;

import com.cantv.wechatphoto.R;
import com.tencent.bugly.beta.Beta;

import cn.can.tvlib.upgrade.*;
import cn.can.tvlib.utils.ToastUtils;

/**
 * Created by Atangs on 2017/6/28.
 */

public class WechatUpgradeListener implements UpgradeListener {

    private String TAG = "WechatUpgradeListener";
    private Context mAppContext;

    public WechatUpgradeListener(Context context) {
        mAppContext = context;
    }

    @Override
    public void onNewVersion(UpgradeInfo upgradeInfo, boolean download) {
        if (download) {
            UpgradeActivity.start(mAppContext, upgradeInfo);
        } else {
            ToastUtils.show(mAppContext, "发现新版本");
        }
    }

    @Override
    public void onNoNewVersion() {
        Beta.checkUpgrade(false, true);
    }

    @Override
    public void onDownloadStart(UpgradeInfo upgradeInfo) {

    }

    @Override
    public void onDownloading(long downloaded, long total) {

    }

    @Override
    public void onDownloadCompleted(UpgradeInfo upgradeInfo) {
        Upgrade.reportDownloadStatus(upgradeInfo.getTaskId(),true);
    }

    @Override
    public void onDownloadFailed(UpgradeInfo upgradeInfo, Exception e) {
        Upgrade.reportDownloadStatus(upgradeInfo.getTaskId(),false);
        Beta.checkUpgrade(false, true);
    }

    @Override
    public void onMd5Error(UpgradeInfo upgradeInfo) {
        Upgrade.reportMd5Error(upgradeInfo.getTaskId());
        if (mAppContext == null) {
            return;
        }
        ToastUtils.show(mAppContext, mAppContext.getString(R.string.upgrade_package_error));
    }
}
