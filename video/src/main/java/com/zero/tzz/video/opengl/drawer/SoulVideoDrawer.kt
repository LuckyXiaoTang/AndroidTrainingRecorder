package com.zero.tzz.video.opengl.drawer

import android.graphics.SurfaceTexture
import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.opengl.Matrix
import com.zero.tzz.video.opengl.GLTools
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 *
 * @author Zero_Tzz
 * @date 2019-11-29 16:44
 * @description VideoDrawer
 */
class SoulVideoDrawer : IDrawer {
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
                    "uniform mat4 uMatrix;" +
                    "attribute vec2 aCoordinate;" +
                    "varying vec2 vCoordinate;" +
                    "attribute float alpha;" +
                    "varying float inAlpha;" +
                    "void main() {" +
                    "    gl_Position = uMatrix*aPosition;" +
                    "    vCoordinate = aCoordinate;" +
                    "    inAlpha = alpha;" +
                    "}"
        const val CODE_FRAGMENT_SHADER =
            "#extension GL_OES_EGL_image_external : require\n" +
                    // 配置float精度，使用了float数据一定要配置：lowp(低)/mediump(中)/highp(高)
                    "precision mediump float;" +
                    // 从Java传递进入来的纹理单元
                    "uniform samplerExternalOES uTexture;" +
                    // 从顶点着色器传递进来的纹理坐标
                    "varying vec2 vCoordinate;" +
                    "varying float inAlpha;" +
                    "uniform sampler2D uSoulTexture;" +
                    "uniform float progress;" +
                    "uniform int drawFBO;" +
                    "void main() {" +
                        // 透明度[0,0.4]
                        "float alpha = 0.6 * (1.0 - progress);" +
                        // 缩放比例[1.0,1.8]
                        "float scale = 1.0 + (1.5 - 1.0) * progress;" +

                        // 放大纹理坐标
                        // 根据放大比例，得到放大纹理坐标 [0,0],[0,1],[1,1],[1,0]
                        "float soulX = 0.5 + (vCoordinate.x - 0.5) / scale;\n" +
                        "float soulY = 0.5 + (vCoordinate.y - 0.5) / scale;\n" +
                        "vec2 soulTextureCoords = vec2(soulX, soulY);" +
                        // 获取对应放大纹理坐标下的纹素(颜色值rgba)
                        "vec4 soulMask = texture2D(uSoulTexture, soulTextureCoords);" +

                        "vec4 color = texture2D(uTexture, vCoordinate);" +

                        "if (drawFBO == 0) {" +
                        // 颜色混合 默认颜色混合方程式 = mask * (1.0-alpha) + weakMask * alpha
                        "    gl_FragColor = color * (1.0 - alpha) + soulMask * alpha;" +
                        "} else {" +
                        "   gl_FragColor = vec4(color.r, color.g, color.b, inAlpha);" +
                        "}" +
                    "}"
    }

    /** 默认顶点坐标 */
    private val mDefVertexCoords = floatArrayOf(
        -1.0f, -1.0f,
        1.0f, -1.0f,
        -1.0f, 1.0f,
        1.0f, 1.0f
    )

    /** 上下颠倒的顶点矩阵 */
    private val mReserveVertexCoords = floatArrayOf(
        -1f, 1f,
        1f, 1f,
        -1f, -1f,
        1f, -1f
    )

    /** 顶点坐标 */
    private var mVertexCoords = mDefVertexCoords

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

    //////////////////////////////////////////// 灵魂出窍特效 成员变量 ////////////////////////////////////////////
    /** 帧缓存 */
    private var mSoulFrameBuffer = -1

    /** 纹理ID */
    private var mSoulTextureID = -1

    /** 纹理接收者 */
    private var mSoulTextureHandler = -1

    /** 进度接收者 */
    private var mSoulProgressHandler = -1

    /** 是否更新FBO */
    private var mDrawFBO = -1

    /** 更新FBO标记接收者 */
    private var mSoulDrawFBOHandler = -1

    /** 一帧FBO的间隔时间 */
    private var mModifyTime = -1L

    private lateinit var mVertexBuffer: FloatBuffer
    private lateinit var mTextureBuffer: FloatBuffer

    init {
        // 1.初始化
        initPos()
    }

    private fun initPos() {
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
            // 3.更新FBO
            updateFBO()
            // 4.激活并绑定灵魂出窍纹理
            activeSoulTexture()
            // 5.激活并绑定默认纹理
            activeDefTexture()
            // 6.绑定图片到纹理单元
            updateTexture()
            // 7.开始绘制
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

            mSoulTextureHandler = GLES20.glGetUniformLocation(mProgram, "uSoulTexture")
            mSoulProgressHandler = GLES20.glGetUniformLocation(mProgram, "progress")
            mSoulDrawFBOHandler = GLES20.glGetUniformLocation(mProgram, "drawFBO")
        }
        // 使用OpenGL程序
        GLES20.glUseProgram(mProgram)
    }

    private fun updateFBO() {
        if (mSoulTextureID == -1) {
            // 创建FBO纹理
            mSoulTextureID = GLTools.createFBOTexture(mVideoWidth, mVideoHeight)
        }
        if (mSoulFrameBuffer == -1) {
            mSoulFrameBuffer = GLTools.createFrameBuffer()
        }
        if (System.currentTimeMillis() - mModifyTime > 500) {
            mModifyTime = System.currentTimeMillis()
            // 绑定FBO
            GLTools.bindFBO(mSoulFrameBuffer, mSoulTextureID)
            // 配置FBO窗口
            configFBOViewport()
            // 激活默认纹理
            activeDefTexture()
            // 更新纹理
            updateTexture()
            // 绘制到FBO
            doDraw()
            // 解绑FBO
            GLTools.unbindFBO()
            // 恢复默认绘制窗口
            configDefViewport()
        }
    }

    private fun configFBOViewport() {
        mDrawFBO = 1
        // 将变换矩阵回复为单位矩阵（将画面拉升到整个窗口大小，设置窗口宽高和FBO纹理宽高一致，画面刚好可以正常绘制到FBO纹理上）
        Matrix.setIdentityM(mMatrix, 0)
        // 设置颠倒的顶点坐标
        mVertexCoords = mReserveVertexCoords
        // 重新初始化顶点坐标
        initPos()
        GLES20.glViewport(0, 0, mVideoWidth, mVideoHeight)
        GLES20.glClearColor(0f, 0f, 0f, 0f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
    }

    private fun configDefViewport() {
        mDrawFBO = 0
        mMatrix = null
        mVertexCoords = mDefVertexCoords
        initPos()
        initDefMatrix()
        GLES20.glViewport(0, 0, mWorldWidth, mWorldHeight)
    }


    private fun activeDefTexture() {
        activateTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mTextureID, 0, mTextureHandler)
    }

    private fun activeSoulTexture() {
        activateTexture(GLES20.GL_TEXTURE_2D, mSoulTextureID, 1, mSoulTextureHandler)
    }

    private fun activateTexture(type: Int, textureId: Int, index: Int, textureHandler: Int) {
        // 激活指定纹理单元
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + index)
        // 绑定纹理到纹理单元
        GLES20.glBindTexture(type, textureId)
        // 注入纹理单元到着色器
        GLES20.glUniform1i(textureHandler, index)
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
        GLES20.glUniform1f(mSoulProgressHandler, (System.currentTimeMillis() - mModifyTime) / 500f)
        GLES20.glUniform1i(mSoulDrawFBOHandler, mDrawFBO)
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