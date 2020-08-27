//
// Created by Terry on 2020/4/29.
//
#include <jni.h>
#include "LogUtil.h"
#include "GLRenderContext.h"

#define NATIVE_RENDER_CLASS_NAME "com/zero/tzz/gles3/NativeRender"


#ifdef __cplusplus
extern "C" {
#endif
JNIEXPORT void JNICALL native_Init(JNIEnv *env, jobject thiz) {
    GLRenderContext::GetInstance();
}

JNIEXPORT void JNICALL native_UnInit(JNIEnv *env, jobject thiz) {
    GLRenderContext::DestoryInstance();
}

JNIEXPORT void JNICALL native_OnSurfaceCreated(JNIEnv *env, jobject thiz) {
    GLRenderContext::GetInstance()->OnSurfaceCreated();
}

JNIEXPORT void JNICALL native_OnSurfaceChanged(JNIEnv *env, jobject thiz, jint width, jint height) {
    GLRenderContext::GetInstance()->OnSurfaceChanged(width, height);
}

JNIEXPORT void JNICALL native_OnDrawFrame(JNIEnv *env, jobject thiz) {
    GLRenderContext::GetInstance()->OnDrawFrame();
}

JNIEXPORT void JNICALL native_SetParamsInt(JNIEnv *env, jobject thiz, jint position) {
    GLRenderContext::GetInstance()->SetParamsInt(position);
}

JNIEXPORT void JNICALL
native_SetImageData(JNIEnv *env, jobject thiz, jint format, jint width, jint height,
                    jbyteArray imageData) {
    int len = env->GetArrayLength(imageData);
    auto *buf = new uint8_t[len];
    env->GetByteArrayRegion(imageData, 0, len, reinterpret_cast<jbyte *>(buf));
    GLRenderContext::GetInstance()->SetImageData(format, width, height, buf);
    delete[] buf;
    env->DeleteLocalRef(imageData);
}

#ifdef __cplusplus
}
#endif

static JNINativeMethod gl_RenderMethods[] = {
        {"native_Init",             "()V",      (void *) native_Init},
        {"native_UnInit",           "()V",      (void *) native_UnInit},
        {"native_OnSurfaceCreated", "()V",      (void *) native_OnSurfaceCreated},
        {"native_OnSurfaceChanged", "(II)V",    (void *) native_OnSurfaceChanged},
        {"native_OnDrawFrame",      "()V",      (void *) native_OnDrawFrame},
        {"native_SetParamsInt",     "(I)V",     (void *) native_SetParamsInt},
        {"native_SetImageData",     "(III[B)V", (void *) native_SetImageData}
};

static int RegisterNativeMethods(JNIEnv *env, const char *className,
                                 JNINativeMethod *methods, int methodNum) {
    LOGE(">>> RegisterNativeMethods <<<");
    jclass clazz = env->FindClass(className);
    if (clazz == NULL) {
        LOGE("RegisterNativeMethods fail. clazz == NULL");
        return JNI_FALSE;
    }
    if (env->RegisterNatives(clazz, methods, methodNum) < 0) {
        LOGE("RegisterNativeMethods fail");
        return JNI_FALSE;
    }
    return JNI_TRUE;
}

static void UnRegisterNativeMethods(JNIEnv *env, const char *className) {
    LOGE(">>> RegisterNativeMethods <<<");
    jclass clazz = env->FindClass(className);
    if (clazz == NULL) {
        LOGE("UnRegisterNativeMethods fail. clazz == NULL");
        return;
    }
    env->UnregisterNatives(clazz);
}

extern "C" jint JNI_OnLoad(JavaVM *jvm, void *p) {
    LOGE(">>> JNI_OnLoad <<<");
    jint jniRet = JNI_ERR;
    JNIEnv *env = NULL;
    if (jvm->GetEnv((void **) (&env), JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    }

    jniRet = RegisterNativeMethods(env, NATIVE_RENDER_CLASS_NAME,
                                   gl_RenderMethods,
                                   sizeof(gl_RenderMethods) / sizeof(gl_RenderMethods[0]));

    if (jniRet != JNI_TRUE) {
        return JNI_ERR;
    }

    return JNI_VERSION_1_6;
}

extern "C" void JNI_OnUnload(JavaVM *jvm, void *p) {
    JNIEnv *env = NULL;
    if (jvm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return;
    }
    UnRegisterNativeMethods(env, NATIVE_RENDER_CLASS_NAME);
}