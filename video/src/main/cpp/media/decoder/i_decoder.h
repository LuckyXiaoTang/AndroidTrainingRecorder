//
// Created by Terry on 2020/4/3.
//

#ifndef ANDROIDTRAININGRECORDER_I_DECODER_H
#define ANDROIDTRAININGRECORDER_I_DECODER_H

class IDecoder {
    public:
    virtual void goOn() = 0;

    virtual void pause() = 0;

    virtual void stop() = 0;

    virtual void isRunning() = 0;

    virtual void getDurition() = 0;

    virtual void getCurrentPos() = 0;
};

#endif //ANDROIDTRAININGRECORDER_I_DECODER_H
