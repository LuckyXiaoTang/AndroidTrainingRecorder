//
// Created by Terry on 2020/4/29.
//

#include "GLRenderContext.h"
#include "TriangleSample.h"
#include "TextureMapSample.h"
#include "NV21TextureMapSample.h"

GLRenderContext *GLRenderContext::m_pContext = nullptr;

GLRenderContext::GLRenderContext() {
    m_pCurrentSample = new TriangleSample();
    m_pBeforeSample = nullptr;
}

GLRenderContext::~GLRenderContext() {
    if (m_pCurrentSample) {
        delete m_pCurrentSample;
        m_pCurrentSample = nullptr;
    }

    if (m_pBeforeSample) {
        delete m_pBeforeSample;
        m_pBeforeSample = nullptr;
    }
}

void GLRenderContext::SetParamsInt(int position) {
    LOGE(">>> GLRenderContext::SetParamsInt position = %d <<<", position);
    m_pBeforeSample = m_pCurrentSample;
    LOGE(">>> GLRenderContext::SetParamsInt m_pBeforeSample = %p <<<", m_pBeforeSample);
    switch (position) {
        case SAMPLE_TYPE_TRIANGLE:
            m_pCurrentSample = new TriangleSample();
            break;
        case SAMPLE_TYPE_TEXTURE_MAP:
            m_pCurrentSample = new TextureMapSample();
            break;
        case SAMPLE_TYPE_YUV_TEXTURE_MAP:
            m_pCurrentSample = new NV21TextureMapSample();
            break;
        default:
            m_pCurrentSample = nullptr;
            break;
    }
    LOGE(">>> GLRenderContext::SetParamsInt m_pBeforeSample = %p, m_pCurrentSample=%p <<<", m_pBeforeSample, m_pCurrentSample);
}

void GLRenderContext::SetImageData(int format, int width, int height, uint8_t *pData) {
    LOGE(">>> GLRenderContext::SetImageData format=%d, width=%d, height=%d, pData=%p <<<", format,
         width, height, pData);
    NativeImage nativeImage;
    nativeImage.format = format;
    nativeImage.width = width;
    nativeImage.height = height;
    nativeImage.ppPlane[0] = pData;
    switch (format) {
        case IMAGE_FORMAT_RGBA:
            break;
        case IMAGE_FORMAT_NV12:
        case IMAGE_FORMAT_NV21:
            nativeImage.ppPlane[1] =
                    nativeImage.ppPlane[0] + width * height;
            break;
        case IMAGE_FORMAT_I420:
            nativeImage.ppPlane[1] =
                    nativeImage.ppPlane[0] + width * height;
            nativeImage.ppPlane[2] =
                    nativeImage.ppPlane[1] + width * height / 4;
            break;
        default:
            break;
    }
    if (m_pCurrentSample) {
        m_pCurrentSample->LoadImage(&nativeImage);
    }
}

GLRenderContext *GLRenderContext::GetInstance() {
    LOGE(">>> GLRenderContext::GetInstance <<<");
    if (m_pContext == nullptr) {
        m_pContext = new GLRenderContext();
    }
    return m_pContext;
}

void GLRenderContext::OnSurfaceCreated() {
    LOGE(">>> GLRenderContext::OnSurfaceCreated <<<");
    glClearColor(1.0f, 1.0f, 0.5f, 1.0f);
    m_pCurrentSample->Init();
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
    if (m_pBeforeSample) {
        m_pBeforeSample->Destroy();
        delete m_pBeforeSample;
        m_pBeforeSample = nullptr;
    }
    if (m_pCurrentSample) {
        m_pCurrentSample->Init();
        m_pCurrentSample->Draw(m_ScreenW, m_ScreenH);
    }
}

void GLRenderContext::DestoryInstance() {
    LOGE(">>> GLRenderContext::GetInstance <<<");
    if (m_pContext) {
        delete m_pContext;
        m_pContext = nullptr;
    }
}
