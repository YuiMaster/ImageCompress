package com.lib.imageCompress.listener;

/**
 * Created by Jane on 2017/9/14.
 * 单图片压缩回调
 */

public interface OnCompressListener {
    //开始压缩
    void onCompressStart();

    /*图片压缩成功
    @paramer filePath 压缩以后的图片路径
     */
    void onCompressSuccess(String filePath);

    //图片压缩失败
    void onCompressError(Throwable e);

}