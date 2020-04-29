//
// Created by Terry on 2020/4/29.
//

#include "GLRenderContext.h"

GLRenderContext::GLRenderContext() {

}

GLRenderContext::~GLRenderContext() {

}

void GLRenderContext::OnSurfaceCreated() {
    LOGE(">>> GLRenderContext::OnSurfaceCreated <<<");
    glClearColor(1f, 1f, 0.5f, 1f);
    m_Sample->Init();
}

void GLRenderContext::OnSurfaceChanged(int width, int height) {
    LOGE(">>> GLRenderContext::OnSurfaceChanged [w,h] = [%d,%d] <<<", width, height);
    glViewport(0, 0, width, height);
    m_ScreenW = width;
    m_ScreenH = height;
}

void GLRenderContext::OnDrawFrame() {
    LOGE(">>> GLRenderContext::OnDrawFrame <<<");
    glClear(GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
    m_Sample->Draw(m_ScreenW, m_ScreenH);
}

GLRenderContext *GLRenderContext::GetInstance() {
    LOGE(">>> GLRenderContext::GetInstance <<<");
    if (m_pContext == NULL) {
        m_pContext = new GLRenderContext();
    }
    return m_pContext;
}

void GLRenderContext::DestoryInstance() {
    LOGE(">>> GLRenderContext::GetInstance <<<");
    if (m_pContext) {
        delete m_pContext;
        m_pContext = nullptr;
    }
}
