package com.lib.imageCompress;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.os.Environment.MEDIA_MOUNTED;

/**
 * 工具类
 */
public class CompressUtil {
    private static List<String> format = new ArrayList<>();


    static {
        format.add(Constant.JPG);
        format.add(Constant.JPEG);
        format.add(Constant.PNG);
        format.add(Constant.WEBP);
        format.add(Constant.GIF);
    }

    //是否是图片
    public static boolean isImage(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        String suffix = path.substring(path.lastIndexOf(".") + 1, path.length());
        return format.contains(suffix.toLowerCase());
    }


    public static boolean isJPG(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }

        String suffix = path.substring(path.lastIndexOf("."), path.length()).toLowerCase();
        return suffix.contains(Constant.JPG) || suffix.contains(Constant.JPEG);
    }

    //获得文件后缀
    public static String getSuffix(String path) {
        if (TextUtils.isEmpty(path)) {
            return ".jpg";
        }

        return path.substring(path.lastIndexOf("."), path.length());
    }

    //是否需要压缩
    public static boolean isNeedCompress(int leastCompressSize, String path) {
        if (leastCompressSize > 0) {
            File source = new File(path);
            if (!source.exists()) {
                return false;
            }

            if (source.length() <= (leastCompressSize << 10)) {
                return false;
            }
        }
        return true;
    }


    public static boolean isFileExit(String filePath) {
        {
            try {
                File f = new File(filePath);
                if (!f.exists()) {
                    return false;
                }

            } catch (Exception e) {
                return false;
            }

            return true;
        }
    }


    //SD卡是否存在

    public static boolean isSDCardExsit() {
        String state = Environment.getExternalStorageState();
        if (state == null) return false;
        return MEDIA_MOUNTED.equals(state);
    }

    public static String getDiskCacheDir(Context context) {
        String cachePath = null;
        if (isSDCardExsit()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return cachePath;
    }

    //压缩图片存储位置
    public static File getPhotoCacheDir(Context context) {
        return getPhotoCacheDir(context, Constant.DEFAULT_COMPRESS_DISK_CACHE_DIR);
    }

    //压缩图片存储位置
    public static File getPhotoCacheDir(Context context, String cacheName) {
        String cachePath = null;
        if (isSDCardExsit()) {
            cachePath = context.getExternalFilesDir(cacheName).getPath();
        } else {
            cachePath = context.getFilesDir().getPath();
        }
        File cacheDir = new File(cachePath);
        if (cacheDir != null) {
            File result = new File(cacheDir, cacheName);
            if (!result.mkdirs() && (!result.exists() || !result.isDirectory())) {
                // File wasn't able to create a directory, or the result exists but not a directory
                return null;
            }
            return result;
        }
        return null;
    }


    /**
     * 清空目标文件或文件夹
     * Empty the target file or folder
     */
    private void deleteFile(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            for (File file : fileOrDirectory.listFiles()) {
                deleteFile(file);
            }
        }
        fileOrDirectory.delete();
    }
}
