package com.topvision.media

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.topvision.media.drawer.TriangleDrawer
import com.topvision.media.egl.EGLRender
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val mRender = EGLRender()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        TriangleDrawer().apply {
            mRender.addDrawer(this)
        }
        mRender.setRenderView(sfv)
    }
}
