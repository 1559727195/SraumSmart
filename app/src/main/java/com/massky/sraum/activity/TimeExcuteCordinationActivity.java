package com.massky.sraum.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.bigkoo.pickerview.listener.CustomListener;
import com.dialog.CommonData;
import com.dialog.ToastUtils;
import com.example.jpushdemo.ExampleUtil;
import com.massky.sraum.R;
import com.massky.sraum.Util.SharedPreferencesUtil;
import com.massky.sraum.Util.ToastUtil;
import com.massky.sraum.Utils.ApiHelper;
import com.massky.sraum.Utils.AppManager;
import com.massky.sraum.base.BaseActivity;
import com.yanzhenjie.statusview.StatusUtils;
import com.ypy.eventbus.EventBus;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import webapp.download.DownLoadUtils;
import webapp.download.DownloadApk;

import static com.example.jpushdemo.MyReceiver.ACTION_NOTIFICATION_OPENED_MAIN;

/**
 * Created by zhu on 2018/1/9.
 */

public class TimeExcuteCordinationActivity extends BaseActivity {
    public static final String MESSAGE_TIME_EXCUTE_ACTION = "com.massky.sraum.time_excute";
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.next_step_txt)
    TextView next_step_txt;
    @BindView(R.id.again_time_exeute_set)
    RelativeLayout again_time_exeute_set;
    @BindView(R.id.rel_scene_set)
    RelativeLayout rel_scene_set;
    @BindView(R.id.root_layout)
    LinearLayout root_layout;
    private TimePickerView pvCustomTime;
    @BindView(R.id.start_scenetime)
    TextView start_scenetime;
    private Map time_map = new HashMap();
    @BindView(R.id.again_time_exeute)
    TextView again_time_exeute;

    @Override
    protected int viewId() {
        return R.layout.timeexecute_cordination_act;
    }

    @Override
    protected void onView() {
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        initCustomTimePicker();
        registerMessageReceiver();
    }

    //for receive customer msg from jpush server
    private MessageReceiver mMessageReceiver;
    public static final String KEY_TITLE = "title";

    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_TIME_EXCUTE_ACTION);
        registerReceiver(mMessageReceiver, filter);
    }

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (MESSAGE_TIME_EXCUTE_ACTION.equals(intent.getAction())) {

//                Map map = new HashMap();
//                map.put("name",stringBuffer_name);
//                map.put("condition","5");
//                map.put("minValue",stringBuffer_value);
//                sendBroad(map);
//                break;
                time_map = (Map) intent.getSerializableExtra("time_map");
                again_time_exeute.setText(time_map.get("name").toString());
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mMessageReceiver);
    }

    @Override
    protected void onEvent() {
        back.setOnClickListener(this);
        next_step_txt.setOnClickListener(this);
        again_time_exeute_set.setOnClickListener(this);
        rel_scene_set.setOnClickListener(this);
    }

    @Override
    protected void onData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                TimeExcuteCordinationActivity.this.finish();
                break;
            case R.id.next_step_txt:

//                startActivity(new Intent(TimeExcuteCordinationActivity.this,
//                        SelectExecuteSceneResultActivity.class));
                String startTime = start_scenetime.getText().toString();
                if (startTime == null || startTime.equals("")) {
                    ToastUtil.showToast(TimeExcuteCordinationActivity.this, "请选择定时时间");
                    return;
                }
                Map map = new HashMap();
                map.put("deviceType", "");
                map.put("deviceId", "");
                map.put("name", startTime);
                map.put("boxName", "");
                map.put("type", "102");

                if (time_map.get("name") == null) {
                    ToastUtil.showToast(TimeExcuteCordinationActivity.this, "请选择定时执行条件");
                    return;
                }
                map.put("action", time_map.get("name"));
                map.put("condition", time_map.get("condition"));
                map.put("minValue", time_map.get("minValue"));
                map.put("maxValue", "");
                map.put("name1", startTime);
                map.put("startTime", startTime);
                map.put("endTime", "");

                Intent intent = null;
                boolean add_condition = (boolean) SharedPreferencesUtil.getData(TimeExcuteCordinationActivity.this, "add_condition", false);
                if (add_condition) {
//            AppManager.getAppManager().removeActivity_but_activity_cls(MainfragmentActivity.class);
                    AppManager.getAppManager().finishActivity_current(SelectSensorActivity.class);
                    AppManager.getAppManager().finishActivity_current(EditLinkDeviceResultActivity.class);
                    intent = new Intent(TimeExcuteCordinationActivity.this, EditLinkDeviceResultActivity.class);
                    intent.putExtra("sensor_map", (Serializable) map);
                    startActivity(intent);
                    TimeExcuteCordinationActivity.this.finish();
                } else {
                    intent = new Intent(TimeExcuteCordinationActivity.this,
                            SelectiveLinkageActivity.class);
                    intent.putExtra("link_map", (Serializable) map);
                    startActivity(intent);
                }


                break;
            case R.id.again_time_exeute_set://重复设置方式，执行一次
                startActivity(new Intent(TimeExcuteCordinationActivity.this, AutoAgainSceneActivity.class));
                break;
            case R.id.rel_scene_set:
//                showPopFormBottom(null);
                pvCustomTime.show(); //弹出自定义时间选择器
                break;//设置时间
        }
    }

    private void initCustomTimePicker() {

        /**
         * @description
         *
         * 注意事项：
         * 1.自定义布局中，id为 optionspicker 或者 timepicker 的布局以及其子控件必须要有，否则会报空指针.
         * 具体可参考demo 里面的两个自定义layout布局。
         * 2.因为系统Calendar的月份是从0-11的,所以如果是调用Calendar的set方法来设置时间,月份的范围也要是从0-11
         * setRangDate方法控制起始终止时间(如果不设置范围，则使用默认时间1900-2100年，此段代码可注释)
         */
        Calendar selectedDate = Calendar.getInstance();//系统当前时间
        Calendar startDate = Calendar.getInstance();
        startDate.set(2014, 1, 23);
        Calendar endDate = Calendar.getInstance();
        endDate.set(2027, 2, 28);
        //时间选择器 ，自定义布局
        pvCustomTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                getTime(date);
                Log.e("robin debug", "getTime(date):" + getTime(date));
                String hourPicker = String.valueOf(date.getHours());
                String minutePicker = String.valueOf(date.getMinutes());
                switch (String.valueOf(minutePicker).length()) {
                    case 1:
                        minutePicker = "0" + minutePicker;
                        break;
                }

                switch (String.valueOf(hourPicker).length()) {
                    case 1:
                        hourPicker = "0" + hourPicker;
                        break;
                }

                start_scenetime.setText(hourPicker + ":" + minutePicker);
            }
        })
                /*.setType(TimePickerView.Type.ALL)//default is all
                .setCancelText("Cancel")
                .setSubmitText("Sure")
                .setContentSize(18)
                .setTitleSize(20)
                .setTitleText("Title")
                .setTitleColor(Color.BLACK)
               /*.setDividerColor(Color.WHITE)//设置分割线的颜色
                .setTextColorCenter(Color.LTGRAY)//设置选中项的颜色
                .setLineSpacingMultiplier(1.6f)//设置两横线之间的间隔倍数
                .setTitleBgColor(Color.DKGRAY)//标题背景颜色 Night mode
                .setBgColor(Color.BLACK)//滚轮背景颜色 Night mode
                .setSubmitColor(Color.WHITE)
                .setCancelColor(Color.WHITE)*/
                /*.gravity(Gravity.RIGHT)// default is center*/
                .setDate(selectedDate)
                .setRangDate(startDate, endDate)
                .setLayoutRes(R.layout.pickerview_custom_time, new CustomListener() {

                    @Override
                    public void customLayout(View v) {
                        final ImageView tvSubmit = (ImageView) v.findViewById(R.id.ok_bt);
                        ImageView ivCancel = (ImageView) v.findViewById(R.id.finish_bt);
                        tvSubmit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomTime.returnData();
                                pvCustomTime.dismiss();
                            }
                        });

                        ivCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomTime.dismiss();
                            }
                        });
                    }
                })
                .setContentSize(18)
                .setType(new boolean[]{false, false, false, true, true, false})
                .setLabel("年", "月", "日", "时", "分", "秒")
                .setLineSpacingMultiplier(1.2f)
                .setTextXOffset(0, 0, 0, 0, 0, 0)
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setDividerColor(0xFF24AD9D)
                .build();
    }

    private String getTime(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }
}
