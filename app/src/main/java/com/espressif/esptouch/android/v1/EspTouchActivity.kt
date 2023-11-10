package com.espressif.esptouch.android.v1

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import com.espressif.esptouch.android.EspTouchActivityAbs
import com.espressif.esptouch.android.v1.EspTouchActivity
import com.espressif.iot.esptouch1.EsptouchTask
import com.espressif.iot.esptouch1.IEsptouchListener
import com.espressif.iot.esptouch1.IEsptouchResult
import com.espressif.iot.esptouch1.IEsptouchTask
import com.espressif.iot.esptouch1.util.ByteUtil
import com.espressif.iot.esptouch1.util.TouchNetUtil
import com.massky.sraum.R
import com.massky.sraum.Utils.App
import java.lang.ref.WeakReference
import java.util.*

 class EspTouchActivity : EspTouchActivityAbs() {
    private var mViewModel: EspTouchViewModel? = null
    private var mTask: EsptouchAsyncTask4? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_esptouch)
        mViewModel = EspTouchViewModel()
        mViewModel!!.apSsidTV = findViewById(R.id.apSsidText)
        mViewModel!!.apBssidTV = findViewById(R.id.apBssidText)
        mViewModel!!.apPasswordEdit = findViewById(R.id.apPasswordEdit)
        mViewModel!!.deviceCountEdit = findViewById(R.id.deviceCountEdit)
        mViewModel!!.packageModeGroup = findViewById(R.id.packageModeGroup)
        mViewModel!!.messageView = findViewById(R.id.messageView)
        mViewModel!!.confirmBtn = findViewById(R.id.confirmBtn)
        mViewModel!!.confirmBtn!!.setOnClickListener { v: View? -> executeEsptouch() }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
            requestPermissions(permissions, REQUEST_PERMISSION)
        }
        App.getInstance().observeBroadcast(this, Observer { broadcast: String ->
            Log.d(TAG, "onCreate: Broadcast=$broadcast")
            onWifiChanged()
        })
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun viewId(): Int {
        return 0
    }

    override fun onView() {}
    override fun onEvent() {}
    override fun onData() {}
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onWifiChanged()
            } else {
                AlertDialog.Builder(this)
                        .setTitle(R.string.esptouch1_location_permission_title)
                        .setMessage(R.string.esptouch1_location_permission_message)
                        .setCancelable(false)
                        .setPositiveButton(android.R.string.ok) { dialog: DialogInterface?, which: Int -> finish() }
                        .show()
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun getEspTouchVersion(): String {
        return getString(R.string.esptouch1_about_version, IEsptouchTask.ESPTOUCH_VERSION)
    }

    private fun check(): StateResult {
        var result = checkPermission()
        if (!result.permissionGranted) {
            return result
        }
        result = checkLocation()
        result.permissionGranted = true
        if (result.locationRequirement) {
            return result
        }
        result = checkWifi()
        result.permissionGranted = true
        result.locationRequirement = false
        return result
    }

    private fun onWifiChanged() {
        val stateResult = check()
        mViewModel!!.message = stateResult.message
        mViewModel!!.ssid = stateResult.ssid
        mViewModel!!.ssidBytes = stateResult.ssidBytes
        mViewModel!!.bssid = stateResult.bssid
        mViewModel!!.confirmEnable = false
        if (stateResult.wifiConnected) {
            mViewModel!!.confirmEnable = true
            if (stateResult.is5G) {
                mViewModel!!.message = getString(R.string.esptouch1_wifi_5g_message)
            }
        } else {
            if (mTask != null) {
                mTask!!.cancelEsptouch()
                mTask = null
                AlertDialog.Builder(this@EspTouchActivity)
                        .setMessage(R.string.esptouch1_configure_wifi_change_message)
                        .setNegativeButton(android.R.string.cancel, null)
                        .show()
            }
        }
        mViewModel!!.invalidateAll()
    }

    private fun executeEsptouch() {
        val viewModel = mViewModel
        val ssid = if (viewModel!!.ssidBytes == null) ByteUtil.getBytesByString(viewModel.ssid) else viewModel.ssidBytes
        val pwdStr: CharSequence? = mViewModel!!.apPasswordEdit!!.text
        val password = if (pwdStr == null) null else ByteUtil.getBytesByString(pwdStr.toString())
        val bssid = TouchNetUtil.parseBssid2bytes(viewModel.bssid)
        val devCountStr: CharSequence? = mViewModel!!.deviceCountEdit!!.text
        val deviceCount = devCountStr?.toString()?.toByteArray() ?: ByteArray(0)
        val broadcast = byteArrayOf((if (mViewModel!!.packageModeGroup!!.checkedRadioButtonId == R.id.packageBroadcast) 1 else 0).toByte())
        if (mTask != null) {
            mTask!!.cancelEsptouch()
        }
        mTask = EsptouchAsyncTask4(this)
        mTask!!.execute(ssid, bssid, password, deviceCount, broadcast)
    }

    override fun onClick(v: View) {}
    private class EsptouchAsyncTask4 internal constructor(activity: EspTouchActivity) : AsyncTask<ByteArray?, IEsptouchResult?, List<IEsptouchResult>?>() {
        private val mActivity: WeakReference<EspTouchActivity>
        private val mLock = Any()
        private var mProgressDialog: ProgressDialog? = null
        private var mResultDialog: AlertDialog? = null
        private var mEsptouchTask: IEsptouchTask? = null
        fun cancelEsptouch() {
            cancel(true)
            if (mProgressDialog != null) {
                mProgressDialog!!.dismiss()
            }
            if (mResultDialog != null) {
                mResultDialog!!.dismiss()
            }
            if (mEsptouchTask != null) {
                mEsptouchTask!!.interrupt()
            }
        }

        override fun onPreExecute() {
            val activity: Activity? = mActivity.get()
            mProgressDialog = ProgressDialog(activity)
            mProgressDialog!!.setMessage(activity!!.getString(R.string.esptouch1_configuring_message))
            mProgressDialog!!.setCanceledOnTouchOutside(false)
            mProgressDialog!!.setOnCancelListener { dialog: DialogInterface? ->
                synchronized(mLock) {
                    if (mEsptouchTask != null) {
                        mEsptouchTask!!.interrupt()
                    }
                }
            }
            mProgressDialog!!.setButton(DialogInterface.BUTTON_NEGATIVE, activity.getText(android.R.string.cancel)
            ) { dialog: DialogInterface?, which: Int ->
                synchronized(mLock) {
                    if (mEsptouchTask != null) {
                        mEsptouchTask!!.interrupt()
                    }
                }
            }
            mProgressDialog!!.show()
        }


        override fun onProgressUpdate(vararg values: IEsptouchResult?) {
            val context: Context? = mActivity.get()
            if (context != null) {
                val result = values[0]
                Log.i(TAG, "EspTouchResult: $result")
                val text = result!!.bssid + " is connected to the wifi"
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
            }
        }

        override fun doInBackground(vararg params: ByteArray?): List<IEsptouchResult>? {
            val activity = mActivity.get()
            var taskResultCount: Int
            synchronized(mLock) {
                val apSsid = params[0]
                val apBssid = params[1]
                val apPassword = params[2]
                val deviceCountData = params[3]
                val broadcastData = params[4]
                taskResultCount = if (deviceCountData!!.size == 0) -1 else String(deviceCountData).toInt()
                val context = activity!!.applicationContext
                mEsptouchTask = EsptouchTask(apSsid, apBssid, apPassword, context)
                mEsptouchTask!!.setPackageBroadcast((broadcastData!![0] as Int) == 1)
                mEsptouchTask!!.setEsptouchListener(IEsptouchListener { values: IEsptouchResult? -> publishProgress(values) })
            }
            return mEsptouchTask!!.executeForResults(taskResultCount)
        }

        override fun onPostExecute(result: List<IEsptouchResult>?) {
            val activity = mActivity.get()
            activity!!.mTask = null
            mProgressDialog!!.dismiss()
            if (result == null) {
                mResultDialog = AlertDialog.Builder(activity)
                        .setMessage(R.string.esptouch1_configure_result_failed_port)
                        .setPositiveButton(android.R.string.ok, null)
                        .show()
                mResultDialog!!.setCanceledOnTouchOutside(false)
                return
            }

            // check whether the task is cancelled and no results received
            val firstResult = result[0] //bssid=68c63ab9b669, address=192.168.169.216, suc=true, cancel=false
            if (firstResult.isCancelled) {
                return
            }
            // the task received some results including cancelled while
            // executing before receiving enough results
            if (!firstResult.isSuc) {
                mResultDialog = AlertDialog.Builder(activity)
                        .setMessage(R.string.esptouch1_configure_result_failed)
                        .setPositiveButton(android.R.string.ok, null)
                        .show()
                mResultDialog!!.setCanceledOnTouchOutside(false)
                return
            }
            val resultMsgList = ArrayList<CharSequence>(result.size)
            for (touchResult in result) {
                val message = activity.getString(R.string.esptouch1_configure_result_success_item,
                        touchResult.bssid, touchResult.inetAddress.hostAddress)
                resultMsgList.add(message)
            }
            val items = arrayOfNulls<CharSequence>(resultMsgList.size)
            mResultDialog = AlertDialog.Builder(activity)
                    .setTitle(R.string.esptouch1_configure_result_success)
                    .setItems(resultMsgList.toArray(items), null)
                    .setPositiveButton(android.R.string.ok, null)
                    .show()
            mResultDialog!!.setCanceledOnTouchOutside(false)
        }

        init {
            mActivity = WeakReference(activity)
        }
    }

    companion object {
        private val TAG = EspTouchActivity::class.java.simpleName
        private const val REQUEST_PERMISSION = 0x01
    }
}