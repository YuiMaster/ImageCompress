package com.lib.imageCompress.bean;

/**
 * Created by Jane on 2017/9/14.
 * 数据类：压缩结果
 */

public class CompressResultBean {
    //压缩状态
    int status;
    //压缩的图片存储路径
    String filePath;

    public CompressResultBean() {
    }

    public CompressResultBean(int status, String filePath) {
        this.status = status;
        this.filePath = filePath;
    }


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
