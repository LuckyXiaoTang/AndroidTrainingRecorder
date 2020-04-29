package com.zero.tzz.gles.ext

import android.app.Activity
import android.content.Intent

/**
 *
 * @author Zero_Tzz
 * @date 2019-12-04 15:35
 * @description ext
 */

private var mLastTime = 0L
fun <T : Activity> Activity.safetyStartActivity(clazz: Class<T>) {
    if (System.currentTimeMillis() - mLastTime > 800) {
        mLastTime = System.currentTimeMillis()
        startActivity(Intent(this, clazz))
    }
}