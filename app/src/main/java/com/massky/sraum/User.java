package com.massky.sraum;

import android.app.Service;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhu on 2017/11/14.
 */

public class User {
    public String UDScavenging;
    /**
     * 搜索网关信息(APP -> 网关)
     */
    public String MAC;
    public String proto;
    public String ip;
    public String result;
    public String command;

    //推送修改网关名称（网关->APP）
//    public String  newName;
    //推送添加设备（网关->APP）
    public String number;

    //推送修改设备按钮名称（网关->APP）
//    deviceNumber：设备编号
//    buttonNumber：按钮编号
//    type：按钮类型，1-灯，2-调光，3-窗帘，4-空调，5-地暖，6-新风 详见附件1
//    newName：按钮名称
//    newName1：按钮名称，按钮为窗帘时存在该字段
//    newName2：按钮名称，按钮为窗帘时存在该字段
    public String deviceNumber;
    public String buttonNumber;
    public String newName;
    public String newName1;
    public String newName2;

    //推送设备按钮状态变化（网关->APP）

    //    deviceNumber：设备编号
//    buttonNumber：按钮编号
//    type：按钮类型，1-灯，2-调光，3-窗帘，4-空调，5-地暖，6-新风 详见附件1
//    status：状态，1-开，0-关（若为窗帘，则1-全开，0-全关，2-暂停，3-组1开组2关，4-组1开组2暂停，5-组1关组2开，6-组1关组2暂停，7-组1暂停组2关，8-组1暂停组2开）
//    以下参数根据不同的设备类型调整，详见附件1对照表
//    dimmer：调光值，范围0-100
//    mode：空调模式，1-制冷，2-制热，3-除湿，4-自动，5-通风
//    temperature：空调或新风或地暖的温度，范围16-30
//    speed：空调或新风或地暖的风速，1-低风，2-中风，3-高风，4-强力，5-送风，6-自动
//    public String status;
    public String dimmer;
    public String timeStamp;
    public String id;

    //获取所有房间信息（APP->网关）
    /**
     * 首页->获取所有房间信息
     */
    public List<roomList> roomList;
    public List<personList> personList;
    public String token;
    public String expires_in;
    public String userName;
    public String avatar;
    public String accountType;
    public String panelType;
    public String panelName;
    public String panelMAC;
    public String manuallyCount;
    public String autoCount;
    public String deviceStatus;
    public String panelStatus;
    public String gatewayMAC;
    public String boxNumber;
    public String version;
    public String versionCode;
    public String LRScavenging;
    public String messageTitle;
    public String eventTime;
    public String boxName;
    public String currentVersion;
    public String newVersion;
    @Nullable
    public String wifi;
    @Nullable
    public String custom1;
    @Nullable
    public String custom2;
    @Nullable
    public String custom3;
    @Nullable
    public String custom4;
    @Nullable
    public String custom5;
    @Nullable
    public String nextSearchValue;
    @NotNull
    public  String accessToken;
    @NotNull
    public  String url;
    @NotNull
    public  List<externalSwitchList> externalSwitchList;
    @NotNull
    public  String curtainHas;


    public static class externalSwitchList {
        public String switchId;
        public String   button;
    }


    public static class roomList {
        public String number;
        public String name;
        public String count;
    }

    public static class personList {
        public String personNo;
        public String personName;
        public String sign;
    }



     /*    result：1-json 格式解析失败，100-成功，101-token 错误，102-
         areaNumber 错误 103-deviceId 不正确,104-searchType 不正确
     2. step:纵坐标步长(刻度 5 个，0 开始累加)
     3. currentTotal:50
                 4. data:[
         {日期：度数}
     ]*/


    public static class data {
        public String date;
        public String value;
    }

    public List<data> data;

    public String step;
    public String currentTotal;


    /**
     * 获取单个房间关联信息（APP->网关）
     */
    public String count;
    public List<list_scene> list;

    public static class list_scene implements Serializable {

        public String type;
        public String online;
        public String number;
        public String name;
        public String status;
        public String fatherName;
        public String dimmer;
        public String mode;
        public String temperature;
        public String speed;
        public String electricity;
        public String vlaue;
        public String pm25;
        public String pm1;
        public String pm10;
        public String humidity;
        public String alarm;
        public String roomNumber;
        public String roomName;
        public String panelName;
        public String boxName;
        public String panelMac;
        public String gatewayMac;


        //根据面板id-》去查找设备列表
        public String id;
        public String startTime;
        public String endTime;
        public String monday;
        public String tuesday;
        public String wednesday;
        public String thursday;
        public String friday;
        public String saturday;
        public String sunday;
    }

    /**
     * 获取全部信息（APP->网关）
     */

    /**
     * 推送修改网关名称（网关->APP）
     */

    /**
     * 推送修改网关密码（网关->APP）
     */
    public String newPassword;

    /**
     * 窗帘
     */
    public String name1;
    public String name2;

    /**
     * 空调
     */
    public String mode;
    public String temperature;
    public String speed;

    /***
     * 获取网关基本信息（APP->网关）
     */
    public String type;
    public String status;
    public String name;
    public String mac;
    public String versionType;
    public String hardware;
    public String firmware;
    public String bootloader;
    public String coordinator;
    public String panid;
    public String channel;
    public String deviceCount;
    public String sceneCount;


    /**
     * 获取所有区域
     */

    public List<areaList> areaList;

    public static class areaList
            implements Serializable {
        public String number;
        public String areaName;
        public String sign;
        public String authType;
        public String roomCount;
    }


    /*下载全部智能设备
  * type：设备类型，1-灯，2-调光灯，3-空调，4-窗帘，5-新风，6-地暖
  number：设备编号
  name：自定义设备名称
  status：状态，1-开，0-关（若为窗帘，则 1-全开，0-全关，2-暂停，3-组 1 开
  组 2 开，4-组 1 开组 2 关，5-组 1 关组 2 开，6-组 1 关组 2 关）
  dimmer：调光值，范围 0-100
  mode：空调模式，1-制冷，2-制热，3-除湿，4-自动，5-通风
  temperature：空调或新风或地暖的温度，范围 16-30
  speed：空调或新风或地暖的风速，1-低风，2-中风，3-高风，4-强力，5-送风，
  6-自动*/
    public List<device> deviceList;

    public static class device implements Serializable {
        //根据面板id-》去查找设备列表
        public String type;
        public String number;
        public String name;
        public String status;
        public String mode;
        public String dimmer;
        public String temperature;
        public String speed;
        public String name1;
        public String name2;
        public boolean flag;
        public String panelName;
        public String panelMac;
        public String deviceId;
        public String boxNumber;
        public String boxName;
        public String button;
        public String roomName;
        public String device_name;
        public String energy;
    }

    public List<panellist> panelList;

    /*
  * id：面板编号
  mac：面板 MAC 地址
  name：面板名称
  type：面板类型
  status：面板状态 1-在线，0-离线*/
    public static class panellist {
        public String id;
        public String mac;
        public String name;
        public String type;
        public String status;
        public String buttonStatus;
        public String button5Name;
        public String button5Type;
        public String   button5SceneId;
        public String button5SceneType;
        public String button6Name;
        public String button6Type;
        public String button6SceneId;
        public String button6SceneType;
        public String button7Name;
        public String button7Type;
        public String button7SceneId;
        public String button7SceneType;
        public String button8Name;
        public String button8Type;
        public String button8SceneId;
        public String button8SceneType;
        public String panelNumber;
        public String panelName;
        public String panelType;
        public String boxNumber;
        public String boxName;
        public String firmware;
        public String hardware;
        public String gatewayid;
        public int isUse;
        public String number;
        public String panelMac;
        public String roomNumber;
        public String roomName;
        public String wifi;
        public String gatewayMac;
    }

    public List<otherPanelList> otherPanelList;
    public static class otherPanelList {
        public String id;
        public String mac;
        public String name;
        public String type;
        public String status;
        public String buttonStatus;
        public String button1Name;
        public String button1Type;
        public String button1SceneId;
        public String button1SceneType;
        public String button2Name;
        public String button2Type;
        public String button2SceneId;
        public String button2SceneType;
        public String button3Name;
        public String button3Type;
        public String button3SceneId;
        public String button3SceneType;
        public String button4Name;
        public String button4Type;
        public String button4SceneId;
        public String button4SceneType;
        public String button5Name;
        public String button5Type;
        public String button5SceneId;
        public String button5SceneType;
        public String button6Name;
        public String button6Type;
        public String button6SceneId;
        public String button6SceneType;
    }





    public List<wifi_device> wifiList;//wifi设备列表

    /**
     * WIFI设备
     */
    public static class wifi_device implements Serializable {
        //根据面板id-》去查找设备列表
        public String type;
        public String number;
        public String name;
        public String status;
        public String mode;
        public String dimmer;
        public String temperature;
        public String speed;
        public String mac;
        public String deviceId;

        public String id;
        public String controllerId;
        public String wifi;
        public String isUse;
        public String roomNumber;
        public String roomName;
        public String panelMac;
    }

    /**
     * 获取我的网关列表
     */
    public List<gateway> gatewayList;

    /**
     * 网关设备
     */
    public static class gateway implements Serializable {
        public String id;
        public String name;
        public String number;
        public String type;
        public String status;
    }


    public List<scenelist> sceneList;

    //gatewayid面板id panelType面板类型
    public static class scenelist {
        public String type;
        public String name;
        public String sceneStatus;
        public String gatewayid;
        public String panelType;
        public String status;
        public String id;
        public String panelNumber;
        public String buttonNumber;
        public String sceneId;
        public String sceneName;
        public String sceneType;
        public String boxNumber;
        public String boxName;
        public String number;
        @Nullable
        public String orderNo;
    }


    public String deviceType;
    public String eviceStatus;
    public String deviceName;
    public String roomNumber;
    public String roomName;
    public String isUse;


    /**
     * 按钮列表
     */
    public List<button> buttonList;

    public static class button implements Serializable {
        //根据面板id-》去查找设备列表
        public String type;
        public String number;
        public String name;
        public String status;
        public String mode;
        public String dimmer;
        public String temperature;
        public String speed;
        public String name1;
        public String name2;
        public boolean flag;
        public String panelName;
        public String panelMac;
    }

    /**
     * 设备联动信息
     */
    public deviceLinkInfo deviceLinkInfo;

    public static class deviceLinkInfo implements Serializable {
        public String token;
        public String deviceId;
        public String deviceType;
        public String linkName;
        public String condition;
        public String minValue;
        public String maxValue;
        public String startTime;
        public String endTime;
        public List<deviceList> deviceList;
        public List<personRelate> personRelate;
        public String deviceName;
        public String type;
        public String boxName;
    }

    /**
     * 获取门磁等第三方设备
     */
    public static class deviceList implements Serializable {
        public String name;
        public String number;
        public String type;
        public String status;
        public String mode;
        public String dimmer;
        public String temperature;
        public String speed;
        public String boxNumber;
        public String boxName;
        public String panelMac;
        public String gatewayMac;
    }


    public static class personRelate implements Serializable {
        public String personNo;
        public String personName;
        public String sign;
    }


    /**
     * 获取我的设备联动,自动场景
     */

    public List<deviceLinkList> deviceLinkList;

    public static class deviceLinkList {
        public String id;
        public String name;
        public String isUse;
        public String type;
        @Nullable
        public String orderNo;
    }


    /**
     * 获取 wifi 红外转发设备列表
     */

    public List<controllerList> controllerList;

    public static class controllerList
            implements Serializable {
        public String type;
        public String name;
        public String number;
        public String controllerId;
    }

    public List<familylist> familyList;

    //家人列表
    public static class familylist {
        public String name;
        public String mobilePhone;
        public String userid1;
        public String userid2;
    }


    /**
     * 分页获取消息列表
     */

    public List<messageList> messageList;

    public static class messageList implements Serializable {
        public String id;
        public String messageType;
        public String messageTitle;
        public String deviceName;
        public String readStatus;
        public String eventTime;
    }


    /**
     * 获取操作记录
     */

    public List<recordList> recordList;

    public String endId;

    public static class recordList implements Serializable {
        public String id;
        public String recordTitile;
        public String controlType;
        public String dateTime;
    }


    //recordInfo

    /*  获取操作记录详情*/

    public recordInfo recordInfo;

    public static class recordInfo implements Serializable {
        public String userName;
        public String deviceSceneName;
        public String deviceSceneType;
        public action action;
        public String panelNumber;
        public String panelName;
        public String panelAddress;
        public String gatewayNumber;
        public String gatewayName;
        public String wifiName;
        public String result;
    }


    public static class action implements Serializable {
        public String status;
        public String dimmer;
        public String speed;
        public String mode;
        public String temperature;
    }


    public userinfo userInfo;

    /*获得个人信息
    * customName：自定义用户名
        nickName：昵称
        avatar：头像
        gender：性别
        birthDay：生日
        mobilePhone：手机号
        family：家庭成员数量*/
    public static class userinfo implements Serializable {
        public String userId;
        public String nickname;
        public String avatar;
        public String gender;
        public String mode;
        public String birthDay;
        public String mobilePhone;
        public String family;
        public String nickName;
    }


    public List<opinion> opinionList;

    /**
     * 获取意见反馈记录
     */
    public static class opinion implements Serializable {
        public String id;
        public String content;
        public String dt;
        public String type;
    }


    /**
     * 获取意见反馈记录
     */
    public opinionInfo opinionInfo;

    public static class opinionInfo implements Serializable {
        public String id;
        public String content;
        public String dt;
        public String type;
        public String img;
    }
};
