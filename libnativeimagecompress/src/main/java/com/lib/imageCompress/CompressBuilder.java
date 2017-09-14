package com.lib.imageCompress;

import android.content.Context;
import android.graphics.Bitmap;

import com.lib.imageCompress.listener.OnCompressListener;
import com.lib.imageCompress.listener.OnMultiCompressListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jane on 2017/9/14.
 */


public class CompressBuilder {
    private Context context;
    //压缩后存储的目录
    public String mTargetDir;
    //待处理的地址
    public List<String> mPaths;
    //多大图片就不进行压缩了
    public int mLimiteCompressSize = 100;
    public OnCompressListener mOnCompressListener;
    public OnMultiCompressListener mOnMulitCompressListener;
    public int mGear = Constant.THIRD_GEAR;
    //自定义最大的大小(kb为单位)
    public int mMaxSize;
    //自定义最大的宽度
    public int mMaxWidth;
    //自定义最大的高度
    public int mMaxHeight;
    //压缩格式
    public Bitmap.CompressFormat mCompressFormat = Bitmap.CompressFormat.JPEG;


    public CompressBuilder(Context context) {
        this.context = context;
        mPaths = new ArrayList<>();
        mTargetDir = CompressUtil.getPhotoCacheDir(context).getAbsolutePath();
    }


    public CompressBuilder load(File file) {
        this.mPaths.add(file.getAbsolutePath());
        return this;
    }

    public CompressBuilder load(String string) {
        this.mPaths.add(string);
        return this;
    }

    public CompressBuilder load(List<String> list) {
        this.mPaths.addAll(list);
        return this;
    }

    public CompressBuilder putGear(int gear) {
        this.mGear = gear;
        return this;
    }

    public CompressBuilder setCompressListener(OnCompressListener listener) {
        this.mOnCompressListener = listener;
        return this;
    }


    public CompressBuilder setMuliteCompressListener(OnMultiCompressListener listener) {
        this.mOnMulitCompressListener = listener;
        return this;
    }

    public CompressBuilder setTargetDir(String targetDir) {
        this.mTargetDir = targetDir;
        return this;
    }

    /**
     * 当图片大小少于多少取消压缩
     *
     * @param size 单位 KB, default 100K
     */
    public CompressBuilder ignoreBy(int size) {
        this.mLimiteCompressSize = size;
        return this;
    }

    public CompressBuilder setMaxSize(int size) {
        this.mMaxSize = size;
        return this;
    }

    public CompressBuilder setMaxHeight(int height) {
        this.mMaxHeight = height;
        return this;
    }

    public CompressBuilder setMaxWidth(int width) {
        this.mMaxWidth = width;
        return this;
    }


    public CompressBuilder setCompressFormat(Bitmap.CompressFormat format) {
        this.mCompressFormat = format;
        return this;
    }


}
