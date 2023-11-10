package com.massky.sraum.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.lib.WheelView;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.view.BasePickerView;
import com.bigkoo.pickerview.view.WheelTime;
import com.massky.sraum.R;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class ApModePickerView extends ApBaseBottomView implements View.OnClickListener {
    private int layoutRes;
    private CustomListener customListener;

    WheelTime wheelTime; //自定义控件
    private Button btnSubmit, btnCancel; //确定、取消按钮
    private TextView tvTitle;//标题
    private OnTimeSelectListener timeSelectListener;//回调接口
    private int gravity = Gravity.CENTER;//内容显示位置 默认居中
    private boolean[] type;// 显示类型

    private String Str_Submit;//确定按钮字符串
    private String Str_Cancel;//取消按钮字符串
    private String Str_Title;//标题字符串

    private int Color_Submit;//确定按钮颜色
    private int Color_Cancel;//取消按钮颜色
    private int Color_Title;//标题颜色

    private int Color_Background_Wheel;//滚轮背景颜色
    private int Color_Background_Title;//标题背景颜色

    private int Size_Submit_Cancel;//确定取消按钮大小
    private int Size_Title;//标题字体大小
    private int Size_Content;//内容字体大小

    private Calendar date;//当前选中时间
    private Calendar startDate;//开始时间
    private Calendar endDate;//终止时间
    private int startYear;//开始年份
    private int endYear;//结尾年份

    private boolean cyclic;//是否循环
    private boolean cancelable;//是否能取消
    private boolean isCenterLabel;//是否只显示中间的label
    private boolean isLunarCalendar;//是否显示农历

    private int textColorOut; //分割线以外的文字颜色
    private int textColorCenter; //分割线之间的文字颜色
    private int dividerColor; //分割线的颜色
    private int backgroundId; //显示时的外部背景色颜色,默认是灰色

    // 条目间距倍数 默认1.6
    private float lineSpacingMultiplier = 1.6F;
    private boolean isDialog;//是否是对话框模式
    private String label_year, label_month, label_day, label_hours, label_mins, label_seconds;
    private int xoffset_year, xoffset_month, xoffset_day, xoffset_hours, xoffset_mins, xoffset_seconds;
    private WheelView.DividerType dividerType;//分隔线类型

    private static final String TAG_SUBMIT = "submit";
    private static final String TAG_CANCEL = "cancel";

    //构造方法
    public ApModePickerView(Builder builder) {
        super(builder.context);
        this.timeSelectListener = builder.timeSelectListener;
        this.gravity = builder.gravity;
        this.type = builder.type;
        this.Str_Submit = builder.Str_Submit;
        this.Str_Cancel = builder.Str_Cancel;
        this.Str_Title = builder.Str_Title;
        this.Color_Submit = builder.Color_Submit;
        this.Color_Cancel = builder.Color_Cancel;
        this.Color_Title = builder.Color_Title;
        this.Color_Background_Wheel = builder.Color_Background_Wheel;
        this.Color_Background_Title = builder.Color_Background_Title;
        this.Size_Submit_Cancel = builder.Size_Submit_Cancel;
        this.Size_Title = builder.Size_Title;
        this.Size_Content = builder.Size_Content;
        this.startYear = builder.startYear;
        this.endYear = builder.endYear;
        this.startDate = builder.startDate;
        this.endDate = builder.endDate;
        this.date = builder.date;
        this.cyclic = builder.cyclic;
        this.isCenterLabel = builder.isCenterLabel;
        this.isLunarCalendar = builder.isLunarCalendar;
        this.cancelable = builder.cancelable;
        this.label_year = builder.label_year;
        this.label_month = builder.label_month;
        this.label_day = builder.label_day;
        this.label_hours = builder.label_hours;
        this.label_mins = builder.label_mins;
        this.label_seconds = builder.label_seconds;
        this.xoffset_year = builder.xoffset_year;
        this.xoffset_month = builder.xoffset_month;
        this.xoffset_day = builder.xoffset_day;
        this.xoffset_hours = builder.xoffset_hours;
        this.xoffset_mins = builder.xoffset_mins;
        this.xoffset_seconds = builder.xoffset_seconds;
        this.textColorCenter = builder.textColorCenter;
        this.textColorOut = builder.textColorOut;
        this.dividerColor = builder.dividerColor;
        this.customListener = builder.customListener;
        this.layoutRes = builder.layoutRes;
        this.lineSpacingMultiplier = builder.lineSpacingMultiplier;
        this.isDialog = builder.isDialog;
        this.dividerType = builder.dividerType;
        this.backgroundId = builder.backgroundId;
        this.decorView = builder.decorView;
        initView(builder.context);
    }


    //建造器
    public static class Builder {
        private int layoutRes = R.layout.pickerview_time;
        private CustomListener customListener;
        private Context context;
        private OnTimeSelectListener timeSelectListener;
        private boolean[] type = new boolean[]{true, true, true, true, true, true};//显示类型 默认全部显示
        private int gravity = Gravity.CENTER;//内容显示位置 默认居中

        private String Str_Submit;//确定按钮文字
        private String Str_Cancel;//取消按钮文字
        private String Str_Title;//标题文字

        private int Color_Submit;//确定按钮颜色
        private int Color_Cancel;//取消按钮颜色
        private int Color_Title;//标题颜色

        private int Color_Background_Wheel;//滚轮背景颜色
        private int Color_Background_Title;//标题背景颜色

        private int Size_Submit_Cancel = 17;//确定取消按钮大小
        private int Size_Title = 18;//标题字体大小
        private int Size_Content = 18;//内容字体大小
        private Calendar date;//当前选中时间
        private Calendar startDate;//开始时间
        private Calendar endDate;//终止时间
        private int startYear;//开始年份
        private int endYear;//结尾年份

        private boolean cyclic = false;//是否循环
        private boolean cancelable = true;//是否能取消

        private boolean isCenterLabel = true;//是否只显示中间的label
        private boolean isLunarCalendar = false;//是否显示农历
        public ViewGroup decorView;//显示pickerview的根View,默认是activity的根view

        private int textColorOut; //分割线以外的文字颜色
        private int textColorCenter; //分割线之间的文字颜色
        private int dividerColor; //分割线的颜色
        private int backgroundId; //显示时的外部背景色颜色,默认是灰色
        private WheelView.DividerType dividerType;//分隔线类型
        // 条目间距倍数 默认1.6
        private float lineSpacingMultiplier = 1.6F;

        private boolean isDialog;//是否是对话框模式

        private String label_year, label_month, label_day, label_hours, label_mins, label_seconds;//单位
        private int xoffset_year, xoffset_month, xoffset_day, xoffset_hours, xoffset_mins, xoffset_seconds;//单位

        //Required
        public Builder(Context context, OnTimeSelectListener listener) {
            this.context = context;
            this.timeSelectListener = listener;
        }

        //Option
        public Builder setType(boolean[] type) {
            this.type = type;
            return this;
        }

        public Builder gravity(int gravity) {
            this.gravity = gravity;
            return this;
        }


        public Builder setContentSize(int Size_Content) {
            this.Size_Content = Size_Content;
            return this;
        }

        /**
         * 因为系统Calendar的月份是从0-11的,所以如果是调用Calendar的set方法来设置时间,月份的范围也要是从0-11
         *
         * @param date
         * @return
         */
        public Builder setDate(Calendar date) {
            this.date = date;
            return this;
        }

        public Builder setLayoutRes(int res, CustomListener customListener) {
            this.layoutRes = res;
            this.customListener = customListener;
            return this;
        }

        /**
         * 设置起始时间
         * 因为系统Calendar的月份是从0-11的,所以如果是调用Calendar的set方法来设置时间,月份的范围也要是从0-11
         *
         * @return
         */

        public Builder setRangDate(Calendar startDate, Calendar endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
            return this;
        }


        public ApModePickerView build() {
            return new ApModePickerView(this);
        }
    }


    private void initView(Context context) {
        initViews(0);
        init();
        initEvents();

        customListener.customLayout(LayoutInflater.from(context).inflate(layoutRes, contentContainer));
        // 时间转轮 自定义控件
        LinearLayout ApModePickerView = (LinearLayout) findViewById(R.id.timepicker);

    }


    @Override
    public void onClick(View v) {

    }


    public interface OnTimeSelectListener {
        void onTimeSelect(Date date, View v);
    }

    @Override
    public boolean isDialog() {
        return isDialog;
    }
}
