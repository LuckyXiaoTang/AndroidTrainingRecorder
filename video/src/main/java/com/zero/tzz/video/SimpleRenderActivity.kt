package com.zero.tzz.video

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zero.tzz.video.opengl.SimpleRender
import com.zero.tzz.video.opengl.drawer.BitmapDrawer
import com.zero.tzz.video.opengl.drawer.IDrawer
import com.zero.tzz.video.opengl.drawer.TriangleDrawer
import kotlinx.android.synthetic.main.activity_simple_render.*

/**
 *
 * @author Zero_Tzz
 * @date 2019-11-29 13:08
 * @description SimpleRenderActivity
 */
class SimpleRenderActivity : AppCompatActivity() {

    private lateinit var mDrawer: IDrawer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple_render)
        val type = intent.getIntExtra("type", 0)
        mDrawer = if (type == 0) {
            TriangleDrawer()
        } else {
            val bitmap = BitmapFactory.decodeResource(resources, R.drawable.timg_3)
            BitmapDrawer(bitmap)
        }
        initRender()
    }

    private fun initRender() {
        gl_sfv.setEGLContextClientVersion(2)
        gl_sfv.setRenderer(SimpleRender().apply {
            addDrawer(mDrawer)
        })
    }

    override fun onDestroy() {
        mDrawer.release()
        super.onDestroy()
    }
}