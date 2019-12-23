package com.zero.tzz.gles.drawer.simple

import android.content.Context
import android.opengl.GLES20
import com.zero.tzz.gles.base.BaseDrawer
import com.zero.tzz.gles.utils.GLHelper

/**
 *
 * @author Zero_Tzz
 * @date 2019-12-10 13:29
 * @description Triangle
 */
class TriangleEqual(context: Context) : BaseDrawer(context) {

    private val mVertexCoordinate = floatArrayOf(
        -0.75f, -0.75f, 0f, // 1
        0.75f, -0.75f, 0f, // 2
        0.0f, 0.75f, 0f // 3
    )

    // R-G-B-A
    private val mColor = floatArrayOf(
        1f, 1f, 1f, 1f
    )
    override fun initGL() {
        mVertexBuffer = GLHelper.floatArrayToBuffer(mVertexCoordinate)
        mProgram = GLHelper.createProgramFromAssets(
            mContext.resources,
            "simple/vert/triangle_equal.vert",
            "simple/frag/triangle_equal.frag"
        )
    }

    override fun doDraw() {
        // 将程序加入到OpenGLES2.0环境
        GLES20.glUseProgram(mProgram)
        // 获取顶点着色器的vPosition成员句柄
        mPositionHandler = GLES20.glGetAttribLocation(mProgram, "vPosition")
        // 获取变换矩阵vMatrix成员句柄
        mMatrixHandler = GLES20.glGetUniformLocation(mProgram, "vMatrix")
        // 获取片元着色器的vColor成员句柄
        mColorHandler = GLES20.glGetUniformLocation(mProgram, "vColor")
        // 指定vMatrix值
        GLES20.glUniformMatrix4fv(mMatrixHandler, 1, false, mMVPMatrix, 0)
        // 启用三角形顶点的句柄
        GLES20.glEnableVertexAttribArray(mPositionHandler)
        // 准备三角的顶点坐标数据
        GLES20.glVertexAttribPointer(
            mPositionHandler,
            3,
            GLES20.GL_FLOAT,
            false,
            3 * 4,
            mVertexBuffer
        )
        // 设置颜色
        GLES20.glUniform4fv(mColorHandler, 1, mColor, 0)
        // 绘制三角形
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, mVertexCoordinate.size / 3)
    }

    override fun release() {
        GLES20.glDisableVertexAttribArray(mPositionHandler)
    }
}