package com.example.smarttourism.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

public class BitmapUtils {
    //裁剪图片为正方形，使其适应控件的宽高
    public static Bitmap zoomBitmap(Bitmap source, int size) {
        //先得到 source 的宽高
        int w = source.getWidth();
        int h = source.getHeight();
        //计算居中正方形的起点和边长
        int squareSize = Math.min(w, h);
        int x = (w - squareSize) / 2;
        int y = (h - squareSize) / 2;
        //从 source 上裁剪出正方形子图
        Bitmap square = Bitmap.createBitmap(source, x, y, squareSize, squareSize);
        //再把正方形缩放到指定 size×size（如果需要）
        Bitmap scaled = Bitmap.createScaledBitmap(square, size, size, true);
        return scaled;
    }

    //图片按照宽高调整裁剪(圆形头像实现)
    public static Bitmap circleBitmap(Bitmap source, int size) {
        //先得到 source 的宽高
        int w = source.getWidth();
        int h = source.getHeight();
        //计算居中正方形的起点和边长
        int squareSize = Math.min(w, h);
        int x = (w - squareSize) / 2;
        int y = (h - squareSize) / 2;
        //从 source 上裁剪出正方形子图
        Bitmap square = Bitmap.createBitmap(source, x, y, squareSize, squareSize);
        //再把正方形缩放到指定 size×size（如果需要）
        Bitmap scaled = Bitmap.createScaledBitmap(square, size, size, true);
        //在 scaled 上画圆形遮罩
        Bitmap output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        float radius = size / 2f;
        //画一个圆
        canvas.drawCircle(radius, radius, radius, paint);
        //取交集
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(scaled, 0, 0, paint);
        return output;
    }
}
