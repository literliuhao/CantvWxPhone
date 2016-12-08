package com.cantv.wechatphoto.upgrade;

import com.cantv.wechatphoto.App;
import com.tencent.bugly.beta.UpgradeInfo;
import com.tencent.bugly.beta.upgrade.UpgradeListener;
import com.tencent.bugly.proguard.aa;

public class MyUpgradeListener implements UpgradeListener {
    @Override
    public boolean onUpgrade(aa aa, int i, String s) {
        return false;
    }

    @Override
    public void onUpgrade(int ret, UpgradeInfo strategy, boolean isManual, boolean
            isSilence) {
        if (strategy != null) {
            UpgradeManager.getIntance(App.getAppContext()).init();
        }
    }
}
