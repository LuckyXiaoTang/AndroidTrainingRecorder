package com.zero.tzz.gles.base

import android.content.Context
import android.opengl.Matrix
import com.zero.tzz.gles.drawer.IDrawer
import java.nio.FloatBuffer
import java.nio.ShortBuffer

/**
 *
 * @author Zero_Tzz
 * @date 2019-12-10 14:00
 * @description BaseDrawer
 */
abstract class BaseDrawer(protected val mContext: Context) : IDrawer {
    /** Buffer */
    protected var mVertexBuffer: FloatBuffer? = null
    protected var mColorBuffer: FloatBuffer? = null
    protected var mElementIndexBuffer: ShortBuffer? = null
    /** 工程ID */
    protected var mProgram = 0
    /** 纹理ID */
    protected var mTextureID = 0
    /** 世界坐标的宽高 */
    protected var mWorldWidth = 0
    protected var mWorldHeight = 0
    /** 操作句柄 */
    protected var mPositionHandler = 0
    protected var mMatrixHandler = 0
    protected var mColorHandler = 0
    /** 变换矩阵 */
    protected var mProjectMatrix = FloatArray(16)
    protected var mViewMatrix = FloatArray(16)
    protected var mMVPMatrix = FloatArray(16)

    abstract fun initGL()

    override fun setTextureID(id: Int) {
        mTextureID = id
        initGL()
    }

    override fun setWorldSize(width: Int, height: Int) {
        mWorldWidth = width
        mWorldHeight = height
        createDefMatrix()
    }

    private fun createDefMatrix() {
        // 计算宽高比 ratio应该需要 < 1
        val ratio: Float
        if (mWorldWidth > mWorldHeight) {
            ratio = mWorldHeight.toFloat() / mWorldWidth
            // 设置透视投影 以宽为基准（max = 1）
            Matrix.frustumM(mProjectMatrix, 0, -1f, 1f, -ratio, ratio, 3f, 7f)
            Matrix.setLookAtM(mViewMatrix, 0, 0f, 0f, 7f, 0f, 0f, 0f, 0f, 1f, 0f)
            Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0)
        } else {
            ratio = mWorldWidth.toFloat() / mWorldHeight
            // 设置透视投影 以高为基准（max = 1）
            Matrix.frustumM(mProjectMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 7f)
            Matrix.setLookAtM(mViewMatrix, 0, 0f, 0f, 7f, 0f, 0f, 0f, 0f, 1f, 0f)
            Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0)
        }
    }
}