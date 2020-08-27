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

    virtual void Init();

    virtual void Draw(int width, int height);

    virtual void Destroy();
};


#endif //ANDROIDTRAININGRECORDER_TRIANGLESAMPLE_H
