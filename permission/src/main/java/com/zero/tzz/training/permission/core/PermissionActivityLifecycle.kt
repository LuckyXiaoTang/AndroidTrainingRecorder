package com.zero.tzz.training.permission.core

import android.app.Activity
import android.app.Application
import android.os.Bundle
import java.util.*

/**
 *
 * @author Zero_Tzz
 * @date 2019-10-18 13:22
 * @description PermissionActivityLifecycle
 */
class PermissionActivityLifecycle : Application.ActivityLifecycleCallbacks {

    private val mActivitys = Stack<Activity?>()

    fun getActivity(): Activity? {
        return mActivitys[mActivitys.size - 1]
    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
        mActivitys.add(activity)
    }

    override fun onActivityDestroyed(activity: Activity?) {
        mActivitys.remove(activity)

    }

    override fun onActivityPaused(activity: Activity?) {

    }

    override fun onActivityResumed(activity: Activity?) {

    }

    override fun onActivityStarted(activity: Activity?) {

    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {

    }

    override fun onActivityStopped(activity: Activity?) {

    }
}