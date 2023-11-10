package com.massky.sraum.Util;

import android.content.Context;

/**
 * Created by zhu on 2018/1/2.
 */

public class DipUtil {
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
//将pixel转换成dip(dp)
    public static int pixelToDip(Context context, float pixelValue) {
        float density = context.getResources().getDisplayMetrics().density;
        int dipValue = (int) (pixelValue / density + 0.5f);
        System.out.println("pixelToDip---> pixelValue=" + pixelValue + ",density=" + density + ",dipValue=" + dipValue);
        return dipValue;
    }

    //将dip(dp)转换成pixel
    public static int dipToPixel(Context context, float dipValue) {
        float density = context.getResources().getDisplayMetrics().density;
        int pixelValue = (int) (dipValue * density + 0.5f);
        System.out.println("dipToPixel---> dipValue=" + dipValue + ",density=" + density + ",pixelValue=" + pixelValue);
        return pixelValue;
    }

    //将pixel转换成sp
    public static int pixelToSp(Context context, float pixelValue) {
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        int sp = (int) (pixelValue / scaledDensity + 0.5f);
        System.out.println("pixelToSp---> pixelValue=" + pixelValue + ",scaledDensity=" + scaledDensity + ",sp=" + sp);
        return sp;
    }

    //将sp转换成pixel
    public static int spToPixel(Context context, float spValue) {
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        int pixelValue = (int) (spValue * scaledDensity + 0.5f);
        System.out.println("spToPixel---> spValue=" + spValue + ",scaledDensity=" + scaledDensity + ",pixelValue=" + pixelValue);
        return pixelValue;
    }
}

