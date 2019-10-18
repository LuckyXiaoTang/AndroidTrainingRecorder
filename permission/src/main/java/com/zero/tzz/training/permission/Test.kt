package com.zero.tzz.training.permission

import android.Manifest
import com.zero.tzz.training.permission.core.PermissionFailed
import com.zero.tzz.training.permission.core.PermissionHelper
import com.zero.tzz.training.permission.core.PermissionSucceed

/**
 *
 * @author Zero_Tzz
 * @date 2019-10-18 17:34
 * @description Test
 */
object Test {
    fun test() {
        PermissionHelper
            .permissions(Manifest.permission.CALL_PHONE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .request(this)
    }


    @PermissionSucceed(Manifest.permission.CALL_PHONE)
    fun aaa1() {
        println("aaaaa1")
    }

    @PermissionFailed(Manifest.permission.CALL_PHONE)
    fun aaa2() {
        println("aaaaa2")
    }

    @PermissionSucceed(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun bbb1() {
        println("bbbbb1")
    }

    @PermissionFailed(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun bbb2() {
        println("bbbbb2")
    }
}