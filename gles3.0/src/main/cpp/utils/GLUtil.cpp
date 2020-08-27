//
// Created by Terry on 2020/4/29.
//

#include "GLUtil.h"
#include <stdlib.h>
#include <LogUtil.h>

GLuint GLUtil::LoadShader(GLenum shaderType, const char *pSource) {
    GLuint shader = 0;
    shader = glCreateShader(shaderType);
    if (shader) {
        glShaderSource(shader, 1, &pSource, NULL);
        glCompileShader(shader);
        GLint compiled = 0;
        glGetShaderiv(shader, GL_COMPILE_STATUS, &compiled);
        if (!compiled) {
            GLint infoLen = 0;
            glGetShaderiv(shader, GL_INFO_LOG_LENGTH, &infoLen);
            if (infoLen) {
                char *buf = (char *) malloc((size_t) infoLen);
                if (buf) {
                    glGetShaderInfoLog(shader, infoLen, NULL, buf);
                    LOGE("GLUtils::LoadShader Could not compile shader %d:\n%s\n", shaderType, buf);
                    free(buf);
                }
                glDeleteShader(shader);
                shader = 0;
            }
        }
    }
    return shader;
}

GLuint GLUtil::CreateProgram(const char *pVertexShaderSource, const char *pFragmentShaderSource,
                             GLuint &vertexShaderHandle, GLuint &fragmentShaderHandle) {
    GLuint program = 0;
    vertexShaderHandle = LoadShader(GL_VERTEX_SHADER, pVertexShaderSource);
    if (!vertexShaderHandle)return program;
    fragmentShaderHandle = LoadShader(GL_FRAGMENT_SHADER, pFragmentShaderSource);
    if (!fragmentShaderHandle)return program;
    program = glCreateProgram();
    if (program) {
        glAttachShader(program, vertexShaderHandle);
        CheckGLError("glAttachShader");
        glAttachShader(program, fragmentShaderHandle);
        CheckGLError("glAttachShader");
        glLinkProgram(program);
        GLint linkStatus = GL_FALSE;
        glGetProgramiv(program, GL_LINK_STATUS, &linkStatus);

        glDetachShader(program, vertexShaderHandle);
        glDeleteShader(vertexShaderHandle);
        vertexShaderHandle = 0;
        glDetachShader(program, fragmentShaderHandle);
        glDeleteShader(fragmentShaderHandle);
        fragmentShaderHandle = 0;
        if (linkStatus != GL_TRUE) {
            GLint bufLength = 0;
            glGetProgramiv(program, GL_INFO_LOG_LENGTH, &bufLength);
            if (bufLength) {
                char *buf = (char *) malloc((size_t) bufLength);
                if (buf) {
                    glGetProgramInfoLog(program, bufLength, NULL, buf);
                    LOGE("GLUtils::CreateProgram Could not link program:\n%s\n", buf);
                    free(buf);
                }
            }
            glDeleteProgram(program);
            program = GL_NONE;
        }
    }
    LOGE("GLUtils::CreateProgram program = %d", program);
    return program;
}

GLuint GLUtil::CreateProgram(const char *pVertexShaderSource, const char *pFragmentShaderSource) {
    GLuint vertexShaderHandle, fragmentShaderHandle;
    return CreateProgram(pVertexShaderSource,pFragmentShaderSource,vertexShaderHandle,fragmentShaderHandle);
}

void GLUtil::DeleteProgram(GLuint &program) {
    LOGE("GLUtils::DeleteProgram");
    if (program) {
        glUseProgram(GL_NONE);
        glDeleteProgram(program);
        program = GL_NONE;
    }
}

void GLUtil::CheckGLError(const char *pGLOperation) {
    for (GLint error = glGetError(); error; error = glGetError()) {
        LOGE("GLUtils::CheckGLError GL Operation %s() glError (0x%x)\n", pGLOperation, error);
    }
}

