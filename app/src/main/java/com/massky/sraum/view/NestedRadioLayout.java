package com.massky.sraum.view;

import android.content.Context;
import android.util.AttributeSet;


public class NestedRadioLayout extends BaseRadioLayout {

    public NestedRadioLayout(Context context) {
        super(context);
    }

    public NestedRadioLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NestedRadioLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public NestedRadioLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void toggle() {
        if (!isChecked()) {
            super.toggle();
        } else {//说明点击是它自己,并且他自己已经处于选中阶段
            //setChecked(true);
            if (mOnCheckedChangeWidgetListener != null) {
                mOnCheckedChangeWidgetListener.onCheckedByItemSelf();
            }
        }
    }
}
