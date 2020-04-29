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
    return 0;
}

GLuint GLUtil::CreateProgram(const char *pVertexShaderSource, const char *pFragmentShaderSource) {
    return 0;
}

void GLUtil::DeleteProgram(GLuint &program) {

}

void GLUtil::CheckGLError(const char *pGLOperation) {

}

