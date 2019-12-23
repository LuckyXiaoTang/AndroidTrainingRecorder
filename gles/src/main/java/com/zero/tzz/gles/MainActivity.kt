package com.zero.tzz.gles

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zero.tzz.gles.ui.DrawSimpleActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val map = mutableMapOf<String, Class<out Activity>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView.layoutManager = LinearLayoutManager(this)
        initAllDemo()
        MainAdapter(map.keys.toList()).apply {
            recyclerView.adapter = this
            setOnItemClickListener {
//                safetyStartActivity(map[it]!!)
//                Test.output(1)
                Test.output(2)
//                Test.output(3)
//                Test.output(4)
            }
        }
    }

    private fun initAllDemo() {
        map["绘制形体"] = DrawSimpleActivity::class.java
        map["图片处理"] = DrawSimpleActivity::class.java
    }

    class MainAdapter(val list: List<String>) : RecyclerView.Adapter<MainAdapter.MainViewHolder>() {

        private var mListener: ((String) -> Unit)? = null
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_main_list, parent, false)
            return MainViewHolder(view)
        }

        override fun getItemCount(): Int = list.size

        override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
            holder.tvTitle.text = list[position]
        }

        fun setOnItemClickListener(block: (String) -> Unit) {
            mListener = block
        }

        inner class MainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvTitle: TextView = itemView.findViewById(R.id.tv_title)

            init {
                itemView.setOnClickListener {
                    mListener?.invoke(list[adapterPosition])
                }
            }
        }
    }
}
