//
// Created by Terry on 2020/6/8.
//

#ifndef ANDROIDTRAININGRECORDER_IMAGEDEF_H
#define ANDROIDTRAININGRECORDER_IMAGEDEF_H

#include <string.h>
#include "stdint.h"
#include "malloc.h"
#include "LogUtil.h"

#define IMAGE_FORMAT_RGBA       0x01
#define IMAGE_FORMAT_NV21       0x02
#define IMAGE_FORMAT_NV12       0x03
#define IMAGE_FORMAT_I420       0x04


typedef struct _tag_NativeImage {
    int width;
    int height;
    int format;
    uint8_t *ppPlane[3];

    _tag_NativeImage() {
        width = 0;
        height = 0;
        format = 0;
        ppPlane[0] = nullptr;
        ppPlane[1] = nullptr;
        ppPlane[2] = nullptr;
    }
} NativeImage;

class NativeImageUtil {
public:
    static void AllocNativeImage(NativeImage *pImage) {
        if (pImage->width == 0 || pImage->height == 0)return;
        switch (pImage->format) {
            case IMAGE_FORMAT_RGBA:
                pImage->ppPlane[0] = static_cast<uint8_t *>(malloc(
                        pImage->width * pImage->height * 4));
                break;
            case IMAGE_FORMAT_NV21:
            case IMAGE_FORMAT_NV12:
                pImage->ppPlane[0] = static_cast<uint8_t *>(malloc(
                        pImage->width * pImage->height * 1.5));
                pImage->ppPlane[1] = pImage->ppPlane[0] + pImage->width * pImage->height;
                break;
            case IMAGE_FORMAT_I420:
                pImage->ppPlane[0] = static_cast<uint8_t *>(malloc(
                        pImage->width * pImage->height * 1.5));
                pImage->ppPlane[1] = pImage->ppPlane[0] + pImage->width * pImage->height;
                pImage->ppPlane[2] = pImage->ppPlane[1] + pImage->width * (pImage->height >> 2);
                break;
            default:
                LOGE(">>> NativeImageUtil::AllocNativeImage do not support the format. Format = %d <<<",
                     pImage->format);
                break;

        }
    };

    static void FreeNativeImage(NativeImage *pImage) {
        if (pImage == nullptr || pImage->ppPlane[0] == nullptr)return;
        free(pImage->ppPlane[0]);
        pImage->ppPlane[0] = nullptr;
        pImage->ppPlane[1] = nullptr;
        pImage->ppPlane[2] = nullptr;
    };

    static void CopyNativeImage(NativeImage *pSrcImage, NativeImage *pDstImage) {
        if (pSrcImage == nullptr || pSrcImage->ppPlane[0] == nullptr)return;
        if (pSrcImage->width != pDstImage->width
            || pSrcImage->height != pDstImage->height
            || pSrcImage->format != pDstImage->format
                )
            return;
        if (pDstImage->ppPlane[0] == nullptr)AllocNativeImage(pDstImage);
        switch (pSrcImage->format) {
            case IMAGE_FORMAT_I420:
            case IMAGE_FORMAT_NV21:
            case IMAGE_FORMAT_NV12:
                memcpy(pDstImage->ppPlane[0], pSrcImage->ppPlane[0],
                       pSrcImage->width * pSrcImage->height * 1.5);
                break;
            case IMAGE_FORMAT_RGBA:
                memcpy(pDstImage->ppPlane[0], pSrcImage->ppPlane[0],
                       pSrcImage->width * pSrcImage->height * 4);
                break;
            default:
                LOGE(">>> NativeImageUtil::CopyNativeImage do not support the format. Format = %d <<<",
                     pSrcImage->format);
                break;
        }
    };
};

#endif //ANDROIDTRAININGRECORDER_IMAGEDEF_H
