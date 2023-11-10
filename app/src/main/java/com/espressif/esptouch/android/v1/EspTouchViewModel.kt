package com.espressif.esptouch.android.v1

import android.text.Editable
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import com.massky.sraum.view.ClearEditText

class EspTouchViewModel {
    var apSsidTV: TextView? = null
    var apBssidTV: TextView? = null
    var apPasswordEdit: EditText? = null
    var deviceCountEdit: EditText? = null
    var packageModeGroup: RadioGroup? = null
    var messageView: TextView? = null
    var confirmBtn: Button? = null
    var ssid: String? = null
    var password:String? = null
    var ssidBytes: ByteArray? = null
    var bssid: String? = null
    var message: CharSequence? = null
    var confirmEnable = false
    var ssid_second:String? = null
    var edit_password_gateway: ClearEditText? = null

    var edit_wifi: ClearEditText? = null
    fun invalidateAll() {
        if (apSsidTV != null)
            apSsidTV!!.text = ssid
        if (apBssidTV != null)
            apBssidTV!!.text = bssid
        if (messageView != null)
            messageView!!.text = message
        if (confirmBtn != null)
            confirmBtn!!.isEnabled = confirmEnable
        if (edit_password_gateway != null)
            edit_password_gateway!!.setText(message)
        if (edit_wifi != null)
            edit_wifi!!.setText(ssid)
    }
}