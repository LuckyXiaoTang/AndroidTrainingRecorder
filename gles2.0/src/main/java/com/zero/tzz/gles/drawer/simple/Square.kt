package com.zero.tzz.gles.drawer.simple

import android.content.Context
import android.opengl.GLES20
import com.zero.tzz.gles.base.BaseDrawer
import com.zero.tzz.gles.utils.GLHelper

/**
 *
 * @author Zero_Tzz
 * @date 2019-12-16 16:13
 * @description Square
 */
class Square(context: Context) : BaseDrawer(context) {

    private val mVertexCoordinates = floatArrayOf(
        -0.75f, -0.75f, 0f,
        0.75f, -0.75f, 0f,
        -0.75f, 0.75f, 0f,
        0.75f, 0.75f, 0f
    )

    private val mColors = floatArrayOf(
        1f, 0f, 0f, 1f,
        0f, 0f, 1f, 1f,
        0f, 1f, 0f, 1f,
        1f, 0f, 1f, 1f
    )

    private val mElementIndex = shortArrayOf(0, 1, 2, 1, 2, 3)

    override fun initGL() {
        mVertexBuffer = GLHelper.floatArrayToBuffer(mVertexCoordinates)
        mColorBuffer = GLHelper.floatArrayToBuffer(mColors)
        mElementIndexBuffer = GLHelper.shortArrayToBuffer(mElementIndex)
        mProgram = GLHelper.createProgramFromAssets(
            mContext.resources,
            "simple/vert/square.vert",
            "simple/frag/square.frag"
        )

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

        // 通过DrawArrays 绘制
        // GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, mVertexCoordinates.size / 3)
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