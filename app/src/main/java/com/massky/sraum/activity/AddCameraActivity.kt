package com.massky.sraum.activity

import android.app.Activity
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.widget.*
import com.ipcamera.demo.BridgeService
import com.ipcamera.demo.BridgeService.CallBackMessageInterface
import com.ipcamera.demo.BridgeService.IpcamClientInterface
import com.ipcamera.demo.PlayActivity
import com.ipcamera.demo.utils.*
import com.massky.sraum.R
import com.massky.sraum.activity.AddCameraActivity
import vstc2.nativecaller.NativeCaller
import java.io.File
import java.util.*

class AddCameraActivity : Activity(), View.OnClickListener, AdapterView.OnItemSelectedListener, IpcamClientInterface, CallBackMessageInterface {
    private var userEdit: EditText? = null
    private var pwdEdit: EditText? = null
    private var didEdit: EditText? = null
    private var textView_top_show: TextView? = null
    private var done: Button? = null
    private var option = ContentCommon.INVALID_OPTION
    private val CameraType = ContentCommon.CAMERA_TYPE_MJPEG
    private var btnSearchCamera: Button? = null
    private var progressdlg: ProgressDialog? = null
    private val isSearched = false
    private var receiver: MyBroadCast? = null
    private var manager: WifiManager? = null
    private var progressBar: ProgressBar? = null
    private val myWifiThread: MyWifiThread? = null
    private var blagg = false
    private var intentbrod: Intent? = null
    private var info: WifiInfo? = null
    private var button_play: Button? = null
    private var button_setting: Button? = null
    private var pic_video: Button? = null
    private var button_linkcamera: Button? = null
    private var btn_ip: Button? = null
    private var btn_info: Button? = null
    private var tag = 0

    internal inner class MyTimerTask : TimerTask() {
        override fun run() {
            updateListHandler.sendEmptyMessage(100000)
        }
    }

    internal inner class MyWifiThread : Thread() {
        override fun run() {
            while (blagg == true) {
                super.run()
                updateListHandler.sendEmptyMessage(100000)
                try {
                    sleep(1000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private inner class MyBroadCast : BroadcastReceiver() {
        override fun onReceive(arg0: Context, arg1: Intent) {
            finish()
            Log.d("ip", "AddCameraActivity.this.finish()")
        }
    }

    internal inner class StartPPPPThread : Runnable {
        override fun run() {
            try {
                Thread.sleep(100)
                if (VuidUtils.isVuid(SystemValue.deviceId)) {
                    val vuidStatus = NativeCaller.StartVUID("0", SystemValue.devicePass, 1, "", "", 0, SystemValue.deviceId, 0)
                    Log.e("vst", "vuidStatus$vuidStatus")
                    if (vuidStatus == -2) {
                        // TODO: 2019-11-25 VUID  无效
                        val bd = Bundle()
                        val msg = PPPPMsgHandler.obtainMessage()
                        msg.what = ContentCommon.PPPP_MSG_VSNET_NOTIFY_TYPE_VUIDSTATUS
                        bd.putInt(STR_MSG_PARAM, -2)
                        bd.putString(STR_DID, SystemValue.deviceId)
                        msg.data = bd
                        PPPPMsgHandler.sendMessage(msg)
                    }
                } else {
                    startCameraPPPP()
                }
            } catch (e: Exception) {
            }
        }
    }

    private fun startCameraPPPP() {
        try {
            Thread.sleep(100)
        } catch (e: Exception) {
        }
        if (SystemValue.deviceId.toLowerCase().startsWith("vsta")) {
            NativeCaller.StartPPPPExt(SystemValue.deviceId, SystemValue.deviceName,
                    SystemValue.devicePass, 1, "", "EFGFFBBOKAIEGHJAEDHJFEEOHMNGDCNJCDFKAKHLEBJHKEKMCAFCDLLLHAOCJPPMBHMNOMCJKGJEBGGHJHIOMFBDNPKNFEGCEGCBGCALMFOHBCGMFK", 0)
        } else if (SystemValue.deviceId.toLowerCase().startsWith("vstd")) {
            NativeCaller.StartPPPPExt(SystemValue.deviceId, SystemValue.deviceName,
                    SystemValue.devicePass, 1, "", "HZLXSXIALKHYEIEJHUASLMHWEESUEKAUIHPHSWAOSTEMENSQPDLRLNPAPEPGEPERIBLQLKHXELEHHULOEGIAEEHYEIEK-$$", 1)
        } else if (SystemValue.deviceId.toLowerCase().startsWith("vstf")) {
            NativeCaller.StartPPPPExt(SystemValue.deviceId, SystemValue.deviceName,
                    SystemValue.devicePass, 1, "", "HZLXEJIALKHYATPCHULNSVLMEELSHWIHPFIBAOHXIDICSQEHENEKPAARSTELERPDLNEPLKEILPHUHXHZEJEEEHEGEM-$$", 1)
        } else if (SystemValue.deviceId.toLowerCase().startsWith("vste")) {
            NativeCaller.StartPPPPExt(SystemValue.deviceId, SystemValue.deviceName,
                    SystemValue.devicePass, 1, "", "EEGDFHBAKKIOGNJHEGHMFEEDGLNOHJMPHAFPBEDLADILKEKPDLBDDNPOHKKCIFKJBNNNKLCPPPNDBFDL", 0)
        } else if (SystemValue.deviceId.toLowerCase().startsWith("pisr")) {
            NativeCaller.StartPPPPExt(SystemValue.deviceId, SystemValue.deviceName,
                    SystemValue.devicePass, 1, "", "EFGFFBBOKAIEGHJAEDHJFEEOHMNGDCNJCDFKAKHLEBJHKEKMCAFCDLLLHAOCJPPMBHMNOMCJKGJEBGGHJHIOMFBDNPKNFEGCEGCBGCALMFOHBCGMFK", 0)
        } else if (SystemValue.deviceId.toLowerCase().startsWith("vstg")) {
            NativeCaller.StartPPPPExt(SystemValue.deviceId, SystemValue.deviceName,
                    SystemValue.devicePass, 1, "", "EEGDFHBOKCIGGFJPECHIFNEBGJNLHOMIHEFJBADPAGJELNKJDKANCBPJGHLAIALAADMDKPDGOENEBECCIK:vstarcam2018", 0)
        } else if (SystemValue.deviceId.toLowerCase().startsWith("vsth")) {
            NativeCaller.StartPPPPExt(SystemValue.deviceId, SystemValue.deviceName,
                    SystemValue.devicePass, 1, "", "EEGDFHBLKGJIGEJLEKGOFMEDHAMHHJNAGGFABMCOBGJOLHLJDFAFCPPHGILKIKLMANNHKEDKOINIBNCPJOMK:vstarcam2018", 0)
        } else if (SystemValue.deviceId.toLowerCase().startsWith("vstb") || SystemValue.deviceId.toLowerCase().startsWith("vstc")) {
            NativeCaller.StartPPPPExt(SystemValue.deviceId, SystemValue.deviceName,
                    SystemValue.devicePass, 1, "", "ADCBBFAOPPJAHGJGBBGLFLAGDBJJHNJGGMBFBKHIBBNKOKLDHOBHCBOEHOKJJJKJBPMFLGCPPJMJAPDOIPNL", 0)
        } else if (SystemValue.deviceId.toLowerCase().startsWith("vstj")) {
            NativeCaller.StartPPPPExt(SystemValue.deviceId, SystemValue.deviceName,
                    SystemValue.devicePass, 1, "", "EEGDFHBLKGJIGEJNEOHEFBEIGANCHHMBHIFEAHDEAMJCKCKJDJAFDDPPHLKJIHLMBENHKDCHPHNJBODA:vstarcam2019", 0)
        } else if (SystemValue.deviceId.toLowerCase().startsWith("vstk")) {
            NativeCaller.StartPPPPExt(SystemValue.deviceId, SystemValue.deviceName,
                    SystemValue.devicePass, 1, "", "EBGDEJBJKGJFGJJBEFHPFCEKHGNMHNNMHMFFBICPAJJNLDLLDHACCNONGLLPJGLKANMJLDDHODMEBOCIJEMA:vstarcam2019", 0)
        } else if (SystemValue.deviceId.toLowerCase().startsWith("vstm")) {
            NativeCaller.StartPPPPExt(SystemValue.deviceId, SystemValue.deviceName,
                    SystemValue.devicePass, 1, "", "EBGEEOBOKHJNHGJGEAGAEPEPHDMGHINBGIECBBCBBJIKLKLCCDBBCFODHLKLJJKPBOMELECKPNMNAICEJCNNJH:vstarcam2019", 0)
        } else if (SystemValue.deviceId.toLowerCase().startsWith("vstn")) {
            NativeCaller.StartPPPPExt(SystemValue.deviceId, SystemValue.deviceName,
                    SystemValue.devicePass, 1, "", "EEGDFHBBKBIFGAIAFGHDFLFJGJNIGEMOHFFPAMDMAAIIKBKNCDBDDMOGHLKCJCKFBFMPLMCBPEMG:vstarcam2019", 0)
        } else if (SystemValue.deviceId.toLowerCase().startsWith("vstl")) {
            NativeCaller.StartPPPPExt(SystemValue.deviceId, SystemValue.deviceName,
                    SystemValue.devicePass, 1, "", "EEGDFHBLKGJIGEJIEIGNFPEEHGNMHPNBGOFIBECEBLJDLMLGDKAPCNPFGOLLJFLJAOMKLBDFOGMAAFCJJPNFJP:vstarcam2019", 0)
        } else if (SystemValue.deviceId.toLowerCase().startsWith("vstp")) {
            NativeCaller.StartPPPPExt(SystemValue.deviceId, SystemValue.deviceName,
                    SystemValue.devicePass, 1, "", "EEGDFHBLKGJIGEJLEIGJFLENHLNBHCNMGAFGBNCOAIJMLKKODNALCCPKGBLHJLLHAHMBKNDFOGNGBDCIJFMB:vstarcam2019", 0)
        } else {
            NativeCaller.StartPPPPExt(SystemValue.deviceId, SystemValue.deviceName,
                    SystemValue.devicePass, 1, "", "", 0)
        }
    }

    private fun stopCameraPPPP() {
        NativeCaller.StopPPPP(SystemValue.deviceId)
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.add_camera)
        progressdlg = ProgressDialog(this)
        progressdlg!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressdlg!!.setMessage(getString(R.string.searching_tip))
        findView()
        manager = getSystemService(WIFI_SERVICE) as WifiManager
        InitParams()
        BridgeService.setCallBackMessage(this)
        receiver = MyBroadCast()
        val filter = IntentFilter()
        filter.addAction("finish")
        registerReceiver(receiver, filter)
        intentbrod = Intent("drop")
    }

    override fun onStart() {
        // TODO Auto-generated method stub
        super.onStart()
    }

    override fun onPause() {
        // TODO Auto-generated method stub
        super.onPause()
    }

    override fun onResume() {
        // TODO Auto-generated method stub
        super.onResume()
        blagg = true
    }

    private fun InitParams() {
        done!!.setOnClickListener(this)
        btnSearchCamera!!.setOnClickListener(this)
    }

    override fun onStop() {
        super.onStop()
        if (myWifiThread != null) {
            blagg = false
        }
        progressdlg!!.dismiss()
        NativeCaller.StopSearch()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
        NativeCaller.Free()
        val intent = Intent()
        intent.setClass(this, BridgeService::class.java)
        stopService(intent)
        tag = 0
    }

    var updateThread = Runnable {
        NativeCaller.StopSearch()
        progressdlg!!.dismiss()
        val msg = updateListHandler.obtainMessage()
        msg.what = 1
        updateListHandler.sendMessage(msg)
    }
    var updateListHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {}
    }

    private fun findView() {
        progressBar = findViewById<View>(R.id.main_model_progressBar1) as ProgressBar
        textView_top_show = findViewById<View>(R.id.login_textView1) as TextView
        button_play = findViewById<View>(R.id.play) as Button
        button_setting = findViewById<View>(R.id.setting) as Button
        pic_video = findViewById<View>(R.id.location_pics_videos) as Button
        pic_video!!.setOnClickListener(this)
        done = findViewById<View>(R.id.done) as Button
        //done.setText("连接");
        userEdit = findViewById<View>(R.id.editUser) as EditText
        pwdEdit = findViewById<View>(R.id.editPwd) as EditText
        didEdit = findViewById<View>(R.id.editDID) as EditText
        btnSearchCamera = findViewById<View>(R.id.btn_searchCamera) as Button
        button_linkcamera = findViewById<View>(R.id.btn_linkcamera) as Button
        btn_ip = findViewById<View>(R.id.btn_ip) as Button
        btn_info = findViewById<View>(R.id.getInfo) as Button
        button_linkcamera!!.setOnClickListener(this)
        button_play!!.setOnClickListener(this)
        button_setting!!.setOnClickListener(this)
        btn_ip!!.setOnClickListener(this)
        btn_info!!.setOnClickListener(this)
        if (false) {
            //具体使用方法，请联系 dfc@vstarcam.com
            btn_ip!!.visibility = View.GONE
            btn_info!!.visibility = View.GONE
        }
    }

    /**
     * 摄像机在线时可以获取一张摄像机当前的画面图
     */
    /*private void getSnapshot(){
		String msg="snapshot.cgi?loginuse=admin&loginpas=" + SystemValue.devicePass
		 + "&user=admin&pwd=" + SystemValue.devicePass;
		NativeCaller.TransferMessage(SystemValue.deviceId, msg, 1);
	}
*/
    override fun onClick(v: View) {
        when (v.id) {
            R.id.play -> {
                val intent = Intent(this@AddCameraActivity, PlayActivity::class.java)
                if (MySharedPreferenceUtil.getDeviceInformation(this,
                                SystemValue.deviceId, ContentCommon.DEVICE_MODEL_TYPE) == "1" || MySharedPreferenceUtil.getDeviceInformation(this, SystemValue.deviceId, ContentCommon.DEVICE_MODEL_TYPE) == "2") {
                    //intent = new Intent(AddCameraActivity.this,PlayVRActivity.class);
                }
                startActivity(intent)
            }
            R.id.setting -> if (tag == 1) {
                val intent1 = Intent(this@AddCameraActivity,
                        SettingActivity::class.java)
                intent1.putExtra(ContentCommon.STR_CAMERA_ID,
                        SystemValue.deviceId)
                intent1.putExtra(ContentCommon.STR_CAMERA_NAME,
                        SystemValue.deviceName)
                intent1.putExtra(ContentCommon.STR_CAMERA_PWD, SystemValue.devicePass)
                startActivity(intent1)
                overridePendingTransition(R.anim.in_from_right,
                        R.anim.out_to_left)
            } else {
                Toast.makeText(this@AddCameraActivity, resources.getString(R.string.main_setting_prompt), Toast.LENGTH_SHORT).show()
            }
            R.id.location_pics_videos -> if (SystemValue.deviceId != null) {
                val div = File(Environment.getExternalStorageDirectory(),
                        "ipcamerademo/takepic")
                if (!div.exists()) {
                    div.mkdirs()
                }
                Toast.makeText(this@AddCameraActivity, "保存在" + div.absolutePath, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@AddCameraActivity, "请确认是否选择设备", Toast.LENGTH_SHORT).show()
            }
            R.id.done -> if (tag == 1) {
                Toast.makeText(this@AddCameraActivity, "设备已经是在线状态了", Toast.LENGTH_SHORT).show()
            } else if (tag == 2) {
                Toast.makeText(this@AddCameraActivity, "设备不在线", Toast.LENGTH_SHORT).show()
            } else {
                done()
            }
            R.id.btn_searchCamera -> {

            }
            R.id.btn_linkcamera -> {

            }
            R.id.btn_ip -> {

            }
            R.id.getInfo -> {

            }
            else -> {

            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.repeatCount == 0) {
            finish()
            return false
        }
        System.exit(0)
        return false
    }

    private fun done() {
        val `in` = Intent()
        val strUser = userEdit!!.text.toString()
        val strPwd = pwdEdit!!.text.toString()
        val strDID = didEdit!!.text.toString()
        if (strDID.length == 0) {
            Toast.makeText(this@AddCameraActivity,
                    resources.getString(R.string.input_camera_id), Toast.LENGTH_SHORT).show()
            return
        }
        if (strUser.length == 0) {
            Toast.makeText(this@AddCameraActivity,
                    resources.getString(R.string.input_camera_user), Toast.LENGTH_SHORT).show()
            return
        }
        if (option == ContentCommon.INVALID_OPTION) {
            option = ContentCommon.ADD_CAMERA
        }
        `in`.putExtra(ContentCommon.CAMERA_OPTION, option)
        `in`.putExtra(ContentCommon.STR_CAMERA_ID, strDID)
        `in`.putExtra(ContentCommon.STR_CAMERA_USER, strUser)
        `in`.putExtra(ContentCommon.STR_CAMERA_PWD, strPwd)
        `in`.putExtra(ContentCommon.STR_CAMERA_TYPE, CameraType)
        progressBar!!.visibility = View.VISIBLE
        SystemValue.deviceName = strUser
        SystemValue.deviceId = strDID
        SystemValue.devicePass = strPwd
        BridgeService.setIpcamClientInterface(this)
        NativeCaller.Init()
        Thread(StartPPPPThread()).start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {//Intent()
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            val bundle = data.extras
            val scanResult = bundle!!.getString("result")
            didEdit!!.setText(scanResult)
        }
    }

    val infoSSID: String
        get() {
            info = manager!!.connectionInfo
            return info!!.getSSID()
        }
    val infoIp: Int
        get() {
            info = manager!!.connectionInfo
            return info!!.getIpAddress()
        }
    private val PPPPMsgHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            val bd = msg.data
            val msgParam = bd.getInt(STR_MSG_PARAM)
            val msgType = msg.what
            Log.i("aaa", "====$msgType--msgParam:$msgParam")
            val did = bd.getString(STR_DID)
            var resid = R.string.pppp_status_connecting
            when (msgType) {
                ContentCommon.PPPP_MSG_TYPE_PPPP_STATUS -> {
                    when (msgParam) {
                        ContentCommon.PPPP_STATUS_CONNECTING -> {
                            resid = R.string.pppp_status_connecting
                            progressBar!!.visibility = View.VISIBLE
                            tag = 2
                        }
                        ContentCommon.PPPP_STATUS_CONNECT_FAILED -> {
                            resid = R.string.pppp_status_connect_failed
                            progressBar!!.visibility = View.GONE
                            tag = 0
                        }
                        ContentCommon.PPPP_STATUS_DISCONNECT -> {
                            resid = R.string.pppp_status_disconnect
                            progressBar!!.visibility = View.GONE
                            tag = 0
                        }
                        ContentCommon.PPPP_STATUS_INITIALING -> {
                            resid = R.string.pppp_status_initialing
                            progressBar!!.visibility = View.VISIBLE
                            tag = 2
                        }
                        ContentCommon.PPPP_STATUS_INVALID_ID -> {
                            resid = R.string.pppp_status_invalid_id
                            progressBar!!.visibility = View.GONE
                            tag = 0
                        }
                        ContentCommon.PPPP_STATUS_ON_LINE -> {
                            resid = R.string.pppp_status_online
                            progressBar!!.visibility = View.GONE
                            //摄像机在线之后读取摄像机类型
                            val cmd = ("get_status.cgi?loginuse=admin&loginpas=" + SystemValue.devicePass
                                    + "&user=admin&pwd=" + SystemValue.devicePass)
                            NativeCaller.TransferMessage(did, cmd, 1)
                            tag = 1
                        }
                        ContentCommon.PPPP_STATUS_DEVICE_NOT_ON_LINE -> {
                            resid = R.string.device_not_on_line
                            progressBar!!.visibility = View.GONE
                            tag = 0
                        }
                        ContentCommon.PPPP_STATUS_CONNECT_TIMEOUT -> {
                            resid = R.string.pppp_status_connect_timeout
                            progressBar!!.visibility = View.GONE
                            tag = 0
                        }
                        ContentCommon.PPPP_STATUS_CONNECT_ERRER -> {
                            resid = R.string.pppp_status_pwd_error
                            progressBar!!.visibility = View.GONE
                            tag = 0
                        }
                        else -> resid = R.string.pppp_status_unknown
                    }
                    textView_top_show!!.text = resources.getString(resid)
                    if (msgParam == ContentCommon.PPPP_STATUS_ON_LINE) {
                        NativeCaller.PPPPGetSystemParams(did, ContentCommon.MSG_TYPE_GET_PARAMS)
                        NativeCaller.TransferMessage(did,
                                "get_factory_param.cgi?loginuse=admin&loginpas="
                                        + SystemValue.devicePass + "&user=admin&pwd=" + SystemValue.devicePass, 1) // 检测push值
                    }
                    if (msgParam == ContentCommon.PPPP_STATUS_INVALID_ID || msgParam == ContentCommon.PPPP_STATUS_CONNECT_FAILED || msgParam == ContentCommon.PPPP_STATUS_DEVICE_NOT_ON_LINE || msgParam == ContentCommon.PPPP_STATUS_CONNECT_TIMEOUT || msgParam == ContentCommon.PPPP_STATUS_CONNECT_ERRER) {
                        NativeCaller.StopPPPP(did)
                    }
                }
                ContentCommon.PPPP_MSG_TYPE_PPPP_MODE -> {
                }
                ContentCommon.PPPP_MSG_VSNET_NOTIFY_TYPE_VUIDSTATUS -> {
                    when (msgParam) {
                        ContentCommon.PPPP_STATUS_CONNECTING -> {
                            resid = R.string.pppp_status_connecting
                            progressBar!!.visibility = View.VISIBLE
                            tag = 2
                        }
                        ContentCommon.PPPP_STATUS_CONNECT_FAILED -> {
                            resid = R.string.pppp_status_connect_failed
                            progressBar!!.visibility = View.GONE
                            tag = 0
                        }
                        ContentCommon.PPPP_STATUS_DISCONNECT -> {
                            resid = R.string.pppp_status_disconnect
                            progressBar!!.visibility = View.GONE
                            tag = 0
                        }
                        ContentCommon.PPPP_STATUS_INITIALING -> {
                            resid = R.string.pppp_status_initialing
                            progressBar!!.visibility = View.VISIBLE
                            tag = 2
                        }
                        ContentCommon.PPPP_STATUS_INVALID_ID -> {
                            resid = R.string.pppp_status_invalid_id
                            progressBar!!.visibility = View.GONE
                            tag = 0
                        }
                        ContentCommon.PPPP_STATUS_ON_LINE -> {
                            resid = R.string.pppp_status_online
                            progressBar!!.visibility = View.GONE
                            //摄像机在线之后读取摄像机类型
                            val cmd = ("get_status.cgi?loginuse=admin&loginpas=" + SystemValue.devicePass
                                    + "&user=admin&pwd=" + SystemValue.devicePass)
                            NativeCaller.TransferMessage(did, cmd, 1)
                            tag = 1
                        }
                        ContentCommon.PPPP_STATUS_DEVICE_NOT_ON_LINE -> {
                            resid = R.string.device_not_on_line
                            progressBar!!.visibility = View.GONE
                            tag = 0
                        }
                        ContentCommon.PPPP_STATUS_CONNECT_TIMEOUT -> {
                            resid = R.string.pppp_status_connect_timeout
                            progressBar!!.visibility = View.GONE
                            tag = 0
                        }
                        ContentCommon.PPPP_STATUS_CONNECT_ERRER -> {
                            resid = R.string.pppp_status_pwd_error
                            progressBar!!.visibility = View.GONE
                            tag = 0
                        }
                        ContentCommon.PPPP_STATUS_INVALID_VUID -> {
                            resid = R.string.pppp_status_invalid_id
                            progressBar!!.visibility = View.GONE
                            tag = 0
                        }
                        ContentCommon.PPPP_STATUS_ALLOT_VUID -> {
                        }
                        else -> resid = R.string.pppp_status_unknown
                    }
                    textView_top_show!!.text = resources.getString(resid)
                    if (msgParam == ContentCommon.PPPP_STATUS_ON_LINE) {
                        NativeCaller.PPPPGetSystemParams(did, ContentCommon.MSG_TYPE_GET_PARAMS)
                        NativeCaller.TransferMessage(did,
                                "get_factory_param.cgi?loginuse=admin&loginpas="
                                        + SystemValue.devicePass + "&user=admin&pwd=" + SystemValue.devicePass, 1) // 检测push值
                    }
                    if (msgParam == ContentCommon.PPPP_STATUS_INVALID_ID || msgParam == ContentCommon.PPPP_STATUS_CONNECT_FAILED || msgParam == ContentCommon.PPPP_STATUS_DEVICE_NOT_ON_LINE || msgParam == ContentCommon.PPPP_STATUS_CONNECT_TIMEOUT || msgParam == ContentCommon.PPPP_STATUS_CONNECT_ERRER) {
                        NativeCaller.StopPPPP(did)
                    }
                }
            }
        }
    }

    override fun BSMsgNotifyData(did: String, type: Int, param: Int) {
        Log.d("ip", "type:$type param:$param")
        val bd = Bundle()
        val msg = PPPPMsgHandler.obtainMessage()
        msg.what = type
        bd.putInt(STR_MSG_PARAM, param)
        bd.putString(STR_DID, did)
        msg.data = bd
        PPPPMsgHandler.sendMessage(msg)
        if (type == ContentCommon.PPPP_MSG_TYPE_PPPP_STATUS) {
            intentbrod!!.putExtra("ifdrop", param)
            sendBroadcast(intentbrod)
        }
    }

    override fun BSSnapshotNotify(did: String, bImage: ByteArray, len: Int) {
        // TODO Auto-generated method stub
        Log.i("ip", "BSSnapshotNotify---len$len")
    }

    override fun callBackUserParams(did: String, user1: String, pwd1: String,
                                    user2: String, pwd2: String, user3: String, pwd3: String) {
        // TODO Auto-generated method stub
    }

    override fun CameraStatus(did: String, status: Int) {}
    override fun onItemSelected(arg0: AdapterView<*>?, arg1: View, arg2: Int,
                                arg3: Long) {
        // TODO Auto-generated method stub
    }

    override fun onNothingSelected(arg0: AdapterView<*>?) {
        // TODO Auto-generated method stub
    }

    override fun CallBackGetStatus(did: String, resultPbuf: String, cmd: Int) {
        // TODO Auto-generated method stub
        if (cmd == ContentCommon.CGI_IEGET_STATUS) {
            val cameraType = spitValue(resultPbuf, "upnp_status=")
            val cameraSysver = MyStringUtils.spitValue(resultPbuf, "sys_ver=")
            MySharedPreferenceUtil.saveSystemVer(this@AddCameraActivity, did, cameraSysver)
            val intType = cameraType.toInt()
            var type14 = (intType shr 16) and 1 // 14位 来判断是否报警联动摄像机
            if (intType == 2147483647) { // 特殊值
                type14 = 0
            }
            if (type14 == 1) {
                updateListHandler.sendEmptyMessage(2)
            }
        }
    }

    private fun spitValue(name: String, tag: String): String {
        val strs = name.split(";".toRegex()).toTypedArray()
        for (i in strs.indices) {
            var str1 = strs[i].trim { it <= ' ' }
            if (str1.startsWith("var")) {
                str1 = str1.substring(4, str1.length)
            }
            if (str1.startsWith(tag)) {
                return str1.substring(str1.indexOf("=") + 1)
            }
        }
        return "-1"
    }

    companion object {
        private const val SEARCH_TIME = 3000
        private const val STR_DID = "did"
        private const val STR_MSG_PARAM = "msgparam"
        fun int2ip(ipInt: Long): String {
            val sb = StringBuilder()
            sb.append(ipInt and 0xFF).append(".")
            sb.append(ipInt shr 8 and 0xFF).append(".")
            sb.append(ipInt shr 16 and 0xFF).append(".")
            sb.append(ipInt shr 24 and 0xFF)
            return sb.toString()
        }
    }
}