package com.zero.tzz.gles.drawer.simple

import android.content.Context
import android.opengl.GLES20
import android.opengl.Matrix
import com.zero.tzz.gles.base.BaseDrawer
import com.zero.tzz.gles.utils.GLHelper

/**
 *
 * @author Zero_Tzz
 * @date 2019-12-17 11:29
 * @description Cube
 */
class Cube(context: Context) : BaseDrawer(context) {

    // 立方体的8个顶点
    private val mVertexCoordinates = floatArrayOf(
        -1.0f, 1.0f, 1.0f,     // 正面左上0
        -1.0f, -1.0f, 1.0f,    // 正面左下1
        1.0f, -1.0f, 1.0f,     // 正面右下2
        1.0f, 1.0f, 1.0f,      // 正面右上3
        -1.0f, 1.0f, -1.0f,    // 反面左上4
        -1.0f, -1.0f, -1.0f,   // 反面左下5
        1.0f, -1.0f, -1.0f,    // 反面右下6
        1.0f, 1.0f, -1.0f      // 反面右上7
    )

    private val mColors = floatArrayOf(
        1f, 0f, 0f, 1f,
        0f, 0f, 1f, 1f,
        0f, 1f, 0f, 1f,
        0f, 1f, 1f, 1f,
        1f, 0f, 1f, 1f,
        1f, 0f, 0f, 1f,
        1f, 1f, 1f, 1f,
        1f, 0f, 1f, 1f
    )

    private val mElementIndex = shortArrayOf(
        6, 7, 4, 6, 4, 5,    // 后面
        6, 3, 7, 6, 2, 3,    // 右面
        6, 5, 1, 6, 1, 2,    // 下面
        0, 3, 2, 0, 2, 1,    // 正面
        0, 1, 5, 0, 5, 4,    // 左面
        0, 7, 3, 0, 4, 7     // 上面
    )

    override fun initGL() {
        // 3D图像 需要开启深度测试
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        mVertexBuffer = GLHelper.floatArrayToBuffer(mVertexCoordinates)
        mColorBuffer = GLHelper.floatArrayToBuffer(mColors)
        mElementIndexBuffer = GLHelper.shortArrayToBuffer(mElementIndex)
        mProgram = GLHelper.createProgramFromAssets(
            mContext.resources,
            "simple/vert/cube.vert",
            "simple/frag/cube.frag"
        )
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
            Matrix.frustumM(mProjectMatrix, 0, -1f, 1f, -ratio, ratio, 3f, 20f)
            Matrix.setLookAtM(mViewMatrix, 0, 5f, 5f, 10f, 0f, 0f, 0f, 0f, 1f, 0f)
            Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0)
        } else {
            ratio = mWorldWidth.toFloat() / mWorldHeight
            // 设置透视投影 以高为基准（max = 1）
            Matrix.frustumM(mProjectMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 20f)
            Matrix.setLookAtM(mViewMatrix, 0, 5f, 5f, 10f, 0f, 0f, 0f, 0f, 1f, 0f)
            Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0)
        }
    }

    override fun doDraw() {
        GLES20.glUseProgram(mProgram)
        mPositionHandler = GLES20.glGetAttribLocation(mProgram, "vPosition")
        mMatrixHandler = GLES20.glGetUniformLocation(mProgram, "vMatrix")
        mColorHandler = GLES20.glGetAttribLocation(mProgram, "aColor")
        GLES20.glEnableVertexAttribArray(mPositionHandler)
        GLES20.glEnableVertexAttribArray(mColorHandler)
        GLES20.glVertexAttribPointer(
            mPositionHandler,
            3,
            GLES20.GL_FLOAT,
            false,
            3 * 4,
            mVertexBuffer
        )
        GLES20.glVertexAttribPointer(mColorHandler, 4, GLES20.GL_FLOAT, false, 0, mColorBuffer)
        GLES20.glUniformMatrix4fv(mMatrixHandler, 1, false, mMVPMatrix, 0)

        // 通过DrawElements 绘制
        GLES20.glDrawElements(
            GLES20.GL_TRIANGLES,
            mElementIndex.size,
            GLES20.GL_UNSIGNED_SHORT,
            mElementIndexBuffer
        )
    }

    override fun release() {
        GLES20.glDisableVertexAttribArray(mPositionHandler)
    }
}