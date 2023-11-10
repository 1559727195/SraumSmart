package com.massky.sraum.activity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.AddTogenInterface.AddTogglenInterfacer;
import com.massky.sraum.EditGateWayResultActivity;
import com.massky.sraum.R;
import com.massky.sraum.User;
import com.massky.sraum.Util.DialogUtil;
import com.massky.sraum.Util.MyOkHttp;
import com.massky.sraum.Util.Mycallback;
import com.massky.sraum.Util.SharedPreferencesUtil;
import com.massky.sraum.Util.ToastUtil;
import com.massky.sraum.Util.TokenUtil;
import com.massky.sraum.Utils.ApiHelper;
import com.massky.sraum.Utils.AppManager;
import com.massky.sraum.base.BaseActivity;
import com.massky.sraum.view.ClearEditText;
import com.massky.sraum.view.ClearLengthEditText;
import com.massky.sraum.widget.ApplicationContext;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * Created by zhu on 2018/1/2.
 */

public class EditTablePMActivity extends BaseActivity {
    private String mac;
    @BindView(R.id.gatedditexttwo)
    ClearLengthEditText gatedditexttwo;
    @BindView(R.id.standard)
    TextView standard;
    @BindView(R.id.save_btn)
    Button save_btn;
    private DialogUtil dialogUtil;
    private String areaNumber;
    @BindView(R.id.back)
    ImageView back;
    private String id;

    @Override
    protected int viewId() {
        return R.layout.edit_table_pm_act;
    }

    @Override
    protected void onView() {
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        dialogUtil = new DialogUtil(this);
        mac = (String) getIntent().getSerializableExtra("mac");
        id = (String) getIntent().getSerializableExtra("id");
        standard.setText(mac);
        areaNumber = (String) SharedPreferencesUtil.getData(EditTablePMActivity.this, "areaNumber", "");
    }

    @Override
    protected void onEvent() {
        save_btn.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    @Override
    protected void onData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save_btn:
                if (gatedditexttwo.getText().toString().trim().equals("")) {
                    ToastUtil.showDelToast(EditTablePMActivity.this, "设备名称为空");
                    return;
                }
                //
                sraum_addWifiDeviceCommon(gatedditexttwo.getText().toString().trim());

                break;
            case R.id.back:
                EditTablePMActivity.this.finish();
                break;
        }
    }


    /**
     * 通用版添加 wifi 设备
     *
     */
    private void sraum_addWifiDeviceCommon(String name) {

        //deivce_number,roomNumber;
        //修改房间关联信息（APP->网关）
        Map map = new HashMap();// current_room_number
        map.put("token", TokenUtil.getToken(EditTablePMActivity.this));
        map.put("areaNumber", areaNumber);
        map.put("id", id);
        map.put("mac", mac);
        map.put("type", "AD02");
        map.put("name", name);
        map.put("wifi", "");
        if (dialogUtil != null) dialogUtil.loadDialog();
        MyOkHttp.postMapObject(ApiHelper.sraum_addWifiDeviceCommon, map,
                new Mycallback(new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {

                    }
                }, EditTablePMActivity.this, dialogUtil) {
                    @Override
                    public void onSuccess(User user) {
//                        project_select.setText(user.name);
                        ToastUtil.showToast(EditTablePMActivity.this,"绑定成功");
                        //去关联房间
                       EditTablePMActivity.this.finish();
                        AppManager.getAppManager().removeActivity_but_activity_cls(MainGateWayActivity.class);
                        Map map = new HashMap();
                        map.put("deviceId", id);
                        map.put("deviceType", "AD02");
                        map.put("type", "2");
                        map.put("areaNumber",areaNumber);
                        Intent intent = new Intent(EditTablePMActivity.this, SelectRoomActivity.class);
                        intent.putExtra("map_deivce", (Serializable) map);
                        startActivity(intent);
                    }

                    @Override
                    public void wrongBoxnumber() {
                        super.wrongBoxnumber();
                        ToastUtil.showToast(EditTablePMActivity.this, "areaNumber\n" +
                                "不存在");
                    }

                    @Override
                    public void threeCode() {
                        super.threeCode();
                        ToastUtil.showToast(EditTablePMActivity.this, "type 类型不存在");
                    }

                    @Override
                    public void fourCode() {
                        super.fourCode();
                        ToastUtil.showToast(EditTablePMActivity.this, "id 不存在");
                    }

                    @Override
                    public void fiveCode() {
                        ToastUtil.showToast(EditTablePMActivity.this, "该设备已绑定过");
                    }
                });
    }
}
