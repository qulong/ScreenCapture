package com.demo.screencapture;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.TextureView;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Copyright (c) 2018, 数字多⽹网络技术有限公司 All rights reserved.
 * File Name:
 * Version:V1.0
 * Author:qulonglong
 * Date:2019/1/18
 */

public class DeleteRunnable implements Runnable {
    private long mRegEx;
    private String dirPath;
    private boolean isPrefix;
    private static final String TAG = DeleteRunnable.class.getSimpleName();

    /**
     * @param dirPath  要删除文件所在的目录路径
     * @param isPrefix true为前缀 false为后缀
     * @param mRegEx   规则
     */
    public DeleteRunnable(String dirPath, boolean isPrefix, long mRegEx) {
        this.mRegEx = mRegEx;
        this.dirPath = dirPath;
        this.isPrefix = isPrefix;
    }

    @Override
    public void run() {
        enumAllFileList();
    }

    /**
     * 枚举并删除所有符合条件(前缀)的文件
     */
    private void enumAllFileList() {
        if (!TextUtils.isEmpty(dirPath)) {
            File adDir = new File(dirPath);
            if (adDir.exists() && adDir.isDirectory()) {
                if (mRegEx > 1L) {
                    DeleteFileFilter filter = new DeleteFileFilter(isPrefix, mRegEx);
                    // 2.匹配是否是需要删除的文件
                    File[] fileList = adDir.listFiles(filter);
                    if (fileList != null && fileList.length > 0) {
                        for (File file : fileList) {
                            if (file.isFile() && file.exists()) {
                                boolean delete = file.delete();
                                Log.i(TAG, "删除符合条件前缀的img" + (delete ? "成功~" : "失败！"));
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 以xxx开头或后缀的文件名的过滤器
     */
    class DeleteFileFilter implements FilenameFilter {
        private boolean isPrefix;
        // 前缀或后缀规则
        private Long mRegEx;

        public DeleteFileFilter(boolean isPrefix, @NonNull Long regEx) {
            this.isPrefix = isPrefix;
            this.mRegEx = regEx;
        }

        @Override
        public boolean accept(File file, String s) {
//            return isPrefix ? s.startsWith(mRegEx) : s.endsWith(mRegEx);
            //这个只用后缀时间戳
            String subTimes = s.substring(11, s.length() - 4);
            if (TextUtils.isEmpty(subTimes)) {
                return false;
            }
            return Long.valueOf(subTimes) < mRegEx;
        }
    }
}
