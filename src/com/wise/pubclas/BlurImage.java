package com.wise.pubclas;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

public class BlurImage {
    
    /**
     * 圆形图片
     * @param bitmap
     * @return
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = bitmap.getWidth() / 2;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }
    /**
     * 获取正方形图片
     * @param bitmap
     * @return
     */
    public static Bitmap getSquareBitmaps(Bitmap bitmap){
        int imageWidth = bitmap.getWidth();
        int imageHeight = bitmap.getHeight();
        Log.e("imageWidth",imageWidth+"");  //  640
        Log.e("imageHeight",imageHeight+"");  //  480
        int y = 0;
        Bitmap image = null;
        if(imageHeight > imageWidth){
        	y = (imageHeight - imageWidth)/2;
        	image = Bitmap.createBitmap(bitmap, 0, y, imageWidth, imageWidth);
        }else{
        	y = (imageWidth - imageHeight)/2;
        	image = Bitmap.createBitmap(bitmap, y, 0, imageHeight, imageHeight);
        }
        return image;
    }
    public static Bitmap getSquareBitmap(Bitmap bitmap){
        int imageWidth = bitmap.getWidth();
        int imageHeight = bitmap.getHeight();
        int y = 0;
        Bitmap image = null;
        if(imageHeight > imageWidth){
            y = (imageHeight - imageWidth)/2;
            image = Bitmap.createBitmap(bitmap, 0, y, imageWidth, imageWidth);
        }else{
            y = (imageWidth - imageHeight)/2;
            image = Bitmap.createBitmap(bitmap, y, 0, imageHeight, imageHeight);
        }
        return image;
    }
    
    /**
     * 缩小图片
     * @param Path 文件sd卡路径
     * @param reqWidth 缩小的宽度
     * @param reqHeight 缩小的高度
     * @return
     */
    public static Bitmap decodeSampledBitmapFromPath(String Path, int reqWidth, int reqHeight) {  
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小  
        final BitmapFactory.Options options = new BitmapFactory.Options();  
        options.inJustDecodeBounds = true;  
        //BitmapFactory.decodeResource(res, resId, options);  
        BitmapFactory.decodeFile(Path, options);
        // 调用上面定义的方法计算inSampleSize值  
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);  
        // 使用获取到的inSampleSize值再次解析图片  
        options.inJustDecodeBounds = false;  
        return BitmapFactory.decodeFile(Path, options);
    }
    
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {  
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小  
        final BitmapFactory.Options options = new BitmapFactory.Options();  
        options.inJustDecodeBounds = true;  
        BitmapFactory.decodeResource(res, resId, options);  
        // 调用上面定义的方法计算inSampleSize值  
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);  
        // 使用获取到的inSampleSize值再次解析图片  
        options.inJustDecodeBounds = false;  
        return BitmapFactory.decodeResource(res, resId, options);  
    }
    
    /**
     * 计算缩放尺寸
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {  
        //BitmapFactory.decodeFile(pathName, opts)
        // 源图片的高度和宽度  
        final int height = options.outHeight;  
        final int width = options.outWidth;
        int inSampleSize = 1;
        if(height > width){//竖着拍
            if (height > reqHeight || width > reqWidth) {
                // 计算出实际宽高和目标宽高的比率  
                final int heightRatio = Math.round((float) height / (float) reqHeight);  
                final int widthRatio = Math.round((float) width / (float) reqWidth);  
                // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高  
                // 一定都会大于等于目标的宽和高。  
                inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;  
            }
        }else{//横这拍
            if (height > reqWidth || width > reqHeight) {
                // 计算出实际宽高和目标宽高的比率  
                final int heightRatio = Math.round((float) height / (float) reqWidth);  
                final int widthRatio = Math.round((float) width / (float) reqHeight);  
                // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高  
                // 一定都会大于等于目标的宽和高。  
                inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;  
            }
        }          
        return inSampleSize;  
    }
    
    
    
    
    /** 水平方向模糊度 */
    private static float hRadius = 5;
    /** 竖直方向模糊度 */
    private static float vRadius = 5;
    /** 模糊迭代度 */
    private static int iterations = 5;
    /**
     * 高斯模糊
     * @param bmp
     * @return
     */
    public static Bitmap BoxBlurFilter(Bitmap bmp) {
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        int[] inPixels = new int[width * height];
        int[] outPixels = new int[width * height];
        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bmp.getPixels(inPixels, 0, width, 0, 0, width, height);
        for (int i = 0; i < iterations; i++) {
            blur(inPixels, outPixels, width, height, hRadius);
            blur(outPixels, inPixels, height, width, vRadius);
        }
        blurFractional(inPixels, outPixels, width, height, hRadius);
        blurFractional(outPixels, inPixels, height, width, vRadius);
        bitmap.setPixels(inPixels, 0, width, 0, 0, width, height);
        //Drawable drawable = new BitmapDrawable(bitmap);
        return bitmap;
    }

    public static void blur(int[] in, int[] out, int width, int height,
            float radius) {
        int widthMinus1 = width - 1;
        int r = (int) radius;
        int tableSize = 2 * r + 1;
        int divide[] = new int[256 * tableSize];

        for (int i = 0; i < 256 * tableSize; i++)
            divide[i] = i / tableSize;

        int inIndex = 0;

        for (int y = 0; y < height; y++) {
            int outIndex = y;
            int ta = 0, tr = 0, tg = 0, tb = 0;

            for (int i = -r; i <= r; i++) {
                int rgb = in[inIndex + clamp(i, 0, width - 1)];
                ta += (rgb >> 24) & 0xff;
                tr += (rgb >> 16) & 0xff;
                tg += (rgb >> 8) & 0xff;
                tb += rgb & 0xff;
            }

            for (int x = 0; x < width; x++) {
                out[outIndex] = (divide[ta] << 24) | (divide[tr] << 16)
                        | (divide[tg] << 8) | divide[tb];

                int i1 = x + r + 1;
                if (i1 > widthMinus1)
                    i1 = widthMinus1;
                int i2 = x - r;
                if (i2 < 0)
                    i2 = 0;
                int rgb1 = in[inIndex + i1];
                int rgb2 = in[inIndex + i2];

                ta += ((rgb1 >> 24) & 0xff) - ((rgb2 >> 24) & 0xff);
                tr += ((rgb1 & 0xff0000) - (rgb2 & 0xff0000)) >> 16;
                tg += ((rgb1 & 0xff00) - (rgb2 & 0xff00)) >> 8;
                tb += (rgb1 & 0xff) - (rgb2 & 0xff);
                outIndex += height;
            }
            inIndex += width;
        }
    }

    public static void blurFractional(int[] in, int[] out, int width,
            int height, float radius) {
        radius -= (int) radius;
        float f = 1.0f / (1 + 2 * radius);
        int inIndex = 0;

        for (int y = 0; y < height; y++) {
            int outIndex = y;

            out[outIndex] = in[0];
            outIndex += height;
            for (int x = 1; x < width - 1; x++) {
                int i = inIndex + x;
                int rgb1 = in[i - 1];
                int rgb2 = in[i];
                int rgb3 = in[i + 1];

                int a1 = (rgb1 >> 24) & 0xff;
                int r1 = (rgb1 >> 16) & 0xff;
                int g1 = (rgb1 >> 8) & 0xff;
                int b1 = rgb1 & 0xff;
                int a2 = (rgb2 >> 24) & 0xff;
                int r2 = (rgb2 >> 16) & 0xff;
                int g2 = (rgb2 >> 8) & 0xff;
                int b2 = rgb2 & 0xff;
                int a3 = (rgb3 >> 24) & 0xff;
                int r3 = (rgb3 >> 16) & 0xff;
                int g3 = (rgb3 >> 8) & 0xff;
                int b3 = rgb3 & 0xff;
                a1 = a2 + (int) ((a1 + a3) * radius);
                r1 = r2 + (int) ((r1 + r3) * radius);
                g1 = g2 + (int) ((g1 + g3) * radius);
                b1 = b2 + (int) ((b1 + b3) * radius);
                a1 *= f;
                r1 *= f;
                g1 *= f;
                b1 *= f;
                out[outIndex] = (a1 << 24) | (r1 << 16) | (g1 << 8) | b1;
                outIndex += height;
            }
            out[outIndex] = in[width - 1];
            inIndex += width;
        }
    }
    
    /**
     * 图片缩放
     */
    
    public static Bitmap zoomImg(String pathName, int newWidth ,int newHeight){
    		Bitmap bm = BitmapFactory.decodeFile(pathName);
    	   // 获得图片的宽高
    	   int width = bm.getWidth();
    	   int height = bm.getHeight();
    	   // 计算缩放比例
    	   float scaleWidth = ((float) newWidth) / width;
    	   float scaleHeight = ((float) newHeight) / height;
    	   // 取得想要缩放的matrix参数
    	   Matrix matrix = new Matrix();
    	   matrix.postScale(scaleWidth, scaleWidth);
    	   // 得到新的图片
    	   Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
    	    return newbm;
    	}

    public static int clamp(int x, int a, int b) {
        return (x < a) ? a : (x > b) ? b : x;
    }
}
