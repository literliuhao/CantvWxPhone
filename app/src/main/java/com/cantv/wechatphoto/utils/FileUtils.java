package com.cantv.wechatphoto.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.StatFs;

import com.cantv.wechatphoto.R;
import com.cantv.wechatphoto.interfaces.ICallBack;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 缓存设置
 */
public class FileUtils {
    /**
     * sd卡的根目录
     */
    private static String mSdRootPath = Environment.getExternalStorageDirectory().getPath();
    /**
     * 手机的缓存根目录
     */
    private static String mDataRootPath = null;
    /**
     * 保存Image的目录名
     */
    private final static String FOLDER_NAME = "/GlideCache";

    private static FileUtils fileUtils;

    public static FileUtils getInstance(Context mContext) {
        if (null == fileUtils) {
            fileUtils = new FileUtils(mContext);
        }
        return fileUtils;
    }

    private ICallBack mCallBack;

    public FileUtils(Context context) {
        mDataRootPath = context.getCacheDir().getPath();
    }

    /**
     * 获取储存Image的目录
     *
     * @return
     */
    private String getStorageDirectory() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ? mSdRootPath + FOLDER_NAME : mDataRootPath + FOLDER_NAME;
    }

    /**
     * 保存Image的方法，有sd卡存储到sd卡，没有就存储到手机目录
     *
     * @param fileName
     * @param bitmap
     * @throws IOException
     */
    public void savaBitmap(String fileName, Bitmap bitmap) throws Exception {
        if (bitmap == null) {
            return;
        }
        String path = getStorageDirectory();
        File folderFile = new File(path);
        if (!folderFile.exists()) {
            folderFile.mkdir();
        }
        File file = new File(path + File.separator + MD5FileUtil.encode(fileName));
        if (file.exists()) {
            return;
        }
        file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file);
        bitmap.compress(CompressFormat.JPEG, 100, fos);
        fos.flush();
        fos.close();
    }

    /**
     * 从手机或者sd卡获取Bitmap
     *
     * @param fileName
     * @return
     */
    public Bitmap getBitmap(String fileName) {
        return BitmapFactory.decodeFile(getStorageDirectory() + File.separator + fileName);
    }

    /**
     * 判断文件是否存在
     *
     * @param fileName
     * @return
     */
    public boolean isFileExists(String fileName) throws Exception {
        return new File(getStorageDirectory() + File.separator + MD5FileUtil.encode(fileName)).exists();
    }

    public String getFileCache(String fileName) throws Exception {
        return new File(getStorageDirectory() + File.separator + MD5FileUtil.encode(fileName)).getPath();
    }

    /**
     * 获取文件的大小
     *
     * @param fileName
     * @return
     */
    public long getFileSize(String fileName) {
        return new File(getStorageDirectory() + File.separator + fileName).length();
    }

    /**
     * 删除SD卡或者手机的缓存图片和目录
     */
    public void deleteFile() {
        File dirFile = new File(getStorageDirectory());
        if (!dirFile.exists()) {
            return;
        }
        if (dirFile.isDirectory()) {
            String[] children = dirFile.list();
            for (int i = 0; i < children.length; i++) {
                new File(dirFile, children[i]).delete();
            }
        }

        dirFile.delete();
    }

    public void readSDCard(ICallBack callBack) {
        this.mCallBack = callBack;
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File sdcardDir = Environment.getExternalStorageDirectory();
            StatFs sf = new StatFs(sdcardDir.getPath());
            long blockSize = sf.getBlockSize();
            long blockCount = sf.getBlockCount();
            long availCount = sf.getAvailableBlocks();

//            Log.i("readSDCard", "block大小:" + blockSize + ",block数目:" + blockCount + ",总大小:" + blockSize * blockCount / 1024 + "KB");
//            Log.i("readSDCard", "可用的block数目：:" + availCount + ",剩余空间:" + availCount * blockSize / 1024 + "KB");

            long total = availCount * blockSize / 1024;
            if (total < 10240 && total > 200) {
                mCallBack.result(R.string.wechat_start_error_10MB);
            } else if (total < 200) {
                mCallBack.result(R.string.wechat_start_error);
            } else {
                mCallBack.done();
            }
        } else {
            mCallBack.done();
        }
    }
}