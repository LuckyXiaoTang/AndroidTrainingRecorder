package com.zero.tzz.baselib.ext

import android.util.Log
import com.zero.tzz.baselib.BuildConfig

/**
 *
 * @author Zero_Tzz
 * @date 2020-03-20 15:16
 * @description LogExt
 */
private val TAG = "LogExt"
private val DEBUG =
    Log.isLoggable(TAG, Log.VERBOSE) or "debug".equals(BuildConfig.BUILD_TYPE, true)

private fun buildMessage() {

}
/*private static String buildMessage(String format, Object... args)
{
    String msg = (args == null) ? format : String.format(Locale.US, format, args);
    StackTraceElement[] trace = new Throwable().fillInStackTrace().getStackTrace();

    String caller = "<unknown>";
    for (int i = 2; i < trace.length; i++)
    {
        Class<?> clazz = trace[i].getClass();
        if (!clazz.equals(LogHelper.class))
            {
                String callingClass = trace[i].getClassName();
                callingClass = callingClass.substring(callingClass.lastIndexOf('.') + 1);
                String endWords = callingClass.substring(callingClass.lastIndexOf('$') + 1);
                if (!isNumeric(endWords))
                {
                    callingClass = endWords;
                }
                caller = callingClass + "." + trace[i].getMethodName();
                break;
            }
    }
    return String.format(
        Locale.US, "[%d] %s: %s",
        Thread.currentThread().getId(), caller, msg);
}

private static boolean isNumeric(String str)
{
    Pattern pattern = Pattern.compile("[0-9]+");
    return pattern.matcher(str).matches();
}*/
