package com.zero.tzz.video.media.decoder

/**
 * 解码状态
 * @author Zero_Tzz
 * @date 2019-11-26 11:58
 * @description DecodeState
 */
enum class DecodeState {
    /**开始状态*/
    START,
    /**解码中*/
    DECODING,
    /**解码暂停*/
    PAUSE,
    /**正在快进*/
    SEEKING,
    /**解码完成*/
    FINISH,
    /**解码器释放*/
    STOP
}