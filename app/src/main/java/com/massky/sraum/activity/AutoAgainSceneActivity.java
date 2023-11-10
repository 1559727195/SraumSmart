package com.massky.sraum.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.massky.sraum.R;
import com.massky.sraum.Util.ToastUtil;
import com.massky.sraum.Utils.AppManager;
import com.massky.sraum.adapter.AddHandSceneAdapter;
import com.massky.sraum.adapter.AgainAutoSceneAdapter;
import com.massky.sraum.base.BaseActivity;
import com.massky.sraum.widget.ApplicationContext;
import com.yanzhenjie.statusview.StatusUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

import static com.massky.sraum.activity.TimeExcuteCordinationActivity.MESSAGE_TIME_EXCUTE_ACTION;

/**
 * Created by zhu on 2018/1/9.
 */

public class AutoAgainSceneActivity extends BaseActivity {
    private AgainAutoSceneAdapter againAutoSceneAdapter;
    private List<Map> list_hand_scene;
    @BindView(R.id.xListView_scan)
    ListView xListView_scan;
    @BindView(R.id.custom_again_time)
    TextView custom_again_time;
    @BindView(R.id.next_step_txt)
    TextView next_step_txt;
    @BindView(R.id.back)
    ImageView back;
    String[] again_elements = {"每天", "工作日", "周末", "自定义"};
    private String condition;
    private Map current_map = new HashMap();

    @Override
    protected int viewId() {
        return R.layout.autoagain_scene_act;//
    }

    @Override
    protected void onView() {
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        list_hand_scene = new ArrayList<>();
        for (String element : again_elements) {
            Map map = new HashMap();
            map.put("name", element);
            switch (element) {
                case "每天":
                    condition = "2";
                    break;
                case "工作日":
                    condition = "3";
                    break;
                case "周末":
                    condition = "4";
                    break;
                case "自定义":
                    condition = "5";
                    break;
            }
            map.put("type", "0");
            map.put("condition", condition);
            list_hand_scene.add(map);//
        }
        againAutoSceneAdapter = new AgainAutoSceneAdapter(AutoAgainSceneActivity.this, list_hand_scene
                , new AgainAutoSceneAdapter.AgainAutoSceneListener() {
            @Override
            public void again_auto_listen(int position) {
                current_map = list_hand_scene.get(position);
            }
        });
        xListView_scan.setAdapter(againAutoSceneAdapter);
    }

    /*
     * 通知
     * */
    private void sendBroad(Map map) {
        Intent mIntent = new Intent(MESSAGE_TIME_EXCUTE_ACTION);
        mIntent.putExtra("time_map", (Serializable) map);
        sendBroadcast(mIntent);
    }

    @Override
    protected void onEvent() {
        custom_again_time.setOnClickListener(this);
        next_step_txt.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    @Override
    protected void onData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.custom_again_time:
                startActivity(new Intent(AutoAgainSceneActivity.this, CustomDefineDaySceneActivity.class));
                break;//自定义是哪天执行，
            case R.id.next_step_txt://下一步
                Map map = new HashMap();
                if (current_map.get("name") == null) {
                    ToastUtil.showToast(AutoAgainSceneActivity.this,"请选择执行条件");
                    return;
                }
                map.put("name",current_map.get("name"));
                map.put("condition",current_map.get("condition"));
                map.put("minValue","");
                sendBroad(map);
//                AutoAgainSceneActivity.this.finish();
                AppManager.getAppManager().finishActivity_current(AutoAgainSceneActivity.class);
                break;
            case R.id.back:
                AutoAgainSceneActivity.this.finish();
                break;
        }
    }
}
