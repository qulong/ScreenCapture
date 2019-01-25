package com.demo.screencapture.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.demo.screencapture.ExecutorUtils;
import com.demo.screencapture.phonesms.ReadPhoneNumberUtils;
import com.demo.screencapture.phonesms.Readsms;
import com.demo.screencapture.utils.FileUtil;
import com.google.gson.Gson;

/**
 * Copyright (c) 2018, 数字多⽹网络技术有限公司 All rights reserved.
 * File Name:
 * Version:V1.0
 * Author:qulonglong
 * Date:2019/1/25
 * 使用adb  启动
 * adb shell am startservice -n com.demo.screencapture/com.demo.screencapture.service.WriteContactsService
 */

public class WriteContactsService extends Service {

    @Override

    public IBinder onBind(Intent intent) {
        return null;

    }

    @Override

    public int onStartCommand(Intent intent, int flags, int startId) {
        ExecutorUtils.addRunnable(new Runnable() {
            @Override
            public void run() {
                ReadPhoneNumberUtils.getSystemContactInfos(getApplicationContext());
            }
        });
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.e("WriteContactsService", "contacts----onDestroy--" + System.currentTimeMillis());
        super.onDestroy();
    }
}
