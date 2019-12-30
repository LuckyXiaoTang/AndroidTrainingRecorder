package com.zero.tzz.coroutine

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zero.tzz.coroutine.core.HttpManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tv.setOnClickListener {
            HttpManager.getDefault().get<Bean>("https://wanandroid.com/wxarticle/chapters/json") {
                success {

                }
                error {

                }
            }
        }
    }
}
