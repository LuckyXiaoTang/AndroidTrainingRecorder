package com.zero.tzz.training.permission.core

import android.app.Fragment
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import java.util.*

/**
 *
 * @author Zero_Tzz
 * @date 2019-10-18 16:21
 * @description PermissionFragment
 */
class PermissionFragment : Fragment() {

    val mRandomCode = Random().nextInt(10000)
    var mCallback: Callback? = null


    @RequiresApi(Build.VERSION_CODES.M)
    fun requestPermission(permissions: ArrayList<String>) {
        requestPermissions(permissions.toArray(arrayOf()), mRandomCode)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val success = arrayListOf<String>()
        val fail = arrayListOf<String>()
        if (requestCode == mRandomCode) {
            val size = permissions.size
            for (i in 0 until size) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    success.add(permissions[i])
                } else {
                    fail.add(permissions[i])
                }
            }
            mCallback?.onSuccess(success)
            mCallback?.onFail(fail)
        }
    }

    interface Callback {
        fun onSuccess(permissions: ArrayList<String>)

        fun onFail(permissions: ArrayList<String>)
    }

    fun setOnRequestCallback(callback: Callback) {
        mCallback = callback
    }
}