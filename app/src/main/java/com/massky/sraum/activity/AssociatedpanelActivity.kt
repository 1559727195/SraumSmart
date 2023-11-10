package com.massky.sraum.activity

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import butterknife.BindView
import com.AddTogenInterface.AddTogglenInterfacer
import com.google.gson.Gson
import com.massky.sraum.R
import com.massky.sraum.User
import com.massky.sraum.Util.*
import com.massky.sraum.Utils.ApiHelper
import com.massky.sraum.adapter.AsccociatedpanelAdapter
import com.massky.sraum.base.BaseActivity
import com.yanzhenjie.statusview.StatusUtils
import java.lang.StringBuilder
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by masskywcy on 2017-03-22.
 */
//关联面板界面
class AssociatedpanelActivity : BaseActivity(), AdapterView.OnItemClickListener {

    @JvmField
    @BindView(R.id.backrela_id)
    var backrelaId: RelativeLayout? = null

    @JvmField
    @BindView(R.id.titlecen_id)
    var titlecenId: TextView? = null

    @JvmField
    @BindView(R.id.panelistview)
    var panelistview: ListView? = null

    @JvmField
    @BindView(R.id.paonebtn)
    var paonebtn: Button? = null

    @JvmField
    @BindView(R.id.patwobtn)
    var patwobtn: Button? = null

    @JvmField
    @BindView(R.id.pathreebtn)
    var pathreebtn: Button? = null

    @JvmField
    @BindView(R.id.pafourbtn)
    var pafourbtn: Button? = null


    @JvmField
    @BindView(R.id.panelrela)
    var panelrela: RelativeLayout? = null

    @JvmField
    @BindView(R.id.paonerela)
    var paonerela: RelativeLayout? = null

    @JvmField
    @BindView(R.id.patworela)
    var patworela: RelativeLayout? = null

    @JvmField
    @BindView(R.id.pathreerela)
    var pathreerela: RelativeLayout? = null

    @JvmField
    @BindView(R.id.pafourrela)
    var pafourrela: RelativeLayout? = null

    @JvmField
    @BindView(R.id.pafiverela)
    var pafiverela: RelativeLayout? = null

    @JvmField
    @BindView(R.id.pasixrela)
    var pasixrela: RelativeLayout? = null

    @JvmField
    @BindView(R.id.pasevenrela)
    var pasevenrela: RelativeLayout? = null

    @JvmField
    @BindView(R.id.paeightrela)
    var paeightrela: RelativeLayout? = null

    @JvmField
    @BindView(R.id.backsave)
    var backsave: RelativeLayout? = null

    @JvmField
    @BindView(R.id.pafivetext)
    var pafivetext: TextView? = null

    @JvmField
    @BindView(R.id.pasixtext)
    var pasixtext: TextView? = null

    @JvmField
    @BindView(R.id.paseventext)
    var paseventext: TextView? = null

    @JvmField
    @BindView(R.id.paeighttext)
    var paeighttext: TextView? = null

    @JvmField
    @BindView(R.id.panelinearone)
    var panelinearone: LinearLayout? = null

    @JvmField
    @BindView(R.id.panelineartwo)
    var panelineartwo: LinearLayout? = null

    @JvmField
    @BindView(R.id.panelinearthree)
    var panelinearthree: LinearLayout? = null

    @JvmField
    @BindView(R.id.panelinearfour)
    var panelinearfour: LinearLayout? = null

    @JvmField
    @BindView(R.id.ptlitone)
    var ptlitone: RelativeLayout? = null

    @JvmField
    @BindView(R.id.ptlittwo)
    var ptlittwo: RelativeLayout? = null

    @JvmField
    @BindView(R.id.ptlitthree)
    var ptlitthree: RelativeLayout? = null

    @JvmField
    @BindView(R.id.ptlittwoone)
    var ptlittwoone: RelativeLayout? = null

    @JvmField
    @BindView(R.id.ptlittwotwo)
    var ptlittwotwo: RelativeLayout? = null

    @JvmField
    @BindView(R.id.ptlitoneone)
    var ptlitoneone: RelativeLayout? = null

    @JvmField
    @BindView(R.id.paneThreeLuTiaoGuang)
    var paneThreeLuTiaoGuang: LinearLayout? = null

    @JvmField
    @BindView(R.id.paonerela_sanlu)
    var paonerela_sanlu: RelativeLayout? = null

    @JvmField
    @BindView(R.id.patworela_sanlu)
    var patwobtn_sanlu: RelativeLayout? = null

    @JvmField
    @BindView(R.id.pathreerela_sanlu)
    var pathreebtn_sanlu: RelativeLayout? = null

    @JvmField
    @BindView(R.id.pafourrela_sanlu)
    var pafourbtn_sanlu: RelativeLayout? = null

    @JvmField
    @BindView(R.id.more_key_linear_one)
    var more_key_linear_one: LinearLayout? = null

    //key_one
    @JvmField
    @BindView(R.id.more_key_one)
    var more_key_one: Button? = null

    @JvmField
    @BindView(R.id.more_key_two)
    var more_key_two: Button? = null

    @JvmField
    @BindView(R.id.more_key_three)
    var more_key_three: Button? = null

    @JvmField
    @BindView(R.id.more_key_four)
    var more_key_four: Button? = null

    @JvmField
    @BindView(R.id.more_key_five)
    var more_key_five: Button? = null

    @JvmField
    @BindView(R.id.more_key_six)
    var more_key_six: Button? = null

    @JvmField
    @BindView(R.id.more_key_linear_two)
    var more_key_linear_two: LinearLayout? = null

    @JvmField
    @BindView(R.id.more_key_linear_three)
    var more_key_linear_three: LinearLayout? = null

    @JvmField
    @BindView(R.id.panelrela_more_key)
    var panelrela_more_key: RelativeLayout? = null

    @JvmField
    @BindView(R.id.more_key_one_rel)
    var more_key_one_rel: RelativeLayout? = null

    @JvmField
    @BindView(R.id.more_key_two_rel)
    var more_key_two_rel: RelativeLayout? = null

    @JvmField
    @BindView(R.id.more_key_three_rel)
    var more_key_three_rel: RelativeLayout? = null

    @JvmField
    @BindView(R.id.more_key_four_rel)
    var more_key_four_rel: RelativeLayout? = null

    @JvmField
    @BindView(R.id.more_key_five_rel)
    var more_key_five_rel: RelativeLayout? = null

    @JvmField
    @BindView(R.id.more_key_six_rel)
    var more_key_six_rel: RelativeLayout? = null

    @JvmField
    @BindView(R.id.back)
    var back: ImageView? = null
    private var dialogUtil: DialogUtil? = null
    private var dialogUtilview: DialogUtil? = null
    private val boxNumber: String? = null
    private var panelNumber: String? = ""
    private var type: String? = null
    private var button1Type: String? = null
    private var button2Type: String? = null
    private var button3Type: String? = null
    private var button4Type: String? = null
    private var button5Type: String? = null
    private var button6Type: String? = null
    private var button7Type: String? = null
    private var button8Type: String? = null
    private var button1SceneId: String? = null
    private var button2SceneId: String? = null
    private var button3SceneId: String? = null
    private var button4SceneId: String? = null
    private var button5SceneId: String? = null
    private var button6SceneId: String? = null
    private var button7SceneId: String? = null
    private var button8SceneId: String? = null
    private var button1SceneType: String? = null
    private var button2SceneType: String? = null
    private var button3SceneType: String? = null
    private var button4SceneType: String? = null
    private var button5SceneType: String? = null
    private var button6SceneType: String? = null
    private var button7SceneType: String? = null
    private var button8SceneType: String? = null

    private var sceneName: String? = null
    private var sceType: String? = null
    private var buttonNumber = ""
    private var flagimageone = ""
    private var flagimagetwo = ""
    private var flagimagethree = ""
    private var flagimagefour = ""
    private var flagimagefive = ""
    private var flagimagesix = ""
    private var flagimageseven = ""
    private var flagimageight = ""
    private var panelid: String? = null
    private val assopanelname = ""
    private var gatewayid: String? = null
    private var panelType: String? = null
    private var sceneId: String? = null
    private var btnflag = ""
    private var adapter: AsccociatedpanelAdapter? = null
    private val checkList: MutableList<Boolean> = ArrayList()
    private var panelList: MutableList<HashMap<String, Any>?> = ArrayList()
    private var otherPanelList: MutableList<HashMap<String, Any>?> = ArrayList()
    private var flagone = true
    private var flagtwo = true
    private var flagthree = true
    private var flagfour = true
    private var flagfive = true
    private var flagsix = true
    private var flagseven = true
    private var flageight = true
    private var bundle: Bundle? = null
    private var dtext_id: TextView? = null
    private var belowtext_id: TextView? = null
    private var qxbutton_id: Button? = null
    private var checkbutton_id: Button? = null
    private var position = 0


    override fun viewId(): Int {
        return R.layout.asspanel
    }

    var TAG = "onView"
    override fun onView() {
        StatusUtils.setFullToStatusBar(this) // StatusBar.
        bundle = IntentUtil.getIntentBundle(this@AssociatedpanelActivity)
        sceneName = bundle!!.getString("sceneName", "")
        Log.e(TAG, "onView: " + ",sceneName:" + sceneName)
        sceType = bundle!!.getString("type", "")
        Log.e(TAG, "onView: " + ",sceneType:" + sceType)
        gatewayid = if (bundle!!.getString("boxNumber", "") == null) "" else bundle!!.getString("boxNumber", "")
        panelType = bundle!!.getString("panelType", "")
        panelNumber = bundle!!.getString("panelNumber", "")
        Log.e(TAG, "onView: " + ",panelNumber:" + panelNumber)
        buttonNumber = bundle!!.getString("buttonNumber", "")
        Log.e(TAG, "onView: " + ",buttonNumber:" + buttonNumber)
        //sceneId
        sceneId = bundle!!.getString("sceneId", "")

        Log.e(TAG, "onView: " + "sceneId: " + sceneId)
//        bundle1.putString("sceneName", linkName);
//        bundle1.putString("sceneType", "1");
//        bundle1.putString("boxNumber", boxNumber);
//        bundle1.putString("panelType", "");
//        bundle1.putString("panelNumber", "");
//        bundle1.putString("buttonNumber", "");
        LogUtil.eLength("查看数据panelNumber", panelNumber + "数据问题" + buttonNumber)
        //        boxNumber = (String) SharedPreferencesUtil.getData(AssociatedpanelActivity.this, "boxnumber", "");
        dialogUtil = DialogUtil(this@AssociatedpanelActivity)
        backrelaId!!.setOnClickListener(this)
        paonerela!!.setOnClickListener(this)
        patworela!!.setOnClickListener(this)
        pathreerela!!.setOnClickListener(this)
        pafourrela!!.setOnClickListener(this)
        pafiverela!!.setOnClickListener(this)
        pasixrela!!.setOnClickListener(this)
        pasevenrela!!.setOnClickListener(this)
        paeightrela!!.setOnClickListener(this)
        panelistview!!.onItemClickListener = this
        backsave!!.setOnClickListener(this)
        ptlitone!!.setOnClickListener(this)
        ptlittwo!!.setOnClickListener(this)
        ptlitthree!!.setOnClickListener(this)
        ptlittwoone!!.setOnClickListener(this)
        ptlittwotwo!!.setOnClickListener(this)
        ptlitoneone!!.setOnClickListener(this)

        //三路调光
        paonerela_sanlu!!.setOnClickListener(this)
        patwobtn_sanlu!!.setOnClickListener(this)
        pathreebtn_sanlu!!.setOnClickListener(this)
        pafourbtn_sanlu!!.setOnClickListener(this)
        back!!.setOnClickListener(this)

        init_more_key_click()
        titlecenId!!.text = "关联面板"
        getData(1, "refresh")
        replacePanel()
    }

    private fun init_more_key_click() {
        more_key_one!!.setOnClickListener(this)
        more_key_two!!.setOnClickListener(this)
        more_key_three!!.setOnClickListener(this)
        more_key_four!!.setOnClickListener(this)
        more_key_five!!.setOnClickListener(this)
        more_key_six!!.setOnClickListener(this)
    }

    override fun onEvent() {}
    override fun onData() {}
    private fun replacePanel() {
        val view = layoutInflater.inflate(R.layout.check, null)
        dtext_id = view.findViewById(R.id.dtext_id)
        belowtext_id = view.findViewById(R.id.belowtext_id)
        qxbutton_id = view.findViewById(R.id.qxbutton_id)
        checkbutton_id = view.findViewById(R.id.checkbutton_id)
        dtext_id!!.setText("替换场景")
        qxbutton_id!!.setOnClickListener(this)
        checkbutton_id!!.setOnClickListener(this)
        dialogUtilview = DialogUtil(this@AssociatedpanelActivity, view)
    }

    //设置选中的位置，将其他位置设置为未选
    fun checkPosition(position: Int) {
        for (i in checkList.indices) {
            if (position == i) { // 设置已选位置
                checkList[i] = true
            } else {
                checkList[i] = false
            }
        }
        adapter!!.setLists(checkList)
        adapter!!.notifyDataSetChanged()
    }


    private fun getData(index: Int, action: String?) {
        dialogUtil!!.loadDialog()
        var api = ""
        val map: MutableMap<String, Any?> = HashMap()
        val areaNumber = SharedPreferencesUtil.getData(this@AssociatedpanelActivity,
                "areaNumber", "") as String
        map["token"] = TokenUtil.getToken(this@AssociatedpanelActivity)
        map["areaNumber"] = areaNumber
        when (gatewayid) {
            "" -> {
                // api = ApiHelper.sraum_getWifiDeviceCommon
                api = ApiHelper.sraum_getRelateWifiDevice
            }
            else -> {
                map["boxNumber"] = gatewayid
                api = ApiHelper.sraum_getAllPanel
            }
        }

        MyOkHttp.postMapObject(api,
                map, object : Mycallback(AddTogglenInterfacer { getData(index, action) }, this@AssociatedpanelActivity, dialogUtil) {
            override fun onSuccess(user: User) {
                super.onSuccess(user)
                panelList.clear()
                get_panel_list(user)
                get_other_panel_list(user)
                Log.e(TAG, "onSuccess: " + ",user:" + Gson().toJson(panelList))
                checkList.clear()
                val panelListnew: MutableList<HashMap<String, Any>?> = ArrayList()
                for (i in panelList.indices) {
                    val upanel = panelList[i]
                    if (upanel!!["type"] == "A401" || upanel!!["type"] == "A501" || upanel!!["type"] == "A601" || upanel!!["type"] == "A701" || upanel!!["type"] == "A611" ||
                            upanel!!["type"] == "A711" || upanel!!["type"] == "A511") {
                        panelListnew.add(upanel)
                    }
                }

                panelList.removeAll(panelListnew)
                var type = ""
                if (panelList.size != 0) {
                    val upone = panelList[0]
                    for (i in panelList.indices) {
                        val up = panelList[i]
                        checkList.add(false)
                        when (gatewayid) {
                            "" -> {
                                type = ondata_wifi(up!!, type, i, upone, index, action)
                            }
                            else -> {
                                type = ondata_zigbee(up!!, type, i, upone, index)
                            }
                        }
                    }
                }
                //该场景关联了面板，实现如果该场景未关联该面板的按钮，则面板框不显示，面板不被选中（待实现）
                key_or_more_keys_visible(type)
                adapter = AsccociatedpanelAdapter(this@AssociatedpanelActivity, panelList, checkList)
                panelistview!!.adapter = adapter
                when (gatewayid) {
                    "" -> {
                        when (action) {
                            "onclick" -> {
                                common_adapter(index)
                                onitemclick(position)
                            }
                            "refresh" -> {
                                refresh_panel_status_show(index)
                            }
                        }
                    }
                    else -> {
                        common_adapter(index)
                    }
                }
            }

            override fun wrongToken() {
                super.wrongToken()
            }
        })
    }

    private fun refresh_panel_status_show(index: Int) {
        refresh_panel_status_show_second(-1)
        common_adapter(index)
    }

    private fun refresh_panel_status_show_second(position: Int) {
        for (i in panelList.indices) {
            //                                    buttonStatus!!.clear()
            var isexit = false
            if (panelList[i]!!["buttonStatus"].toString().contains("1")
            ) {
                var panel = panelList[i]
                when (sceneName) {
                    panel!!["button1Name"],
                    panel!!["button2Name"],
                    panel!!["button3Name"],
                    panel!!["button4Name"],
                    panel!!["button5Name"],
                    panel!!["button6Name"],
                    panel!!["button7Name"],
                    panel!!["button8Name"] -> {//存在sceneName
                        isexit = true
                    }
                    else -> {//不存在sceneName
                        isexit = false
                    }
                }
            }
            if (isexit) {
                //  panelList.set(0,panelList[i])
                if(position == -1)
                Collections.swap(panelList, i, 0)
                checkList[i] = true
            } else {
                checkList[i] = false
            }
        }
        if(position != -1 && position != -2) checkList[position] = true
        adapter!!.setLists(checkList)
        adapter!!.notifyDataSetChanged()
    }

    private fun get_other_panel_list(user: User) {
        if (user.otherPanelList != null && user.otherPanelList.size != 0) {
            for (i in user.otherPanelList.indices) {
                val mapdevice: MutableMap<String, String> = java.util.HashMap()
                mapdevice["id"] = user.otherPanelList[i].id
                mapdevice["name"] = user.otherPanelList[i].name
                mapdevice["type"] = user.otherPanelList[i].type
                mapdevice["mac"] = user.otherPanelList[i].mac
                mapdevice["status"] = user.otherPanelList[i].status
                mapdevice["buttonStatus"] = user.otherPanelList[i].buttonStatus
                mapdevice["button1Name"] = if (user.otherPanelList[i].button1Name == null) "" else user.otherPanelList[i].button1Name
                mapdevice["button1Type"] = if (user.otherPanelList[i].button1Type == null) "" else user.otherPanelList[i].button1Type
                mapdevice["button1SceneId"] = if (user.otherPanelList[i].button1SceneId == null) "" else user.otherPanelList[i].button1SceneId
                mapdevice["button1SceneType"] = if (user.otherPanelList[i].button1SceneType == null) "" else user.otherPanelList[i].button1SceneType
                mapdevice["button2Name"] = if (user.otherPanelList[i].button2Name == null) "" else user.otherPanelList[i].button2Name
                mapdevice["button2Type"] = if (user.otherPanelList[i].button2Type == null) "" else user.otherPanelList[i].button2Type
                mapdevice["button2SceneId"] = if (user.otherPanelList[i].button2SceneId == null) "" else user.otherPanelList[i].button2SceneId
                mapdevice["button2SceneType"] = if (user.otherPanelList[i].button2SceneType == null) "" else user.otherPanelList[i].button2SceneType
                mapdevice["button3Name"] = if (user.otherPanelList[i].button3Name == null) "" else user.otherPanelList[i].button3Name
                mapdevice["button3Type"] = if (user.otherPanelList[i].button3Type == null) "" else user.otherPanelList[i].button3Type
                mapdevice["button3SceneId"] = if (user.otherPanelList[i].button3SceneId == null) "" else user.otherPanelList[i].button3SceneId
                mapdevice["button3SceneType"] = if (user.otherPanelList[i].button3SceneType == null) "" else user.otherPanelList[i].button3SceneType
                mapdevice["button4Name"] = if (user.otherPanelList[i].button4Name == null) "" else user.otherPanelList[i].button4Name
                mapdevice["button4Type"] = if (user.otherPanelList[i].button4Type == null) "" else user.otherPanelList[i].button4Type
                mapdevice["button4SceneId"] = if (user.otherPanelList[i].button4SceneId == null) "" else user.otherPanelList[i].button4SceneId
                mapdevice["button4SceneType"] = if (user.otherPanelList[i].button4SceneType == null) "" else user.otherPanelList[i].button4SceneType
                mapdevice["button5Name"] = if (user.otherPanelList[i].button5Name == null) "" else user.otherPanelList[i].button5Name
                mapdevice["button5Type"] = if (user.otherPanelList[i].button5Type == null) "" else user.otherPanelList[i].button5Type
                mapdevice["button5SceneId"] = if (user.otherPanelList[i].button5SceneId == null) "" else user.otherPanelList[i].button5SceneId
                mapdevice["button5SceneType"] = if (user.otherPanelList[i].button5SceneType == null) "" else user.otherPanelList[i].button5SceneType
                mapdevice["button6Type"] = if (user.otherPanelList[i].button6Type == null) "" else user.otherPanelList[i].button6Type
                mapdevice["button6Name"] = if (user.otherPanelList[i].button6Name == null) "" else user.otherPanelList[i].button6Name
                mapdevice["button6SceneId"] = if (user.otherPanelList[i].button6SceneId == null) "" else user.otherPanelList[i].button6SceneId
                mapdevice["button6SceneType"] = if (user.otherPanelList[i].button6SceneType == null) "" else user.otherPanelList[i].button6SceneType
                (panelList as ArrayList<Map<*, *>>).add(mapdevice)
            }
        }
    }

    private fun get_panel_list(user: User) {
        if (user.panelList != null && user.panelList.size != 0) {
            for (i in user.panelList.indices) {
                val mapdevice: MutableMap<String, String> = java.util.HashMap()
                mapdevice["id"] = user.panelList[i].id
                mapdevice["name"] = user.panelList[i].name
                mapdevice["type"] = user.panelList[i].type
                mapdevice["mac"] = user.panelList[i].mac
                mapdevice["status"] = user.panelList[i].status
                mapdevice["buttonStatus"] = user.panelList[i].buttonStatus
                mapdevice["button5Name"] = if (user.panelList[i].button5Name == null) "" else user.panelList[i].button5Name
                mapdevice["button5Type"] = if (user.panelList[i].button5Type == null) "" else user.panelList[i].button5Type
                mapdevice["button5SceneId"] = if (user.panelList[i].button5SceneId == null) "" else user.panelList[i].button5SceneId
                mapdevice["button5SceneType"] = if (user.panelList[i].button5SceneType == null) "" else user.panelList[i].button5SceneType
                mapdevice["button6Name"] = if (user.panelList[i].button6Name == null) "" else user.panelList[i].button6Name
                mapdevice["button6Type"] = if (user.panelList[i].button6Type == null) "" else user.panelList[i].button6Type
                mapdevice["button6SceneId"] = if (user.panelList[i].button6SceneId == null) "" else user.panelList[i].button6SceneId
                mapdevice["button6SceneType"] = if (user.panelList[i].button6SceneType == null) "" else user.panelList[i].button6SceneType
                mapdevice["button7Name"] = if (user.panelList[i].button7Name == null) "" else user.panelList[i].button7Name
                mapdevice["button7Type"] = if (user.panelList[i].button7Type == null) "" else user.panelList[i].button7Type
                mapdevice["button7SceneId"] = if (user.panelList[i].button7SceneId == null) "" else user.panelList[i].button7SceneId
                mapdevice["button7SceneType"] = if (user.panelList[i].button7SceneType == null) "" else user.panelList[i].button7SceneType
                mapdevice["button8Name"] = if (user.panelList[i].button8Name == null) "" else user.panelList[i].button8Name
                mapdevice["button8Type"] = if (user.panelList[i].button8Type == null) "" else user.panelList[i].button8Type
                mapdevice["button8SceneId"] = if (user.panelList[i].button8SceneId == null) "" else user.panelList[i].button8SceneId
                mapdevice["button8SceneType"] = if (user.panelList[i].button8SceneType == null) "" else user.panelList[i].button8SceneType
                (panelList as ArrayList<Map<*, *>>).add(mapdevice)
            }
        }
    }

    private fun common_adapter(index: Int) {
        when (index) {
            1 -> {
                var i = 0
                while (i < checkList.size) {
                    if (checkList[i]) {
                        onitemclick(0)
                        break
                    }
                    i++
                }
            }
        }
    }


    private fun ondata_wifi(up: HashMap<String, Any>, type: String, i: Int, upone: HashMap<String, Any>?, index: Int, action: String?): String {
        var type = type
        if (name_common != null && name_common!!.equals(up!!["name"])) { //说明该面板已经关联了该场景，置顶该面板
            position = i
            type = up["type"].toString()
            panelid = up["id"].toString()
            panelList[0] = up
            panelList[i] = upone //替换位置
            LogUtil.eLength("改变图片", "数据问题")
            setLinear(up["type"].toString())
            set_parent_button_name(up)
            setFlag(up["buttonStatus"].toString(),
                    up["button1Type"].toString(),
                    up["button2Type"].toString(), up["button3Type"].toString(), up["button4Type"].toString(), up["button5Type"].toString(),
                    up["button6Type"].toString(), up["button7Type"].toString(), up["button8Type"].toString(), type)

            when (index) {
                1 -> checkList[0] = true
            }
        } else {
            //                                checkList.add(false);
            when (index) {
                1 -> checkList[i] = false
            }
        }
        when (action) {
            "refresh" -> {
                return type
            }
            "onclick" -> {
                return type
            }
        }
        return type
    }

    private fun set_parent_button_name(up: HashMap<String, Any>) {
        val onename = LengthUtil.doit_spit_str(if (up["button1Name"] == null) "" else up["button1Name"].toString())
        val twoname = LengthUtil.doit_spit_str(if (up["button2Name"] == null) "" else up["button2Name"].toString())
        val threename = LengthUtil.doit_spit_str(if (up["button3Name"] == null) "" else up["button3Name"].toString())
        val fourname = LengthUtil.doit_spit_str(if (up["button4Name"] == null) "" else up["button4Name"].toString())
        val fivename = LengthUtil.doit_spit_str(if (up["button5Name"] == null) "" else up["button5Name"].toString())
        val sixname = LengthUtil.doit_spit_str(if (up["button6Name"] == null) "" else up["button6Name"].toString())
        val sevenname = LengthUtil.doit_spit_str(if (up["button7Name"] == null) "" else up["button7Name"].toString())
        val eightname = LengthUtil.doit_spit_str(if (up["button8Name"] == null) "" else up["button8Name"].toString())
        set_button_name(onename, twoname, threename, fourname, fivename, sixname, sevenname, eightname)
    }


    private fun ondata_zigbee(up: HashMap<String, Any>, type: String, i: Int, upone: HashMap<String, Any>?, index: Int): String {
        var type = type
        if (panelNumber == up!!["id"].toString()) { //说明该面板已经关联了该场景，置顶该面板
            type = up["type"].toString()
            panelid = up["id"].toString()
            panelList[0] = up
            panelList[i] = upone //替换位置
            LogUtil.eLength("改变图片", "数据问题")
            setLinear(up["type"].toString())
            set_parent_button_name(up)
            setFlag(up["buttonStatus"].toString(),
                    up["button1Type"].toString(),
                    up["button2Type"].toString(), up["button3Type"].toString(), up["button4Type"].toString(), up["button5Type"].toString(),
                    up["button6Type"].toString(), up["button7Type"].toString(), up["button8Type"].toString(), type)

            when (index) {
                1 -> checkList[0] = true
            }
        } else {
            //                                checkList.add(false);
            when (index) {
                1 -> checkList[i] = false
            }
        }
        return type
    }

    /**
     * 显示那种布局
     *
     * @param linearType
     */
    private fun setLinear(linearType: String) {
        clear()
        when (linearType) {
            "AD11","ADA1" -> {
                more_key_linear_one!!.visibility = View.VISIBLE
            }
            "AD12","ADA2" -> {
                more_key_linear_two!!.visibility = View.VISIBLE
                more_key_linear_one!!.visibility = View.VISIBLE
            }
            "AD13","ADA3" -> {
                more_key_linear_three!!.visibility = View.VISIBLE
                more_key_linear_two!!.visibility = View.VISIBLE
                more_key_linear_one!!.visibility = View.VISIBLE
            }
            "A201", "A2A1" -> {
                panelinearone!!.visibility = View.VISIBLE
                LogUtil.eLength("这是进入A201", "看看操作")
            }
            "A202", "A411", "A311", "A2A2", "A421" -> {
                panelineartwo!!.visibility = View.VISIBLE
                LogUtil.eLength("这是进入A202", "进入了")
            }
            "A203", "A412", "A312", "A321", "A2A3" -> {
                panelinearthree!!.visibility = View.VISIBLE
                LogUtil.eLength("这是进入A203", "看看操作")
            }
            "A204", "A313", "A322", "A331", "A413", "A414", "A2A4" -> panelinearfour!!.visibility = View.VISIBLE
            "A303" -> {
                flagfive = false
                flagsix = false
                flagseven = false
                flageight = true
                paneThreeLuTiaoGuang!!.visibility = View.VISIBLE
            }
            else -> panelinearfour!!.visibility = View.VISIBLE
        }
    }

    private fun setFlag(buttonStatus: String?,
                        onetype: String?, twotype: String?,
                        threetype: String?, fourtype: String?, fivetype: String?, sixtype: String?,
                        sevemtype: String?, eighttype: String?, type: String) {
        when (gatewayid) {
            "" -> {
                wifi_getflag(buttonStatus)
            }
            else -> {
                zigbee_getflag(onetype, twotype, threetype, fourtype, fivetype, sixtype, sevemtype, eighttype, type)
            }
        }
    }

    private fun wifi_getflag(buttonStatus: String?) {
        var five = buttonStatus!!.get(0)
    }

    private fun zigbee_getflag(onetype: String?, twotype: String?,
                               threetype: String?, fourtype: String?, fivetype: String?, sixtype: String?, sevemtype: String?, eighttype: String?, type: String) {


        flagimageone = if (onetype == null) {
            "1"
        } else {
            "3"
        }
        flagimagetwo = if (twotype == null) {
            "1"
        } else {
            "3"
        }
        flagimagethree = if (threetype == null) {
            "1"
        } else {
            "3"
        }
        flagimagefour = if (fourtype == null) {
            "1"
        } else {
            "3"
        }
        flagimagefive = if (fivetype == null) {
            "1"
        } else {
            "3"
        }
        flagimagesix = if (sixtype == null) {
            "1"
        } else {
            "3"
        }
        flagimageseven = if (sevemtype == null) {
            "1"
        } else {
            "3"
        }
        flagimageight = if (eighttype == null) {
            "1"
        } else {
            "3"
        }
        val scenename = LengthUtil.doit_spit_str(if (sceneName == null) "" else sceneName)
        when (type) {
            "AD11", "AD12", "AD13","ADA1", "ADA2", "ADA3" -> {
                more_keys_button_set_name(scenename)
            }
            else -> {
                normal_button_set_name(scenename)
            }
        }
    }

    private fun more_keys_button_set_name(scenename: String?) {
        when (buttonNumber) {
            "1" -> {
                more_key_one!!.text = scenename
                flagimageone = "2"
            }
            "2" -> {
                more_key_two!!.text = scenename
                flagimagetwo = "2"
            }
            "3" -> {
                more_key_three!!.text = scenename
                flagimagethree = "2"
            }
            "4" -> {
                more_key_four!!.text = scenename
                flagimagefour = "2"
            }
            "5" -> {
                more_key_five!!.text = scenename
                flagimagefive = "2"
            }
            "6" -> {
                more_key_six!!.text = scenename
                flagimagesix = "2"
            }
        }
    }

    private fun normal_button_set_name(scenename: String?) {
        when (buttonNumber) {
            "5" -> {
                pafivetext!!.text = scenename
                flagimagefive = "2"
            }
            "6" -> {
                pasixtext!!.text = scenename
                flagimagesix = "2"
            }
            "7" -> {
                paseventext!!.text = scenename
                flagimageseven = "2"
            }
            "8" -> {
                paeighttext!!.text = scenename
                flagimageight = "2"
            }
        }
    }

    override fun onClick(v: View) {
        val scenename = if(sceneName == null) "" else StringUtils.replaceBlank(sceneName)
        when (v.id) {
            R.id.ptlitone -> ToastUtil.showToast(this@AssociatedpanelActivity, "不可以设置场景")
            R.id.ptlittwo -> ToastUtil.showToast(this@AssociatedpanelActivity, "不可以设置场景")
            R.id.ptlitthree -> ToastUtil.showToast(this@AssociatedpanelActivity, "不可以设置场景")
            R.id.ptlittwoone -> ToastUtil.showToast(this@AssociatedpanelActivity, "不可以设置场景")
            R.id.ptlittwotwo -> ToastUtil.showToast(this@AssociatedpanelActivity, "不可以设置场景")
            R.id.ptlitoneone -> ToastUtil.showToast(this@AssociatedpanelActivity, "不可以设置场景")
            R.id.paonerela_sanlu -> ToastUtil.showToast(this@AssociatedpanelActivity, "不可以设置场景")
            R.id.patworela_sanlu -> ToastUtil.showToast(this@AssociatedpanelActivity, "不可以设置场景")
            R.id.pathreerela_sanlu -> ToastUtil.showToast(this@AssociatedpanelActivity, "不可以设置场景")
            R.id.pafourrela_sanlu -> ToastUtil.showToast(this@AssociatedpanelActivity, "不可以设置场景")
            R.id.qxbutton_id -> dialogUtilview!!.removeviewDialog()
            R.id.checkbutton_id -> {
                dialogUtilview!!.removeviewDialog()
                when (btnflag) {//切换
                    "1" -> {
                        when (gatewayid) {
                            "" -> {
                                common_wifi_one(scenename)
                            }
                            else -> {
                                buttonNumber = "1"
                                panelRelation("0", panelNumber, "")
                            }
                        }
                    }
                    "2" -> {
                        when (gatewayid) {
                            "" -> {
                                common_wifi_two(scenename)
                            }
                            else -> {
                                buttonNumber = "2"
                                panelRelation("0", panelNumber, "")
                            }
                        }
                    }
                    "3" -> {
                        when (gatewayid) {
                            "" -> {
                                common_wifi_three(scenename)
                            }
                            else -> {
                                buttonNumber = "3"
                                panelRelation("0", panelNumber, "")
                            }
                        }
                    }
                    "4" -> {
                        when (gatewayid) {
                            "" -> {
                                common_wifi_four(scenename)
                            }
                            else -> {
                                buttonNumber = "4"
                                panelRelation("0", panelNumber, "")
                            }
                        }
                    }
                    "5" -> {
                        when (gatewayid) {
                            "" -> {
                                common_wifi_five(scenename)
                            }
                            else -> {
                                buttonNumber = "5"
                                panelRelation("0", panelNumber, "")
                            }
                        }
                    }
                    "6" -> {
//                        buttonNumber = "6"
//                        panelRelation("0", panelNumber)
                        when (gatewayid) {
                            "" -> {
                                common_wifi_six(scenename)
                            }
                            else -> {
                                buttonNumber = "6"
                                panelRelation("0", panelNumber, "")
                            }
                        }
                    }
                    "7" -> {
//                        buttonNumber = "7"
//                        panelRelation("0", panelNumber)
                        when (gatewayid) {
                            "" -> {
                                comon_wifi_seven(scenename)
                            }
                            else -> {
                                buttonNumber = "7"
                                panelRelation("0", panelNumber, "")
                            }
                        }
                    }
                    "8" -> {
                        /*             buttonNumber = "8"
                                     panelRelation("0", panelNumber)*/

                        when (gatewayid) {
                            "" -> {
                                common_wifi_eight(scenename)
                            }
                            else -> {
                                buttonNumber = "8"
                                panelRelation("0", panelNumber, "")
                            }
                        }

                    }
                }
            }
            R.id.backrela_id, R.id.back -> finish()
            R.id.paonerela -> ToastUtil.showToast(this@AssociatedpanelActivity, "不可以设置场景")
            R.id.patworela -> ToastUtil.showToast(this@AssociatedpanelActivity, "不可以设置场景")
            R.id.pathreerela -> ToastUtil.showToast(this@AssociatedpanelActivity, "不可以设置场景")
            R.id.pafourrela -> ToastUtil.showToast(this@AssociatedpanelActivity, "不可以设置场景")
            R.id.pafiverela -> {
                when (gatewayid) {
                    "" -> {
                        if (common_dialog(button5SceneType, "5", StringUtils.replaceBlank(pafivetext!!.text.toString()), scenename)) return
                        common_wifi_five(scenename)
                    }
                    else -> {
                        five_click_zigbee(scenename, null, pafivetext)
                    }
                }
            }

            R.id.pasixrela -> {
                when (gatewayid) {
                    "" -> {
                    if (common_dialog(button6SceneType, "6", StringUtils.replaceBlank(pasixtext!!.text.toString()),
                                    scenename)) return
                    common_wifi_six(scenename)
                }
                    else -> {
                        sixclick_zigbee(scenename, null, pasixtext)
                    }
                }
            }
            R.id.pasevenrela -> {
                when (gatewayid) {
                    "" -> {
                        if (common_dialog(button7SceneType, "7", StringUtils.replaceBlank(paseventext!!.text.toString()), scenename)) return
                        comon_wifi_seven(scenename)
                    }
                    else -> {
                        sevenclick_zigbee(scenename)
                    }
                }
            }

            R.id.paeightrela -> {

                when (gatewayid) {
                    "" -> {
                        if (common_dialog(button8SceneType, "8", StringUtils.replaceBlank(paeighttext!!.text.toString()), scenename)) return
                        common_wifi_eight(scenename)
                    }
                    else -> {
                        eightclick_zigbee(scenename)
                    }
                }
            }
            R.id.more_key_one -> {
                if (not_can_set_scene(0, 1)) return
                when (gatewayid) {
                    "" -> {
                        if (common_dialog(button1SceneType, "1", StringUtils.replaceBlank(more_key_one!!.text.toString()), scenename)) return
                        common_wifi_one(scenename)
                    }
                    else -> {
                        oneclick_zigbee(scenename)
                    }
                }
            }
            R.id.more_key_two -> {
                if (not_can_set_scene(1, 2)) return
                when (gatewayid) {
                    "" -> {
                        if (common_dialog(button2SceneType, "2", StringUtils.replaceBlank(more_key_two!!.text.toString()), scenename)) return
                        common_wifi_two(scenename)
                    }
                    else -> {
                        twoclick_zigbee(scenename)
                    }
                }
            }
            R.id.more_key_three -> {
                if (not_can_set_scene(2, 3)) return
                when (gatewayid) {
                    "" -> {
                        if (common_dialog(button3SceneType, "3", StringUtils.replaceBlank(more_key_three!!.text.toString()), scenename)) return
                        common_wifi_three(scenename)
                    }
                    else -> {
                        threeclick_zigbee(scenename)
                    }
                }
            }
            R.id.more_key_four -> {
                if (not_can_set_scene(3, 4)) return
                when (gatewayid) {
                    "" -> {
                        if (common_dialog(button4SceneType, "4", StringUtils.replaceBlank(more_key_four!!.text.toString()), scenename)) return
                        common_wifi_four(scenename)
                    }
                    else -> {
                        fourclick_zigbee(scenename)
                    }
                }
            }
            R.id.more_key_five -> {
                if (not_can_set_scene(4, 5)) return
                when (gatewayid) {
                    "" -> {
                        if (common_dialog(button5SceneType, "5", StringUtils.replaceBlank(more_key_five!!.text.toString()), scenename)) return
                        common_wifi_five(scenename)
                    }
                    else -> {
                        five_click_zigbee(scenename, more_key_five, null)
                    }
                }
            }

            R.id.more_key_six -> {
                if (not_can_set_scene(5, 6)) return
                when (gatewayid) {
                    "" -> {
                        if (common_dialog(button6SceneType, "6", StringUtils.replaceBlank(more_key_six!!.text.toString()), scenename)) return
                        common_wifi_six(scenename)
                    }
                    else -> {
                        sixclick_zigbee(scenename, more_key_six, null)
                    }
                }
            }
        }
    }

    private fun not_can_set_scene(start: Int?, end: Int?): Boolean {
        var status = buttonStatus!!.substring(start!!, end!!)
        when (status) {
            "2" -> {
                ToastUtil.showToast(this@AssociatedpanelActivity, "不可以设置场景")
                return true
            }
        }
        return false
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun new_device_buttonStatus_get_scene() {
        if(buttonStatus == null || buttonStatus!!.length == 0) return
        var length = buttonStatus!!.length
        for (start in 0..length - 1) {
            var status = buttonStatus!!.substring(start, start + 1)
            when (status) {
                "2" -> {
                    common_set_scene_background(start,R.drawable.relatedpanel_whitebtn)
                }
                else -> {
                    common_set_scene_background(start,R.drawable.relatedpanel_blue)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun common_set_scene_background(start: Int, relatedpanelWhitebtn: Int) {
        when (start) {
            0 -> {
                more_key_one_rel!!.background = resources.getDrawable(relatedpanelWhitebtn)
            }
            1 -> {
                more_key_two_rel!!.background = resources.getDrawable( relatedpanelWhitebtn)
            }
            2 -> {
                more_key_three_rel!!.background = resources.getDrawable(relatedpanelWhitebtn)
            }
            3 -> {
                more_key_four_rel!!.background = resources.getDrawable(relatedpanelWhitebtn)
            }
            4 -> {
                more_key_five_rel!!.background = resources.getDrawable(relatedpanelWhitebtn)
            }
            5 -> {
                more_key_six_rel!!.background = resources.getDrawable( relatedpanelWhitebtn)
            }
        }
    }


    private fun common_dialog(sceneType: String?, btnflag1: String?, scene_txt: String?, scenename: String): Boolean {
        if (scene_txt.equals(scenename) || scene_txt.equals("")) return false
        when (sceType) {
            "101" -> {//确定把云场景替换了 (网关场景)，
                when (sceneType) {
                    "101" -> {//确定把"1"替换了"2"?
                        belowtext_id!!.text = "确定把 " + sceneName +
                                " 替换了 " + scene_txt + " 吗？"
                    }
                    else -> {//确定把云场景替换了 (网关场景)
                        belowtext_id!!.text = "确定把云场景 " + "\"" + sceneName + "\"" +
                                " 替换了网关场景 " + "\"" + scene_txt + "\"" + " 吗？"
                    }
                }
            }
            else -> {
                when (sceneType) {
                    "101" -> {//确定把网关场景替换云场景？
                        belowtext_id!!.text = "确定把网关场景 " + "\"" + sceneName + "\"" +
                                " 替换了云场景 " + "\"" + scene_txt + "\"" + " 吗？"
                    }
                    else -> {//确定“1”替换“2”？
                        belowtext_id!!.text = "确定把 " + sceneName +
                                " 替换了 " + scene_txt + " 吗？"
                    }
                }
            }
        }

        btnflag = btnflag1!!
        dialogUtilview!!.loadViewResizeDialog()
        return true

//         //if (StringUtils.replaceBlank(pasixtext!!.text.toString()) != scenename) {
//        if (sceneType != null && !sceneType.equals("101") && !scene_txt.equals(scenename)) {
//            //belowtext_id!!.text = "是否要替换网关场景？"
//
//            belowtext_id!!.text = "确定把 " + sceneName +
//                    " 替换了 " + StringUtils.replaceBlank(paeighttext!!.text.toString()) + " 吗？"
//            btnflag = btnflag1!!
//            dialogUtilview!!.loadViewdialog()
//            return true
//        }
        //  return false
    }

    private fun common_wifi_eight(scenename: String?) {
        //var eightStatus = panelList[position]!!.buttonStatus.substring(3, 4)
        common_eight_wifi(scenename)
    }

    private fun common_eight_wifi(scenename: String?) {
        var deleteSceneId = button8SceneId
        if (buttonStatus == null) return
        var eightStatus = buttonStatus!!.substring(3, 4)

        buttonNumber = "8"
        panelNumber = panelid
        if (StringUtils.replaceBlank(paeighttext!!.text.toString()) != scenename) {
            paeighttext!!.text = scenename
            panelRelation("1", panelNumber, deleteSceneId)
            return
        }
        paeighttext!!.text = scenename
        when (eightStatus) {
            "0" -> {
                //go to rela
                panelRelation("1", panelNumber, deleteSceneId)
            }
            else -> {
                panelRelation("2", panelNumber, sceneId)
            }
        }
    }

    private fun comon_wifi_seven(scenename: String?) {
        //  var sevenStatus = panelList[position]!!.buttonStatus.substring(2, 3)
        var deleteSceneId = button7SceneId
        if (buttonStatus == null) return
        var sevenStatus = buttonStatus!!.substring(2, 3)

        buttonNumber = "7"
        panelNumber = panelid
        if (StringUtils.replaceBlank(paseventext!!.text.toString()) != scenename) {
            paseventext!!.text = scenename
            panelRelation("1", panelNumber, deleteSceneId)
            return
        }
        paseventext!!.text = scenename
        when (sevenStatus) {
            "0" -> {
                //go to rela
                panelRelation("1", panelNumber, deleteSceneId)
            }
            else -> {
                panelRelation("2", panelNumber, sceneId)
            }
        }
    }

    var buttonStatus: StringBuilder? = StringBuilder()

    private fun common_wifi_one(scenename: String?) {
        var deleteSceneId = button1SceneId
        // var fiveStatus = panelList[position]!!.buttonStatus.substring(0, 1)
        if (buttonStatus == null) return
        var oneStatus = buttonStatus!!.substring(0, 1)

        buttonNumber = "1"
        panelNumber = panelid
        if (click_set_button_name(scenename, deleteSceneId, more_key_one)) return
        when (oneStatus) {
            "0" -> { // 0 （oneStatus）-> 1 (panelRelation) 是去关联场景按钮
                //go to rela
                panelRelation("1", panelNumber, deleteSceneId)
            }
            else -> {
                panelRelation("2", panelNumber, sceneId)
            }
        }
    }


    private fun common_wifi_two(scenename: String?) {
        var deleteSceneId = button2SceneId
        // var fiveStatus = panelList[position]!!.buttonStatus.substring(0, 1)
        if (buttonStatus == null) return
        var twoStatus = buttonStatus!!.substring(1, 2)
        buttonNumber = "2"
        panelNumber = panelid
        if (click_set_button_name(scenename, deleteSceneId, more_key_two)) return
        when (twoStatus) {
            "0" -> {
                //go to rela
                panelRelation("1", panelNumber, deleteSceneId)
            }
            else -> {
                panelRelation("2", panelNumber, sceneId)
            }
        }
    }

    private fun common_wifi_three(scenename: String?) {
        var deleteSceneId = button3SceneId
        // var fiveStatus = panelList[position]!!.buttonStatus.substring(0, 1)
        if (buttonStatus == null) return
        var threeStatus = buttonStatus!!.substring(2, 3)

        buttonNumber = "3"
        panelNumber = panelid
        if (click_set_button_name(scenename, deleteSceneId, more_key_three)) return
        when (threeStatus) {
            "0" -> {
                //go to rela
                panelRelation("1", panelNumber, deleteSceneId)
            }
            else -> {
                panelRelation("2", panelNumber, sceneId)
            }
        }
    }

    private fun common_wifi_four(scenename: String?) {
        var deleteSceneId = button4SceneId
        // var fiveStatus = panelList[position]!!.buttonStatus.substring(0, 1)
        if (buttonStatus == null) return
        var fourStatus = buttonStatus!!.substring(3, 4)

        buttonNumber = "4"
        panelNumber = panelid
        if (click_set_button_name(scenename, deleteSceneId, more_key_four)) return
        when (fourStatus) {
            "0" -> {
                //go to rela
                panelRelation("1", panelNumber, deleteSceneId)
            }
            else -> {
                panelRelation("2", panelNumber, sceneId)
            }
        }
    }

    private fun common_wifi_five(scenename: String?) {
        var deleteSceneId = button5SceneId
        // var fiveStatus = panelList[position]!!.buttonStatus.substring(0, 1)
        if (buttonStatus == null) return
        var fiveStatus: String? = null
        when (type) {
            "AD11", "AD12", "AD13" ,"ADA1", "ADA2", "ADA3"-> {
                fiveStatus = buttonStatus!!.substring(4, 5)
            }
            else -> {
                fiveStatus = buttonStatus!!.substring(0, 1)
            }
        }

        buttonNumber = "5"
        panelNumber = panelid
        when (type) {
            "AD11", "AD12", "AD13","ADA1", "ADA2", "ADA3" -> {
                if (click_set_button_name(scenename, deleteSceneId, more_key_five)) return
            }
            else -> {
                if (click_set_button_name(scenename, deleteSceneId, pafivetext)) return
            }
        }
        when (fiveStatus) {
            "0" -> {
                //go to rela
                panelRelation("1", panelNumber, deleteSceneId)
            }
            else -> {
                panelRelation("2", panelNumber, sceneId)
            }
        }
    }

    private fun click_set_button_name(scenename: String?, deleteSceneId: String?, view: View?): Boolean {
        var views: View? = null
        views = view!!
        if (view is TextView) {
            views = views as TextView
            if (StringUtils.replaceBlank(views.text.toString()) != scenename) {
                views.text = scenename
                panelRelation("1", panelNumber, deleteSceneId)
                return true
            }
            views.text = scenename
        } else {
            views = views as Button
            if (StringUtils.replaceBlank(views.text.toString()) != scenename) {
                views.text = scenename
                panelRelation("1", panelNumber, deleteSceneId)
                return true
            }
            views.text = scenename
        }
        return false
    }


    private fun common_wifi_six(scenename: String?) {
        var deleteSceneId = button6SceneId
        if (buttonStatus == null) return
        var sixStatus: String? = null
        when (type) {
            "AD11", "AD12", "AD13","ADA1", "ADA2", "ADA3" -> {
                sixStatus = buttonStatus!!.substring(5, 6)
            }
            else -> {
                sixStatus = buttonStatus!!.substring(1, 2)
            }
        }
        buttonNumber = "6"
        panelNumber = panelid

        when (type) {
            "AD11", "AD12", "AD13","ADA1", "ADA2", "ADA3" -> {
                if (click_set_button_name(scenename, deleteSceneId, more_key_six)) return
            }
            else -> {
                if (click_set_button_name(scenename, deleteSceneId, pasixtext)) return
            }
        }


        when (sixStatus) {
            "0" -> {
                //go to rela
                panelRelation("1", panelNumber, deleteSceneId)
            }
            else -> {
                panelRelation("2", panelNumber, sceneId)
            }
        }
    }

    private fun eightclick_zigbee(scenename: String?) {
        LogUtil.eLength("查看数据", sceType + flageight + "第二次数据" + panelNumber)
        if (flageight) {
            when (flagimageight) {
                "1" -> {
                    LogUtil.eLength("确定关联数据", "传入")
                    flagimageight = "2"
                    buttonNumber = "8"
                    panelNumber = panelid
                    paeighttext!!.text = scenename
                    panelRelation("0", panelNumber, "")
                }
                "2" -> {
                    LogUtil.eLength("相等传输数据", "传入")
                    flagimageight = "1"
                    buttonNumber = "0"
                    panelRelation("0", "0", "")
                }
                "3" -> if (StringUtils.replaceBlank(paeighttext!!.text.toString()) == sceneName) {
                    LogUtil.eLength("相等传输数据", "传入")
                    flagimageight = "1"
                    buttonNumber = "0"
                    panelRelation("0", "0", "")
                } else {
//                    belowtext_id!!.text = "确定从 " +
//                            StringUtils.replaceBlank(paeighttext!!.text.toString()) + " 替换成 " + sceneName + " 吗？"


                    common_dialog(button8Type, "8", StringUtils.replaceBlank(paeighttext!!.text.toString()), scenename!!)
//                    belowtext_id!!.text = "确定把 " + sceneName +
//                            " 替换了 " + StringUtils.replaceBlank(paeighttext!!.text.toString()) + " 吗？"
//                    btnflag = "8"
//                    dialogUtilview!!.loadViewdialog()
                }
                else -> {

                }
            }
        } else {
            ToastUtil.showToast(this@AssociatedpanelActivity, "不可以设置场景")
        }
    }

    private fun oneclick_zigbee(scenename: String?) {
        LogUtil.eLength("查看数据", sceType + flagone + "数据" + flagimageone)
        if (flagone) {
            when (flagimageone) {
                "1" -> {
                    LogUtil.eLength("直接选中空白", "第七数据判断")
                    flagimageone = "2"
                    buttonNumber = "1"
                    panelNumber = panelid
                    more_key_one!!.text = scenename
                    panelRelation("0", panelNumber, "")
                }
                "2" -> {
                    LogUtil.eLength("直接取消状态", "取消行为")
                    flagimageone = "1"
                    buttonNumber = "0"
                    panelRelation("0", "0", "")
                }
                "3" -> if (StringUtils.replaceBlank(more_key_one!!.text.toString()) == sceneName) {
                    LogUtil.eLength("直接取消状态", "取消行为")
                    flagimageone = "1"
                    buttonNumber = "0"
                    panelRelation("0", "0", "")
                } else {
                    common_dialog(button1Type, "1", StringUtils.replaceBlank(more_key_one!!.text.toString()), scenename!!)
                }
                else -> {

                }
            }
        } else {
            ToastUtil.showToast(this@AssociatedpanelActivity, "不可以设置场景")
        }
    }

    private fun twoclick_zigbee(scenename: String?) {
        LogUtil.eLength("查看数据", sceType + flagtwo + "数据" + flagimagetwo)
        if (flagtwo) {
            when (flagimagetwo) {
                "1" -> {
                    LogUtil.eLength("直接选中空白", "第七数据判断")
                    flagimagetwo = "2"
                    buttonNumber = "2"
                    panelNumber = panelid
                    more_key_two!!.text = scenename
                    panelRelation("0", panelNumber, "")
                }
                "2" -> {
                    LogUtil.eLength("直接取消状态", "取消行为")
                    flagimagetwo = "1"
                    buttonNumber = "0"
                    panelRelation("0", "0", "")
                }
                "3" -> if (StringUtils.replaceBlank(more_key_two!!.text.toString()) == sceneName) {
                    LogUtil.eLength("直接取消状态", "取消行为")
                    flagimagetwo = "1"
                    buttonNumber = "0"
                    panelRelation("0", "0", "")
                } else {
                    common_dialog(button2Type, "2", StringUtils.replaceBlank(more_key_two!!.text.toString()), scenename!!)
                }
                else -> {

                }
            }
        } else {
            ToastUtil.showToast(this@AssociatedpanelActivity, "不可以设置场景")
        }
    }

    private fun threeclick_zigbee(scenename: String?) {
        LogUtil.eLength("查看数据", sceType + flagthree + "数据" + flagimagethree)
        if (flagthree) {
            when (flagimagethree) {
                "1" -> {
                    LogUtil.eLength("直接选中空白", "第七数据判断")
                    flagimagethree = "2"
                    buttonNumber = "3"
                    panelNumber = panelid
                    more_key_three!!.text = scenename
                    panelRelation("0", panelNumber, "")
                }
                "2" -> {
                    LogUtil.eLength("直接取消状态", "取消行为")
                    flagimagethree = "1"
                    buttonNumber = "0"
                    panelRelation("0", "0", "")
                }
                "3" -> if (StringUtils.replaceBlank(more_key_three!!.text.toString()) == sceneName) {
                    LogUtil.eLength("直接取消状态", "取消行为")
                    flagimagethree = "1"
                    buttonNumber = "0"
                    panelRelation("0", "0", "")
                } else {
                    common_dialog(button3Type, "3", StringUtils.replaceBlank(more_key_three!!.text.toString()), scenename!!)
                }
                else -> {

                }
            }
        } else {
            ToastUtil.showToast(this@AssociatedpanelActivity, "不可以设置场景")
        }
    }


    private fun fourclick_zigbee(scenename: String?) {
        LogUtil.eLength("查看数据", sceType + flagfour + "数据" + flagimagefour)
        if (flagfour) {
            when (flagimagefour) {
                "1" -> {
                    LogUtil.eLength("直接选中空白", "第七数据判断")
                    flagimagefour = "2"
                    buttonNumber = "4"
                    panelNumber = panelid
                    more_key_four!!.text = scenename
                    panelRelation("0", panelNumber, "")
                }
                "2" -> {
                    LogUtil.eLength("直接取消状态", "取消行为")
                    flagimagefour = "1"
                    buttonNumber = "0"
                    panelRelation("0", "0", "")
                }
                "3" -> if (StringUtils.replaceBlank(more_key_four!!.text.toString()) == sceneName) {
                    LogUtil.eLength("直接取消状态", "取消行为")
                    flagimagefour = "1"
                    buttonNumber = "0"
                    panelRelation("0", "0", "")
                } else {
                    common_dialog(button4Type, "4", StringUtils.replaceBlank(more_key_four!!.text.toString()), scenename!!)
                }
                else -> {

                }
            }
        } else {
            ToastUtil.showToast(this@AssociatedpanelActivity, "不可以设置场景")
        }
    }

    private fun sevenclick_zigbee(scenename: String?) {
        LogUtil.eLength("查看数据", sceType + flagseven + "数据" + flagimageseven)
        if (flagseven) {
            when (flagimageseven) {
                "1" -> {
                    LogUtil.eLength("直接选中空白", "第七数据判断")
                    flagimageseven = "2"
                    buttonNumber = "7"
                    panelNumber = panelid
                    paseventext!!.text = scenename
                    panelRelation("0", panelNumber, "")
                }
                "2" -> {
                    LogUtil.eLength("直接取消状态", "取消行为")
                    flagimageseven = "1"
                    buttonNumber = "0"
                    panelRelation("0", "0", "")
                }
                "3" -> if (StringUtils.replaceBlank(paseventext!!.text.toString()) == sceneName) {
                    LogUtil.eLength("直接取消状态", "取消行为")
                    flagimageseven = "1"
                    buttonNumber = "0"
                    panelRelation("0", "0", "")
                } else {
                    common_dialog(button7Type, "7", StringUtils.replaceBlank(paseventext!!.text.toString()), scenename!!)
                }
                else -> {

                }
            }
        } else {
            ToastUtil.showToast(this@AssociatedpanelActivity, "不可以设置场景")
        }
    }

    private fun sixclick_zigbee(scenename: String?, btn: Button?, txt: TextView?) {
        var content: String? = null
        if (btn != null) content = btn.text.toString()
        if (txt != null) content = txt.text.toString()
        if (flagsix) { //1代表为空值或者非空值不相等2非空值代表场景一致
            LogUtil.eLength("数据查看", panelNumber)
            when (flagimagesix) {
                "1" -> {
                    LogUtil.eLength("进入行为", "行为操作")
                    flagimagesix = "2"
                    buttonNumber = "6"
                    panelNumber = panelid
                    if (btn != null) btn!!.text = scenename
                    if (txt != null) txt!!.text = scenename
                    panelRelation("0", panelNumber, "")
                }
                "2" -> {
                    LogUtil.eLength("取消行为", "取消数据")
                    flagimagesix = "1"
                    buttonNumber = "0"
                    panelRelation("0", "0", "")
                }
                "3" -> if (StringUtils.replaceBlank(content) == sceneName) {
                    LogUtil.eLength("取消行为", "取消数据")
                    flagimagesix = "1"
                    buttonNumber = "0"
                    panelRelation("0", "0", "")
                } else {
//                    belowtext_id!!.text = "确定从 " +
//                            StringUtils.replaceBlank(pasixtext!!.text.toString()) + " 替换成 " + sceneName + " 吗？"
//                    belowtext_id!!.text = "确定把 " + sceneName +
//                            " 替换了 " + StringUtils.replaceBlank(pasixtext!!.text.toString()) + " 吗？"
//                    btnflag = "6"
//                    dialogUtilview!!.loadViewdialog()
                    common_dialog(button6Type, "6", StringUtils.replaceBlank(content), scenename!!)
                }
                else -> {
                }
            }
        } else {
            ToastUtil.showToast(this@AssociatedpanelActivity, "不可以设置场景")
        }
    }

    private fun five_click_zigbee(scenename: String?, btn: Button?, txt: TextView?) {
        var content: String? = null
        if (btn != null) content = btn.text.toString()
        if (txt != null) content = txt.text.toString()
        if (flagfive) { //第5个按钮可以设置场景
            //1代表为空值2非空值代表场景一致3非空值不相等
            when (flagimagefive) {
                "1" -> {
                    flagimagefive = "2"
                    buttonNumber = "5"
                    panelNumber = panelid
                    if (btn != null) btn!!.text = scenename
                    if (txt != null) txt!!.text = scenename
                    panelRelation("0", panelNumber, "")
                }
                "2" -> {
                    flagimagefive = "1"
                    buttonNumber = "0"
                    panelRelation("0", "0", "")
                }
                "3" -> if (StringUtils.replaceBlank(content) == sceneName) {
                    flagimagefive = "1"
                    buttonNumber = "0"
                    panelRelation("0", "0", "")
                } else {
//                    belowtext_id!!.text = "确定从 " +
//                            StringUtils.replaceBlank(pafivetext!!.text.toString()) + " 替换成 " + sceneName + " 吗？"
//                    belowtext_id!!.text = "确定把 " + sceneName +
//                            " 替换了 " + StringUtils.replaceBlank(pafivetext!!.text.toString()) + " 吗？"
//                    btnflag = "5"
//                    dialogUtilview!!.loadViewdialog()
                    common_dialog(button5Type, "5", StringUtils.replaceBlank(content), scenename!!)
                }
                else -> {
                }
            }
        } else {
            ToastUtil.showToast(this@AssociatedpanelActivity, "不可以设置场景")
        }
    }

    private fun panelRelation(action: String?, panelrenumb: String?, deleteSceneId: String?) {
        dialogUtil!!.loadDialog()
        sraum_panelRelation_(action, panelrenumb, deleteSceneId)
    }

    private fun sraum_panelRelation_(action: String?, panelrenumb: String?, deleteSceneId: String?) {
        val map: MutableMap<String, Any?> = HashMap()
        var api = ""
        map["token"] = TokenUtil.getToken(this@AssociatedpanelActivity)
        val areaNumber = SharedPreferencesUtil.getData(this@AssociatedpanelActivity,
                "areaNumber", "") as String
        map["areaNumber"] = areaNumber
        map["panelNumber"] = panelrenumb
        map["buttonNumber"] = buttonNumber //关联哪个面板上的哪个按钮

        when (gatewayid) {
            "" -> {
                api = ApiHelper.sraum_relateWifiDevice
                map["action"] = action
                map["sceneId"] = sceneId
                map["deleteSceneId"] = if (deleteSceneId == null) "" else deleteSceneId
                map["panelType"] = type
            }
            else -> {
                map["boxNumber"] = gatewayid
                map["sceneName"] = sceneName
                api = ApiHelper.sraum_panelRelation
            }
        }

        MyOkHttp.postMapObject(api, map,
                object : Mycallback(AddTogglenInterfacer { sraum_panelRelation_(action, panelrenumb, deleteSceneId) }, this@AssociatedpanelActivity, dialogUtil) {
                    override fun onSuccess(user: User) {
                        super.onSuccess(user)
                        when (gatewayid) {
                            "" -> {
                                result_wifi_button_status(action)
                                result_scene_name_and_id()
                                refresh_panel_status_show_second(-2)
                            }
                            else -> {
                                getData(1, "onclick")
                            }
                        }
                    }
                    override fun wrongToken() {
                        super.wrongToken()
                    }
                })
    }

    private fun result_wifi_button_status(action: String?) {
        when (buttonNumber) {
            "1" -> {
                when (action) {
                    "1" -> {
                        button_status_change(0, 1, "1", null, null)
                    }
                    "2" -> {
                        button_status_change(0, 1, "0", more_key_one, null)
                    }
                }
            }
            "2" -> {
                when (action) {
                    "1" -> {
                        button_status_change(1, 2, "1", null, null)
                    }
                    "2" -> {
                        button_status_change(1, 2, "0", more_key_two, null)
                    }
                }
            }
            "3" -> {
                when (action) {
                    "1" -> {
                        button_status_change(2, 3, "1", null, null)
                    }
                    "2" -> {
                        button_status_change(2, 3, "0", more_key_three, null)
                    }
                }
            }
            "4" -> {
                when (action) {
                    "1" -> {
                        button_status_change(3, 4, "1", null, null)
                    }
                    "2" -> {
                        button_status_change(3, 4, "0", more_key_four, null)
                    }
                }
            }
            "5" -> {
                when (action) {
                    "1" -> {
                        when (type) {
                            "AD11", "AD12", "AD13","ADA1", "ADA2", "ADA3" -> {
                                button_status_change(4, 5, "1", null, null)
                            }
                            else -> {
                                button_status_change(0, 1, "1", null, null)
                            }
                        }
                    }
                    "2" -> {
                        when (type) {
                            "AD11", "AD12", "AD13","ADA1", "ADA2", "ADA3" -> {
                                button_status_change(4, 5, "0", null, more_key_five)
                            }
                            else -> {
                                button_status_change(0, 1, "0", null, pafivetext)
                            }
                        }
                    }
                }
            }
            "6" -> {
                when (action) {
                    "1" -> {
                        when (type) {
                            "AD11", "AD12", "AD13","ADA1", "ADA2", "ADA3" -> {
                                button_status_change(5, 6, "1", null, null)
                            }
                            else -> {
                                button_status_change(1, 2, "1", null, null)
                            }
                        }
                    }
                    "2" -> {
                        when (type) {
                            "AD11", "AD12", "AD13","ADA1", "ADA2", "ADA3" -> {
                                button_status_change(5, 6, "0", null, more_key_six)
                            }
                            else -> {
                                button_status_change(1, 2, "0", null, pasixtext)
                            }
                        }
                    }
                }
            }
            "7" -> {
                when (action) {
                    "1" -> {
                        button_status_change(2, 3, "1", null, null)
                    }
                    "2" -> {
                        button_status_change(2, 3, "0", null, paseventext)
                    }
                }
            }

            "8" -> {
                when (action) {
                    "1" -> {
                        button_status_change(3, 4, "1", null, null)
                    }
                    "2" -> {
                        button_status_change(3, 4, "0", null, paeighttext)
                    }
                }
            }
        }
    }

    private fun result_scene_name_and_id() {
        for (i in panelList.indices) {
            if (position == i) {
                panelList[i]!!["buttonStatus"] = buttonStatus.toString()
                when (type) {
                    "AD11", "AD12", "AD13" ,"ADA1", "ADA2", "ADA3"-> {
                        panelList[i]!!["button1Name"] = more_key_one!!.text.toString()
                        panelList[i]!!["button2Name"] = more_key_two!!.text.toString()
                        panelList[i]!!["button3Name"] = more_key_three!!.text.toString()
                        panelList[i]!!["button4Name"] = more_key_four!!.text.toString()
                        panelList[i]!!["button5Name"] = more_key_five!!.text.toString()
                        panelList[i]!!["button6Name"] = more_key_six!!.text.toString()
                    }
                    else -> {
                        panelList[i]!!["button5Name"] = pafivetext!!.text.toString()
                        panelList[i]!!["button6Name"] = pasixtext!!.text.toString()
                        panelList[i]!!["button7Name"] = paseventext!!.text.toString()
                        panelList[i]!!["button8Name"] = paeighttext!!.text.toString()
                    }
                }

                when (buttonNumber) {
                    "1" -> {
                        panelList[i]!!["button1SceneId"] = sceneId!!
                    }
                    "2" -> {
                        panelList[i]!!["button2SceneId"] = sceneId!!
                    }
                    "3" -> {
                        panelList[i]!!["button3SceneId"] = sceneId!!
                    }
                    "4" -> {
                        panelList[i]!!["button4SceneId"] = sceneId!!
                    }
                    "5" -> {
                        panelList[i]!!["button5SceneId"] = sceneId!!
                    }
                    "6" -> {
                        panelList[i]!!["button6SceneId"] = sceneId!!
                    }
                    "7" -> {
                        panelList[i]!!["button7SceneId"] = sceneId!!
                    }
                    "8" -> {
                        panelList[i]!!["button8SceneId"] = sceneId!!
                    }
                }
                break
            }
        }
    }

    private fun button_status_change(start: Int?, end: Int?, str: String?, btn: Button?, txt: TextView?) {
        if (btn != null)
            btn!!.text = ""
        if (txt != null)
            txt!!.text = ""
        buttonStatus!!.replace(start!!, end!!, str!!)
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
        this.position = position
        onitemclick(position)
    }


    var name_common: String? = null
    private fun onitemclick(position: Int) {
        buttonStatus!!.clear()
        buttonStatus!!.append(panelList[position]!!["buttonStatus"])
        name_common = panelList[position]!!["name"].toString()
        panelNumber = panelList[position]!!["id"].toString()
        panelid = panelList[position]!!["id"].toString()
        type = panelList[position]!!["type"].toString()
        get_button_params(position)
        when(type) {
            "AD11","AD12","AD13","ADA1", "ADA2", "ADA3"-> {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    new_device_buttonStatus_get_scene()
                }
            }
        }
        val onename = panelList[position]!!["button1Name"].toString()
        val twoname = panelList[position]!!["button2Name"].toString()
        val threename = panelList[position]!!["button3Name"].toString()
        val fourname = panelList[position]!!["button4Name"].toString()
        val fivename = panelList[position]!!["button5Name"].toString()
        val sixname = panelList[position]!!["button6Name"].toString()
        val sevenname = panelList[position]!!["button7Name"].toString()
        val eightname = panelList[position]!!["button8Name"].toString()
        LogUtil.eLength("查看name", fivename + "那么" + sixname + "数据" +
                sevenname + "查看" + eightname + "你看呢")
        compareName(onename, twoname, threename, fourname, fivename, sixname, sevenname, eightname)
        set_button_name(onename, twoname, threename, fourname, fivename, sixname, sevenname, eightname)
        LogUtil.eLength("点击数据", type + "查看数据" + button5Type + "12" + button6Type + "34" +
                button7Type + "45" + button8Type + "67")

        when (gatewayid) {
            "" -> {
                checkWifiPosition(position)
            }
            else -> {
                checkPosition(position)
            }
        }
        key_or_more_keys_visible(type)
        clear()
        LogUtil.eLength("查看item", type + "数据" + position)
        when (type) {
            "AD11" ,"ADA1"-> {
                more_key_linear_one!!.visibility = View.VISIBLE
            }
            "AD12","ADA2" -> {
                more_key_linear_two!!.visibility = View.VISIBLE
                more_key_linear_one!!.visibility = View.VISIBLE
            }
            "AD13","ADA3" -> {
                more_key_linear_three!!.visibility = View.VISIBLE
                more_key_linear_two!!.visibility = View.VISIBLE
                more_key_linear_one!!.visibility = View.VISIBLE
            }
            "A201", "A2A1" -> {
                panelinearone!!.visibility = View.VISIBLE
                LogUtil.eLength("这是进入A201", "看看操作")
            }
            "A202", "A311", "A411", "A2A2", "A421" -> {
                panelineartwo!!.visibility = View.VISIBLE
                LogUtil.eLength("这是进入A202", "进入了")
            }
            "A203", "A312", "A321", "A412", "A2A3" -> {
                panelinearthree!!.visibility = View.VISIBLE
                LogUtil.eLength("这是进入A203", "看看操作")
            }
            "A204", "A313", "A322", "A331", "A413", "A414", "A2A4" -> panelinearfour!!.visibility = View.VISIBLE
            "A301" -> {
                panelinearfour!!.visibility = View.VISIBLE
                flagfive = false
                flagsix = true
                flagseven = true
                flageight = true
            }
            "A302" -> {
                panelinearfour!!.visibility = View.VISIBLE
                flagfive = false
                flagsix = false
                flagseven = true
                flageight = true
            }
            "A303" -> {
                flagfive = false
                flagsix = false
                flagseven = false
                flageight = true
                paneThreeLuTiaoGuang!!.visibility = View.VISIBLE
            }
            else -> panelinearfour!!.visibility = View.VISIBLE
        }
    }

    private fun set_button_name(onename: String?, twoname: String?, threename: String?, fourname: String?, fivename: String, sixname: String, sevenname: String, eightname: String) {
        when (type) {
            "AD11", "AD12", "AD13", "ADA1", "ADA2", "ADA3"-> {
                more_key_one!!.text = LengthUtil.doit_spit_str(onename ?: "")
                more_key_two!!.text = LengthUtil.doit_spit_str(twoname ?: "")
                more_key_three!!.text = LengthUtil.doit_spit_str(threename ?: "")
                more_key_four!!.text = LengthUtil.doit_spit_str(fourname ?: "")
                more_key_five!!.text = LengthUtil.doit_spit_str(fivename ?: "")
                more_key_six!!.text = LengthUtil.doit_spit_str(sixname ?: "")
            }
            else -> {
                pafivetext!!.text = LengthUtil.doit_spit_str(fivename ?: "")
                pasixtext!!.text = LengthUtil.doit_spit_str(sixname ?: "")
                paseventext!!.text = LengthUtil.doit_spit_str(sevenname ?: "")
                paeighttext!!.text = LengthUtil.doit_spit_str(eightname ?: "")
            }
        }
    }

    private fun get_button_params(position: Int) {
        button1SceneId = panelList[position]!!["button1SceneId"].toString()
        button2SceneId = panelList[position]!!["button2SceneId"].toString()
        button3SceneId = panelList[position]!!["button3SceneId"].toString()
        button4SceneId = panelList[position]!!["button4SceneId"].toString()
        button5SceneId = panelList[position]!!["button5SceneId"].toString()
        button6SceneId = panelList[position]!!["button6SceneId"].toString()
        button7SceneId = panelList[position]!!["button7SceneId"].toString()
        button8SceneId = panelList[position]!!["button8SceneId"].toString()

        button1SceneType = panelList[position]!!["button1SceneType"].toString()
        button2SceneType = panelList[position]!!["button2SceneType"].toString()
        button3SceneType = panelList[position]!!["button3SceneType"].toString()
        button4SceneType = panelList[position]!!["button4SceneType"].toString()
        button5SceneType = panelList[position]!!["button5SceneType"].toString()
        button6SceneType = panelList[position]!!["button6SceneType"].toString()
        button7SceneType = panelList[position]!!["button7SceneType"].toString()
        button8SceneType = panelList[position]!!["button8SceneType"].toString()

        button1Type = panelList[position]!!["button1Type"].toString()
        button2Type = panelList[position]!!["button2Type"].toString()
        button3Type = panelList[position]!!["button3Type"].toString()
        button4Type = panelList[position]!!["button4Type"].toString()
        button5Type = panelList[position]!!["button5Type"].toString()
        button6Type = panelList[position]!!["button6Type"].toString()
        button7Type = panelList[position]!!["button7Type"].toString()
        button8Type = panelList[position]!!["button8Type"].toString()
    }

    private fun key_or_more_keys_visible(type: String?) {
        when (type) {
            "AD11", "AD12", "AD13","ADA1", "ADA2", "ADA3" -> {
                panelrela_more_key!!.visibility = View.VISIBLE
                panelrela!!.visibility = View.GONE
            }
            "" -> {
                panelrela!!.visibility = View.GONE
                panelrela_more_key!!.visibility = View.GONE
            }
            else -> {
                panelrela_more_key!!.visibility = View.GONE
                panelrela!!.visibility = View.VISIBLE
            }
        }
    }

    private fun checkWifiPosition(position: Int) {
//        for (i in checkList.indices) {
//            checkList[i] = false
//        }
        refresh_panel_status_show_second(position)
    }

    private fun clear() {
        panelinearone!!.visibility = View.GONE
        panelineartwo!!.visibility = View.GONE
        panelinearthree!!.visibility = View.GONE
        panelinearfour!!.visibility = View.GONE
        paneThreeLuTiaoGuang!!.visibility = View.GONE
        more_key_linear_one!!.visibility = View.GONE
        more_key_linear_two!!.visibility = View.GONE
        more_key_linear_three!!.visibility = View.GONE
    }

    private fun compareName(onename: String?, twoname: String?, threename: String?, fourname: String?, fivename: String?, sixname: String?, sevenname: String?, eightname: String?) {
        var onename = onename
        var twoname = twoname
        var threename = threename
        var fourname = fourname
        var fivename = fivename
        var sixname = sixname
        var sevenname = sevenname
        var eightname = eightname
        if (onename == null) {
            onename = ""
        }
        if (twoname == null) {
            twoname = ""
        }
        if (threename == null) {
            threename = ""
        }
        if (fourname == null) {
            fourname = ""
        }
        if (fivename == null) {
            fivename = ""
        }
        if (sixname == null) {
            sixname = ""
        }
        if (sevenname == null) {
            sevenname = ""
        }
        if (eightname == null) {
            eightname = ""
        }


        //1代表为空值或者2非空值代表场景一直3非空值不相等
        flagimageone = if (onename == "") {
            "1"
        } else {
            if (onename == sceneName) {
                "2"
            } else {
                "3"
            }
        }
        flagimagetwo = if (twoname == "") {
            "1"
        } else {
            if (twoname == sceneName) {
                "2"
            } else {
                "3"
            }
        }
        flagimagethree = if (threename == "") {
            "1"
        } else {
            if (threename == sceneName) {
                "2"
            } else {
                "3"
            }
        }
        flagimagefour = if (fourname == "") {
            "1"
        } else {
            if (fourname == sceneName) {
                "2"
            } else {
                "3"
            }
        }

        flagimagefive = if (fivename == "") {
            "1"
        } else {
            if (fivename == sceneName) {
                "2"
            } else {
                "3"
            }
        }
        flagimagesix = if (sixname == "") {
            "1"
        } else {
            if (sixname == sceneName) {
                "2"
            } else {
                "3"
            }
        }
        flagimageseven = if (sevenname == "") {
            "1"
        } else {
            if (sevenname == sceneName) {
                "2"
            } else {
                "3"
            }
        }
        flagimageight = if (eightname == "") {
            "1"
        } else {
            if (fivename == sceneName) {
                "2"
            } else {
                "3"
            }
        }
    }
}