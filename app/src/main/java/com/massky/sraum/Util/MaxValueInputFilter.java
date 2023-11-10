package com.massky.sraum.Util;

import android.text.InputFilter;
import android.text.Spanned;

public class MaxValueInputFilter implements InputFilter {
    private int maxValue;

    public MaxValueInputFilter(int maxValue) {
        this.maxValue = maxValue;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        try {
            // 将新输入的字符串转换为数字
            int input = Integer.parseInt(dest.toString() + source.toString());
            // 检查是否超过最大值
            if (input > maxValue) {
                // 超过最大值，返回空字符串表示不接受输入
                return "";
            }
        } catch (NumberFormatException e) {
            // 输入的内容无法转换为数字，返回空字符串表示不接受输入
            return "";
        }
        // 接受输入
        return null;
    }
}

