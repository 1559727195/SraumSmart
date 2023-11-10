package com.massky.sraum.activity;

import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.massky.sraum.R;
import com.massky.sraum.Utils.AppManager;
import com.massky.sraum.base.BaseActivity;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

/**
 * Created by zhu on 2018/6/6.
 */

public class AirLinkageControlActivity extends BaseActivity implements
        SeekBar.OnSeekBarChangeListener {
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.next_step_txt)
    TextView next_step_txt;
    @BindView(R.id.project_select)
    TextView project_select;
    @BindView(R.id.status_view)
    StatusView statusView;


    /**
     * 空调
     *
     * @return
     */

    @BindView(R.id.air_control_radio_model)
    RadioGroup air_control_radio_model;
    @BindView(R.id.air_control_tmp_del)
    ImageView air_control_tmp_del;
    //air_control_tmp_add
    @BindView(R.id.air_control_tmp_add)
    ImageView air_control_tmp_add;
    @BindView(R.id.air_control_speed)
    RadioGroup air_control_speed;
    @BindView(R.id.air_control_radio_open_close)
    RadioGroup air_control_radio_open_close;
    @BindView(R.id.tmp_txt)
    TextView tmp_txt;
    private Map air_control_map = new HashMap();
    private int tempture;
    private Map sensor_map = new HashMap();//传感器map
    @BindView(R.id.mode_linear)
    LinearLayout mode_linear;
    @BindView(R.id.speed_linear)
    LinearLayout speed_linear;
    @BindView(R.id.view_speed)
    View view_speed;
    @BindView(R.id.radio_one_model)
    RadioButton radio_one_model;
    @BindView(R.id.radio_two_model)
    RadioButton radio_two_model;
    @BindView(R.id.radio_three_model)
    RadioButton radio_three_model;
    @BindView(R.id.radio_four_model)
    RadioButton radio_four_model;


    private Map map_panel = new HashMap();
    private String type;

    /**
     * 空调
     *
     * @return
     */

    @Override
    protected int viewId() {
        return R.layout.air_linkage_control_act;
    }

    @Override
    protected void onView() {
        // radio_one_model.setText("gdfklgmdfdfg");

        StatusUtils.setFullToStatusBar(this);  // StatusBar.
//        String type = (String) getIntent().getSerializableExtra("type");
        air_control_map = (Map) getIntent().getSerializableExtra("air_control_map");


//        map_panel.put("panelType", panelType);
//        map_panel.put("panelName", panelName);
//        map_panel.put("panelMAC", panelMAC);
//        map_panel.put("gatewayMAC", gatewayMAC);
//
//
//        intent.putExtra("air_control_map", (Serializable) list.get(0));
//        intent.putExtra("panel_map", (Serializable) map_panel);

        map_panel = (Map) getIntent().getSerializableExtra("panel_map");
//

        type = air_control_map.get("type").toString();
        switch (type) {
            case "3":
                mode_linear.setVisibility(View.VISIBLE);
                break;
            case "5":
                mode_linear.setVisibility(View.GONE);
                break;
            case "6":
                speed_linear.setVisibility(View.GONE);
                view_speed.setVisibility(View.GONE);
                break;
        }
        project_select.setText(air_control_map.get("name").toString());
        air_control_map.put("name1", air_control_map.get("name"));
        air_control_map.put("panelName", map_panel.get("panelName"));
        //intent.putExtra("sensor_map", (Serializable)  sensor_map);
        sensor_map = (Map) getIntent().getSerializableExtra("sensor_map");
        back.setOnClickListener(this);

        onEvent();
        next_step_txt.setOnClickListener(this);
        tmp_txt.setText(air_control_map.get("temperature").toString());
//
//        getData(true);
        init_action_select();
    }

    @Override
    protected void onEvent() {
        air_control_radio_model();
        air_control_speed();
        air_control_radio_open_close();
        air_control_tmp_del.setOnClickListener(this);
        air_control_tmp_add.setOnClickListener(this);
    }

    @Override
    protected void onData() {

    }


    /**
     * 初始化空调动作
     */
    private void init_action_select() {

        //温度选择
        String tempture = (String) air_control_map.get("temperature");
        try {
            if (Integer.parseInt(tempture) < 16) {
                tempture = 16 + "";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        tmp_txt.setText(tempture + "");

        //状态，在线，离线
        String status = (String) air_control_map.get("status");
        switch (status) {
            case "1":
                air_control_radio_open_close.check(R.id.radio_status_one);
                break;
            case "0":
                air_control_radio_open_close.check(R.id.radio_status_two);
                break;
        }

        speed();
        switch (type) {
            case "3":
                air_mode();
                break;
            case "6":
                dinuan_mode();
                break;
        }
    }

    private void speed() {
        String speed = (String) air_control_map.get("speed");
        switch (speed) {
            case "1":
                air_control_speed.check(R.id.radio_one_speed);
                break;
            case "2":
                air_control_speed.check(R.id.radio_two_speed);
                break;
            case "3":
                air_control_speed.check(R.id.radio_three_speed);
                break;
            case "6":
                air_control_speed.check(R.id.radio_four_speed);
                break;
            default:
                air_control_speed.check(R.id.radio_four_speed);
                break;
        }
    }

    private void air_mode() {
        String model = (String) air_control_map.get("mode");
        //

        switch (model) {
            case "1":
                air_control_radio_model.check(R.id.radio_one_model);
                break;
            case "2":
                air_control_radio_model.check(R.id.radio_two_model);
                break;
            case "3":
                air_control_radio_model.check(R.id.radio_three_model);
                break;
            case "4":
                air_control_radio_model.check(R.id.radio_four_model);
                break;
            default:
                air_control_radio_model.check(R.id.radio_four_model);
                break;
        }
    }


    private void dinuan_mode() {
        String model = (String) air_control_map.get("mode");
        radio_one_model.setText("加热");
        radio_two_model.setText("睡眠");
        radio_three_model.setText("外出");
        radio_four_model.setVisibility(View.GONE);
        switch (model) {
            case "1":
                air_control_radio_model.check(R.id.radio_one_model);
                break;
            case "2":
                air_control_radio_model.check(R.id.radio_two_model);
                break;
            case "3":
                air_control_radio_model.check(R.id.radio_three_model);
                break;
        }
    }


    /**
     * 风速
     */
    private void air_control_speed() {
        /**
         * 风速
         */
        for (int i = 0; i < air_control_speed.getChildCount(); i++) {
            final View view = air_control_speed.getChildAt(i);
            view.setTag(i);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) view.getTag();
                    switch (position) {
                        case 0:
                            common_doit("speed", "1");
                            break;
                        case 1:
                            common_doit("speed", "2");
                            break;
                        case 2:
                            common_doit("speed", "3");
                            break;
                        case 3:
                            common_doit("speed", "6");
                            break;
                    }
                }
            });
        }
    }

    /**
     * 开关
     */
    private void air_control_radio_open_close() {
        /**
         * 开关
         */
        for (int i = 0; i < air_control_radio_open_close.getChildCount(); i++) {
            final View view = air_control_radio_open_close.getChildAt(i);
            view.setTag(i);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) view.getTag();
                    switch (position) {
                        case 0:
                            common_doit("status", "1");
                            break;
                        case 1:
                            common_doit("status", "0");
                            break;
                    }
                }
            });
        }
    }

    /**
     * 公共项
     *
     * @param status
     * @param value
     */
    private void common_doit(String status, String value) {
        air_control_map.put(status, value);
    }


    /**
     * 模式
     */
    private void air_control_radio_model() {
        /**
         * 模式
         */
        for (int i = 0; i < air_control_radio_model.getChildCount(); i++) {
            final View view = air_control_radio_model.getChildAt(i);
            view.setTag(i);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) view.getTag();
                    switch (position) {
                        case 0:
                            common_doit("mode", "1");
                            break;
                        case 1:
                            common_doit("mode", "2");
                            break;
                        case 2:
                            common_doit("mode", "3");
                            break;
                        case 3:
                            common_doit("mode", "4");
                            break;
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                AirLinkageControlActivity.this.finish();
                break;
            case R.id.next_step_txt:
                init_action();
                AppManager.getAppManager().finishActivity_current(SelectiveLinkageActivity.class);
                AppManager.getAppManager().finishActivity_current(SelectiveLinkageDeviceDetailSecondActivity.class);
                AppManager.getAppManager().finishActivity_current(EditLinkDeviceResultActivity.class);
                Intent intent = new Intent(
                        AirLinkageControlActivity.this,
                        EditLinkDeviceResultActivity.class);
                //        map_panel.put("panelType", panelType);
//        map_panel.put("panelName", panelName);
//        map_panel.put("panelMAC", panelMAC);
//        map_panel.put("gatewayMAC", gatewayMAC);
//

                if (map_panel != null) {
                    String gatewayMAC = map_panel.get("gatewayMac").toString();
                    String panelMAC = map_panel.get("panelMac").toString();
                    String panelNumber = map_panel.get("panelNumber").toString();
                    String boxNumber = map_panel.get("boxNumber").toString();
                    //         map_panel.put("panelNumber", panelNumber);
                    air_control_map.put("gatewayMac", gatewayMAC);
                    air_control_map.put("panelMac", panelMAC);
                    air_control_map.put("panelNumber", panelNumber);
                    //           map_panel.put("boxNumber", boxNumber);
                    air_control_map.put("boxNumber", boxNumber);
                }

                intent.putExtra("device_map", (Serializable) air_control_map);

                intent.putExtra("sensor_map", (Serializable) sensor_map);
                AirLinkageControlActivity.this.finish();
                startActivity(intent);
                break;
            case R.id.air_control_tmp_add:
                try {
                    tempture = Integer.parseInt(air_control_map.get("temperature").toString());
                    if (tempture < 16) {
                        tempture = 16;
                    }
                    if (tempture < 30)
                        tempture++;
                    tmp_txt.setText(tempture + "");
                    common_doit("temperature", "" + tempture);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                break;
            case R.id.air_control_tmp_del:
                try {
                    tempture = Integer.parseInt(air_control_map.get("temperature").toString());
                    if (tempture > 16)
                        tempture--;
                    tmp_txt.setText(tempture + "");
                    common_doit("temperature", "" + tempture);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                break;
        }
    }

    /**
     * 初始化空调动作
     */
    private void init_action() {
        String status = (String) air_control_map.get("status");
        StringBuffer temp = new StringBuffer();
        switch (status) {
            case "1":
                temp = onLine();
                break;
            case "0":
                temp.append("关闭");
                break;
        }
        common_doit("action", temp.toString());
    }

    private StringBuffer onLine() {
        StringBuffer temp = new StringBuffer();
        String speed = (String) air_control_map.get("speed");
        switch (speed) {
            case "1":
                temp.append("低风");
                break;
            case "2":
                temp.append("中风");
                break;
            case "3":
                temp.append("高风");
                break;
            case "6":
                temp.append("自动");
                break;
            default:
                temp.append("自动");
                break;
        }
        String temperature = (String) air_control_map.get("temperature");

        temp.append("  " + temperature + "℃");
        String type = air_control_map.get("type").toString();
        switch (type) {
            case "3":
                common_mode(temp);
                break;
            case "6":
                common_mode_dinuan(temp);
                break;
        }
        return temp;
    }

    private void common_mode(StringBuffer temp) {
        String model = (String) air_control_map.get("mode");
        //

        switch (model) {
            case "1":
                temp.append("  " + "制冷");
                break;
            case "2":
                temp.append("  " + "制热");
                break;
            case "3":
                temp.append("  " + "除湿");
                break;
            case "4":
                temp.append("  " + "自动");
                break;
            default:
                temp.append("自动");
                break;
        }
    }


    private void common_mode_dinuan(StringBuffer temp) {
        String model = (String) air_control_map.get("mode");
        //

        switch (model) {
            case "1":
                temp.append("  " + "加热");
                break;
            case "2":
                temp.append("  " + "睡眠");
                break;
            case "3":
                temp.append("  " + "外出");
                break;
        }
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser) {
        String position_index = "";
        switch (seekBar.getId()) {

        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
