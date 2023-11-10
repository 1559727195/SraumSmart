package com.massky.sraum.myzxingbar.qrcodescanlib

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.hardware.Camera
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Vibrator
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.zxing.BarcodeFormat
import com.google.zxing.Result
import com.massky.sraum.R
import com.massky.sraum.activity.HandAddGateWayActivity
import com.massky.sraum.activity.HandAddYingShiCameraActivity
import com.massky.sraum.myzxingbar.zxing.camera.CameraManager
import com.massky.sraum.myzxingbar.zxing.decoding.CaptureActivityHandler
import com.massky.sraum.myzxingbar.zxing.decoding.InactivityTimer
import com.massky.sraum.myzxingbar.zxing.view.ViewfinderView
import com.massky.sraum.permissions.RxPermissions
import com.yanzhenjie.statusview.StatusUtils
import com.yanzhenjie.statusview.StatusView
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import java.io.IOException
import java.util.*

class CaptureActivity : AppCompatActivity(), SurfaceHolder.Callback, View.OnClickListener {
    private var handler: CaptureActivityHandler? = null
    var viewfinderView: ViewfinderView? = null
        private set
    private var hasSurface = false
    private var decodeFormats: Vector<BarcodeFormat>? = null
    private var characterSet: String? = null
    private var inactivityTimer: InactivityTimer? = null
    private var mediaPlayer: MediaPlayer? = null
    private var playBeep = false
    private var vibrate = false
    private val _ivBack: ImageView? = null
    private val toolbars //toolbar的返回键
            : Toolbar? = null
    private val IsHaveFlash = false
    private val open_light: ImageView? = null
    private var statusView: StatusView? = null
    private var back: ImageView? = null
    private var edit_gateway: ImageView? = null
    private var screen_width = 0
    private var screen_height = 0
    private var item: String? = null

    /**
     * Called when the activity is first created.
     */
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_capture_new)
        initPermission()
        CameraManager.init(application)
        initView()
        initEvent()
        init_data()
    }

    private fun init_data() {
        item = intent.getStringExtra("item")
    }

    private fun init_splash_light() {
        camera = Camera.open()
        parameters = camera!!.getParameters()
    }

    private fun initPermission() {
        // 清空图片缓存，包括裁剪、压缩后的图片 注意:必须要在上传完成后调用 必须要获取权限
        val permissions = RxPermissions(this)
        permissions.request(Manifest.permission.CAMERA).subscribe(object : Observer<Boolean?> {
            override fun onSubscribe(d: Disposable) {}
            override fun onNext(aBoolean: Boolean?) {}
            override fun onError(e: Throwable) {}
            override fun onComplete() {}
        })
    }

    private fun initView() {
//		toolbars = (Toolbar) findViewById(R.id.toolbar_scanel);
//		setSupportActionBar(toolbars);
//		getSupportActionBar().setDisplayShowHomeEnabled(true);//toolbar返回键设置
        viewfinderView = findViewById<View>(R.id.viewfinder_view) as ViewfinderView
        hasSurface = false
        inactivityTimer = InactivityTimer(this)

//		@BindView(R.id.status_view)
//		StatusView statusView;
        statusView = findViewById<View>(R.id.status_view) as StatusView //
        StatusUtils.setFullToStatusBar(this) // StatusBar.
        setAnyBarAlpha(0) //设置状态栏的颜色为透明
        back = findViewById<View>(R.id.back) as ImageView
        edit_gateway = findViewById<View>(R.id.edit_gateway) as ImageView //手动编辑网关
        val display = windowManager.defaultDisplay
        screen_width = display.width
        screen_height = display.height
    }

    //注册二维码返回
    private fun initEvent() {
//		toolbars.setNavigationOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				onBackPressed();
//			}
//		});
        back!!.setOnClickListener { onBackPressed() }
        edit_gateway!!.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        val surfaceView = findViewById<View>(R.id.preview_view) as SurfaceView
        val surfaceHolder = surfaceView.holder
        if (hasSurface) {
            initCamera(surfaceHolder, screen_width, screen_height)
        } else {
            surfaceHolder.addCallback(this)
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
        }
        decodeFormats = null
        characterSet = null
        playBeep = true
        val audioService = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (audioService.ringerMode != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false
        }
        initBeepSound()
        vibrate = true
    }

    override fun onPause() {
        super.onPause()
        if (handler != null) {
            handler!!.quitSynchronously()
            handler = null
        }
        CameraManager.get().closeDriver()
    }

    override fun onDestroy() {
        inactivityTimer!!.shutdown()
        super.onDestroy()
    }

    private fun initCamera(surfaceHolder: SurfaceHolder, screen_width: Int, screen_height: Int) {
        try {
            CameraManager.get().openDriver(surfaceHolder, screen_width, screen_height)
        } catch (ioe: IOException) {
            return
        } catch (e: RuntimeException) {
            return
        }
        if (handler == null) {
            handler = CaptureActivityHandler(this, decodeFormats,
                    characterSet)
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int,
                                height: Int) {
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        if (!hasSurface) {
            hasSurface = true
            initCamera(holder, screen_width, screen_height)
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        hasSurface = false
    }

    fun getHandler(): Handler? {
        return handler
    }

    fun drawViewfinder() {
        viewfinderView!!.drawViewfinder()
    }

    fun handleDecode(obj: Result, barcode: Bitmap?) {
        inactivityTimer!!.onActivity()
        val code = obj.text
        if (code === "") {
            Toast.makeText(this@CaptureActivity, "Scan failed!", Toast.LENGTH_SHORT).show()
        } else {
            Log.d("SCAN", code)
            val intent = Intent()
            val bundle = Bundle()
            bundle.putString("result", code)
            intent.putExtras(bundle)
            this.setResult(Activity.RESULT_OK, intent)
        }
        finish()
    }

    private fun initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            volumeControlStream = AudioManager.STREAM_MUSIC
            mediaPlayer = MediaPlayer()
            mediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mediaPlayer!!.setOnCompletionListener(beepListener)
            val file = resources.openRawResourceFd(
                    R.raw.beep)
            try {
                mediaPlayer!!.setDataSource(file.fileDescriptor,
                        file.startOffset, file.length)
                file.close()
                mediaPlayer!!.setVolume(BEEP_VOLUME, BEEP_VOLUME)
                mediaPlayer!!.prepare()
            } catch (e: IOException) {
                mediaPlayer = null
            }
        }
    }

    private fun playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer!!.start()
        }
        if (vibrate) {
            val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(VIBRATE_DURATION)
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private val beepListener = MediaPlayer.OnCompletionListener { mediaPlayer -> mediaPlayer.seekTo(0) }
    var camera: Camera? = null
    var parameters: Camera.Parameters? = null
    fun IsHaveFlash(): Boolean //判断设备是否有闪光灯
    {
        return !packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
    }

    //	public ControlFlash() {//初始化
    //		// TODO 自动生成的构造函数存根
    //		camera= Camera.open();
    //		parameters=camera.getParameters();
    //	}
    fun open() { //打开闪光灯
        if (IsHaveFlash) {
            //设备不支持闪光灯
            return
        }
        parameters!!.flashMode = Camera.Parameters.FLASH_MODE_TORCH //设置闪光灯为手电筒模式
        camera!!.parameters = parameters
        camera!!.startPreview()
    }

    fun close() //关闭闪光灯
    {
        parameters!!.flashMode = Camera.Parameters.FLASH_MODE_OFF
        camera!!.parameters = parameters
    }

    /*
     *动态设置状态栏的颜色
     */
    private fun setAnyBarAlpha(alpha: Int) {
//        mToolbar.getBackground().mutate().setAlpha(alpha);
        statusView!!.background.mutate().alpha = alpha
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.edit_gateway -> if (item == null) {
                startActivity(Intent(this@CaptureActivity, HandAddGateWayActivity::class.java))
                finish()
            } else {
                when (item) {
                    "102" -> {
                    }
                    "104" -> {
                        val intent = Intent(CaptureActivity@this, HandAddYingShiCameraActivity::class.java)
                        startActivity(intent)
                        CaptureActivity@this.finish()
                    }
                }
            }
        }
    }

    companion object {
        private const val BEEP_VOLUME = 0.10f
        private const val DECODED_CONTENT_KEY = "codedContent" //扫码的内容
        private const val VIBRATE_DURATION = 200L
    }
}