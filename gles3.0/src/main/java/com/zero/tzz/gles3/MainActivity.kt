package com.zero.tzz.gles3

import android.os.Bundle
import android.util.TypedValue
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
        tv_value.text = "10sp"
        seekBar.progress = 10
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, progress.toFloat())
                tv_value.text = "${progress}sp"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })
    }
}
