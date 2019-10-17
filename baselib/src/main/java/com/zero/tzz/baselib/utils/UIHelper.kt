package com.zero.tzz.baselib.utils

import android.content.res.Resources

/**
 *
 * @author Zero_Tzz
 * @date 2019-07-15 17:42
 * @description UIHelper
 */
object UIHelper {

    fun getScreenWidth(): Int = Resources.getSystem().displayMetrics.widthPixels

    fun getScreenHeight(): Int = Resources.getSystem().displayMetrics.heightPixels

}