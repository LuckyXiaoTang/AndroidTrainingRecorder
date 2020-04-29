//
// Created by Terry on 2020/4/3.
//

#include "base_decoder.h"
#include "../../utils/logger.h"
#include "../../utils/timer.c"

BaseDecoder::BaseDecoder(JNIEnv *env, jstring path) {
    init(env, path);
    createDecodeThread();
}

BaseDecoder::~BaseDecoder() {
    if (m_codec_ctx != NULL) delete m_codec_ctx;
    if (m_format_ctx != NULL) delete m_format_ctx;
    if (m_frame != NULL) delete m_frame;
    if (m_packet != NULL) delete m_packet;
}

void BaseDecoder::init(JNIEnv *env, jstring path) {
    m_path_ref = env->NewGlobalRef(path);
    m_path = env->GetStringUTFChars(path, NULL);
    //获取JVM虚拟机，为创建线程作准备
    env->GetJavaVM(&m_jvm_for_thread);
}

void BaseDecoder::createDecodeThread() {
    // 使用智能指针，线程结束时，自动删除本类指针
    std::shared_ptr<BaseDecoder> self(this);
    std::thread t(decode, self);
    t.detach();
}

void BaseDecoder::decode(std::shared_ptr<BaseDecoder> self) {
    JNIEnv *env;
    if (self->m_jvm_for_thread->AttachCurrentThread(&env, NULL) != JNI_OK) {
        LOGD(self->TAG, self->logSpec(), "Fail to init decode thread");
        return;
    }

    // 初始化编码器
    self->initFFmpegDecoder(env);
    // 分配内存
    self->allocFrameBuffer();
    // 回调子类方法，通知子类解码器初始化完毕
    self->prepare(env);
    // 进入解码循环
    self->loopDecode();
    // 退出解码
    self->doneDecode(env);

    // 解除线程和JVM绑定
    self->m_jvm_for_thread->DetachCurrentThread();
}

void BaseDecoder::initFFmpegDecoder(JNIEnv *env) {
    // 1.初始化上下文
    m_format_ctx = avformat_alloc_context();

    // 2.打开文件
    if (avformat_open_input(&m_format_ctx, m_path, NULL, NULL) != 0) {
        LOG_ERROR(TAG, logSpec(), "Fail to open file [%s]", m_path)
        doneDecode(env);
        return;
    }

    // 3.获取音视频流信息
    if (avformat_find_stream_info(m_format_ctx, NULL) != 0) {
        LOG_ERROR(TAG, logSpec(), "Fail to find stream info")
        doneDecode(env);
        return;
    }

    // 4.查找编解码器
    // 4-1.获取视频流的索引
    int v_idx = -1;
    for (int i = 0; i < m_format_ctx->nb_streams; ++i) {
        if (m_format_ctx->streams[i]->codecpar->codec_type == getMediaType()) {
            v_idx = i;
            break;
        }
    }

    if (v_idx == -1) {
        LOG_ERROR(TAG, logSpec(), "Fail to find stream index")
        doneDecode(env);
        return;
    }
    m_stream_index = v_idx;

    // 4-2.获取解码器参数
    AVCodecParameters *codecPar = m_format_ctx->streams[m_stream_index]->codecpar;

    // 4-3.获取解码器
    m_codec = avcodec_find_decoder(codecPar->codec_id);

    // 4-4.获取解码器上下文
    m_codec_ctx = avcodec_alloc_context3(m_codec);
    if (avcodec_parameters_to_context(m_codec_ctx, codecPar) != 0) {
        LOG_ERROR(TAG, logSpec(), "Fail to obtain av codec context")
        doneDecode(env);
        return;
    }

    // 5.打开解码器
    if (avcodec_open2(m_codec_ctx, m_codec, NULL) < 0) {
        LOG_ERROR(TAG, logSpec(), "Fail to open av codec")
        doneDecode(env);
        return;
    }

    m_durition = (long) ((float) (m_format_ctx->duration / AV_TIME_BASE * 1000));
    LOG_INFO(TAG, logSpec(), "Decoder init success")
}

void BaseDecoder::allocFrameBuffer() {
    // 初始化待解码和解码数据结构
    // 1.初始化avPacket,存放解码前数据
    m_packet = av_packet_alloc();
    // 2.初始化avFrame,存放解码后数据
    m_frame = av_frame_alloc();
}

void BaseDecoder::loopDecode() {
    // 如果已被外部改变状态，维持外部配置
    if (STOP == m_state) {
        m_state = START;
    }
    LOG_INFO(TAG, logSpec(), "Start loop decode")

    while (true) {
        if (m_state !=DECODING &&
            m_state != START &&
            m_state!= STOP
                ) {
            wait();
            // 同步起始时间，去除等待流失的时间
            m_started_t =
        }
    }
}

void BaseDecoder::goOn() {

}

void BaseDecoder::pause() {

}

void BaseDecoder::stop() {

}

void BaseDecoder::isRunning() {

}

void BaseDecoder::getDurition() {

}

void BaseDecoder::getCurrentPos() {

}
