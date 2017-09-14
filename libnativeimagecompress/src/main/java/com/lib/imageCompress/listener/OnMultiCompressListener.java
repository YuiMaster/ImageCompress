package com.lib.imageCompress.listener;

import java.util.List;

/**
 * Created by Jane on 2017/9/14.
 * 多图片压缩回调
 */

public interface OnMultiCompressListener {
    //开始压缩
    void onCompressStart();

    /*图片压缩成功
    *@paramer filePath 压缩以后的图片路径
    */
    void onCompressSuccess(List<String> pathList);

    //压缩失败
    void onCompressError(Throwable e);
}
