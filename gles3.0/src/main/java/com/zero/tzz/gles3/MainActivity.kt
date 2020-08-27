package com.zero.tzz.gles3

import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val mTitles = arrayOf(
        "三角形",
        "纹理",
        "NV21"
    )
    private var mRootView: View? = null
    private var mDialog: AlertDialog? = null
    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: SampleRvAdapter? = null
    private var mSampleSelectedIndex: Int = NativeRender.SAMPLE_TYPE_TRIANGLE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        btn_select.setOnClickListener { showGLSampleDialog() }
    }

    override fun onDestroy() {
        super.onDestroy()
        glsv.unInit()
    }

    private fun showGLSampleDialog() {
        if (mDialog == null) {
            mDialog = AlertDialog.Builder(this).create().apply {
                mRootView = LayoutInflater.from(this@MainActivity)
                    .inflate(R.layout.sample_selected_layout, null)
                mRootView!!.findViewById<Button>(R.id.confirm_btn).setOnClickListener { cancel() }
                mRecyclerView = mRootView!!.findViewById(R.id.recyclerView)
                mRecyclerView!!.layoutManager = LinearLayoutManager(this@MainActivity)
                mAdapter = SampleRvAdapter(this@MainActivity, mTitles.toList())
                mRecyclerView!!.adapter = mAdapter
                mAdapter!!.addOnItemClickListener { view, position -> selectSample(view, position) }
            }
        }
        mRecyclerView?.scrollToPosition(mSampleSelectedIndex)
        mDialog?.show()
        mDialog?.window?.setContentView(mRootView)
    }

    private fun selectSample(view: View?, position: Int) {
        mAdapter?.let { adapter ->
            val index = adapter.getSelectIndex()
            adapter.setSelectIndex(position)
            adapter.notifyItemChanged(index)
            adapter.notifyItemChanged(position)
            mSampleSelectedIndex = position
            glsv.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
            glsv.getRender().setParamsInt(position)
            when (position) {
                NativeRender.SAMPLE_TYPE_TRIANGLE -> {
                    // 最简单图形，不需要做任何操作
                }
                NativeRender.SAMPLE_TYPE_TEXTURE_MAP -> {
                    // 导入RGB图片
                    loadRGBAImage(R.drawable.texture01, glsv)
                }
                NativeRender.SAMPLE_TYPE_YUV_TEXTURE_MAP -> {
                    loadNV21Image("YUV_Image_840x1074.NV21", 840, 1074, glsv)
                }
            }
            glsv.requestRender()
            mDialog?.cancel()
        }
    }
}
