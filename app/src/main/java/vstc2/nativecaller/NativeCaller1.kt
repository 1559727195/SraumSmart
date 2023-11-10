package vstc2.nativecaller

import android.content.Context

object NativeCaller1 {
    external fun PPPPAlarmSetting(did: String?, alarm_audio: Int,
                                  motion_armed: Int, motion_sensitivity: Int, input_armed: Int,
                                  ioin_level: Int, iolinkage: Int, ioout_level: Int, alarmpresetsit: Int,
                                  mail: Int, snapshot: Int, record: Int, upload_interval: Int,
                                  schedule_enable: Int, schedule_sun_0: Int, schedule_sun_1: Int,
                                  schedule_sun_2: Int, schedule_mon_0: Int, schedule_mon_1: Int,
                                  schedule_mon_2: Int, schedule_tue_0: Int, schedule_tue_1: Int,
                                  schedule_tue_2: Int, schedule_wed_0: Int, schedule_wed_1: Int,
                                  schedule_wed_2: Int, schedule_thu_0: Int, schedule_thu_1: Int,
                                  schedule_thu_2: Int, schedule_fri_0: Int, schedule_fri_1: Int,
                                  schedule_fri_2: Int, schedule_sat_0: Int, schedule_sat_1: Int,
                                  schedule_sat_2: Int, defense_plan1: Int, defense_plan2: Int,
                                  defense_plan3: Int, defense_plan4: Int, defense_plan5: Int,
                                  defense_plan6: Int, defense_plan7: Int, defense_plan8: Int,
                                  defense_plan9: Int, defense_plan10: Int, defense_plan11: Int,
                                  defense_plan12: Int, defense_plan13: Int, defense_plan14: Int,
                                  defense_plan15: Int, defense_plan16: Int, defense_plan17: Int,
                                  defense_plan18: Int, defense_plan19: Int, defense_plan20: Int,
                                  defense_plan21: Int, remind_rate: Int): Int

    external fun RecordLocal(uid: String?, path: String?, bRecordLocal: Int): Int // 0解码后的数据，1全部数据
    external fun PPPPInitialOther(svr: String?)
    external fun SetAPPDataPath(path: String?)
    external fun UpgradeFirmware(did: String?, servPath: String?,
                                 filePath: String?, type: Int)

    external fun SetSensorStatus(did: String?, status: Int): Int // set_sensorstatus.cgi
    external fun DeleSensor(did: String?, status: Int): Int // del_sensor.cgi
    external fun EditSensor(did: String?, status: Int, name: String?): Int // set_sensorname.cgi
    external fun SetSensorPrest(did: String?, preset: Int, sensorid: Int): Int // set_sensor_preset.cgi
    external fun TransferMessage(did: String?, msg: String?, len: Int): Int

    /*开启局域网搜索
	 */
    external fun StartSearch()

    /*关闭局域网搜索
	 */
    external fun StopSearch()
    external fun Init()
    external fun Free()
    external fun FormatSD(did: String?)
    external fun StartPPPP(did: String?, user: String?, pwd: String?,
                           bEnableLanSearch: Int, accountname: String?, p2pVer: Int): Int

    external fun StartPPPPExt(did: String?, user: String?, pwd: String?,
                              bEnableLanSearch: Int, accountname: String?, svr_no: String?, p2pVer: Int): Int

    external fun StopPPPP(did: String?): Int
    external fun StartPPPPLivestream(did: String?, streamid: Int,
                                     substreamid: Int): Int

    external fun StopPPPPLivestream(did: String?): Int

    //硬解接口
    external fun SetHardCodeing(did: String?, IsSupport: Int): Int
    external fun PPPPPTZControl(did: String?, command: Int): Int
    external fun PPPPCameraControl(did: String?, param: Int, value: Int): Int
    external fun PPPPGetCGI(did: String?, cgi: Int): Int
    external fun PPPPStartAudio(did: String?): Int
    external fun PPPPStopAudio(did: String?): Int
    external fun PPPPStartTalk(did: String?): Int
    external fun PPPPStartTalk2(did: String?, nEnable: Int): Int
    external fun PPPPStopTalk(did: String?): Int
    external fun PPPPTalkAudioData(did: String?, data: ByteArray?, len: Int): Int
    external fun PPPPNetworkDetect(): Int
    external fun PPPPInitial(svr: String?)
    external fun PPPPSetCallbackContext(`object`: Context?): Int

    /**
     * 初化回调接收服务
     * @param   object:       java层接收回调的Service
     * @param     version         库版本号(通过GetVersion获取,如果是值是-1代表使用的是最新版本)
     * @return    1:成功调用接口
     */
    external fun PPPPSetCallbackContext2(`object`: Context?, version: Int): Int
    external fun PPPPRebootDevice(did: String?): Int
    external fun PPPPRestorFactory(did: String?): Int

    //public native static int StartPlayBack(String did, String filename,
    //		int offset, int picTag);
    external fun StartPlayBack(did: String?, filename: String?, offset: Int, size: Long, strCachePath: String?, sdkVersion: Int, isHD: Int): Int
    external fun StopPlayBack(did: String?): Int
    external fun PausePlayBack(did: String?, pause: Int): Int
    external fun PlayBackMovePos(did: String?, pos: Float): Long
    external fun SetPlayBackPos(did: String?, time: Long): Int
    external fun StrarRecordPlayBack(did: String?, filepath: String?): Int

    //add end
    external fun PPPPGetSDCardRecordFileList(did: String?,
                                             PageIndex: Int, PageSize: Int): Int

    external fun PPPPWifiSetting(did: String?, enable: Int,
                                 ssid: String?, channel: Int, mode: Int, authtype: Int, encryp: Int,
                                 keyformat: Int, defkey: Int, key1: String?, key2: String?, key3: String?,
                                 key4: String?, key1_bits: Int, key2_bits: Int, key3_bits: Int,
                                 key4_bits: Int, wpa_psk: String?): Int

    external fun PPPPNetworkSetting(did: String?, ipaddr: String?,
                                    netmask: String?, gateway: String?, dns1: String?, dns2: String?, dhcp: Int,
                                    port: Int, rtsport: Int): Int

    external fun PPPPUserSetting(did: String?, user1: String?,
                                 pwd1: String?, user2: String?, pwd2: String?, user3: String?, pwd3: String?): Int

    external fun PPPPDatetimeSetting(did: String?, now: Int, tz: Int,
                                     ntp_enable: Int, ntp_svr: String?): Int

    external fun PPPPDDNSSetting(did: String?, service: Int,
                                 user: String?, pwd: String?, host: String?, proxy_svr: String?,
                                 ddns_mode: Int, proxy_port: Int): Int

    external fun PPPPMailSetting(did: String?, svr: String?, port: Int,
                                 user: String?, pwd: String?, ssl: Int, sender: String?, receiver1: String?,
                                 receiver2: String?, receiver3: String?, receiver4: String?): Int

    external fun PPPPFtpSetting(did: String?, svr_ftp: String?,
                                user: String?, pwd: String?, dir: String?, port: Int, mode: Int,
                                upload_interval: Int): Int

    external fun PPPPPTZSetting(did: String?, led_mod: Int,
                                ptz_center_onstart: Int, ptz_run_times: Int, ptz_patrol_rate: Int,
                                ptz_patrul_up_rate: Int, ptz_patrol_down_rate: Int,
                                ptz_patrol_left_rate: Int, ptz_patrol_right_rate: Int,
                                disable_preset: Int): Int

    // public native static int PPPPAlarmSetting(String did, int motion_armed,
    // int motion_sensitivity, int input_armed, int ioin_level,
    // int iolinkage, int ioout_level, int alarmpresetsit, int mail,
    // int snapshot, int record, int upload_interval, int schedule_enable,
    // int schedule_sun_0, int schedule_sun_1, int schedule_sun_2,
    // int schedule_mon_0, int schedule_mon_1, int schedule_mon_2,
    // int schedule_tue_0, int schedule_tue_1, int schedule_tue_2,
    // int schedule_wed_0, int schedule_wed_1, int schedule_wed_2,
    // int schedule_thu_0, int schedule_thu_1, int schedule_thu_2,
    // int schedule_fri_0, int schedule_fri_1, int schedule_fri_2,
    // int schedule_sat_0, int schedule_sat_1, int schedule_sat_2);
    external fun PPPPSDRecordSetting(did: String?,
                                     record_cover_enable: Int, record_timer: Int, record_size: Int, record_chnl: Int,
                                     record_time_enable: Int, record_schedule_sun_0: Int,
                                     record_schedule_sun_1: Int, record_schedule_sun_2: Int,
                                     record_schedule_mon_0: Int, record_schedule_mon_1: Int,
                                     record_schedule_mon_2: Int, record_schedule_tue_0: Int,
                                     record_schedule_tue_1: Int, record_schedule_tue_2: Int,
                                     record_schedule_wed_0: Int, record_schedule_wed_1: Int,
                                     record_schedule_wed_2: Int, record_schedule_thu_0: Int,
                                     record_schedule_thu_1: Int, record_schedule_thu_2: Int,
                                     record_schedule_fri_0: Int, record_schedule_fri_1: Int,
                                     record_schedule_fri_2: Int, record_schedule_sat_0: Int,
                                     record_schedule_sat_1: Int, record_schedule_sat_2: Int, audio_enble: Int): Int

    external fun PPPPEverydaySetting(did: String?,
                                     record_cover_enable: Int, record_timer: Int, record_size: Int, record_chnl: Int,
                                     record_time_enable: Int, audio_enble: Int): Int

    external fun PPPPGetSystemParams(did: String?, paramType: Int): Int

    // takepicture
    external fun YUV4202RGB565(yuv: ByteArray?, rgb: ByteArray?, width: Int,
                               height: Int): Int

    external fun DecodeH264Frame(h264frame: ByteArray?, bIFrame: Int,
                                 yuvbuf: ByteArray?, length: Int, size: IntArray?): Int

    external fun ResetDecodeH264(): Int
    external fun FindProcessByName(process: String?): Int

    //inputbuff//原音频数据
    //length//原音频数据长度
    //outputbuff//转出来的音频数据
    //public native static int DecodeAudio(byte[] aData,int length,byte[] outbuf);
    //public native static int DecodeAudio(byte[] aData,int length, int isClean,int sample,int index,byte[] outbuf);
    //inputbuff//原音频数据
    //length//原音频数据长度
    //outputbuff//转出来的音频数据
    external fun DecodeAudio(aData: ByteArray?, length: Int, isClean: Int, sample: Int, index: Int): Int

    //不使用时释放掉调用
    external fun FreeDecodeAudio(): Int
    external fun YUV420SPTOYUV420P(SrcArray: ByteArray?, DstSrray: ByteArray?, ySize: Int)
    external fun YUV420SPTOYUV420POFFSET(SrcArray: ByteArray?, DstSrray: ByteArray?, ySize: Int, decYsize: Int)
    external fun YUV420OFFSET(SrcArray: ByteArray?, DstSrray: ByteArray?, ySize: Int, decYsize: Int)

    /**************************wifi低功耗设备端接口beg */ //置前台需要连接服务器
    external fun MagLowpowerDeviceConnect(jIP: String?): Int

    //置后台需要断开服务器
    external fun MagLowpowerDeviceDisconnect()

    //初化设备
    external fun MagLowpowerInitDevice(jdid: String?): Int

    //取设备的状态
    external fun MagLowpowerGetDeviceStatus(jdid: String?): Int

    //唤醒设备
    external fun MagLowpowerAwakenDevice(jdid: String?): Int

    // TODO: 2019-10-14 低功耗设备状态(jni__version >= 4665)
    /*
	-2:  p2p  连接fail  需要stop p2p然后再start p2p
	-1: 没有初始化（想要start）  不需要stop p2p可直接start p2p
	1: p2p  连接上
	0:  p2p  连接中
	*/
    external fun GetP2PConnetState(jdid: String?): Int

    /**
     * 保持设备激活
     * @param deviceIdentity 设备id
     * @param time      设备延时休眠时间不得少5秒
     */
    external fun MagLowpowerKeepDeviceActive(jdid: String?, time: Int): Int

    //移除节点节点，需要重新MagLowpowerInitDevice
    external fun MagLowpowerRemoveDevice(jdid: String?): Int

    /**
     * 立刻让设备休眠
     * @param deviceIdentity 设备id
     */
    external fun MagLowpowerSleepDevice(jdid: String?, time: Int): Int
    /**************************wifi低功耗设备端接口end */
    /**************************4G低功耗设备端接口end */ //置前台需要连接服务器
    external fun FlowDeviceConnect(jIP: String?): Int

    //置后台需要断开服务器
    external fun FlowDeviceDisconnect()

    //初化设备
    external fun FlowInitDevice(jdid: String?): Int

    //取设备的状态
    external fun FlowGetDeviceStatus(jdid: String?): Int

    //唤醒设备
    external fun FlowAwakenDevice(jdid: String?): Int

    /**
     * 保持设备激活
     * @param deviceIdentity 设备id
     * @param time      设备延时休眠时间不得少5秒
     */
    external fun FlowKeepDeviceActive(jdid: String?, time: Int): Int

    /**
     * 立刻让设备休眠
     * @param deviceIdentity 设备id
     */
    external fun FlowSleepDevice(jdid: String?, time: Int): Int

    //移除节点节点，需要重新FlowInitDevice
    external fun FlowRemoveDevice(jdid: String?): Int

    /**************************4G低功耗设备端接口end */ /*停止P2P
 	 *did  设备uid
 	 *p2pVer:0->PPPP 1->XQP2P
	 */
    external fun GetP2PVersion(p2pVer: Int): Int

    //加一个接口    //获取vstc2_jni库版本
    external fun GetVersion(): Int

    //PP的P2P初化
    external fun PTPInitial(svr: String?)

    //QX的P2P初化
    external fun QXPTPInitial(svr: String?)

    //PP的P2P是否初化了 返回 0为没有，1为已初化
    external fun IsPTPInitial(): Int

    //QX的P2P是否初化了 返回 0为没有，1为已初化
    external fun IsQXPTPInitial(): Int

    //打印底层jni日志，nEnable=1为开启，0为关闭 
    //默认是关闭的
    external fun PrintJNILog(nEnable: Int)
    external fun FisheyeYUVdataSplit(inYUV: ByteArray?, OutY: ByteArray?, OutU: ByteArray?, OutV: ByteArray?, nVideoWidth: Int, nVideoHeight: Int, nCut: Int)

    /**
     * 双重认证P2P连接
     * @param   did:           UID
     * @param   pwd:           密码
     * @param   bEnableLanSearch:   指定服务器
     * @param   accountname:       accountname
     * @param   svr_no:         P2P服务器串
     * @param   add:            1:首次(绑定设备时) 0:已经绑定好了设备用
     * @param     strVUID:        设备VUID
     * @param     timestamp       上次在线unix时间戳(取不到就传0)
     * @return
     */
    external fun StartVUID(did: String?, pwd: String?, bEnableLanSearch: Int, accountname: String?, svr_no: String?, add: Int, strVUID: String?, timestamp: Long): Int
    //end vuid
    /**
     * 门铃设备TCP语音对讲连接
     *
     * @param did:      设备UID
     * @param strIP:    服务器地址
     * @param port:     服务器端口
     * @param strToken: token
     * @param strUser:  用户
     * @return 1:成功调用接口
     */
    external fun StratVoiceChannel(did: String?, strIP: String?, port: Int, strToken: String?, strUser: String?, roomId: String?): Int

    /**
     * 门铃设备TCP语音对讲数据
     *
     * @param did:  设备UID
     * @param data: 对讲数据
     * @param len:  长度
     * @return 1:成功调用接口
     */
    external fun VoiceTalkAudioData(did: String?, data: ByteArray?, len: Int): Int

    /**
     * 门铃设备TCP语音对讲断开
     *
     * @param did: 设备UID
     * @return 1:成功调用接口
     */
    external fun StopVoiceChannel(did: String?): Int

    /**
     * 播放音频
     */
    external fun PlayerVoice(path: String?): Int

    init {
        System.loadLibrary("vstc2_jni")
    }
}