package com.zero.tzz.coroutine.core

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModelProvider

/**
 * Created by Tzz on 2019/12/30.
 */
@PublishedApi
internal class HttpLifecycle : Application.ActivityLifecycleCallbacks {

    private val maps: MutableMap<Activity, VM> = mutableMapOf()
    private var mTopActivity: Activity? = null
    private var mRetrofit: HttpService =
        HttpRetrofit(HttpProvider.get()).createService(HttpService::class.java)

    init {
        HttpProvider.get().registerActivityLifecycleCallbacks(this)
    }

    fun getViewModel(): VM? {
        return maps.get(mTopActivity)
    }

    fun getHttpService(): HttpService = mRetrofit

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        if (activity is AppCompatActivity) {
            val viewModel = ViewModelProvider(activity).get(VM::class.java)
            activity.lifecycle.addObserver(viewModel)
            viewModel.addLifecycle(activity)
            maps.put(activity, viewModel)
        } else {
            throw IllegalStateException("activity must be extends AppCompatActivity")
        }
    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityResumed(activity: Activity) {
        mTopActivity = activity
    }

    override fun onActivityStarted(activity: Activity) {

    }

    override fun onActivityDestroyed(activity: Activity) {
        if (activity is AppCompatActivity) {
            maps.get(activity)?.let {
                activity.lifecycle.removeObserver(it as LifecycleObserver)
                it.removeLifecycle(activity)
            }
        } else {
            throw IllegalStateException("activity must be extends AppCompatActivity")
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle?) {

    }

    override fun onActivityStopped(activity: Activity) {

    }
}