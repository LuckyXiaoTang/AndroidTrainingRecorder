//
// Created by Terry on 2020/7/10.
//

#ifndef ANDROIDTRAININGRECORDER_NV21TEXTUREMAPSAMPLE_H
#define ANDROIDTRAININGRECORDER_NV21TEXTUREMAPSAMPLE_H

#include "GLSampleBase.h"
#include "../utils/ImageDef.h"

class NV21TextureMapSample : public GLSampleBase {

public:
    NV21TextureMapSample() {
        m_TextureId_Y = GL_NONE;
        m_TextureId_UV = GL_NONE;
        m_SampplerLoc_Y = GL_NONE;
        m_SampplerLoc_UV = GL_NONE;
    }

    virtual ~NV21TextureMapSample() {
        NativeImageUtil::FreeNativeImage(&m_RenderImage)
    }

    virtual void LoadImage(NativeImage *pImage);

    virtual void Init();

    virtual void Draw(int screenW,int screenH);

    virtual void Destroy();

private:
    GLuint m_TextureId_Y;
    GLuint m_TextureId_UV;

    GLint m_SampplerLoc_Y;
    GLint m_SampplerLoc_UV;

    NativeImage m_RenderImage;
};


#endif //ANDROIDTRAININGRECORDER_NV21TEXTUREMAPSAMPLE_H
