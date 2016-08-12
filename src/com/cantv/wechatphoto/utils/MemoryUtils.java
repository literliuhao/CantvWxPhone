package com.cantv.wechatphoto.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

public class MemoryUtils {

	private static final String TAG = "CANTV_MemoryUtils";

	public synchronized static void clearMemory(Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> runningAppProcessInfoList = am.getRunningAppProcesses();
		long availableMem = getAvailableMemory(context);
//		Log.d(TAG, "available memory info : " + availableMem);
		if (availableMem < 200) {
			System.gc();
		}
		if (runningAppProcessInfoList != null) {
			int cnt = runningAppProcessInfoList.size();
			for (int i = 0; i < cnt; ++i) {
				RunningAppProcessInfo appProcessInfo = runningAppProcessInfoList.get(i);
//				Log.d(TAG, "process name : " + appProcessInfo.processName);
//				Log.d(TAG, "importance : " + appProcessInfo.importance);

				if (appProcessInfo.importance > RunningAppProcessInfo.IMPORTANCE_VISIBLE) {
					String[] pkgList = appProcessInfo.pkgList;
					int pkgCnt = pkgList.length;
					for (int j = 0; j < pkgCnt; ++j) {
						Log.d(TAG, "available memory info : " + availableMem);
						Log.d(TAG, "It will be killed, package name : " + pkgList[j]);

						if (!"cn.cibntv.ott".equals(pkgList[j])) {
							am.killBackgroundProcesses(pkgList[j]);
						}
					}
				}
			}
		}
		
		// long afterMem = getAvailableMemory(context);
		// Log.d(TAG, "after memory info : " + afterMem);
		// Toast.makeText(context, "优化 " + count + " 个进程, " + (afterMem -
		// availableMem) / 1024 + "M",
		// Toast.LENGTH_LONG).show();
	}

	public static long getAvailableMemory(Context context) {
		ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		activityManager.getMemoryInfo(mi);
		return mi.availMem / 1024 / 1024;
	}

	/**
	 * 获取SD卡总大小
	 * 
	 * @return
	 */
	public static long getSDTotalSize() {
		File sdcardDir = Environment.getExternalStorageDirectory();
		StatFs statFs = new StatFs(sdcardDir.getPath());
		long blockSize = statFs.getBlockSizeLong();
		long totalSize = statFs.getBlockCountLong();
		return blockSize * totalSize;
	}

	/**
	 * 获得sd卡剩余容量，即可用大小
	 * 
	 * @return 剩余多少M
	 */
	public static long getSDAvaliableSize() {
		// 获取存储卡路径
		File path = Environment.getExternalStorageDirectory();
		// StatFs 看文件系统空间使用情况
		StatFs statFs = new StatFs(path.getPath());
		long blockSize = statFs.getBlockSizeLong();
		long availableBlocks = statFs.getAvailableBlocksLong();
		return blockSize * availableBlocks;
	}

	public static long getApkInstallDirSzie(Context context) {
		long usableSpace = context.getFilesDir().getAbsoluteFile().getUsableSpace();
		return usableSpace;
	}

	/**
	 * 获取系统总内存
	 * 
	 * @param context
	 *            可传入应用程序上下文。
	 * @return 总内存大单位为B。
	 */
	public static long getTotalMemorySize(Context context) {
		String dir = "/proc/meminfo";
		try {
			FileReader fr = new FileReader(dir);
			BufferedReader br = new BufferedReader(fr, 2048);
			String memoryLine = br.readLine();
			String subMemoryLine = memoryLine.substring(memoryLine.indexOf("MemTotal:"));
			br.close();
			return Integer.parseInt(subMemoryLine.replaceAll("\\D+", "")) * 1024l;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}
}
