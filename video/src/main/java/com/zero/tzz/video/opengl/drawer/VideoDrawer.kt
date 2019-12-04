package com.zero.tzz.video.opengl.drawer

import android.graphics.SurfaceTexture
import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.opengl.Matrix
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 *
 * @author Zero_Tzz
 * @date 2019-11-29 16:44
 * @description VideoDrawer
 */
class VideoDrawer : IDrawer {
    override fun scale(scaleX: Float, scaleY: Float) {
        Matrix.scaleM(mMatrix, 0, scaleX, scaleY, 1f)
        // x,y越大 缩放效果越大
        mWidthRatio /= scaleX
        mHeightRatio /= scaleY
    }

    override fun alpha(alpha: Float) {
        mAlpha = alpha
    }

    override fun translate(dx: Float, dy: Float) {
        Matrix.translateM(mMatrix, 0, dx * mWidthRatio * 2, -dy * mHeightRatio * 2, 0f)
    }

    companion object {
        const val CODE_VERTEX_SHADER =
            // 顶点坐标
            "attribute vec4 aPosition;" +
                    "precision mediump float;" +
                    // 纹理坐标
                    "attribute vec2 aCoordinate;" +
                    "attribute float alpha;" +
                    "uniform mat4 uMatrix;" +
                    // 用于传递纹理坐标给片元着色器，命名和片元着色器中的一致
                    "varying vec2 vCoordinate;" +
                    "varying float vAlpha;" +
                    "void main() {" +
                    "  gl_Position = uMatrix*aPosition;" +
                    "  vCoordinate = aCoordinate;" +
                    "  vAlpha = alpha;" +
                    "}"
        const val CODE_FRAGMENT_SHADER =
            "#extension GL_OES_EGL_image_external : require\n" +
                    // 配置float精度，使用了float数据一定要配置：lowp(低)/mediump(中)/highp(高)
                    "precision mediump float;" +
                    // 从Java传递进入来的纹理单元
                    "uniform samplerExternalOES uTexture;" +
                    // 从顶点着色器传递进来的纹理坐标
                    "varying vec2 vCoordinate;" +
                    "varying float vAlpha;" +
                    "void main() {" +
                    // 根据纹理坐标，从纹理单元中取色
                    "  vec4 color = texture2D(uTexture,vCoordinate);" +
                    "  gl_FragColor = vec4(color.r, color.g, color.b, vAlpha);" +
                    "}"
    }

    /** 顶点坐标 */
    private val mVertexCoords = floatArrayOf(
        -1.0f, -1.0f,
        1.0f, -1.0f,
        -1.0f, 1.0f,
        1.0f, 1.0f
    )

    /** 纹理坐标 */
    private val mTextureCoords = floatArrayOf(
        0.0f, 1.0f,
        1.0f, 1.0f,
        0.0f, 0.0f,
        1.0f, 0.0f
    )

    /** 渲染器 */
    private var mSurfaceTexture: SurfaceTexture? = null
    private var mSurfaceTextureBlock: ((SurfaceTexture) -> Unit)? = null

    /** 视频宽高 */
    private var mVideoWidth = -1
    private var mVideoHeight = -1

    /** OpenGL宽高 */
    private var mWorldWidth = -1
    private var mWorldHeight = -1

    private var mWidthRatio = 1f
    private var mHeightRatio = 1f

    /** 纹理ID */
    private var mTextureID = -1

    /** OpenGL程序ID */
    private var mProgram = -1

    /** 透明度 */
    private var mAlpha = 1f

    /** 坐标变换矩阵 */
    private var mMatrix: FloatArray? = null

    /** 顶点坐标接收者 */
    private var mVertexPosHandler = -1

    /** 纹理坐标接收者 */
    private var mTexturePosHandler = -1

    /** 纹理接收者 */
    private var mTextureHandler = -1

    /** 矩阵接受者 */
    private var mMatrixHandler = -1

    /** 透明的接收者 */
    private var mAlphaHandler = -1

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

    override fun setVideoSize(width: Int, height: Int) {
        mVideoWidth = width
        mVideoHeight = height
    }

    override fun setWorldSise(width: Int, height: Int) {
        mWorldWidth = width
        mWorldHeight = height
    }

    override fun setTextureID(id: Int) {
        mTextureID = id
        mSurfaceTexture = SurfaceTexture(id)
        mSurfaceTextureBlock?.invoke(mSurfaceTexture!!)
    }

    override fun getSurfaceTexture(block: (sf: SurfaceTexture) -> Unit) {
        mSurfaceTextureBlock = block
    }

    override fun draw() {
        if (mTextureID != -1) {
            // 初始化矩阵
            initDefMatrix()
            // 2.创建GL工程
            createGLProgram()
            // 3.激活并绑定纹理
            activateTexture()
            // 4.绑定图片到纹理单元
            updateTexture()
            // 5.开始绘制
            doDraw()
        }
    }

    private fun initDefMatrix() {
        if (mMatrix != null) return
        if (mVideoWidth != -1 && mVideoHeight != -1 &&
            mWorldWidth != -1 && mWorldHeight != -1
        ) {
            mMatrix = FloatArray(16)
            var prjMatrix = FloatArray(16)
            val originRatio = mVideoWidth / mVideoHeight.toFloat()
            val worldRatio = mWorldWidth / mWorldHeight.toFloat()
            if (mWorldWidth > mWorldHeight) {
                if (originRatio > worldRatio) {
                    mWidthRatio = worldRatio * originRatio
                    Matrix.orthoM(
                        prjMatrix, 0,
                        -mWidthRatio, mWidthRatio,
                        -mHeightRatio, mHeightRatio,
                        3f, 5f
                    )
                } else {// 原始比例小于窗口比例，缩放宽度会导致宽度度超出，因此，宽度以窗口为准，缩放高度
                    mHeightRatio = worldRatio * originRatio
                    Matrix.orthoM(
                        prjMatrix, 0,
                        -mWidthRatio, mWidthRatio,
                        -mHeightRatio, mHeightRatio,
                        3f, 5f
                    )
                }
            } else {
                if (originRatio > worldRatio) {
                    mHeightRatio = originRatio / worldRatio
                    Matrix.orthoM(
                        prjMatrix, 0,
                        -mWidthRatio, mWidthRatio,
                        -mHeightRatio, mHeightRatio,
                        3f, 5f
                    )
                } else {// 原始比例小于窗口比例，缩放高度会导致高度超出，因此，高度以窗口为准，缩放宽度
                    mWidthRatio = originRatio / worldRatio
                    Matrix.orthoM(
                        prjMatrix, 0,
                        -mWidthRatio, mWidthRatio,
                        -mHeightRatio, mHeightRatio,
                        3f, 5f
                    )
                }
            }

            //设置相机位置
            val viewMatrix = FloatArray(16)
            Matrix.setLookAtM(
                viewMatrix, 0,
                0f, 0f, 5.0f,
                0f, 0f, 0f,
                0f, 1.0f, 0f
            )
            //计算变换矩阵
            Matrix.multiplyMM(mMatrix, 0, prjMatrix, 0, viewMatrix, 0)
        }
    }

    private fun createGLProgram() {
        if (mProgram == -1) {
            val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, CODE_VERTEX_SHADER)
            val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, CODE_FRAGMENT_SHADER)

            // 创建OpenGL程序
            mProgram = GLES20.glCreateProgram()
            // 注入顶点/片元着色器
            GLES20.glAttachShader(mProgram, vertexShader)
            GLES20.glAttachShader(mProgram, fragmentShader)
            // 连接
            GLES20.glLinkProgram(mProgram)

            mVertexPosHandler = GLES20.glGetAttribLocation(mProgram, "aPosition")
            mTexturePosHandler = GLES20.glGetAttribLocation(mProgram, "aCoordinate")
            mAlphaHandler = GLES20.glGetAttribLocation(mProgram, "alpha")
            mTextureHandler = GLES20.glGetUniformLocation(mProgram, "uTexture")
            mMatrixHandler = GLES20.glGetUniformLocation(mProgram, "uMatrix")
        }
        // 使用OpenGL程序
        GLES20.glUseProgram(mProgram)
    }

    private fun activateTexture() {
        // 激活指定纹理单元
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        // 绑定纹理到纹理单元
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mTextureID)
        // 注入纹理单元到着色器
        GLES20.glUniform1i(mTextureHandler, 0)
        //配置边缘过渡参数
        GLES20.glTexParameterf(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_MIN_FILTER,
            GLES20.GL_LINEAR.toFloat()
        )
        GLES20.glTexParameterf(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_MAG_FILTER,
            GLES20.GL_LINEAR.toFloat()
        )
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_WRAP_S,
            GLES20.GL_CLAMP_TO_EDGE
        )
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_WRAP_T,
            GLES20.GL_CLAMP_TO_EDGE
        )
    }

    private fun updateTexture() {
        mSurfaceTexture?.updateTexImage()
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
        GLES20.glVertexAttrib1f(mAlphaHandler, mAlpha)
        GLES20.glUniformMatrix4fv(mMatrixHandler, 1, false, mMatrix, 0)
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
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
    }

    override fun release() {
        GLES20.glDisableVertexAttribArray(mVertexPosHandler)
        GLES20.glDisableVertexAttribArray(mTexturePosHandler)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        GLES20.glDeleteTextures(1, intArrayOf(mTextureID), 0)
        GLES20.glDeleteProgram(mProgram)
    }

}