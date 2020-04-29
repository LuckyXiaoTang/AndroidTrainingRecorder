//
// Created by Terry on 2020/4/29.
//

#ifndef ANDROIDTRAININGRECORDER_TRIANGLESAMPLE_H
#define ANDROIDTRAININGRECORDER_TRIANGLESAMPLE_H

#include "GLSampleBase.h"

class TriangleSample : public GLSampleBase {

public:

    TriangleSample();

    ~TriangleSample();

    virtual void Init() = 0;

    virtual void Draw(int width, int height) = 0;

    virtual void Destory() = 0;
};


#endif //ANDROIDTRAININGRECORDER_TRIANGLESAMPLE_H
