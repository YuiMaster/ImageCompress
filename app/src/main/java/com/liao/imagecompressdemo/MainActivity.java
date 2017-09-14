package com.liao.imagecompressdemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.lib.imageCompress.CompressBuilder;
import com.lib.imageCompress.PictureCompress;
import com.lib.imageCompress.listener.OnCompressListener;

import java.util.ArrayList;
import java.util.List;

/**
 * jni开发推荐网页： https://developer.android.google.cn/studio/projects/add-native-code.html#create-sources
 * <p>
 * <p>
 * CMakeLists.txt编写参考：
 * AndroidStudio用Cmake方式编译NDK代码：http://blog.csdn.net/joe544351900/article/details/53637549
 * <p>
 * <p>
 * 问题：多张大图可能存在OOM问题
 */
public class MainActivity extends AppCompatActivity implements OnCompressListener {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();
        ImageView ivImgPress = (ImageView) findViewById(R.id.compress);
        ivImgPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compressImaage();
            }
        });

    }

    /**
     * 6.0 权限申请
     */
    private void checkPermission() {
        if (checkCallingOrSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission
                    .WRITE_EXTERNAL_STORAGE}, 100);
        } else {

        }
    }

    private void compressImaage() {

        String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath()
                + "/Camera/IMG_20170914_164002.jpg";

        String filePath01 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath()
                + "/Camera/IMG_20170914_164007.jpg";

        String filePath02 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath()
                + "/Camera/IMG_20170914_164004.jpg";

        String filePath03 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath()
                + "/Camera/IMG_20170914_164010.jpg";


        List<String> paths = new ArrayList<>();
        paths.add(filePath);
        paths.add(filePath01);
        paths.add(filePath02);
        paths.add(filePath03);

        CompressBuilder compressBuilder = new CompressBuilder(this)
                .setCompressListener(this)
                .load(filePath);
        new PictureCompress(compressBuilder).launch();
    }

    @Override
    public void onCompressStart() {
        Log.e(TAG, "onCompressStart");

    }

    @Override
    public void onCompressSuccess(String filePath) {
        Log.e(TAG, "onCompressSuccess");

    }

    @Override
    public void onCompressError(Throwable e) {
        Log.e(TAG, "onCompressError");

    }
}
