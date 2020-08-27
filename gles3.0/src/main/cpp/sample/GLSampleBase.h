//
// Created by Terry on 2020/4/29.
//

#ifndef ANDROIDTRAININGRECORDER_GLSAMPLEBASE_H
#define ANDROIDTRAININGRECORDER_GLSAMPLEBASE_H

#include <GLES3/gl3.h>
#include <ImageDef.h>
#include "GLUtil.h"
#include "LogUtil.h"

#define SAMPLE_TYPE                     0
#define SAMPLE_TYPE_TRIANGLE            SAMPLE_TYPE
#define SAMPLE_TYPE_TEXTURE_MAP         SAMPLE_TYPE + 1
#define SAMPLE_TYPE_YUV_TEXTURE_MAP         SAMPLE_TYPE + 2

class GLSampleBase {


public:
    GLSampleBase() {
        m_VertexShader = 0;
        m_FragmentShader = 0;
        m_ProgramObj = 0;
    };

    virtual void Init() = 0;

    virtual void Draw(int width, int height) = 0;

    virtual void Destroy() = 0;

    virtual void LoadImage(NativeImage *pImage){};

protected:
    GLuint m_VertexShader;
    GLuint m_FragmentShader;
    GLuint m_ProgramObj;
};

#endif //ANDROIDTRAININGRECORDER_GLSAMPLEBASE_H
