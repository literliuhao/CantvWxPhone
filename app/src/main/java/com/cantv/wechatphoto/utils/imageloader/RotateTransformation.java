package com.cantv.wechatphoto.utils.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

public class RotateTransformation extends BitmapTransformation {

    private int rotateRotationAngle = 0;
    private Boolean mScale;

    public RotateTransformation(Context context, int rotateRotationAngle, Boolean isScale) {
        super(context);
        this.rotateRotationAngle = rotateRotationAngle;
        this.mScale = isScale;
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        Matrix matrix = new Matrix();
        float scale = (float) outWidth / toTransform.getWidth();
        matrix.postRotate(rotateRotationAngle);
        if (mScale) matrix.postScale(scale, scale);
        return Bitmap.createBitmap(toTransform, 0, 0, toTransform.getWidth(), toTransform.getHeight(), matrix, true);
    }

    @Override
    public String getId() {
        return "rotate" + rotateRotationAngle;
    }
}