package com.zero.tzz.baselib

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment

/**
 *
 * @author Zero_Tzz
 * @date 2019-07-08 10:01
 * @description BaseFragment
 */
abstract class BaseFragment : Fragment() {
    protected lateinit var mActivity: Activity

    @LayoutRes
    abstract fun attachLayout(): Int

    abstract fun initEventAndData()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(attachLayout(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEventAndData()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as Activity
    }
}