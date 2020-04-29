package com.zero.tzz.gles.ui

import androidx.appcompat.app.AlertDialog
import com.zero.tzz.gles.App
import com.zero.tzz.gles.R
import com.zero.tzz.gles.base.BaseActivity
import com.zero.tzz.gles.drawer.simple.*
import com.zero.tzz.gles.glview.SimpleGLSurfaceView
import com.zero.tzz.gles.render.SimpleRender
import kotlinx.android.synthetic.main.activity_draw_simple.*

/**
 *
 * @author Zero_Tzz
 * @date 2019-12-10 11:59
 * @description DrawSimpleShape
 */
class DrawSimpleActivity : BaseActivity<SimpleGLSurfaceView, SimpleRender>() {
    private val drawers = mutableMapOf(
        "三角形" to Triangle(App.getInstance()),
        "等腰三角形" to TriangleEqual(App.getInstance()),
        "彩色等腰三角形" to TriangleEqualColor(App.getInstance()),
        "正方形" to Square(App.getInstance()),
        "圆形" to Circular(App.getInstance()),
        "立方体" to Cube(App.getInstance()),
        "圆锥" to Cone(App.getInstance()),
        "圆柱" to Cylinder(App.getInstance()),
        "球体" to Ball(App.getInstance())
    )

    override fun attachLayoutId(): Int = R.layout.activity_draw_simple

    override fun attachGLSurfaceView(): SimpleGLSurfaceView = simpleGLSurfaceView

    override fun attachGLRender(): SimpleRender = SimpleRender()

    override fun initDataAndEvent() {
        mRender.addDrawers(drawers)
        mRender.updateCurrentDrawer("三角形")
        btn_change.setOnClickListener {
            showChangeList()
        }
    }

    private fun showChangeList() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("选择列表")
        val array = drawers.keys.toTypedArray()
        builder.setItems(array) { dialog, which ->
            mRender.updateCurrentDrawer(array[which])
            dialog.dismiss()
        }
        builder.create().show()
    }
}