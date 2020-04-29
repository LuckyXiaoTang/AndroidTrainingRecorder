//
// Created by Terry on 2020/4/29.
//

#ifndef ANDROIDTRAININGRECORDER_GLUTIL_H
#define ANDROIDTRAININGRECORDER_GLUTIL_H

#include "GLES3/gl3.h"

class GLUtil {
public:
    static GLuint LoadShader(GLenum shaderType, const char *pSource);

    static GLuint CreateProgram(const char *pVertexShaderSource, const char *pFragmentShaderSource,
                                GLuint &vertexShaderHandle, GLuint &fragmentShaderHandle);

    static GLuint CreateProgram(const char *pVertexShaderSource, const char *pFragmentShaderSource);


    static void DeleteProgram(GLuint &program);

    static void CheckGLError(const char *pGLOperation);
};


#endif //ANDROIDTRAININGRECORDER_GLUTIL_H
