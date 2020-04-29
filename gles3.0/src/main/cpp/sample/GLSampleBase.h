//
// Created by Terry on 2020/4/29.
//

#ifndef ANDROIDTRAININGRECORDER_GLSAMPLEBASE_H
#define ANDROIDTRAININGRECORDER_GLSAMPLEBASE_H

#include <GLES3/gl3.h>
#include "GLUtil.h"

class GLSampleBase {


public:
    GLSampleBase() {
        m_VertexShader = 0;
        m_FragmentShader = 0;
        m_ProgramObj = 0;
    };

    virtual void Init() = 0;

    virtual void Draw(int width, int height) = 0;

    virtual void Destory() = 0;

protected:
    GLuint m_VertexShader;
    GLuint m_FragmentShader;
    GLuint m_ProgramObj;
};

#endif //ANDROIDTRAININGRECORDER_GLSAMPLEBASE_H
