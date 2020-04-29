package com.zero.tzz.gles

import android.app.Application

/**
 *
 * @author Zero_Tzz
 * @date 2019-12-12 15:51
 * @description App
 */
class App : Application() {
    companion object {
        private lateinit var instance: App
        fun getInstance() = instance
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}