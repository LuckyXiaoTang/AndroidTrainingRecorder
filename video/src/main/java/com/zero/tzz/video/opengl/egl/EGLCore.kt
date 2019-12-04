package com.zero.tzz.video.opengl.egl

import android.graphics.SurfaceTexture
import android.opengl.*
import android.util.Log
import android.view.Surface

/**
 *
 * @author Zero_Tzz
 * @date 2019-12-03 16:22
 * @description EGLCore
 */
const val FLAG_RECORDABLE = 0x01
// Android 指定的标志
// 告诉EGL它创建的surface必须和视频编解码器兼容。
// 没有这个标志，EGL可能会使用一个MediaCodec不能理解的Buffer
// 这个变量在api26以后系统才自带有，为了兼容，我们自己写好这个值0x3142
const val EGL_RECORDABLE_ANDROID = 0x3142
class EGLCore {
    private val TAG = "EGLCore"

    private var mEGLDisplay: EGLDisplay = EGL14.EGL_NO_DISPLAY
    private var mEGLContext: EGLContext = EGL14.EGL_NO_CONTEXT
    private var mEGLConfig: EGLConfig? = null

    /**
     * 初始化EGLDisplay
     * @param eglContext 共享上下文
     * @param flags      初始化标记
     */
    fun init(eglContext: EGLContext?, flags: Int) {
        if (mEGLDisplay !== EGL14.EGL_NO_DISPLAY) {
            throw Throwable("EGL already set")
        }

        val shareContext = eglContext ?: EGL14.EGL_NO_CONTEXT

        // 1.创建EGLDisplay
        mEGLDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY)
        checkEGLDisplay("Unable to get EGL14 display")

        // 2.初始化EGLDisplay
        val version = IntArray(2)
        if (!EGL14.eglInitialize(mEGLDisplay, version, 0, version, 1)) {
            mEGLDisplay = EGL14.EGL_NO_DISPLAY
            throw Throwable("Unable to initialize EGL14")
        }

        // 3.初始化EGLContext, EGLConfig
        if (mEGLContext === EGL14.EGL_NO_CONTEXT) {
            val config =
                getConfig(flags, 2) ?: throw Throwable("Unable to find a suitable EGLConfig")
            val attr2List = intArrayOf(EGL14.EGL_CONTEXT_CLIENT_VERSION, 2, EGL14.EGL_NONE)
            val context = EGL14.eglCreateContext(mEGLDisplay, config, shareContext, attr2List, 0)
            mEGLConfig = config
            mEGLContext = context
        }
    }

    /**
     * 获取EGL配置信息
     * @param flags   初始化标记
     * @param version EGL版本
     */
    private fun getConfig(flags: Int, version: Int): EGLConfig? {
        var renderableType = EGL14.EGL_OPENGL_ES2_BIT
        if (version >= 3) {
            // EGL3
            renderableType = renderableType or EGLExt.EGL_OPENGL_ES3_BIT_KHR
        }

        // 配置数组，主要是配置RAGA位数和深度位数
        // 两个为一对，前面是key，后面是value
        // 数组必须以EGL14.EGL_NONE结尾
        val attrList = intArrayOf(
            EGL14.EGL_RED_SIZE, 8,
            EGL14.EGL_GREEN_SIZE, 8,
            EGL14.EGL_BLUE_SIZE, 8,
            EGL14.EGL_ALPHA_SIZE, 8,
//            EGL14.EGL_DEPTH_SIZE,16,
//            EGL14.EGL_STENCIL_SIZE,8,
            EGL14.EGL_RENDERABLE_TYPE, renderableType,
            EGL14.EGL_NONE, 0, // placeholder for recordable [@-3]
            EGL14.EGL_NONE
        )
        // 配置Android指定的标记
        if (flags and FLAG_RECORDABLE != 0) {
            attrList[attrList.size - 3] = EGL_RECORDABLE_ANDROID
            attrList[attrList.size - 2] = 1
        }

        val configs = arrayOfNulls<EGLConfig>(1)
        val numConfig = IntArray(1)

        //获取可用的EGL配置列表
        if (!EGL14.eglChooseConfig(
                mEGLDisplay,
                attrList,
                0,
                configs,
                0,
                configs.size,
                numConfig,
                0
            )
        ) {
            Log.e(TAG, "Unable to find RGB8888 / $version EGLConfig")
            return null
        }
        return configs[0]
    }

    /**
     * 创建可显示的渲染缓存
     * @param surface 渲染窗口的surface
     */
    fun createWindowSurface(surface: Any): EGLSurface? {
        if (surface !is Surface && surface !is SurfaceTexture) {
            throw Throwable("Invalid surface:$surface")
        }
        val surfaceAttrs = intArrayOf(EGL14.EGL_NONE)
        return EGL14.eglCreateWindowSurface(mEGLDisplay, mEGLConfig, surface, surfaceAttrs, 0)
            ?: throw Throwable("Surface created, but surface was null")
    }

    /**
     * 创建离屏渲染缓存
     * @param width 缓存宽
     * @param height 缓存高
     */
    fun createPbufferSurface(width: Int, height: Int): EGLSurface {
        val surfaceAttrs = intArrayOf(
            EGL14.EGL_WIDTH, width,
            EGL14.EGL_HEIGHT, height,
            EGL14.EGL_NONE
        )

        return EGL14.eglCreatePbufferSurface(mEGLDisplay, mEGLConfig, surfaceAttrs, 0)
            ?: throw Throwable("Surface created, but surface was null")
    }

    /**
     * 将当前线程与上下文进行绑定
     * @param eglSurface 读写的窗口
     */
    fun makeCurrent(eglSurface: EGLSurface) {
        checkEGLDisplay("EGLDisplay is null, call init first")

        if (!EGL14.eglMakeCurrent(mEGLDisplay, eglSurface, eglSurface, mEGLContext)) {
            throw Throwable("(eglSurface:$eglSurface) , makeCurrent failed")
        }
    }

    /**
     * 将当前线程与上下文进行绑定
     * @param drawSurface 写图像数据缓存
     * @param readSurface 读图像数据缓存
     */
    fun makeCurrent(drawSurface: EGLSurface, readSurface: EGLSurface) {
        checkEGLDisplay("EGLDisplay is null, call init first")

        if (!EGL14.eglMakeCurrent(mEGLDisplay, drawSurface, readSurface, mEGLContext)) {
            throw Throwable("(drawSurface:$drawSurface,readSurface:$readSurface) , makeCurrent failed")
        }
    }

    /**
     * 将缓存图片发送到窗口进行显示
     * @param eglSurface 显示的窗口
     */
    fun swapBuffer(eglSurface: EGLSurface): Boolean {
        return EGL14.eglSwapBuffers(mEGLDisplay, eglSurface)
    }

    /**
     * 设置当前帧时间戳
     * @param eglSurface 窗口
     * @param nsecs 时间戳(纳秒)
     */
    fun setPresentationTimestamp(eglSurface: EGLSurface, nsecs: Long) {
        EGLExt.eglPresentationTimeANDROID(mEGLDisplay, eglSurface, nsecs)
    }

    /**
     * 销毁Surface，解绑上下文
     * @param eglSurface 需要销毁的surface
     */
    fun destoryEGLSurface(eglSurface: EGLSurface) {
        EGL14.eglMakeCurrent(
            mEGLDisplay,
            EGL14.EGL_NO_SURFACE,
            EGL14.EGL_NO_SURFACE,
            EGL14.EGL_NO_CONTEXT
        )
        EGL14.eglDestroySurface(mEGLDisplay, eglSurface)
    }

    fun release() {
        if (mEGLDisplay !== EGL14.EGL_NO_DISPLAY) {
            // Android is unusual in that it uses a reference-counted EGLDisplay.  So for
            // every eglInitialize() we need an eglTerminate().
            EGL14.eglMakeCurrent(
                mEGLDisplay,
                EGL14.EGL_NO_SURFACE,
                EGL14.EGL_NO_SURFACE,
                EGL14.EGL_NO_CONTEXT
            )
            EGL14.eglDestroyContext(mEGLDisplay, mEGLContext)
            EGL14.eglReleaseThread()
            EGL14.eglTerminate(mEGLDisplay)
        }
        mEGLDisplay = EGL14.EGL_NO_DISPLAY
        mEGLContext = EGL14.EGL_NO_CONTEXT
        mEGLConfig = null
    }

    private fun checkEGLDisplay(log: String) {
        if (mEGLDisplay === EGL14.EGL_NO_DISPLAY) {
            throw Throwable(log)
        }
    }
}