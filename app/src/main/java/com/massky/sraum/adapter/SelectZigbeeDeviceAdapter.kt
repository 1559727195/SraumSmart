package com.massky.sraum.adapter

import android.os.Parcelable
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.massky.sraum.fragment.WifiItemFragment
import com.massky.sraum.fragment.ZigBeeItemFragment

/**
 * Created by masskywcy on 2017-11-27.
 */
class SelectZigbeeDeviceAdapter(fm: FragmentManager?, behavior: Int) : FragmentStatePagerAdapter(fm!!, behavior) {
    private var bbsCategoryList: List<String>? = null
    fun setData(data: List<String>?) {
        if (data == null) {
            return
        }
        bbsCategoryList = data
        notifyDataSetChanged()
    }

    override fun getItem(position: Int): Fragment {
        Log.e("TAG", "position=$position")
        when (position) {
            0 -> return ZigBeeItemFragment.newInstance(bbsCategoryList!![position])
            1 -> return WifiItemFragment.newInstance(bbsCategoryList!![position])
        }
        return ZigBeeItemFragment.newInstance(bbsCategoryList!![position])
    }

    override fun getCount(): Int {
        return if (bbsCategoryList != null && bbsCategoryList!!.size > 0) {
            bbsCategoryList!!.size
        } else 0
    }

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {
        super.restoreState(state, loader)
    }

    override fun saveState(): Parcelable? {
        return null
    }
}