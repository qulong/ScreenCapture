package com.demo.screencapture.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.demo.screencapture.ExecutorUtils;
import com.demo.screencapture.phonesms.Readsms;
import com.demo.screencapture.utils.FileUtil;
import com.google.gson.Gson;


/**
 * Copyright (c) 2018, 数字多⽹网络技术有限公司 All rights reserved.
 * File Name:
 * Version:V1.0
 * Author:qulonglong
 * Date:2019/1/25
 *
 * 使用adb  启动
 * adb shell am startservice -n com.demo.screencapture/com.demo.screencapture.service.WriteSmsService
 */

public class WriteSmsService extends Service {

    @Override

    public IBinder onBind(Intent intent) {
        return null;

    }

    @Override

    public int onStartCommand(Intent intent, int flags, int startId) {
        ExecutorUtils.addRunnable(new Runnable() {
            @Override
            public void run() {
                Log.e("WriteSmsService", "sms-----------read" + System.currentTimeMillis());
                Gson gson = new Gson();
                FileUtil.addString_Txt(getApplicationContext(), gson.toJson(Readsms.getSmsFromPhone(getApplicationContext())), FileUtil.smsFielName);
            }
        });

        return super.onStartCommand(intent, flags, startId);
    }


}
