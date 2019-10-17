package com.zero.tzz.baselib

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.view.View
import java.util.*

/**
 *
 * @author Zero_Tzz
 * @date 2019-08-12 14:40
 * @description LRApplication
 */
class LRApplication : Application() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var instance: LRApplication
    }

    var mActivityStack: Stack<Activity>? = null


    override fun onCreate() {
        super.onCreate()
        instance = this
        registerActivityLifecycleCallbacks(mLifecycleCallbacks)
    }

    fun addActivity(activity: Activity?) {
        activity?.let {
            if (mActivityStack == null) {
                mActivityStack = Stack()
            }
            mActivityStack!!.add(activity)
        }
    }

    fun removeActivity(activity: Activity?) {
        activity?.let {
            mActivityStack?.remove(activity)
        }
        if (mActivityStack?.size == 0) {
            unregisterActivityLifecycleCallbacks(mLifecycleCallbacks)
        }
    }

    fun getPreActivityDecorView(): View? {
        return mActivityStack?.get(mActivityStack!!.size - 2)?.window?.decorView
    }

    private val mLifecycleCallbacks = object : ActivityLifecycleCallbacks {
        override fun onActivityPaused(activity: Activity?) {

        }

        override fun onActivityResumed(activity: Activity?) {

        }

        override fun onActivityStarted(activity: Activity?) {

        }

        override fun onActivityDestroyed(activity: Activity?) {
            removeActivity(activity)
        }

        override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {

        }

        override fun onActivityStopped(activity: Activity?) {

        }

        override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
            addActivity(activity)
        }
    }
}