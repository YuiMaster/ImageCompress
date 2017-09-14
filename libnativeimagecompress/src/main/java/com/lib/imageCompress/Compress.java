package com.lib.imageCompress;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.support.annotation.NonNull;

import com.lib.imageCompress.bean.CompressResultBean;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.FuncN;
import rx.schedulers.Schedulers;

/**
 * 图片压缩
 */
public class Compress {
    private static final String TAG = "Compress";

    private final CompressBuilder mBuilder;

    private ByteArrayOutputStream mByteArrayOutputStream;

    public Compress(CompressBuilder builder) {
        mBuilder = builder;
    }

    Observable<CompressResultBean> singleAction(final File file) {
        return Observable.create(new Observable.OnSubscribe<CompressResultBean>() {
            @Override
            public void call(Subscriber<? super CompressResultBean> subscriber) {
                try {
                    if (mBuilder == null || file == null) {
                        subscriber.onNext(null);
                    }
                    CompressResultBean mBean = null;
                    if (CompressUtil.isNeedCompress(mBuilder.mLimiteCompressSize, file.getAbsolutePath())) {
                        mBean = compressImage(mBuilder.mGear, file);
                    } else {
                        mBean = new CompressResultBean(Constant.COMPRESS_SUCCESS_STATUS, file.getPath());
                    }
                    subscriber.onNext(mBean);
                    subscriber.onCompleted();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());


    }

    Observable<List<CompressResultBean>> multiAction(List<File> files) {
        List<Observable<CompressResultBean>> observables = new ArrayList<>(files.size());
        for (final File file : files) {
            observables.add(Observable.create(new Observable.OnSubscribe<CompressResultBean>() {
                @Override
                public void call(Subscriber<? super CompressResultBean> subscriber) {
                    try {
                        if (mBuilder == null || file == null) {
                            subscriber.onNext(null);
                        }

                        CompressResultBean mBean = null;
                        if (CompressUtil.isNeedCompress(mBuilder.mLimiteCompressSize, file.getAbsolutePath())) {
                            mBean = compressImage(mBuilder.mGear, file);
                        } else {
                            mBean = new CompressResultBean(Constant.COMPRESS_SUCCESS_STATUS, file.getPath());
                        }

                        subscriber.onNext(mBean);
                        subscriber.onCompleted();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }));
        }


        return Observable.zip(observables, new FuncN<List<CompressResultBean>>() {
            @Override
            public List<CompressResultBean> call(Object... args) {
                List<CompressResultBean> beans = new ArrayList<>(args.length);
                for (Object o : args) {
                    beans.add((CompressResultBean) o);
                }
                return beans;
            }
        }).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());

    }

    private CompressResultBean compressImage(int gear, File file) throws IOException {
        switch (gear) {
            case Constant.THIRD_GEAR:
                return thirdCompress(file);
            case Constant.SECOND_GEAR:
                return customCompress(file);
            case Constant.FIRST_GEAR:
                return firstCompress(file);
            default:
                return new CompressResultBean();
        }
    }

    private CompressResultBean thirdCompress(@NonNull File file) throws IOException {
        String thumb = getCacheFilePath();
        double size;
        String filePath = file.getAbsolutePath();
        int angle = getImageSpinAngle(filePath);
        int width = getImageSize(filePath)[0];
        int height = getImageSize(filePath)[1];
        boolean flip = width > height;
        int thumbW = width % 2 == 1 ? width + 1 : width;
        int thumbH = height % 2 == 1 ? height + 1 : height;

        width = thumbW > thumbH ? thumbH : thumbW;
        height = thumbW > thumbH ? thumbW : thumbH;

        double scale = ((double) width / height);

        if (scale <= 1 && scale > 0.5625) {
            if (height < 1664) {
                if (file.length() / 1024 < 150) {
                    return new CompressResultBean(Constant.COMPRESS_SUCCESS_STATUS, filePath);
                }

                size = (width * height) / Math.pow(1664, 2) * 150;
                size = size < 60 ? 60 : size;
            } else if (height >= 1664 && height < 4990) {
                thumbW = width / 2;
                thumbH = height / 2;
                size = (thumbW * thumbH) / Math.pow(2495, 2) * 300;
                size = size < 60 ? 60 : size;
            } else if (height >= 4990 && height < 10240) {
                thumbW = width / 4;
                thumbH = height / 4;
                size = (thumbW * thumbH) / Math.pow(2560, 2) * 300;
                size = size < 100 ? 100 : size;
            } else {
                int multiple = height / 1280 == 0 ? 1 : height / 1280;
                thumbW = width / multiple;
                thumbH = height / multiple;
                size = (thumbW * thumbH) / Math.pow(2560, 2) * 300;
                size = size < 100 ? 100 : size;
            }
        } else if (scale <= 0.5625 && scale > 0.5) {
            if (height < 1280 && file.length() / 1024 < 200) {
                return new CompressResultBean(Constant.COMPRESS_SUCCESS_STATUS, filePath);
            }

            int multiple = height / 1280 == 0 ? 1 : height / 1280;
            thumbW = width / multiple;
            thumbH = height / multiple;
            size = (thumbW * thumbH) / (1440.0 * 2560.0) * 400;
            size = size < 100 ? 100 : size;
        } else {
            int multiple = (int) Math.ceil(height / (1280.0 / scale));
            thumbW = width / multiple;
            thumbH = height / multiple;
            size = ((thumbW * thumbH) / (1280.0 * (1280 / scale))) * 500;
            size = size < 100 ? 100 : size;
        }

        return compress(filePath, thumb, flip ? thumbH : thumbW, flip ? thumbW : thumbH, angle,
                (long) size);
    }

    private CompressResultBean firstCompress(@NonNull File file) throws IOException {
        int minSize = 60;
        int longSide = 720;
        int shortSide = 1280;

        String thumbFilePath = getCacheFilePath();
        String filePath = file.getAbsolutePath();

        long size = 0;
        long maxSize = file.length() / 5;

        int angle = getImageSpinAngle(filePath);
        int[] imgSize = getImageSize(filePath);
        int width = 0, height = 0;
        if (imgSize[0] <= imgSize[1]) {
            double scale = (double) imgSize[0] / (double) imgSize[1];
            if (scale <= 1.0 && scale > 0.5625) {
                width = imgSize[0] > shortSide ? shortSide : imgSize[0];
                height = width * imgSize[1] / imgSize[0];
                size = minSize;
            } else if (scale <= 0.5625) {
                height = imgSize[1] > longSide ? longSide : imgSize[1];
                width = height * imgSize[0] / imgSize[1];
                size = maxSize;
            }
        } else {
            double scale = (double) imgSize[1] / (double) imgSize[0];
            if (scale <= 1.0 && scale > 0.5625) {
                height = imgSize[1] > shortSide ? shortSide : imgSize[1];
                width = height * imgSize[0] / imgSize[1];
                size = minSize;
            } else if (scale <= 0.5625) {
                width = imgSize[0] > longSide ? longSide : imgSize[0];
                height = width * imgSize[1] / imgSize[0];
                size = maxSize;
            }
        }

        return compress(filePath, thumbFilePath, width, height, angle, size);
    }

    private CompressResultBean customCompress(@NonNull File file) throws IOException {
        String thumbFilePath = getCacheFilePath();
        String filePath = file.getAbsolutePath();

        int angle = getImageSpinAngle(filePath);
        long fileSize = mBuilder.mMaxSize > 0 && mBuilder.mMaxSize < file.length() / 1024 ? mBuilder.mMaxSize
                : file.length() / 1024;

        int[] size = getImageSize(filePath);
        int width = size[0];
        int height = size[1];

        if (mBuilder.mMaxSize > 0 && mBuilder.mMaxSize < file.length() / 1024f) {
            // find a suitable size
            float scale = (float) Math.sqrt(file.length() / 1024f / mBuilder.mMaxSize);
            width = (int) (width / scale);
            height = (int) (height / scale);
        }

        // check the width&height
        if (mBuilder.mMaxWidth > 0) {
            width = Math.min(width, mBuilder.mMaxWidth);
        }
        if (mBuilder.mMaxHeight > 0) {
            height = Math.min(height, mBuilder.mMaxHeight);
        }
        float scale = Math.min((float) width / size[0], (float) height / size[1]);
        width = (int) (size[0] * scale);
        height = (int) (size[1] * scale);

        // 不压缩
        if (mBuilder.mMaxSize > file.length() / 1024f && scale == 1) {
            return new CompressResultBean(Constant.COMPRESS_SUCCESS_STATUS, filePath);
        }

        return compress(filePath, thumbFilePath, width, height, angle, fileSize);
    }

    private String getCacheFilePath() {
        StringBuilder name = new StringBuilder("IMG_compress_" + System.currentTimeMillis());
        if (mBuilder.mCompressFormat == Bitmap.CompressFormat.WEBP) {
            name.append(".webp");
        } else {
            name.append(".jpg");
        }
        return mBuilder.mTargetDir + File.separator + name;
    }

    /**
     * 获得图片的宽高
     *
     * @param imagePath the path of image
     */
    public static int[] getImageSize(String imagePath) {
        int[] res = new int[2];
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inSampleSize = 1;
        BitmapFactory.decodeFile(imagePath, options);
        res[0] = options.outWidth;
        res[1] = options.outHeight;
        return res;
    }


    /**
     * 通过文件路径读获取Bitmap防止OOM以及解决图片旋转问题
     *
     * @param filePath
     * @return
     */
    public static Bitmap getBitmapFromFile(String filePath, int width, int height, int angle) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;//只读边,不读内容
        BitmapFactory.decodeFile(filePath, newOpts);
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        int inSampleSize = 1;

        while (h / inSampleSize > height || w / inSampleSize > width) {
            inSampleSize *= 2;
        }
        // 获取尺寸压缩倍数
        newOpts.inSampleSize = inSampleSize;
        newOpts.inJustDecodeBounds = false;//读取所有内容
        Bitmap bitmap = null;
        File file = new File(filePath);
        FileInputStream fs = null;
        try {
            fs = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            if (fs != null) {
                bitmap = BitmapFactory.decodeFileDescriptor(fs.getFD(), null, newOpts);
                //旋转图片
                bitmap = rotatingImage(angle, bitmap);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fs != null) {
                try {
                    fs.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bitmap;
    }


    /**
     * 获得图片旋转的角度
     *
     * @param path path of target image
     */
    private int getImageSpinAngle(String path) {
        int degree = 0;
        ExifInterface exifInterface = null;
        try {
            exifInterface = new ExifInterface(path);
        } catch (IOException e) {
            // 图片不支持获取角度
            return 0;
        }
        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL);
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                degree = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                degree = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                degree = 270;
                break;
        }
        return degree;
    }

    /**
     * 指定参数压缩图片
     * create the thumbnail with the true rotate angle
     *
     * @param largeImagePath the big image path
     * @param thumbFilePath  the thumbnail path
     * @param width          width of thumbnail
     * @param height         height of thumbnail
     * @param angle          rotation angle of thumbnail
     * @param size           the file size of image
     */
    private CompressResultBean compress(String largeImagePath, String thumbFilePath, int width, int height,
                                        int angle, long size) throws IOException {

        Bitmap bitmap = getBitmapFromFile(largeImagePath, width, height, angle);
        File result = new File(thumbFilePath.substring(0, thumbFilePath.lastIndexOf("/")));

        if (!result.exists() && !result.mkdirs()) {
            return null;
        }

        if (mByteArrayOutputStream == null) {
            mByteArrayOutputStream = new ByteArrayOutputStream(
                    bitmap.getWidth() * bitmap.getHeight());
        } else {
            mByteArrayOutputStream.reset();
        }

        int options = 100;
        bitmap.compress(mBuilder.mCompressFormat, options, mByteArrayOutputStream);

        while (mByteArrayOutputStream.size() / 1024 > size && options > 6) {
            mByteArrayOutputStream.reset();
            options -= 6;
            bitmap.compress(mBuilder.mCompressFormat, options, mByteArrayOutputStream);
        }
        // JNI保存图片到SD卡 这个关键
        int status = NativeCompress.nativeCompressBitmap(bitmap, options, thumbFilePath, true);
        // 释放Bitmap
        if (!bitmap.isRecycled()) {
            bitmap.recycle();
        }

        return new CompressResultBean(status, thumbFilePath);
    }

    /**
     * 旋转图片
     * rotate the image with specified angle
     *
     * @param angle  the angle will be rotating 旋转的角度
     * @param bitmap target image               目标图片
     */
    private static Bitmap rotatingImage(int angle, Bitmap bitmap) {
        //rotate image
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        //create a new image
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix,
                true);
    }
}
