//
// Created by Terry on 2020/4/29.
//

#include "GLSampleBase.h"
#include "LogUtil.h"
#include <GLES3/gl3.h>

#ifndef ANDROIDTRAININGRECORDER_GLRENDERCONTEXT_H
#define ANDROIDTRAININGRECORDER_GLRENDERCONTEXT_H


class GLRenderContext {
    GLRenderContext();

    ~GLRenderContext();

public:
    void OnSurfaceCreated();

    void OnSurfaceChanged(int width, int height);

    void OnDrawFrame();

    static GLRenderContext *GetInstance();

    static void DestoryInstance();

private:
    static GLRenderContext *m_pContext;
    GLSampleBase *m_Sample;
    int m_ScreenW;
    int m_ScreenH;
};

#endif //ANDROIDTRAININGRECORDER_GLRENDERCONTEXT_H
