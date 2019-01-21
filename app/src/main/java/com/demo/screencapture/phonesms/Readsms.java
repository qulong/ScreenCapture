package com.demo.screencapture.phonesms;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright (c) 2018, 数字多⽹网络技术有限公司 All rights reserved.
 * File Name:
 * Version:V1.0
 * Author:qulonglong
 * Date:2019/1/15
 * 读取短信
 */

public class Readsms {

    private static Uri SMS = Uri.parse("content://sms/");

    public static void getSmsFromPhone1(Context context) {
        ContentResolver cr = context.getContentResolver();
        String[] projection = new String[]{"_id", "address", "person", "body", "date", "service_center"};//"_id", "address", "person",, "date", "type
        Cursor cur = cr.query(SMS, projection, null, null, "date desc");
        if (null == cur)
            return;

        System.out.print("#############################");
        while (cur.moveToNext()) {
            System.out.print(cur.toString());
            String number = cur.getString(cur.getColumnIndex("address"));//手机号
            String name = cur.getString(cur.getColumnIndex("person"));//联系人姓名列表
            String body = cur.getString(cur.getColumnIndex("body"));
            String service_center = cur.getString(cur.getColumnIndex("service_center"));
            //这里我是要获取自己短信服务号码中的验证码~~
//            Pattern pattern = Pattern.compile(" [a-zA-Z0-9]{10}");
//            Matcher matcher = pattern.matcher(body);
//            if (matcher.find()) {
//                String res = matcher.group().substring(1, 11);
//                TextView.setText(res);
//            }
            System.out.println("ReadMsg>>" + number + "/service_center" + service_center + "/" + name + "/" + body);
        }
        System.out.print("#############################");
        cur.close();
    }

    public static List<Map<String, Object>> getSmsFromPhone(Context context) {
        List<Map<String, Object>> list = new ArrayList<>();
        ContentResolver cr = context.getContentResolver();
        String[] projection = new String[]{"_id", "address", "person", "body", "date", "type"};
        Cursor cur = cr.query(SMS, projection, null, null, "date desc");
        if (null == cur) {
            Log.i("readNumber", "************cur == null");
            return null;
        }
        while (cur.moveToNext()) {
            String number = cur.getString(cur.getColumnIndex("address"));//手机号
            String name = cur.getString(cur.getColumnIndex("person"));//联系人姓名列表
            String body = cur.getString(cur.getColumnIndex("body"));//短信内容
            String date = cur.getString(cur.getColumnIndex("date"));//时间
            String type = cur.getString(cur.getColumnIndex("type"));//
            //至此就获得了短信的相关的内容, 以下是把短信加入map中，构建listview,非必要。
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("name", name);
            map.put("number", number);
            map.put("date", date);
            map.put("body", body);
            map.put("type", type);
            list.add(map);
        }
        cur.close();
        return list;
    }
}
