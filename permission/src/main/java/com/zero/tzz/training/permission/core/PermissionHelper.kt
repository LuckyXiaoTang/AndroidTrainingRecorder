package com.zero.tzz.training.permission.core

import android.os.Build

/**
 *
 * @author Zero_Tzz
 * @date 2019-10-18 10:24
 * @description PermissionHelper
 */
class PermissionHelper private constructor() {

    companion object {
        private val mPermissions = arrayListOf<String>()

        /** 添加请求权限 */
        @JvmStatic
        fun permissions(vararg permissions: String): PermissionHelper {
            mPermissions.addAll(permissions)
            return PermissionHelper()
        }
    }


    /** 请求权限 */
    fun request(any: Any) {
        // Version < Android 6.0
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            PermissionUtil.executeSuccessMethod(any, mPermissions)
        } else {
            // Version >= Android 6.0 需要申请权限
            PermissionUtil.requestPermission(any, mPermissions)
        }
        mPermissions.clear()
    }
}