package com.zero.tzz.baselib

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import com.zero.tzz.baselib.utils.StatusBarUtil
import com.zero.tzz.baselib.constants.LearnConstants
import kotlinx.android.synthetic.main.layout_base_comm.*


/**
 *
 * @author Zero_Tzz
 * @date 2019-07-08 10:01
 * @description BaseFragment
 */
abstract class BaseActivity : AppCompatActivity() {
    protected var mColor: Int = 0
    @LayoutRes
    abstract fun attachLayout(): Int

    abstract fun initEventAndData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(attachLayout())
        tv_title.text = intent.getStringExtra(LearnConstants.PARM_TITLE)?.let {
            it
        } ?: getString(applicationInfo.labelRes)

        mColor = intent.getIntExtra(LearnConstants.PARM_COLOR, Color.BLUE)
        toolbar.setBackgroundColor(mColor)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            if (intent.getBooleanExtra(LearnConstants.PARM_BACK, false)) {
                setDisplayHomeAsUpEnabled(true)
                setHomeButtonEnabled(true)
                toolbar.setNavigationOnClickListener { finish() }
            }
            setDisplayShowTitleEnabled(false)
        }
        StatusBarUtil.setColor(this@BaseActivity, mColor)
        initEventAndData()
    }

    override fun setContentView(layoutResID: Int) {
        val root = LayoutInflater.from(this).inflate(R.layout.layout_base_comm, null)
        val layout = root.findViewById<FrameLayout>(R.id.container)
        val view = LayoutInflater.from(this).inflate(layoutResID, null)
        layout.addView(
            view,
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        setContentView(root)
    }

    fun changeActionBarVisiable(visiable: Boolean) {
        toolbar?.visibility = if (visiable) View.VISIBLE else View.GONE
    }
}