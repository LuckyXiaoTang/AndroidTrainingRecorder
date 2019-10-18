package com.zero.tzz.training.permission.core

import android.app.Activity
import android.app.Application
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat

/**
 *
 * @author Zero_Tzz
 * @date 2019-10-18 13:57
 * @description PermissionController
 */
class PermissionController {

    private lateinit var mLifeCycle: PermissionActivityLifecycle

    companion object {
        private var instance: PermissionController? = null
        fun getInstance(): PermissionController {
            if (instance == null) {
                synchronized(PermissionController::class.java) {
                    instance = PermissionController()
                }
            }
            return instance!!
        }
    }

    fun init(application: Application) {
        mLifeCycle = PermissionActivityLifecycle()
        application.registerActivityLifecycleCallbacks(mLifeCycle)
    }

    fun getActivity(): Activity? {
        return mLifeCycle.getActivity()
    }

    fun getDeniedPermissions(permissions: ArrayList<String>): ArrayList<String> {
        val temp = arrayListOf<String>()
        getActivity()?.let {
            for (permission in permissions) {
                if (ContextCompat.checkSelfPermission(it, permission)
                    == PackageManager.PERMISSION_DENIED
                ) {
                    temp.add(permission)
                }
            }
        }
        return temp
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun requestPermission(permissions: ArrayList<String>,callback:PermissionFragment.Callback) {
        val fragment = PermissionFragment()
        getActivity()?.fragmentManager?.beginTransaction()?.add(fragment, "1")
            ?.commitAllowingStateLoss()
        //立即提交
        getActivity()?.fragmentManager?.executePendingTransactions()
        fragment.setOnRequestCallback(callback)
        fragment.requestPermission(permissions)
    }
}