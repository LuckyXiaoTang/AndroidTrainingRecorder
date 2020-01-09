package com.zero.tzz.baselib.ext

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import java.lang.reflect.ParameterizedType
import kotlin.reflect.KProperty

/**
 *
 * @author Zero_Tzz
 * @date 2020-01-06 10:45
 * @description Preference
 */
class Preference<T>(
    private val context: Context,
    private val key: String,
    private val defValue: T
) {
    private val TAG = "Preferences"
    val sp: SharedPreferences by lazy {
        context.getSharedPreferences(
            context.packageName,
            Context.MODE_PRIVATE
        )
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        put(key, value)
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return get(key, defValue)
    }

    @Suppress("UNCHECKED_CAST")
    private fun put(key: String, value: T) {
        sp.edit().apply {
            when (value) {
                is Long -> putLong(key, value)
                is Int -> putInt(key, value)
                is Boolean -> putBoolean(key, value)
                is String -> putString(key, value)
                is Float -> putFloat(key, value)
                is Set<*> -> {
                    val p = javaClass.genericSuperclass as ParameterizedType
                    val typeName = p.actualTypeArguments[0].typeName
                    Log.d(TAG, "put: $typeName")
                    if (typeName != String::javaClass.name) {
                        throw  Throwable("This type cannot be saved")
                    }
                    putStringSet(key, value as Set<String>)
                }
                else -> throw  Throwable("This type cannot be saved")
            }
            apply()
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun get(key: String, defValue: T): T =
        when (defValue) {
            is Long -> sp.getLong(key, defValue) as T
            is Int -> sp.getInt(key, defValue) as T
            is Boolean -> sp.getBoolean(key, defValue) as T
            is String -> sp.getString(key, defValue) as T
            is Float -> sp.getFloat(key, defValue) as T
            is Set<*> -> {
                val p = javaClass.genericSuperclass as ParameterizedType
                val typeName = p.actualTypeArguments[0].typeName
                Log.d(TAG, "put: $typeName")
                if (typeName != String::javaClass.name) {
                    throw  Throwable("This type cannot be saved")
                }
                sp.getStringSet(key, defValue as Set<String>) as T
            }
            else -> throw  Throwable("This type cannot be saved")
        }
}