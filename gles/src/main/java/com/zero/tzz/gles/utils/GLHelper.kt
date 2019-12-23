package com.zero.tzz.gles.utils

import android.content.res.Resources
import android.opengl.GLES20
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

/**
 *
 * @author Zero_Tzz
 * @date 2019-12-10 14:10
 * @description GLHelper
 */
object GLHelper {
    private val TAG = "GLHelper"
    fun floatArrayToBuffer(array: FloatArray): FloatBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(array.size * 4)
        byteBuffer.order(ByteOrder.nativeOrder())
        val floatBuffer = byteBuffer.asFloatBuffer()
        floatBuffer.put(array)
        floatBuffer.position(0)
        return floatBuffer
    }

    fun shortArrayToBuffer(array: ShortArray): ShortBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(array.size * 2)
        byteBuffer.order(ByteOrder.nativeOrder())
        val shortBuffer = byteBuffer.asShortBuffer()
        shortBuffer.put(array)
        shortBuffer.position(0)
        return shortBuffer
    }

    private fun loadShaderFromAssets(type: Int, res: Resources, assetPath: String): Int {
        return loadShader(type, loadCodeFromAssets(res, assetPath))
    }

    private fun loadShader(type: Int, code: String): Int {
        var shader = GLES20.glCreateShader(type)
        if (shader != 0) {
            GLES20.glShaderSource(shader, code)
            GLES20.glCompileShader(shader)
            val compiles = IntArray(1)
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiles, 0)
            if (compiles[0] == 0) {
                Log.e(TAG, "Could not compile shader:$type")
                Log.e(TAG, "GLES20 Error:" + GLES20.glGetShaderInfoLog(shader))
                GLES20.glDeleteShader(shader)
                shader = 0
            }
        }
        return shader
    }

    fun createProgramFromAssets(
        res: Resources,
        vertexAssetsPath: String,
        fragmentAssetsPath: String
    ): Int {
        return createProgram(
            loadCodeFromAssets(res, vertexAssetsPath),
            loadCodeFromAssets(res, fragmentAssetsPath)
        )
    }

    fun createProgram(vertexCode: String, fragmentCode: String): Int {
        val vertex = loadShader(GLES20.GL_VERTEX_SHADER, vertexCode)
        val fragment = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentCode)
        if (vertex == 0 || fragment == 0) return 0
        var program = GLES20.glCreateProgram()
        if (program != 0) {
            GLES20.glAttachShader(program, vertex)
            GLES20.glAttachShader(program, fragment)
            GLES20.glLinkProgram(program)
            val links = IntArray(1)
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, links, 0)
            if (links[0] != GLES20.GL_TRUE) {
                Log.e(TAG, "Could not link program:" + GLES20.glGetProgramInfoLog(program))
                GLES20.glDeleteProgram(program)
                program = 0
            }
        }
        return program
    }

    private fun loadCodeFromAssets(res: Resources, assetPath: String): String {
        val sb = StringBuffer()
        val inputStream = res.assets.open(assetPath)
        val br = BufferedReader(InputStreamReader(inputStream))
        try {
            var lineText = br.readLine()
            while (lineText != null) {
                sb.append(lineText).append("\n")
                lineText = br.readLine()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            br.close()
        }
        return sb.toString()
    }
}
