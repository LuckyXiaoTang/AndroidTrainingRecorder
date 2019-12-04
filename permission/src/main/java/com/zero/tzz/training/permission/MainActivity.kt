package com.zero.tzz.training.permission

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 动态请求权限
        /*PermissionHelper
            .permissions(Manifest.permission.CALL_PHONE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .request(this)*/

//        Test.test()
        J_Test().test()
    }
/*
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
    }*/
}