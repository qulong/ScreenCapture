package com.demo.screencapture;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Copyright (c) 2018, 数字多⽹网络技术有限公司 All rights reserved.
 * File Name:
 * Version:V1.0
 * Author:qulonglong
 * Date:2019/1/18
 */

public class ExecutorUtils {
//    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final ExecutorService executor = Executors.newFixedThreadPool(8);

    //删除指定目录指定前后缀的文件
    public static void delete(String dirPath, boolean isPrefix, long regEx) {
        executor.execute(new DeleteRunnable(dirPath, isPrefix, regEx));
    }

    public static void addRunnable(Runnable runnable) {
        if (runnable == null) {
            return;
        }
        executor.execute(runnable);
    }
}
