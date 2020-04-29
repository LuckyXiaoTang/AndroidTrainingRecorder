//
// Created by Terry on 2020/4/3.
//

#ifndef ANDROIDTRAININGRECORDER_BASE_DECODER_H
#define ANDROIDTRAININGRECORDER_BASE_DECODER_H

#include <jni.h>
#include <thread>
#include "i_decoder.h"
#include "decode_state.h"

extern "C" {
#include <libavcodec/avcodec.h>
#include <libavformat/avformat.h>
};

class BaseDecoder : IDecoder {
private:
    const char *TAG = "base_decoder";
    //------------------------------定义解码相关------------------------------

    // 解码信息上下文
    AVFormatContext *m_format_ctx = NULL;

    // 解码器
    AVCodec *m_codec = NULL;

    // 解码器上下文
    AVCodecContext *m_codec_ctx = NULL;

    // 待解包
    AVPacket *m_packet = NULL;

    // 最终的解码数据
    AVFrame *m_frame = NULL;

    // 当前播放时间
    int64_t m_cur_t_s = 0;

    // 总时长
    long m_durition = 0;

    // 开始播放的时间
    int64_t m_started_t = -1;

    // 解码状态
    DecodeState m_state = STOP;

    // 数据流索引
    int m_stream_index = -1;

    //------------------------------定义私有方法------------------------------
    /**
     * 初始化FFMpeg相关的参数
     * @param env jvm环境
     */
    void init(JNIEnv *env, jstring path);

    /**
     * 初始化FFMpeg相关的参数
     * @param env jvm环境
     */
    void initFFmpegDecoder(JNIEnv *env);


    /**
     * 分配解码过程中需要的缓存
     */
    void allocFrameBuffer();

    /**
     * 循环解码
     */
    void loopDecode();

    /**
     * 获取当前时间戳
     */
    void obtainTimeStamp();


    /**
     * 解码完成
     * @param env jni环境
     */
    void doneDecode(JNIEnv *env);

    /**
     * 时间同步
     */
    void syncRender();


    //------------------------------定义线程相关------------------------------
    // 线程依附的JVM环境
    JavaVM *m_jvm_for_thread = NULL;

    // 原始路径jstring引用，否则无法在线程中操作
    jobject m_path_ref = NULL;

    // 转换后的路径
    const char *m_path = NULL;

    // 线程等待锁
    pthread_mutex_t m_mutex = PTHREAD_MUTEX_INITIALIZER;
    pthread_cond_t m_cond = PTHREAD_COND_INITIALIZER;

    /**
     * 新建解码线程
     */
    void createDecodeThread();

    static void decode(std::shared_ptr<BaseDecoder> self);

protected:

    /**
     * 等待
     * @param second
     */
    void wait(long second = 0);

    /**
     * 发送恢复信号
     */
    void sendSignal();

public:
    //------------------------------构造方法和析构方法------------------------------
    BaseDecoder(JNIEnv *env, jstring path);

    virtual ~BaseDecoder();

    //------------------------------实现基础方法------------------------------
    void goOn() override;

    void pause() override;

    void stop() override;

    void isRunning() override;

    void getDurition() override;

    void getCurrentPos() override;

    //------------------------------子类实现------------------------------
    /**
     * 子类准备回调方法
     * @note 注：在解码线程中回调
     * @param env 解码线程绑定的JVM环境
     */
    virtual void prepare(JNIEnv *env) = 0;

    /**
     * 子类渲染回调方法
     * @note 注：在解码线程中回调
     * @param frame 视频：一帧YUV数据；音频：一帧PCM数据
     */
    virtual void render(AVFrame *frame) = 0;

    /**
     * 子类释放资源回调方法
     */
    virtual void release() = 0;

    virtual const char *const logSpec() = 0;

    virtual AVMediaType getMediaType() = 0;
};


#endif //ANDROIDTRAININGRECORDER_BASE_DECODER_H
