package com.zero.tzz.gles.drawer.simple

import android.content.Context
import android.opengl.GLES20
import com.zero.tzz.gles.base.BaseDrawer
import com.zero.tzz.gles.utils.GLHelper
import kotlin.math.cos
import kotlin.math.sin

/**
 *
 * @author Zero_Tzz
 * @date 2019-12-16 16:14
 * @description Circular
 */
class Circular(context: Context) : BaseDrawer(context) {

    // 圆形顶点坐标
    private val mVertexCoordinates = mutableListOf<Float>()
    // 圆形切片份数（越大 越精细）
    private val N = 360
    // 圆形 半径
    private val mRadius = 0.75f

    private val mColors = floatArrayOf(1f, 1f, 1f, 1f)

    override fun initGL() {
        createVertexCoordinates()
        mVertexBuffer = GLHelper.floatArrayToBuffer(mVertexCoordinates.toFloatArray())
        mProgram = GLHelper.createProgramFromAssets(
            mContext.resources,
            "simple/vert/circular.vert",
            "simple/frag/circular.frag")
    }

    private fun createVertexCoordinates() {
        // 圆点
        mVertexCoordinates.add(0f)
        mVertexCoordinates.add(0f)
        mVertexCoordinates.add(0f)
        // 切片坐标
        val span = 360f / N
        var angle = 0f
        while (angle < span + 360f) {
            val x = mRadius * sin(angle * Math.PI / 180f)
            val y = mRadius * cos(angle * Math.PI / 180f)
            val z = 0f
            mVertexCoordinates.add(x.toFloat())
            mVertexCoordinates.add(y.toFloat())
            mVertexCoordinates.add(z)
            angle += span
        }
    }

    override fun doDraw() {
        GLES20.glUseProgram(mProgram)
        mPositionHandler = GLES20.glGetAttribLocation(mProgram, "vPosition")
        mMatrixHandler = GLES20.glGetUniformLocation(mProgram, "vMatrix")
        mColorHandler = GLES20.glGetUniformLocation(mProgram, "vColor")
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
        GLES20.glUniform4fv(mColorHandler, 1, mColors, 0)
        GLES20.glUniformMatrix4fv(mMatrixHandler, 1, false, mMVPMatrix, 0)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, mVertexCoordinates.size / 3)
    }

    override fun release() {
        GLES20.glDisableVertexAttribArray(mPositionHandler)
    }
}