package com.massky.sraum.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.AddTogenInterface.AddTogglenInterfacer;
import com.massky.sraum.R;
import com.massky.sraum.User;
import com.massky.sraum.Util.MyOkHttp;
import com.massky.sraum.Util.Mycallback;
import com.massky.sraum.Util.ToastUtil;
import com.massky.sraum.Util.TokenUtil;
import com.massky.sraum.Utils.ApiHelper;
import com.massky.sraum.activity.MyfamilyActivity;
import com.massky.sraum.view.ClearEditText;
import com.massky.sraum.view.ClearLengthEditText;
import com.mcxtzhang.swipemenulib.SwipeMenuLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by masskywcy on 2017-05-16.
 */

public class MyAreaListOwnerAdapter extends BaseAdapter {
    private List<Map> list = new ArrayList<>();
    private RefreshListener refreshListener;

    public MyAreaListOwnerAdapter(Context context, List<Map> list, RefreshListener refreshListener) {
        super(context, list);
        this.list = list;
        this.refreshListener = refreshListener;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolderContentType viewHolderContentType = null;
        if (null == convertView) {
            viewHolderContentType = new ViewHolderContentType();
            convertView = LayoutInflater.from(context).inflate(R.layout.myarealist_item, null);
//            viewHolderContentType.device_type_pic = (ImageView) convertView.findViewById(R.id.device_type_pic);
//            viewHolderContentType.hand_device_content = (TextView) convertView.findViewById(R.id.hand_device_content);
            viewHolderContentType.swipe_context = (RelativeLayout) convertView.findViewById(R.id.swipe_context);
            viewHolderContentType.area_name_txt = (TextView) convertView.findViewById(R.id.area_name_txt);
            viewHolderContentType.rename_btn = (Button) convertView.findViewById(R.id.rename_btn);
            viewHolderContentType.swipemenu_layout = (SwipeMenuLayout) convertView.findViewById(R.id.swipemenu_layout);
            viewHolderContentType.delete_btn = (Button) convertView.findViewById(R.id.delete_btn);
            convertView.setTag(viewHolderContentType);
        } else {
            viewHolderContentType = (ViewHolderContentType) convertView.getTag();
        }

//        int element = (Integer) list.get(position).get("image");
//        viewHolderContentType.device_type_pic.setImageResource(element);
//        viewHolderContentType.hand_device_content.setText(list.get(position).get("name").toString());
//

        switch (list.get(position).get("authType").toString()) {
            case "1":
                viewHolderContentType.area_name_txt.setText(list.get(position).get("name").toString() + "(" + "业主" + ")");
                break;
            case "2":
                viewHolderContentType.area_name_txt.setText(list.get(position).get("name").toString() + "(" + "成员" + ")");
//                viewHolderContentType.swipemenu_layout.setSwipeEnable(false);
                break;
        }

//
        viewHolderContentType.swipe_context.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        ((SwipeMenuLayout) convertView).setOnMenuClickListener(new SwipeMenuLayout.OnMenuClickListener() {

            @Override
            public void onItemClick() {

                switch (list.get(position).get("authType").toString()) {
                    case "1":
                        Intent intent = new Intent(context, MyfamilyActivity.class);
                        intent.putExtra("areaNumber", list.get(position).get("number").toString());
                        intent.putExtra("authType", list.get(position).get("authType").toString());
                        context.startActivity(intent);
                        break;
                    case "2":

                        break;
                }
            }


            @Override
            public void onItemClick_By_btn(boolean is_open_to_close1) {//SwipeLayout是否在打开到关闭的过程

            }
        });

//        String authType = (String) SharedPreferencesUtil.getData(context, "authType", "");
//        switch (authType) {
//            case "1":
//
//                break;
//            case "2":
//                viewHolderContentType.swipemenu_layout.setSwipeEnable(false);
//                break;
//        }


        switch (list.get(position).get("sign") == null ? "" : list.get(position).get("sign").toString()) {
            case "1":
                viewHolderContentType.area_name_txt.setTextColor(context.getResources().getColor(R.color.green));
                switch (list.get(position).get("authType").toString()) {
                    case "1":
                        viewHolderContentType.swipemenu_layout.setSwipeEnable(true);
                        viewHolderContentType.delete_btn.setVisibility(View.GONE);
                        break;
                    case "2":
                        viewHolderContentType.swipemenu_layout.setSwipeEnable(false);
                        break;
                }
                break;
            case "0":
                viewHolderContentType.area_name_txt.setTextColor(context.getResources().getColor(R.color.black));
                viewHolderContentType.swipemenu_layout.setSwipeEnable(true);
                switch (list.get(position).get("authType").toString()) {
                    case "1":
                        viewHolderContentType.swipemenu_layout.setSwipeEnable(true);
                        viewHolderContentType.rename_btn.setVisibility(View.VISIBLE);
                        viewHolderContentType.delete_btn.setVisibility(View.VISIBLE);
                        break;
                    case "2":
                        viewHolderContentType.swipemenu_layout.setSwipeEnable(true);
                        viewHolderContentType.rename_btn.setVisibility(View.GONE);
                        break;
                }

                break;
        }

        final ViewHolderContentType finalViewHolderContentType = viewHolderContentType;
        viewHolderContentType.rename_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalViewHolderContentType.swipemenu_layout.quickClose();
                showRenameDialog(list.get(position).get("name").toString()
                        , list.get(position).get("number").toString());
            }
        });
        viewHolderContentType.delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalViewHolderContentType.swipemenu_layout.quickClose();
                switch (list.get(position).get("authType") == null ? "" : list.get(position).get("authType").toString()) {
                    case "2":
                        showCenterDeleteDialog(list.get(position).get("name").toString()
                                , list.get(position).get("number").toString(), 0);
                        break;
                    case "1":
                        //     showCenterDeleteDialog(list.get(position).get("name").toString()
                        //                            , list.get(position).get("number").toString());
                        getFamily(list.get(position).get("name").toString(), list.get(position).get("number").toString());
                        break;

                }
            }
        });
        return convertView;
    }


    //自定义dialog,centerDialog删除对话框
    public void showCenterDeleteDialog(final String name, final String areaNumber, int size) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        // 布局填充器
//        LayoutInflater inflater = LayoutInflater.from(getActivity());
//        View view = inflater.inflate(R.layout.user_name_dialog, null);
//        // 设置自定义的对话框界面
//        builder.setView(view);
//
//        cus_dialog = builder.create();
//        cus_dialog.show();

        View view = LayoutInflater.from(context).inflate(R.layout.promat_dialog, null);
        TextView confirm; //确定按钮
        TextView cancel; //确定按钮
        TextView tv_title;
        TextView name_gloud;
//        final TextView content; //内容
        cancel = (TextView) view.findViewById(R.id.call_cancel);
        confirm = (TextView) view.findViewById(R.id.call_confirm);
        tv_title = (TextView) view.findViewById(R.id.tv_title);//name_gloud
        name_gloud = (TextView) view.findViewById(R.id.name_gloud);
        tv_title.setText(name);
        if (size != 0) {
            name_gloud.setText("检测到该区域下存在" + size
                    + "个家庭成员，\n" +
                    "删除操作将一起删除这些成员，\n是否继续删除？");
        }

//        tv_title.setText("是否拨打119");

//        content.setText(message);
        //显示数据
        final Dialog dialog = new Dialog(context, R.style.BottomDialog);
        dialog.setContentView(view);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int displayWidth = dm.widthPixels;
        int displayHeight = dm.heightPixels;
        android.view.WindowManager.LayoutParams p = dialog.getWindow().getAttributes(); //获取对话框当前的参数值
        p.width = (int) (displayWidth * 0.8); //宽度设置为屏幕的0.5
//        p.height = (int) (displayHeight * 0.5); //宽度设置为屏幕的0.5
//        dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        dialog.getWindow().setAttributes(p);  //设置生效
        dialog.show();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sraum_deleteArea(areaNumber);
                dialog.dismiss();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                linkage_delete(linkId, dialog);
                dialog.dismiss();
            }
        });
    }

    /**
     * 删除区域
     *
     * @param areaNumber
     */
    private void sraum_deleteArea(final String areaNumber) {
        Map<String, String> mapdevice = new HashMap<>();
        mapdevice.put("token", TokenUtil.getToken(context));
        mapdevice.put("areaNumber", areaNumber);
//        mapdevice.put("boxNumber", TokenUtil.getBoxnumber(LinkageListActivity.this));
        MyOkHttp.postMapString(ApiHelper.sraum_deleteArea, mapdevice, new Mycallback(new AddTogglenInterfacer() {
            @Override
            public void addTogglenInterfacer() {//刷新togglen数据
                sraum_deleteArea(areaNumber);
            }
        }, context, null) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
            }

            @Override
            public void pullDataError() {
                super.pullDataError();
            }

            @Override
            public void emptyResult() {
                super.emptyResult();
            }

            @Override
            public void wrongToken() {
                super.wrongToken();
                //重新去获取togglen,这里是因为没有拉到数据所以需要重新获取togglen

            }

            @Override
            public void wrongBoxnumber() {
                ToastUtil.showToast(context, "areaNumber\n" +
                        "不存在");
            }

            @Override
            public void onSuccess(final User user) {
//                refreshLayout.autoRefresh();
                if (refreshListener != null)
                    refreshListener.refresh();
            }

            @Override
            public void threeCode() {
                ToastUtil.showToast(context,"默认区域不能删除");
            }
        });
    }

    class ViewHolderContentType {
        ImageView device_type_pic;
        TextView area_name_txt;
        TextView hand_gateway_content;
        Button rename_btn;
        RelativeLayout swipe_context;
        SwipeMenuLayout swipemenu_layout;
        Button delete_btn;
    }

    //自定义dialog,自定义重命名dialog

    public void showRenameDialog(final String name, final String areaNumber) {
        View view = LayoutInflater.from(context).inflate(R.layout.editscene_dialog, null);
        TextView confirm; //确定按钮
        TextView cancel; //确定按钮
        TextView tv_title;
//        final TextView content; //内容
        cancel = (TextView) view.findViewById(R.id.call_cancel);
        confirm = (TextView) view.findViewById(R.id.call_confirm);
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        final ClearLengthEditText edit_password_gateway = (ClearLengthEditText) view.findViewById(R.id.edit_password_gateway);
        edit_password_gateway.setText(name);
        edit_password_gateway.setSelection(edit_password_gateway.getText().length());
//        tv_title.setText("是否拨打119");
//        content.setText(message);
        //显示数据
        final Dialog dialog = new Dialog(context, R.style.BottomDialog);
        dialog.setContentView(view);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int displayWidth = dm.widthPixels;
        int displayHeight = dm.heightPixels;
        android.view.WindowManager.LayoutParams p = dialog.getWindow().getAttributes(); //获取对话框当前的参数值
        p.width = (int) (displayWidth * 0.8); //宽度设置为屏幕的0.5
//        p.height = (int) (displayHeight * 0.5); //宽度设置为屏幕的0.5
//        dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        dialog.getWindow().setAttributes(p);  //设置生效
        dialog.show();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edit_password_gateway.getText().toString() == null ||
                        edit_password_gateway.getText().toString().trim().equals("")) {
                    ToastUtil.showToast(context, "区域名称为空");
                    return;
                }
                sraum_updateAreaName(areaNumber, edit_password_gateway.getText().toString());
                dialog.dismiss();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    /**
     * 修改区域名称
     */
    private void sraum_updateAreaName(final String areaNumber, final String newName) {
//        areaNumber：区域编号
//        newName：新的区域名称
        Map<String, String> mapdevice = new HashMap<>();
        mapdevice.put("token", TokenUtil.getToken(context));
//        mapdevice.put("boxNumber", TokenUtil.getBoxnumber(SelectSensorActivity.this));
        mapdevice.put("areaNumber", areaNumber);
        mapdevice.put("newName", newName);
        MyOkHttp.postMapString(ApiHelper.sraum_updateAreaName
                , mapdevice, new Mycallback(new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {//刷新togglen数据
                        sraum_updateAreaName(areaNumber, newName);
                    }
                }, context, null) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
                    }

                    @Override
                    public void pullDataError() {
                        super.pullDataError();
                    }

                    @Override
                    public void emptyResult() {
                        super.emptyResult();
                    }

                    @Override
                    public void wrongToken() {
                        super.wrongToken();
                        //重新去获取togglen,这里是因为没有拉到数据所以需要重新获取togglen
                    }

                    @Override
                    public void wrongBoxnumber() {
                        ToastUtil.showToast(context, "areaNumber\n" +
                                "不存在");
                    }

                    @Override
                    public void onSuccess(final User user) {
                        if (refreshListener != null)
                            refreshListener.refresh();
                    }

                    @Override
                    public void threeCode() {
                        ToastUtil.showToast(context, "名字已存在");
                    }
                });
    }


    //获取家庭成员
    private void getFamily(String name, String areaNumber) {
        sraum_get_famliy(name, areaNumber);
    }

    private void sraum_get_famliy(final String name, final String areaNumber) {
        Map<String, Object> map = new HashMap<>();
        map.put("token", TokenUtil.getToken(context));
//        String areaNumber = (String) SharedPreferencesUtil.getData(MyfamilyActivity.this,
//                "areaNumber","");
        map.put("areaNumber", areaNumber);
        MyOkHttp.postMapObject(ApiHelper.sraum_getFamily, map, new Mycallback(new AddTogglenInterfacer() {
            @Override
            public void addTogglenInterfacer() {//获取gogglen刷新数据
                sraum_get_famliy(name, areaNumber);
            }
        }, context, null) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
            }

            @Override
            public void pullDataError() {
                super.pullDataError();
            }

            @Override
            public void wrongToken() {
                super.wrongToken();
            }

            @Override
            public void wrongBoxnumber() {
                super.wrongBoxnumber();
            }

            @Override
            public void emptyResult() {
                super.emptyResult();
            }

            @Override
            public void onSuccess(User user) {
                super.onSuccess(user);
                showCenterDeleteDialog(name
                        , areaNumber, user.familyList.size());

            }
        });
    }


    public interface RefreshListener {
        void refresh();
    }

}
