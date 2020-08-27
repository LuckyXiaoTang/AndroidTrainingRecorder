package com.topvision.media.drawer

import android.graphics.SurfaceTexture
import android.opengl.GLES20
import com.topvision.media.egl.IDrawer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 *
 * @author Zero_Tzz
 * @date 2019-11-29 14:15
 * @description TriangleDrawer
 */
class TriangleDrawer : IDrawer {
    override fun scale(scaleX: Float, scaleY: Float) {

    }

    override fun alpha(alpha: Float) {

    }

    override fun translate(x: Float, y: Float) {

    }

    override fun setVideoSize(width: Int, height: Int) {

    }

    override fun setWorldSise(width: Int, height: Int) {

    }

    companion object {
        const val CODE_VERTEX_SHADER =
            "attribute vec4 aPosition;" +
                    "void main() {" +
                    "  gl_Position = aPosition;" +
                    "}"
        const val CODE_FRAGMENT_SHADER =
            "precision mediump float;" +
                    "void main() {" +
                    "  gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0);" +
                    "}"
    }

    /** 顶点坐标 */
    private val mVertexCoords = floatArrayOf(
        -0.5f, -0.5f,
        0.5f, -0.5f,
        0.0f, 0.5f
    )

    /** 纹理坐标 */
    private val mTextureCoords = floatArrayOf(
        0.25f, 0.75f,
        0.75f, 0.75f,
        0.5f, 0.25f
    )

    /** 纹理ID */
    private var mTextureID = -1

    /** OpenGL程序ID */
    private var mProgram = -1

    /** 顶点坐标接收者 */
    private var mVertexPosHandler = -1

    /** 纹理坐标接收者 */
    private var mTexturePosHandler = -1

    private lateinit var mVertexBuffer: FloatBuffer
    private lateinit var mTextureBuffer: FloatBuffer

    init {
        // 1.初始化
        init()
    }

    private fun init() {
        // 顶点Array -> 顶点Buffer
        val vb = ByteBuffer.allocateDirect(mVertexCoords.size * 4)
        vb.order(ByteOrder.nativeOrder())
        mVertexBuffer = vb.asFloatBuffer()
        mVertexBuffer.put(mVertexCoords)
        mVertexBuffer.position(0)

        // 纹理Array -> 纹理Buffer
        val tb = ByteBuffer.allocateDirect(mTextureCoords.size * 4)
        tb.order(ByteOrder.nativeOrder())
        mTextureBuffer = tb.asFloatBuffer()
        mTextureBuffer.put(mTextureCoords)
        mTextureBuffer.position(0)
    }

    override fun setTextureID(id: Int) {
        mTextureID = id
    }

    override fun draw() {
        if (mTextureID != -1) {
            // 2.创建GL工程
            createGLProgram()
            // 3.开始绘制
            doDraw()
        }
    }

    private fun createGLProgram() {
        if (mProgram == -1) {
            val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER,
                CODE_VERTEX_SHADER
            )
            val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER,
                CODE_FRAGMENT_SHADER
            )

            // 创建OpenGL程序
            mProgram = GLES20.glCreateProgram()
            // 注入顶点/片元着色器
            GLES20.glAttachShader(mProgram, vertexShader)
            GLES20.glAttachShader(mProgram, fragmentShader)
            // 连接
            GLES20.glLinkProgram(mProgram)

            mVertexPosHandler = GLES20.glGetAttribLocation(mProgram, "aPosition")
            mTexturePosHandler = GLES20.glGetAttribLocation(mProgram, "aCoordinate")
        }
        // 使用OpenGL程序
        GLES20.glUseProgram(mProgram)
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        // 根据type创建顶点/片元着色器
        val shader = GLES20.glCreateShader(type)
        // 资源注入到着色器
        GLES20.glShaderSource(shader, shaderCode)
        // 编译
        GLES20.glCompileShader(shader)

        return shader
    }

    private fun doDraw() {
        // 启动句柄
        GLES20.glEnableVertexAttribArray(mVertexPosHandler)
        GLES20.glEnableVertexAttribArray(mTexturePosHandler)
        // 设置着色器参数  第二个参数表示一个顶点包含的数据数量，这里为xy，所以为2
        GLES20.glVertexAttribPointer(mVertexPosHandler, 2, GLES20.GL_FLOAT, false, 0, mVertexBuffer)
        GLES20.glVertexAttribPointer(
            mTexturePosHandler,
            2,
            GLES20.GL_FLOAT,
            false,
            0,
            mTextureBuffer
        )
        // 开始绘制
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 3)
    }

    override fun release() {
        GLES20.glDisableVertexAttribArray(mVertexPosHandler)
        GLES20.glDisableVertexAttribArray(mTexturePosHandler)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        GLES20.glDeleteTextures(1, intArrayOf(mTextureID), 0)
        GLES20.glDeleteProgram(mProgram)
    }

    override fun getSurfaceTexture(block: (sf: SurfaceTexture) -> Unit) {

    }
}