package com.yxc.commonlib.util;

import android.content.Context;

import androidx.core.content.ContextCompat;

/**
 * @author yxc
 * @date 2019/3/1
 */
public class ColorUtil {

    /**
     * 获取资源中的颜色
     * @param color
     * @return
     */
    public static int getResourcesColor(Context context, int color) {

        int ret = 0x00ffffff;
        try {
           // ret = context.getResources().getColor(color);
            ret = ContextCompat.getColor(context,color);
        } catch (Exception e) {
        }

        return ret;
    }
}
