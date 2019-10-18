package com.zero.tzz.training.permission.core

import android.os.Build
import androidx.annotation.RequiresApi
import java.lang.reflect.Method

/**
 *
 * @author Zero_Tzz
 * @date 2019-10-18 10:35
 * @description PermissionUtil
 */
object PermissionUtil {

    fun executeSuccessMethod(any: Any, permissions: ArrayList<String>) {
        val temp = ArrayList(permissions)
        if (temp.size == 0) return
        val methods = any.javaClass.declaredMethods
        for (method in methods) {
            val annotation = method.getAnnotation(PermissionSucceed::class.java)
            annotation?.let {
                if (temp.contains(annotation.permission)) {
                    executeMethod(any, method)
                }
            }
        }
    }

    fun executeFailMethod(any: Any, permissions: ArrayList<String>) {
        val temp = ArrayList(permissions)
        if (temp.size == 0) return
        val methods = any.javaClass.declaredMethods
        for (method in methods) {
            val annotation = method.getAnnotation(PermissionFailed::class.java)
            annotation?.let {
                if (temp.contains(annotation.permission)) {
                    executeMethod(any, method)
                }
            }
        }
    }

    private fun executeMethod(any: Any, method: Method) {
        try {
            method.isAccessible = true
            method.invoke(any)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun requestPermission(any: Any, permissions: ArrayList<String>) {
        val temp = ArrayList(permissions)
        val denied = PermissionController.getInstance().getDeniedPermissions(temp)
        if (denied.size == 0) {
            executeSuccessMethod(any, temp)
            return
        }
        PermissionController.getInstance()
            .requestPermission(denied, object : PermissionFragment.Callback {
                override fun onSuccess(permissions: ArrayList<String>) {
                    temp.removeAll(denied)
                    temp.addAll(permissions)
                    executeSuccessMethod(any, temp)
                }

                override fun onFail(permissions: ArrayList<String>) {
                    executeFailMethod(any, permissions)
                }
            })
    }
}