package com.zero.tzz.coroutine.core

import android.app.Application
import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri

/**
 *
 * @author Zero_Tzz
 * @date 2019-10-18 11:00
 * @description InitProvider
 */
class InitProvider : ContentProvider() {

    override fun onCreate(): Boolean {
        // 在这里获取Application
        val application = context as Application
        HttpProvider.init(application)
        return true
    }


    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? = null

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int = 0

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int = 0

    override fun getType(uri: Uri): String? = null
}