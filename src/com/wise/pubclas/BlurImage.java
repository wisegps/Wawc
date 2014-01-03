package com.wise.pubclas;

import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

public class BlurImage {
    /**
     * 模糊操作
     * 
     * @param bitmap_main
     * @param bitmap_over
     * @param canvas
     * @param blur
     *            （0=<blur<=255）值越大越不清
     */
    public void blurImage(Bitmap bitmap_main, Bitmap bitmap_over,
            Canvas canvas, int blur) {

        int width = bitmap_main.getWidth();
        int height = bitmap_main.getHeight();

        // 设置覆盖图的图片的宽和高与模糊图片相同
        bitmap_over = setOverImage(bitmap_over, width, height);
        Paint paint = new Paint();
        // 消除锯齿
        paint.setAntiAlias(true);
        // 先画要模糊的图片
        canvas.drawBitmap(bitmap_main, 0, 0, paint);
        // 设置画笔透明度(透明度越低，越模糊)
        paint.setAlpha(blur);
        // 画上覆盖图片
        canvas.drawBitmap(bitmap_over, 0, 0, paint);
    }

    /**
     * 以最省内存的方式读取本地资源的图片
     * 
     * @param context
     * @param resId
     * @return
     */
    public Bitmap readBitMap(Context context, int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        // 获取资源图片
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }

    /**
     * 设置覆盖图片的大小
     * 
     * @param bmp
     * @param new_width
     * @param new_height
     * @return
     */
    private Bitmap setOverImage(Bitmap bmp, int new_width, int new_height) {
        Bitmap bitmap = null;
        try {
            int width = bmp.getWidth();
            int height = bmp.getHeight();

            float scale_w = ((float) new_width) / width;
            float scale_h = ((float) new_height) / height;
            // 创建操作图片用的matrix对象
            Matrix matrix = new Matrix();
            // 缩放图片动作
            matrix.postScale(scale_w, scale_h);
            // 创建新的图片
            bitmap = Bitmap
                    .createBitmap(bmp, 0, 0, width, height, matrix, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
