package com.lib.imageCompress;

import com.lib.imageCompress.bean.CompressResultBean;
import com.lib.imageCompress.listener.OnCompressListener;
import com.lib.imageCompress.listener.OnMultiCompressListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by Jane on 2017/9/14.
 */

public class PictureCompress {

    private CompressBuilder mBuilder;
    private OnMultiCompressListener mMultilistener = null;
    private OnCompressListener mListener = null;

    public PictureCompress(CompressBuilder builder) {
        this.mBuilder = builder;
    }


    //压缩单张图片
    public void launch() {
        Observable<CompressResultBean> observable = asObservable();
        if (observable != null) {
            if (mBuilder != null) {
                mListener = mBuilder.mOnCompressListener;
            }
            observable.subscribe(new Subscriber<CompressResultBean>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    if (mListener != null) {
                        mListener.onCompressError(e);
                    }
                }

                @Override
                public void onNext(CompressResultBean bean) {
                    if (bean != null && mListener != null) {
                        if (bean.getStatus() > 0) {
                            mListener.onCompressSuccess(bean.getFilePath());
                        }
                    }
                }
            });
        }

    }


    /**
     * 返回File Observable
     */
    public Observable<CompressResultBean> asObservable() {
        if (mBuilder == null) return null;
        List<String> mPaths = mBuilder.mPaths;
        OnCompressListener listener = mBuilder.mOnCompressListener;
        if (mPaths == null || mPaths.size() == 0 && listener != null) {
            listener.onCompressError(new NullPointerException("image file cannot be null"));
            return null;
        }
        String filePath = mPaths.get(0);

        if (!CompressUtil.isFileExit(filePath) || !CompressUtil.isImage(filePath) && listener != null) {
            listener.onCompressError(new NullPointerException("image file cannot be null"));
            return null;
        }

        File mFile = new File(filePath);
        Compress compresser = new Compress(mBuilder);
        return compresser.singleAction(mFile);
    }

    /**
     * 返回fileList Observable
     */
    public Observable<List<CompressResultBean>> asListObservable() {
        if (mBuilder == null) return null;
        List<String> mPaths = mBuilder.mPaths;
        OnCompressListener listener = mBuilder.mOnCompressListener;
        if (mPaths == null || mPaths.size() == 0 && listener != null) {
            listener.onCompressError(new NullPointerException("image file cannot be null"));
        }
        List<File> files = new ArrayList<>();
        File mFile;
        for (String pathStr : mPaths) {
            if (pathStr == null || !CompressUtil.isFileExit(pathStr) || !CompressUtil.isImage(pathStr) && listener != null) {
                continue;
            } else {
                mFile = new File(pathStr);
                files.add(mFile);
            }
        }

        Compress compresser = new Compress(mBuilder);
        return compresser.multiAction(files);
    }

}
