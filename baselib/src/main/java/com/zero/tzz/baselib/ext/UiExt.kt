package com.zero.tzz.baselib.ext

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import android.widget.Toast
import androidx.fragment.app.Fragment

/**
 *
 * @author Zero_Tzz
 * @date 2019-07-08 10:18
 * @description UIExt
 */

//////////////////////////////////////////// dp sp ////////////////////////////////////////////
val Float.dp: Int
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        Resources.getSystem().displayMetrics
    ).toInt()

val Int.dp: Int
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    ).toInt()

val Float.sp: Int
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        this,
        Resources.getSystem().displayMetrics
    ).toInt()

val Int.sp: Int
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    ).toInt()

//////////////////////////////////////////// toast ////////////////////////////////////////////

var toast: Toast? = null
fun Context.toast(content: String, durition: Int = Toast.LENGTH_SHORT) {
    toast?.cancel()
    toast = Toast.makeText(this, content, durition)
    toast?.show()
}

fun Fragment.toast(content: String, durition: Int = Toast.LENGTH_SHORT) {
    toast?.cancel()
    toast = Toast.makeText(context, content, durition)
    toast?.show()
}

fun Any.toast(context: Context, durition: Int = Toast.LENGTH_SHORT) {
    toast?.cancel()
    toast = Toast.makeText(context, toString(), durition)
    toast?.show()
}