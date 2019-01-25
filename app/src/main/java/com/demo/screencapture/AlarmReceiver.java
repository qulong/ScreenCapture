package com.demo.screencapture;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.demo.screencapture.service.LongTimeService;

/**
 * Copyright (c) 2018, 数字多⽹网络技术有限公司 All rights reserved.
 * File Name:
 * Version:V1.0
 * Author:qulonglong
 * Date:2019/1/25
 */

public class AlarmReceiver extends BroadcastReceiver {

    @Override

    public void onReceive(Context context, Intent intent) {

        Intent i = new Intent(context, LongTimeService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(i);
        } else {
            context.startService(i);
        }
    }


}
