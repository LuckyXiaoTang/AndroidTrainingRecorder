package com.zero.tzz.gles.drawer.simple

import android.content.Context
import android.opengl.GLES20
import android.opengl.Matrix
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
class Ball(context: Context) : BaseDrawer(context) {

    // 球体顶点坐标
    private val mVertexCoordinates = mutableListOf<Float>()
    // 球体切片份数（越大 越精细）
    private val N = 360
    // 球体 半径
    private val mRadius = 1f

    override fun initGL() {
        // 3D图形 开启深度测试
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        createVertexCoordinates()
        mVertexBuffer = GLHelper.floatArrayToBuffer(mVertexCoordinates.toFloatArray())
        mProgram = GLHelper.createProgramFromAssets(
            mContext.resources,
            "simple/vert/ball.vert",
            "simple/frag/ball.frag"
        )
    }

    private fun createVertexCoordinates() {
        // 切片坐标
        val span = 360f / N
        var angle = -90f
        //球以(0,0,0)为中心，以R为半径，则球上任意一点的坐标为
        // ( R * cos(a) * sin(b),y0 = R * sin(a),R * cos(a) * cos(b))
        // 其中，a为圆心到点的线段与xz平面的夹角，b为圆心到点的线段在xz平面的投影与z轴的夹角
        var h1 = 0f
        var r1 = 0f
        var h2 = 0f
        var r2 = 0f
        var sin = 0f
        var cos = 0f
        while (angle < span + 90f) {
            r1 = mRadius * cos(angle * Math.PI / 180f).toFloat()
            r2 = mRadius * cos((angle + span) * Math.PI / 180f).toFloat()
            h1 = mRadius * sin(angle * Math.PI / 180f).toFloat()
            h2 = mRadius * sin((angle + span) * Math.PI / 180f).toFloat()
            // 固定纬度, 360 度旋转遍历一条纬线
            val span2 = span * 2
            var angle2 = 0f
            while (angle2 < span + 360f) {
                cos = cos(angle2 * Math.PI / 180f).toFloat()
                sin = sin(angle2 * Math.PI / 180f).toFloat()
                angle2 += span2

                mVertexCoordinates.add(r2 * cos)
                mVertexCoordinates.add(h2)
                mVertexCoordinates.add(r2 * sin)

                mVertexCoordinates.add(r1 * cos)
                mVertexCoordinates.add(h1)
                mVertexCoordinates.add(r1 * sin)
            }
            angle += span
        }
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
            Matrix.setLookAtM(mViewMatrix, 0, 1.0f, -10.0f, -4.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)
            Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0)
        } else {
            ratio = mWorldWidth.toFloat() / mWorldHeight
            // 设置透视投影 以高为基准（max = 1）
            Matrix.frustumM(mProjectMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 20f)
            Matrix.setLookAtM(mViewMatrix, 0, 1.0f, -10.0f, -4.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)
            Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0)
        }
    }

    override fun doDraw() {
        GLES20.glUseProgram(mProgram)
        mPositionHandler = GLES20.glGetAttribLocation(mProgram, "vPosition")
        mMatrixHandler = GLES20.glGetUniformLocation(mProgram, "vMatrix")
        GLES20.glEnableVertexAttribArray(mPositionHandler)
        GLES20.glVertexAttribPointer(
            mPositionHandler,
            3,
            GLES20.GL_FLOAT,
            false,
            3 * 4,
            mVertexBuffer
        )
        GLES20.glUniformMatrix4fv(mMatrixHandler, 1, false, mMVPMatrix, 0)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, mVertexCoordinates.size / 3)
    }

    override fun release() {
        GLES20.glDisableVertexAttribArray(mPositionHandler)
    }
}