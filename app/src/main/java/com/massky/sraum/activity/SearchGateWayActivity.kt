package com.massky.sraum.activity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.*
import butterknife.BindView
import com.massky.sraum.EditGateWayResultActivity
import com.massky.sraum.R
import com.massky.sraum.Util.ByteUtils
import com.massky.sraum.Util.ToastUtil
import com.massky.sraum.activity.SearchGateWayActivity
import com.massky.sraum.adapter.ShowUdpServerAdapter
import com.massky.sraum.base.BaseActivity
import com.massky.sraum.fragment.SearchDialogFragment
import com.massky.sraum.view.SycleSearchView
import com.yanzhenjie.statusview.StatusUtils
import com.yanzhenjie.statusview.StatusView
import ru.alexbykov.nopermission.PermissionHelper
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.SocketException
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Created by zhu on 2018/1/2.
 */
class SearchGateWayActivity : BaseActivity() {
    @JvmField
    @BindView(R.id.status_view)
    var statusView: StatusView? = null

    @JvmField
    @BindView(R.id.back)
    var back: ImageView? = null

    @JvmField
    @BindView(R.id.toolbar_txt)
    var toolbar_txt: TextView? = null

    @JvmField
    @BindView(R.id.sycle_search)
    var sycle_search: SycleSearchView? = null
    var test_start = false

    @JvmField
    @BindView(R.id.fangdajing)
    var fangdajing: ImageView? = null

    @JvmField
    @BindView(R.id.search_result)
    var search_result: LinearLayout? = null

    @JvmField
    @BindView(R.id.list_show_rev_item)
    var list_show_rev_item: ListView? = null

    @JvmField
    @BindView(R.id.search_txt)
    var search_txt: TextView? = null

    //    @BindView(R.id.list_show_rev_item_detail)
    //    ListView list_show_rev_item_detail;
    @JvmField
    @BindView(R.id.rel_list_show)
    var rel_list_show: RelativeLayout? = null

    @JvmField
    @BindView(R.id.goimage_id)
    var goimage_id: ImageView? = null

    @JvmField
    @BindView(R.id.detailimage_id)
    var detailimage_id: ImageView? = null

    @JvmField
    @BindView(R.id.stopimage_id)
    var stopimage_id: ImageView? = null
    var isFirst = true
    var t: WThread? = null
    private var n = 0
    private val flag = true
    private val buffer = ByteArray(MAX_DATA_PACKET_LENGTH)
    private var serSocFlag = false

    //添加一个boolean变量测量那个UDPServerSocket是否已经死掉，只有死掉的时候才能重新去发送UDPSocket
    private var UDPServerSocket_is_death = false
    var list_rev_udp = CopyOnWriteArrayList<String>() //存储来自udp服务器端的数据列表
    private var showUdpServerAdapter: ShowUdpServerAdapter? = null
    private var list: MutableList<String> = ArrayList()
    private val REQUEST_SCAN_WANGGUAN = 0x006
    private val REQUEST_SCAN_NO_WANGGUAN = 0x007
    private val REQUEST_SUBMIT_WANGGUAN = 0x009
    private var tiaozhuan_bool = false //跳转判断 = false
    private var istiaozhuan = false
    private var newFragment: SearchDialogFragment? = null
    private var lock: WifiManager.MulticastLock? = null
    private var permissionHelper: PermissionHelper? = null
    private var islocked = false
    override fun viewId(): Int {
        return R.layout.search_gate_act
    }

    override fun onView() {
        StatusUtils.setFullToStatusBar(this)
        init_wifi_quanxian()
        toolbar_txt!!.text = "搜索网关"
        fangdajing!!.setOnClickListener(this)
        serSocFlag = true
        startanim()
        search_txt!!.text = "正在搜索局域网"
        BroadCastUdp("021001000021F403").start()
        showUdpServerAdapter = ShowUdpServerAdapter(this@SearchGateWayActivity
                , list)

        /*
          这个是局部listview的监听事件
         */list_show_rev_item!!.adapter = showUdpServerAdapter
        list_show_rev_item!!.onItemClickListener = MyOnItemCLickListener()
        initDialog()
    }

    private fun init_wifi_quanxian() {
        permissionHelper = PermissionHelper(this)
        permissionHelper!!.check(
                Manifest.permission.CHANGE_WIFI_MULTICAST_STATE
        ).onSuccess { this.onSuccess() }.onDenied { onDenied() }.onNeverAskAgain { onNeverAskAgain() }.run()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        permissionHelper!!.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun onSuccess() {
        val manager = this.applicationContext
                .getSystemService(Context.WIFI_SERVICE) as WifiManager
        lock = manager.createMulticastLock("UDPwifi")
    }

    private fun onDenied() {
        ToastUtil.showToast(this, "权限被拒绝，10.0系统无法获取wifi锁")
    }

    private fun onNeverAskAgain() {
        ToastUtil.showToast(this, "权限被拒绝，10.0系统无法获取wifi锁,下次不会在询问了")
    }

    /*
     * 初始化dialog
     */
    private fun initDialog() {
        // TODO Auto-generated method stub
        newFragment = SearchDialogFragment.newInstance(this@SearchGateWayActivity, "", "",
                object : SearchDialogFragment.DialogClickListener {
                    override fun dialogDismiss() {
                        init_udp_search()
                    }
                })
         //初始化快配和搜索设备dialogFragment
        sendParamsInterfacer = newFragment
    }

    /*
     * 初始化udp搜索
     */
    private fun init_udp_search() {
        serSocFlag = true
        t!!.onResume()
        sycle_search!!.startsycle()
        search_txt!!.text = "正在搜索局域网"
        list_rev_udp = CopyOnWriteArrayList() //只有搜索完毕后，才能清空
        BroadCastUdp("021001000021F403").start() //重新去搜
    }

    /**
     * 给全屏窗体传参数
     */
    private var sendParamsInterfacer: SendParamsInterfacer? = null

    interface SendParamsInterfacer {
        fun sendparams(list: List<String>?)
    }

    /**
     * 展示全窗体dialog对话框
     */
    private fun show_dialog_fragment() {
        if (!newFragment!!.isAdded) { //DialogFragment.show()内部调用了FragmentTransaction.add()方法，所以调用DialogFragment.show()方法时候也可能
            val manager = supportFragmentManager
            val ft = manager.beginTransaction()
            ft.add(newFragment!!, "dialog")
            ft.commit()
        }
    }

    private inner class MyOnItemCLickListener : AdapterView.OnItemClickListener {
        override fun onItemClick(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
            //销毁activity返回到AddGateWayActivity
//            Intent intent = getIntent();
//            intent.putExtra("gateWayMac", list.get(position).toString());
//            intent.putExtra("act_flag", "Search");//我是SearchgatewayActivity
//            setResult(RESULT_OK, intent);
//            SearchGateWayActivity.this.finish();
            val intent = Intent(this@SearchGateWayActivity, EditGateWayResultActivity::class.java)
            intent.putExtra("gateid", list[position]) //跳转到编辑网关密码界面
            startActivity(intent)
            serSocFlag = false
            t!!.onStop()
        }
    }

    /*监听并开启动画展示*/
    private fun startanim() {
        goimage_id!!.setOnClickListener(this)
        detailimage_id!!.setOnClickListener(this)
        stopimage_id!!.setOnClickListener(this)
        sycle_search!!.startsycle()
        if (isFirst) {
            isFirst = false
            t = WThread()
            t!!.start()
        }
    }

    override fun onEvent() {
        back!!.setOnClickListener(this)
        sycle_search!!.setOnClickListener(this)
        search_result!!.setOnClickListener(this)
    }

    override fun onData() {}
    override fun onClick(v: View) {
        when (v.id) {
            R.id.back -> {
                val intent = intent
                istiaozhuan = true //表明正在跳转到其他界面
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
            R.id.sycle_search -> test_start = !test_start
            R.id.fangdajing -> {
            }
            R.id.search_result -> finish()
            R.id.goimage_id -> {
                t!!.onResume()
                islocked = false
                serSocFlag = true
                if (UDPServerSocket_is_death) {
                    sycle_search!!.startsycle()
                    list = ArrayList()
                    showUdpServerAdapter!!.clear()
                    search_txt!!.text = "正在搜索局域网"
                    list_rev_udp = CopyOnWriteArrayList() //只有搜索完毕后，才能清空
                    BroadCastUdp("021001000021F403").start() //重新去搜
                }
            }
            R.id.detailimage_id -> {
                serSocFlag = false
                t!!.onPause()
                if (list.size != 0) {
                    show_dialog_fragment()
                    if (sendParamsInterfacer != null) sendParamsInterfacer!!.sendparams(list)
                }
            }
            R.id.stopimage_id -> {
                serSocFlag = false
                islocked = false
                sycle_search!!.stopsycle()
                t!!.onPause()
            }
        }
    }

    /*自定义线程类实现开启，暂停等操作*/
    inner class WThread : Thread() {
        private val lock: Object
        var isPause: Boolean
        var isStop: Boolean
        var c: Int

        //暂停
        fun onPause() {
            if (!isPause) isPause = true
        }

        fun onWait() {
            synchronized(lock) {
                try {
                    lock.wait()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }

        fun onResume() {
            synchronized(lock) {
                isPause = false
                lock.notifyAll()
            }
        }

        fun onStop() {
            //如果线程处于wait状态，那么会唤醒它，但该中断也就被消耗了，无法捕捉到,退出操作会在下一个循环时实现
            //但如果线程处于running状态，那么该中断便可以被捕捉到
            isStop = true
            interrupt()
        }

        override fun run() {
            super.run()
            try {
                while (!isStop) {
                    //捕获中断
                    if (interrupted()) {
                        //结束中断
                        if (isStop) {
                            return
                            //如果中断不是由我们手动发出，那么就不予处理，直接交由更上层处理
                            //不应该生吞活剥
                        } else {
                            interrupt()
                            continue
                        }
                    }
                    n++
                    if (n == 13) {
                        n = 0
                    }
                    Log.e("robin debug", "n:$n")
                    if (isPause) onWait()
                    //lock.wait();需要在run中才能起作用
                    //模拟耗时操作
                    sleep(200)
                    val m = h.obtainMessage()
                    m.what = n
                    h.sendMessage(m)
                }
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }

        init {
            lock = Object()
            isPause = false
            isStop = false
            c = 1
        }
    }

    inner class BroadCastUdp(private val dataString: String) : Thread() {
        private var udpSocket: DatagramSocket? = null
        override fun run() {
            var dataPacket: DatagramPacket? = null
            try {
                udpSocket = DatagramSocket(9991)
                dataPacket = DatagramPacket(buffer, MAX_DATA_PACKET_LENGTH)
                //				byte[] data = dataString.getBytes();
                val data = ByteUtils.hexStringToBytes(dataString) //字符串转换为byte
                dataPacket.data = data
                dataPacket.length = data.size
                dataPacket.port = 9991
                val broadcastAddr: InetAddress
                broadcastAddr = InetAddress.getByName("255.255.255.255")
                dataPacket.address = broadcastAddr
            } catch (e: Exception) {
                Log.e("robin debug", e.toString())
            }
            // while( start ){
            try {
                udpSocket!!.send(dataPacket)
                udpSocket!!.close()
                //客户端UDP发送之后就开始监听，服务器端UDP返回数据
                if (islocked) return
                ReceivBroadCastUdp().start()
                //				sleep(10);
            } catch (e: Exception) {
                Log.e("robin debug", e.toString())
            }
            // }
        }

    }

    //处理接收到的UDPServer的数据
    var handler_udp_recev: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            val wanggguan_mac = msg.obj as String //021001001262a001ff1008313030385352677700000000643603
            //实时显示列表数据
            list_rev_udp.add(wanggguan_mac)
            //去除list集合中重复项的几种方法
            val hs = HashSet(list_rev_udp) //此时已经去掉重复的数据保存在hashset中
            val stringIterator: Iterator<String> = hs.iterator()
            list = ArrayList()
            for (i in hs.indices) {
                list.add(stringIterator.next())
            }
            showUdpServerAdapter!!.clear()
            showUdpServerAdapter!!.addAll(list)
        }
    }

    /**
     * 接收来自UDP服务器端发送过来,UDP 通信是应答模式，你发我收
     */
    inner class ReceivBroadCastUdp : Thread() {
        private var udpSocket: DatagramSocket? = null
        var udpPacket: DatagramPacket? = null
        var add = 0
        private var timer: Timer? = null
        private var task: TimerTask? = null
        private var UDPServer = false
        override fun run() {
            //搞个定时器10s后,关闭该UDPReceverSocket
            UDPServer = true
            UDPServerSocket_is_death = false
            initTimer()
            val data = ByteArray(256)
            try {
                udpSocket = DatagramSocket(8881) //服务器端UDP端口号，网关端口9991
                udpPacket = DatagramPacket(data, data.size)
            } catch (e1: SocketException) {
                e1.printStackTrace()
            }
            Log.e("udp", "start_udpSocket")
            while (UDPServer) {
                try {
                    if (lock != null) {
                        islocked = true
                        lock!!.acquire()
                    }
                    Log.e("udp", "start-receiver")
                    udpSocket!!.receive(udpPacket)
                } catch (e: Exception) {
                    islocked = false
                    Log.e("udp", "end-receiver")
                    if (lock != null) lock!!.release()
                }
                if (udpPacket != null) {
                    if (null != udpPacket!!.address) {
                        //02 1001 0012 62a001ff1008 313030385352677700000000643603
                        val codeString = ByteUtils.bytesToHexString(data, udpPacket!!.length)
                                ?: return
                        if (codeString.length > 22) {
                            val wanggguan_mac = codeString.substring(10, 22) //62a001ff1008
                            Log.e("zhu", "wanggguan_mac:$wanggguan_mac")
                            val message = Message()
                            message.obj = wanggguan_mac
                            handler_udp_recev.sendMessage(message)
                        }
                    }
                }
                if (lock != null) {
                    if (islocked) {
                        Log.e("udp", "end-receiver")
                        lock!!.release()
                        islocked = false
                    }
                }
            }
            Log.e("udp", "close_udpSocket")
            if (udpSocket != null) udpSocket!!.close()
        }

        /**
         * 初始化定时器
         */
        private fun initTimer() {
            if (timer == null) timer = Timer()
            //3min
            //关闭clientSocket即客户端socket
            if (task == null) task = object : TimerTask() {
                override fun run() {
                    add++
                    Log.e("robin debug", "add: $add")
                    if (add > 400) { //10s= 1000 * 10
                        try {
                            add = 0
                            closeTimerAndClientSocket()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else if (!serSocFlag) { //长连接倒计时3分钟未到，TcpServer退出，要停掉该定时器，并关闭clientSocket即客户端socket
                        try {
                            closeTimerAndClientSocket()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }

                /**
                 * 关闭定时器和socket客户端
                 *
                 * @throws IOException
                 */
                @Throws(IOException::class)
                private fun closeTimerAndClientSocket() {
                    if (timer != null) {
                        timer!!.cancel()
                        timer = null
                    }
                    if (task != null) {
                        task!!.cancel()
                        task = null
                    }
                    closeUDPServerSocket() //关闭clientSocket即客户端socket
                }
            }
            timer!!.schedule(task, 100, 25) // 1s后执行task,经过1ms再次执行
        }

        /**
         * 关闭UDPServerSocket服务器端
         */
        private fun closeUDPServerSocket() {
            islocked = false
            UDPServer = false
            UDPServerSocket_is_death = true
            if (sycle_search == null) return
            sycle_search!!.stopsycle()
            Log.e("udp", "close_udpSocket")
            if (udpSocket != null) udpSocket!!.close()
            t!!.onPause()
            if (list.size == 0) {
                runOnUiThread {
                    if (!istiaozhuan) {
                        search_txt!!.text = "未搜索到局域网"
                        //没有搜到局域网时，就跳进没有搜到局域网的界面
//                            Intent intent = new Intent(SearchGateWayActivity.this, GatedetailActivity.class);
//                            startActivityForResult(intent, REQUEST_SCAN_NO_WANGGUAN);
                        serSocFlag = false
                        t!!.onPause()
                    }
                }
            } else {
//                list = new ArrayList<>();
                runOnUiThread { search_txt!!.text = "" }
            }
        }
    }

    var h: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {}
    }

    override fun onDestroy() {
        super.onDestroy()
        serSocFlag = false
        t!!.onStop()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

//        WifiManager manager = (WifiManager) this.getApplicationContext()
//                .getSystemService(Context.WIFI_SERVICE);
//        lock = manager.createMulticastLock("test wifi");


        // 扫描二维码/条码回传
        if (requestCode == REQUEST_SCAN_WANGGUAN && resultCode == Activity.RESULT_OK) {
            t!!.onResume()
            serSocFlag = true
            if (UDPServerSocket_is_death) {
                list_rev_udp = CopyOnWriteArrayList() //只有搜索完毕后，才能清空
                search_txt!!.text = "正在搜索局域网"
                BroadCastUdp("021001000021F403").start() //重新去搜
            }
        } else if (requestCode == REQUEST_SUBMIT_WANGGUAN && resultCode == Activity.RESULT_OK) {
            val intent = intent
            setResult(Activity.RESULT_OK, intent)
            finish()
        } else if (requestCode == REQUEST_SCAN_NO_WANGGUAN && resultCode == Activity.RESULT_OK) {
            search_txt!!.text = "正在搜索局域网"
            t!!.onResume()
            serSocFlag = true
            list = ArrayList()
            list_rev_udp = CopyOnWriteArrayList() //只有搜索完毕后，才能清空
            showUdpServerAdapter!!.clear()
            BroadCastUdp("021001000021F403").start() //重新去搜
            //            list_show_rev_item_detail.setVisibility(View.INVISIBLE);
            rel_list_show!!.visibility = View.VISIBLE
            //            titlecen_id.setVisibility(View.GONE);
        }
    }

    override fun onBackPressed() {
        if (!tiaozhuan_bool) {
            istiaozhuan = true //表明正在跳转到其他界面
            val intent = intent
            setResult(Activity.RESULT_OK, intent)
            finish()
        } else {
            tiaozhuan_bool = false
            //            list_show_rev_item_detail.setVisibility(View.INVISIBLE);
            rel_list_show!!.visibility = View.VISIBLE
            //            titlecen_id.setVisibility(View.GONE);
//            titlecen_id.setText("");
            serSocFlag = true
            t!!.onResume()
            list = ArrayList()
            showUdpServerAdapter!!.clear()
            search_txt!!.text = "正在搜索局域网"
            list_rev_udp = CopyOnWriteArrayList() //只有搜索完毕后，才能清空
            BroadCastUdp("021001000021F403").start() //重新去搜
        }
    }

    private fun end_activity() {
        val intent = Intent()
        val bundle = Bundle()
        //                        bundle.putString("result", code);
        intent.putExtras(bundle)
        this@SearchGateWayActivity.setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun end_search_gateway_ui() {
        fangdajing!!.visibility = View.VISIBLE
        sycle_search!!.stopsycle()
        sycle_search!!.visibility = View.GONE
        search_result!!.visibility = View.VISIBLE
    }

    companion object {
        private const val MAX_DATA_PACKET_LENGTH = 1400 //报文长度
    }
}