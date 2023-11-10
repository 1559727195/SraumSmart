package com.massky.sraum.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.massky.sraum.R
import com.massky.sraum.User
import com.massky.sraum.Util.*
import com.massky.sraum.Utils.ApiHelper
import com.massky.sraum.activity.SearchGateWayActivity
import com.massky.sraum.base.BaseActivity
import com.massky.sraum.bean.GateWayInfoBean
import com.massky.sraum.myzxingbar.qrcodescanlib.CaptureActivity
import com.massky.sraum.permissions.RxPermissions
import com.massky.sraum.service.MyService
import com.massky.sraum.tool.Constants
import com.massky.sraum.view.ClearEditText
import com.massky.sraum.widget.ApplicationContext
import com.yanzhenjie.statusview.StatusUtils
import com.yanzhenjie.statusview.StatusView
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by zhu on 2017/12/29.
 */
class LoginGateWayActivity : BaseActivity() {
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
    @BindView(R.id.scan_gateway)
    var scan_gateway: ImageView? = null

    @JvmField
    @BindView(R.id.search_gateway_btn)
    var search_gateway_btn: TextView? = null

    @JvmField
    @BindView(R.id.btn_login_gateway)
    var btn_login_gateway: Button? = null

    @JvmField
    @BindView(R.id.edit_wangguan_id)
    var edit_gateway_id: ClearEditText? = null

    @JvmField
    @BindView(R.id.edit_password_gateway)
    var edit_password_gateway: ClearEditText? = null

    @JvmField
    @BindView(R.id.eyeimageview_id_gateway)
    var eyeimageview_id_gateway: ImageView? = null
    private var eyeUtil: EyeUtil? = null

    //sraum_login
    override fun viewId(): Int { //
        return R.layout.login_gateway
    }

    override fun onView() { //
//        if (!StatusUtils.setStatusBarDarkFont(this, true)) {// Dark font for StatusBar.
//            statusView.setBackgroundColor(Color.BLACK);
//        }
        StatusUtils.setFullToStatusBar(this) // StatusBar.
        toolbar_txt!!.text = "登录网关"
        scan_gateway!!.setOnClickListener(this)
        UDPClient.activity_destroy(false)
        val intent = Intent(this, MyService::class.java)
        startService(intent)
        //        udp_searchGateway();//初始化UDP，和TCP连接...
        //
        val number = SharedPreferencesUtil.getData(this@LoginGateWayActivity, "number",
                "") as String
        val password = SharedPreferencesUtil.getData(this@LoginGateWayActivity, "password",
                "") as String
        if (number != null) edit_gateway_id!!.setText(number)
        if (password != null) edit_password_gateway!!.setText(password)
    }

    /**
     * 登录网关
     */
    private fun init_login_gateway() {
//        String edit_gateway_id_str =  edit_gateway_id.getText().toString();
//        String edit_password_gateway_str = edit_password_gateway.getText().toString();
        val gateWayInfoBean = queryGateWayInfo() ?: return
        val number = gateWayInfoBean.number
        //密码是后6位
        val password = StringBuffer()
        val strs = number.split(":".toRegex()).toTypedArray()
        for (i in strs.size - 4 until strs.size) {
            password.append(strs[i])
        }
        //密码是后6位
//        String password = number.substring(number.length() - 6);
        val map = HashMap<Any?, Any?>()
        map["command"] = ApiHelper.Sraum_Login //sraum_verifySocket
        map["number"] = number //sraum_verifySocket
        map["password"] = password //系统分配用户
        //                    sraum_send_tcp(map,"sraum_verifySocket");//认证有效的TCP链接
        MyService.getInstance().sraum_send_tcp(map, ApiHelper.Sraum_Login)
    }

    /**
     * 解析获取的user
     *
     * @param intent
     */
    private fun init_user_from_tcp(intent: Intent): User {
        val tcpreceiver = intent.getStringExtra("strcontent")
        //                ToastUtil.showToast(context, "tcpreceiver:" + tcpreceiver);
        //解析json数据
        return GsonBuilder().registerTypeAdapterFactory(
                NullStringToEmptyAdapterFactory<Any?>()).create().fromJson(tcpreceiver, User::class.java)
    }

    /* // command:命令标识 sraum_searchFGateway

    private void udp_searchGateway() {
        Map map = new HashMap();
        map.put("command", ApiHelper.Sraum_SearchGateway);
//                send_udp(new Gson().toJson(map),"sraum_searchGateway");
        UDPClient.initUdp(new Gson().toJson(map), ApiHelper.Sraum_SearchGateway, new ICallback() {
            @Override
            public void process(final Object data) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        udp_sraum_setGatewayTime();
//                        ToastUtil.showToast(Main_New_Activity.this,"sraum_searchGateway_success!");
                        Map map = (Map) data;
                        String command = (String) map.get("command");
                        String content = (String) map.get("content");
                        final User user = new GsonBuilder().registerTypeAdapterFactory(
                                new NullStringToEmptyAdapterFactory()).create().fromJson(content, User.class);//json字符串转换为对象
                        if (user == null) return;
//                        SharedPreferencesUtil.saveData(LoginGateWayActivity.this,"tcp_server_ip",user.ip);
                        //存储数据库，搜索网关信息表
                        insert_gateway_infor(user);
//                        init_tcp_connect();
                    }
                });
            }

            @Override
            public void error(String data) {//Socket close
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(LoginGateWayActivity.this, "网关断线或异常");
                    }
                });

            }//
        });
    }


     // 插入网关信息表
     //
     // @param user
     //
     //

    private void insert_gateway_infor(User user) {
//        MovieBean movie = new MovieBean();
//        movie.setTitle("这是第" + ++i + "条数据。");
//        DaoSession daoSession = ((MyApp) getApplication()).getDaoSession();
//        MovieBeanDao movieBeanDao = daoSession.getMovieBeanDao();
//        movieBeanDao.insert(movie);
//        queryMovieBean();
        GateWayInfoBean gateWayInfoBean = new GateWayInfoBean();
        gateWayInfoBean.setIp(user.ip);
        gateWayInfoBean.setMAC(user.MAC);
        gateWayInfoBean.setName(user.name);
        gateWayInfoBean.setNumber(user.number);
        gateWayInfoBean.setProto(user.proto);
        gateWayInfoBean.setStatus(user.status);
        gateWayInfoBean.setTimeStamp(user.timeStamp);
        DaoSession daoSession = ((ApplicationContext) getApplication()).getDaoSession();
//        MovieBeanDao movieBeanDao = daoSession.getMovieBeanDao();
//        movieBeanDao.insert(movie);
        GateWayInfoBeanDao gateWayInfoBeanDao = daoSession.getGateWayInfoBeanDao();
        gateWayInfoBeanDao.insert(gateWayInfoBean);
    }
*/
    // 查找GateWayInfoMation
    private fun queryGateWayInfo(): GateWayInfoBean {
        //查询某条数据是否存在
        val daoSession = (application as ApplicationContext).daoSession
        val gateWayInfoBeanDao = daoSession.gateWayInfoBeanDao
        val beanQuery = gateWayInfoBeanDao.queryBuilder()
                .build()
        //        if (gateWayInfoBean != null) {
//            Toast.makeText(this, "数据查询成功", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(this, "这条数据不存在", Toast.LENGTH_SHORT).show();
//        }
        return beanQuery.unique()
        //        queryMovieBean();
    }

    /**
     * 初始化TCP连接
     */
    private fun init_tcp_connect() {
//        final  String ip = (String) SharedPreferencesUtil.getData(LoginGateWayActivity.this,"tcp_server_ip","");
        val gateWayInfoBean = queryGateWayInfo() ?: return
        val ip = gateWayInfoBean.ip //192.168.169.42
        Thread {
            MyService.getInstance().connectTCP(ip, object : IConnectTcpback {
                //连接tcpServer成功
                override fun process() {
                    runOnUiThread {
                        //ToastUtil.showToast(LoginGateWayActivity.this, "连接TCPServer成功");
                        sraum_verifySocket() //认证有效的TCP链接 (APP ->网关)
                    }
                }

                override fun error(connect_ctp: Int) {
                    runOnUiThread {
                        if (connect_ctp >= 0) { //去主动，网关断线后，每隔10s去连接。
                            // 收到异常，重连，没收到，心跳之后，第一步，再次发心跳。超时5s，再次收到异常，显示网关断线。去连网关。
                            Handler().postDelayed({ init_tcp_connect() }, 10000) //10s
                        } else { //退出后，界面相应变化，网关异常断线，显示断线，不能直接退出。
                            ToastUtil.showToast(this@LoginGateWayActivity, "显示网关断线。去连网关")
                        }
                    }
                }
            }, object : ICallback {
                override fun process(data: Any) {}
                override fun error(data: String) {
                    //收到tcp服务端断线
                    init_tcp_connect() //网关断线后，每隔10s去连接。
                }
            })
        }.start()
    }

    /***
     *
     * //认证有效的TCP链接（APP-》网关）
     */
    private fun sraum_verifySocket() {
        val map= HashMap<Any?, Any?>()
        map["command"] = ApiHelper.Sraum_VerifySocket //sraum_verifySocket
        map["user"] = "sraum" //系统分配用户
        val time = Timeuti.getTime()
        map["timeStamp"] = time
        map["signature"] = MD5Util.md5("sraummassky_gw2_6206$time")
        //                    sraum_send_tcp(map,"sraum_verifySocket");//认证有效的TCP链接
        MyService.getInstance().sraum_send_tcp(map, ApiHelper.Sraum_VerifySocket)
    }

    override fun onEvent() {
        back!!.setOnClickListener(this)
        search_gateway_btn!!.setOnClickListener(this)
        btn_login_gateway!!.setOnClickListener(this)
        eyeimageview_id_gateway!!.setOnClickListener(this)
        eyeUtil = EyeUtil(this@LoginGateWayActivity, eyeimageview_id_gateway, edit_password_gateway, true)
    }

    override fun onData() {}

    /**
     * command:命令标识 sraum_searchFGateway
     */
    private fun udp_sraum_setGatewayTime() {
        val foo = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
        println("foo:" + foo.format(Date()))
        val time = foo.format(Date())
        val map= HashMap<Any?, Any?>()
        map["command"] = ApiHelper.Sraum_SetGatewayTime
        map["time"] = time
        //        send_udp(new Gson().toJson(map), "sraum_setGatewayTime");

//        UDPClient.activity_destroy(false);
        UDPClient.initUdp(Gson().toJson(map), ApiHelper.Sraum_SetGatewayTime, object : ICallback {
            override fun process(data: Any) {
                runOnUiThread {
                    ToastUtil.showToast(this@LoginGateWayActivity, "sraum_setGatewayTime_success!")
                    //去连接TCPServer
                }
            }

            override fun error(data: String) {}
        })
    }

    private fun init_permissions() {

        // 清空图片缓存，包括裁剪、压缩后的图片 注意:必须要在上传完成后调用 必须要获取权限
        val permissions = RxPermissions(this)
        permissions.request(Manifest.permission.CAMERA).subscribe(object : Observer<Boolean?> {
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(aBoolean: Boolean?) {}
            override fun onError(e: Throwable) {}
            override fun onComplete() {
                val openCameraIntent = Intent(this@LoginGateWayActivity, CaptureActivity::class.java)
                startActivityForResult(openCameraIntent, Constants.SCAN_REQUEST_CODE)
            }
        })
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.back -> finish()
            R.id.scan_gateway -> {
                init_permissions()
            }
            R.id.search_gateway_btn -> {
                val searchGateWayIntent = Intent(this@LoginGateWayActivity, SearchGateWayActivity::class.java)
                startActivityForResult(searchGateWayIntent, Constants.SEARCH_GATEGAY)
            }
            R.id.btn_login_gateway -> {
                //                udp_searchGateway();
//                startActivity(new Intent(LoginGateWayActivity.this,MainGateWayActivity.class));
                val edit_gateway_id_str = edit_gateway_id!!.text.toString()
                val edit_password_gateway_str = edit_password_gateway!!.text.toString()
                if (edit_gateway_id_str == "" || edit_password_gateway_str == "") {
                    ToastUtil.showDelToast(this@LoginGateWayActivity, "网关编号或网关" +
                            "密码不能为空")
                } else init_tcp_connect() //去连接tcp
            }
            R.id.eyeimageview_id_gateway -> eyeUtil!!.EyeStatus()
        }
    }



    override fun onDestroy() {
        super.onDestroy()
        //        UDPClient.activity_destroy(true);//udp线程被杀死,暂时不能被杀死
    }

    /**
     * 二维码扫描回调操作
     */
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.SCAN_REQUEST_CODE) {
            camera_receive(data)
        } else if (resultCode == Activity.RESULT_OK && requestCode == Constants.SEARCH_GATEGAY) {
            search_gate_way_receive(data)
        }
    }

    /*
    * 显示搜出来的网关ID和网关密码
    * */
    private fun search_gate_way_receive(data: Intent?) {
//        String edit_gateway_id_str = edit_gateway_id.getText().toString();
//        String edit_password_gateway_str = edit_password_gateway.getText().toString();
        val gateWayInfoBean = queryGateWayInfo() ?: return
        val number = gateWayInfoBean.number
        //密码是后6位
        val password = number.substring(number.length - 6)
        edit_gateway_id!!.setText(number)
        edit_password_gateway!!.setText(password)
        SharedPreferencesUtil.saveData(this@LoginGateWayActivity, "number", number)
        SharedPreferencesUtil.saveData(this@LoginGateWayActivity, "password", password)
    }

    private fun camera_receive(data: Intent?) {
        val result = data!!.extras!!.getString("result")
        if (TextUtils.isEmpty(result)) //ab42bc2fa4223430774240259671db94
            return
        val key = "masskysraum-6206" //masskysraum-6206
        // 解密
        var DeString: String? = null
        try {
//                    content = "0a4ab23ad13aac565069283aac3882e5";
            DeString = AES.Encrypt(result, key)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (DeString == null) {
            ToastUtil.showToast(this@LoginGateWayActivity, "非本系统二维码")
        } else {
            //搜索网关信息 ，udp->tcp
            udp_searchGateway(DeString)
        }
    }

    /**
     * command:命令标识 sraum_searchFGateway
     *
     * @param number
     */
    private fun udp_searchGateway(number: String) {
        val map = HashMap<Any?, Any?>()
        map["command"] = ApiHelper.Sraum_SearchGateway
        //number：网关编号，唯一编号 网关的MAC地址去掉冒号，所有字母小写；若为””，则搜索所有网关
        map["number"] = number
        UDPClient.initUdp(Gson().toJson(map), ApiHelper.Sraum_SearchGateway, object : ICallback {
            override fun process(data: Any) {
                runOnUiThread(Runnable {
                    udp_sraum_setGatewayTime()
                    //                        ToastUtil.showToast(Main_New_Activity.this,"sraum_searchGateway_success!");
                    val map = data as Map<*, *>
                    val command = map["command"] as String?
                    val content = map["content"] as String?
                    val user = GsonBuilder().registerTypeAdapterFactory(
                            NullStringToEmptyAdapterFactory<Any?>()).create().fromJson(content, User::class.java)
                            ?: return@Runnable //json字符串转换为对象
                    //                        SharedPreferencesUtil.saveData(LoginGateWayActivity.this,"tcp_server_ip",user.ip);
                    //存储数据库，搜索网关信息表
                    deleteGateWay()
                    insert_gateway_infor(user)
                    //init_tcp_connect();
                    //
                    search_gate_way_receive(null) //填充网关ID和密码
                })
            }

            override fun error(data: String) { //Socket close
                runOnUiThread { ToastUtil.showToast(this@LoginGateWayActivity, "网关断线或异常") }
            } //
        })
    }

    private fun deleteGateWay() {
        val daoSession = (application as ApplicationContext).daoSession
        val gateWayInfoBeanDao = daoSession.gateWayInfoBeanDao
        gateWayInfoBeanDao.deleteAll()
        //        Query<MovieBean> query = movieBeanDao.queryBuilder()
//                .where(MovieBeanDao.Properties.Id.eq(10))
//                .build();
//        Query<GateWayInfoBean> query = gateWayInfoBeanDao.queryBuilder()
//                .build();
////        MovieBean movieBean = query.unique();
////        if (movieBean != null) {
////            movieBeanDao.delete(movieBean);
////            Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
////        } else {
////            Toast.makeText(this, "此条数据不存在", Toast.LENGTH_SHORT).show();
////        }
//        List<GateWayInfoBean> beanList = query.list();
//        if (beanList != null) {
//            for (GateWayInfoBean movie : beanList) {
////                movie.setTitle("这条是更新的数据。");
////                movieBeanDao.update(movie);
//                gateWayInfoBeanDao.delete(movie);
//            }
//        }
    }

    /**
     * 插入网关信息表
     *
     * @param user
     */
    private fun insert_gateway_infor(user: User) {
//        MovieBean movie = new MovieBean();
//        movie.setTitle("这是第" + ++i + "条数据。");
//        DaoSession daoSession = ((MyApp) getApplication()).getDaoSession();
//        MovieBeanDao movieBeanDao = daoSession.getMovieBeanDao();
//        movieBeanDao.insert(movie);
//        queryMovieBean();
        val gateWayInfoBean = GateWayInfoBean()
        gateWayInfoBean.ip = user.ip
        gateWayInfoBean.mac = user.MAC
        gateWayInfoBean.name = user.name
        gateWayInfoBean.number = user.number
        gateWayInfoBean.proto = user.proto
        gateWayInfoBean.status = user.status
        gateWayInfoBean.timeStamp = user.timeStamp
        val daoSession = (application as ApplicationContext).daoSession
        //        MovieBeanDao movieBeanDao = daoSession.getMovieBeanDao();
//        movieBeanDao.insert(movie);
        val gateWayInfoBeanDao = daoSession.gateWayInfoBeanDao
        gateWayInfoBeanDao.insert(gateWayInfoBean)
    }

    companion object {
        const val MESSAGE_SRAUM_VERIFI_SOCKET = "com.massky.sraumsmarthome.sraum_verifySocket"
        const val MESSAGE_SRAUM_LOGIN = "com.massky.sraumsmarthome.sraum_login"
    }
}