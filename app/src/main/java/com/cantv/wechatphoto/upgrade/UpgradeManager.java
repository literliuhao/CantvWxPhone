package com.cantv.wechatphoto.upgrade;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.cantv.wechatphoto.R;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.beta.download.DownloadListener;
import com.tencent.bugly.beta.download.DownloadTask;

import java.util.ArrayList;

public class UpgradeManager {

    private static UpgradeManager mInstance = null;
    private Context mContext;
    private String mTargeVersion;
    private String mUpgradeApkPath;

    //版本更新的一些信息，数据源
    private ArrayList<String> mUpgradeInfoList;

    private UpgradeManager(final Context context) {
        super();
        this.mContext = context.getApplicationContext();
    }

    public static UpgradeManager getIntance(final Context context) {
        if (mInstance == null) {
            synchronized (UpgradeManager.class) {
                if (mInstance == null) {
                    mInstance = new UpgradeManager(context);
                }
            }
        }
        return mInstance;
    }

    public void init() {
        if (mUpgradeInfoList == null) {
            mUpgradeInfoList = new ArrayList<>();
        }
        mUpgradeInfoList.clear();
        mUpgradeInfoList.add(Beta.getUpgradeInfo().newFeature);
        mTargeVersion = Beta.getUpgradeInfo().versionName;
        DownloadTask task = Beta.startDownload();
        Beta.registerDownloadListener(new DownloadListener() {
            @Override
            public void onReceive(DownloadTask task) {
                Toast.makeText(mContext, "没有更新", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCompleted(DownloadTask task) {
                Toast.makeText(mContext, "没有更新", Toast.LENGTH_LONG).show();
                show();
            }

            @Override
            public void onFailed(DownloadTask task, int code, String extMsg) {
                Toast.makeText(mContext, "没有更新", Toast.LENGTH_LONG).show();
            }
        });
    }

    public UpgradeManager(final Context context, String upgradeVersion, ArrayList<String> list, String path) {
        super();
        this.mContext = context;
        this.mUpgradeInfoList = list;
        mTargeVersion = upgradeVersion;
        mUpgradeApkPath = path;
    }

    public void show() {
        Dialog dialog = null;
        CustomDialog.Builder customBuilder = new CustomDialog.Builder(mContext);

        customBuilder.setTitle("软件版本更新")
                .setNewcode("新版本：" + mTargeVersion)
                .setList(mUpgradeInfoList)
                .setPositiveButton("升级", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Beta.unregisterDownloadListener();
                    }
                });

        dialog = customBuilder.create();
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.setCancelable(true);
        dialog.show();
        Button btn = (Button) dialog.findViewById(R.id.positiveButton);
        btn.requestFocus();
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Beta.unregisterDownloadListener();
            }
        });
    }

//    private void installApk(String uri) {
//        Intent installIntent = new Intent(Intent.ACTION_VIEW);
//        installIntent.setDataAndType(Uri.parse(uri), "application/vnd.android.package-archive");
//        installIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//        mContext.startActivity(installIntent);
//    }

}
