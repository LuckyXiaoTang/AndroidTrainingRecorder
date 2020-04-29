package com.zero.tzz.gles.base

import android.opengl.GLSurfaceView
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity

/**
 *
 * @author Zero_Tzz
 * @date 2019-12-10 11:40
 * @description BaseActivity
 */
abstract class BaseActivity<V : BaseGLSurfaceView,
        R : BaseRender> : AppCompatActivity() {

    lateinit var mGLSurfaceView: V
    lateinit var mRender: R

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(attachLayoutId())
        initBase()
        initDataAndEvent()
    }

    private fun initBase() {
        mGLSurfaceView = attachGLSurfaceView()
        mRender = attachGLRender()

        mGLSurfaceView.setEGLContextClientVersion(2)
        mGLSurfaceView.setRenderer(mRender)
        mGLSurfaceView.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY

        mRender.attachGLSurfaceView(mGLSurfaceView)
    }

    abstract fun attachGLSurfaceView(): V

    abstract fun attachGLRender(): R

    @LayoutRes
    abstract fun attachLayoutId(): Int

    abstract fun initDataAndEvent()
}