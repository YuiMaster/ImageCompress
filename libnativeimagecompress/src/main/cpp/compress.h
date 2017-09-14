//
// Created by Administrator on 12/21/2016.
//

#ifndef COMPRESS_COMPRESS_H_H
#define COMPRESS_COMPRESS_H_H

#include "lang.h"
#include "log.h"
#include <stdlib.h>
#include <android/bitmap.h>

#include <setjmp.h>
#include <jpeglib.h>
#include <string.h>

#define true 1
#define false 0

//#ifdef __cplusplus
//extern "C" {
//#endif
JNIEXPORT jint JNICALL
Java_com_zbsd_ydb_NativeCompress_nativeCompressBitmap(JNIEnv *env, jclass type,
                                                               jobject bitmap, jint quality,
                                                               jstring dstFile_,
                                                               jboolean optimize);


JNIEXPORT jint JNICALL
Java_com_zbsd_ydb_NativeCompress_nativeCompressBitmapWithWidthAndHeight(JNIEnv* env,
                                                                       jobject thiz, jobject bitmapcolor, int w, int h, int quality,
                                                                       jbyteArray fileNameStr, jboolean optimize);


//
//#ifdef __cplusplus
//}
//#endif
#endif //COMPRESS_COMPRESS_H_H
