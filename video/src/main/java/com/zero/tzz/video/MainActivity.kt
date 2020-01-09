package com.zero.tzz.video

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }

    private fun initView() {
        btn_simple_player.setOnClickListener {
            val i = Intent(this, SimplePlayerActivity::class.java)
            startActivity(i)
        }
        btn_simple_render_1.setOnClickListener {
            val i = Intent(this, SimpleRenderActivity::class.java)
            i.putExtra("type", 0)
            startActivity(i)
        }
        btn_simple_render_2.setOnClickListener {
            val i = Intent(this, SimpleRenderActivity::class.java)
            i.putExtra("type", 1)
            startActivity(i)
        }
        btn_simple_render_3.setOnClickListener {
            val i = Intent(this, OpenGLPlayerActivity::class.java)
            startActivity(i)
        }
        btn_simple_render_4.setOnClickListener {
            val i = Intent(this, MultiOpenGLPlayerActivity::class.java)
            startActivity(i)
        }
        btn_simple_render_5.setOnClickListener {
            val i = Intent(this, EGLPlayerActivity::class.java)
            startActivity(i)
        }
        btn_simple_render_6.setOnClickListener {
            val i = Intent(this, OpenGLSoulPlayerActivity::class.java)
            startActivity(i)
        }
        btn_simple_render_7.setOnClickListener {
            val i = Intent(this, SynthesizerActivity::class.java)
            startActivity(i)
        }
    }
}
