//
// Created by Terry on 2020/5/9.
//

#ifndef ANDROIDTRAININGRECORDER_TEXTUREMAPSAMPLE_H
#define ANDROIDTRAININGRECORDER_TEXTUREMAPSAMPLE_H

#include "GLSampleBase.h"
#include "ImageDef.h"

class TextureMapSample : public GLSampleBase {

public:
    TextureMapSample();

    ~TextureMapSample();

    void LoadImage(NativeImage *pImage);

    virtual void Init();

    virtual void Draw(int screenW, int screenH);

    virtual void Destroy();

private:
    GLuint m_TextrueId;
    GLint m_SamplerLoc;
    NativeImage m_RenderImage;
};


#endif //ANDROIDTRAININGRECORDER_TEXTUREMAPSAMPLE_H
