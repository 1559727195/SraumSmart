package com.massky.sraum.activity;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ipcamera.demo.bean.TimeSelectBean;
import com.king.photo.adapter.AlbumGridViewAdapter;
import com.massky.sraum.R;
import com.massky.sraum.Util.ToastUtil;
import com.massky.sraum.Utils.AppManager;
import com.massky.sraum.adapter.AgainAutoDaysSelectSceneAdapter;
import com.massky.sraum.adapter.AgainAutoSceneAdapter;
import com.massky.sraum.base.BaseActivity;
import com.massky.sraum.widget.ApplicationContext;
import com.yanzhenjie.statusview.StatusUtils;
import java.io.Serializable;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import butterknife.BindView;

import static com.massky.sraum.activity.TimeExcuteCordinationActivity.MESSAGE_TIME_EXCUTE_ACTION;

/**
 * Created by zhu on 2018/1/9.
 */

public class CustomDefineDaySceneActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.xListView_scan)
    ListView xListView_scan;
    @BindView(R.id.next_step_txt)
    TextView next_step_txt;
    String[] again_elements = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
    private List<TimeSelectBean> list;
    private List<Map> list_hand_scene;
    private AgainAutoDaysSelectSceneAdapter againAutoSceneAdapter;
    private List<Map> list_select = new ArrayList<>();

    @Override
    protected int viewId() {
        return R.layout.custom_define_dayscene_act;
    }

    @Override
    protected void onView() {
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        list_hand_scene = new ArrayList<>();
        list = new ArrayList<>();
        list_select = new ArrayList<>();
        for (String element : again_elements) {
            Map map = new HashMap();
            map.put("name", element);
//            map.put("type", "0");
            switch (element) {
                case "周一":
                    map.put("time", 2);
                    break;
                case "周二":
                    map.put("time", 3);
                    break;
                case "周三":
                    map.put("time", 4);
                    break;
                case "周四":
                    map.put("time", 5);
                    break;
                case "周五":
                    map.put("time", 6);
                    break;
                case "周六":
                    map.put("time", 7);
                    break;
                case "周日":
                    map.put("time", 1);
                    break;
            }
            list_hand_scene.add(map);
        }
        againAutoSceneAdapter = new AgainAutoDaysSelectSceneAdapter(CustomDefineDaySceneActivity.this, list_hand_scene);
        xListView_scan.setAdapter(againAutoSceneAdapter);
        xListView_scan.setOnItemClickListener(this);
    }

    @Override
    protected void onEvent() {
        back.setOnClickListener(this);
        next_step_txt.setOnClickListener(this);
    }

    @Override
    protected void onData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                CustomDefineDaySceneActivity.this.finish();
                break;
            case R.id.next_step_txt://下一步
                next_step();
                break;
        }
    }

    private void next_step() {
        list.clear();
        if (list_select.size() == 0) {
            ToastUtil.showToast(CustomDefineDaySceneActivity.this,"请选择执行条件");
            return;
        }
        for (Map map : list_select) {
            list.add(new TimeSelectBean((String) map.get("name"),(int)map.get("time")));
        }
        Collections.sort(list);
        StringBuffer stringBuffer_name = new StringBuffer();
        StringBuffer stringBuffer_value = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            if (i != list.size() - 1) {
                stringBuffer_name.append(list.get(i).getName() + ",");
                stringBuffer_value.append(list.get(i).getAge() + ",");
            } else {
                stringBuffer_name.append(list.get(i).getName());
                stringBuffer_value.append(list.get(i).getAge());
            }
        }
        Map map = new HashMap();
        map.put("name",stringBuffer_name);
        map.put("condition","5");
        map.put("minValue",stringBuffer_value);
        sendBroad(map);
        AppManager.getAppManager().finishActivity_current(AutoAgainSceneActivity.class);
        CustomDefineDaySceneActivity.this.finish();
    }

    /*
     * 通知
     * */
    private void sendBroad(Map map)  {
        Intent mIntent = new Intent(MESSAGE_TIME_EXCUTE_ACTION);
        mIntent.putExtra("time_map", (Serializable) map);
        sendBroadcast(mIntent);
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        CheckBox cb = view.findViewById(R.id.scene_checkbox);
        cb.toggle();
        if (cb.isChecked()) {
            list_select.add(list_hand_scene.get(i));
        } else {
            for (Map map : list_select) {
                if (map.get("time").equals(list_hand_scene.get(i).get("time"))) {
                    list_select.remove(map);
                    break;
                }
            }
        }
    }
}
