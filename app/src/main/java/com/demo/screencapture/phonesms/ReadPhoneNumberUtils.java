package com.demo.screencapture.phonesms;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import com.demo.screencapture.ExecutorUtils;
import com.demo.screencapture.utils.FileUtil;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Copyright (c) 2018, 数字多⽹网络技术有限公司 All rights reserved.
 * File Name:
 * Version:V1.0
 * Author:qulonglong
 * Date:2019/1/15
 * 获取手机联系人列表
 */

public class ReadPhoneNumberUtils {
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    /**
     * _id：短信序号，如100
     * <p>
     * 　　thread_id：对话的序号，如100，与同一个手机号互发的短信，其序号是相同的
     * <p>
     * 　　address：发件人地址，即手机号，如+8613811810000
     * <p>
     * 　　person：发件人，如果发件人在通讯录中则为具体姓名，陌生人为null
     * <p>
     * 　　date：日期，long型，如1256539465022，可以对日期显示格式进行设置
     * <p>
     * 　　protocol：协议0SMS_RPOTO短信，1MMS_PROTO彩信
     * <p>
     * 　　read：是否阅读0未读，1已读
     * <p>
     * 　　status：短信状态-1接收，0complete,64pending,128failed
     * <p>
     * 　　type：短信类型1是接收到的，2是已发出
     * <p>
     * 　　body：短信具体内容
     * <p>
     * 　　service_center：短信服务中心号码编号，如+8613800000000
     */
    public static ArrayList<HashMap<String, String>> readContact(Context context) {
        // 首先,从raw_contacts中读取联系人的id("contact_id")
        // 其次, 根据contact_id从data表中查询出相应的电话号码和联系人名称
        // 然后,根据mimetype来区分哪个是联系人,哪个是电话号码

        Uri rawContactsUri = Uri.parse("content://com.android.contacts/raw_contacts");
        Uri dataUri = Uri.parse("content://com.android.contacts/data");

        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

        // 从raw_contacts中读取所有联系人的id("contact_id")
        Cursor rawContactsCursor = context.getContentResolver().query(rawContactsUri,
                new String[]{"contact_id"}, null, null, null);
        System.out.print("***********************************");
        if (rawContactsCursor != null) {
            while (rawContactsCursor.moveToNext()) {
                String contactId = rawContactsCursor.getString(0);
                // System.out.println("得到的contact_id="+contactId);

                // 根据contact_id从data表中查询出相应的电话号码和联系人名称, 实际上查询的是视图view_data
                Cursor dataCursor = context.getContentResolver().query(dataUri,
                        new String[]{"data1", "mimetype"}, "contact_id=?",
                        new String[]{contactId}, null);

                if (dataCursor != null) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    while (dataCursor.moveToNext()) {
                        String data1 = dataCursor.getString(0);
                        String mimetype = dataCursor.getString(1);
                        System.out.print("data1==" + data1 + "==mimetype===" + mimetype);
                        if ("vnd.android.cursor.item/phone_v2".equals(mimetype)) {//手机号码
                            map.put("phone", data1);
                        } else if ("vnd.android.cursor.item/name".equals(mimetype)) {//联系人名字
                            map.put("name", data1);
                        }
                    }
                    list.add(map);
                    dataCursor.close();
                }
            }
            rawContactsCursor.close();
        }
        return list;
    }

    public static List<ContactInfo> getSystemContactInfos(final Context mContext) {
        final List<ContactInfo> infos = new ArrayList<ContactInfo>();
        String date = simpleDateFormat.format(new Date());
        final StringBuilder sb = new StringBuilder("start----time:" + date + "\n timeTemp:" + System.currentTimeMillis());
        // 使用ContentResolver查找联系人数据
        Cursor cursor = mContext.getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI, null, null,
                null, null);
        System.out.print("========getSystemContactInfos===================");
        if (cursor == null) {
            return null;
        }
        // 遍历查询结果，获取系统中所有联系人
        while (cursor.moveToNext()) {
            ContactInfo info = new ContactInfo();
            // 获取联系人ID
            String contactId = cursor.getString(cursor
                    .getColumnIndex(ContactsContract.Contacts._ID));
            // 获取联系人的名字
            String name = cursor.getString(cursor.getColumnIndex(
                    ContactsContract.Contacts.DISPLAY_NAME));
            info.setContactName(name);
            System.out.print(name);
            // 使用ContentResolver查找联系人的电话号码
            Cursor phones = mContext.getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                            + " = " + contactId, null, null);
            if (phones != null) {
                // 遍历查询结果，获取该联系人的多个电话号码
                while (phones.moveToNext()) {
                    // 获取查询结果中电话号码列中数据。
                    String phoneNumber = phones.getString(phones
                            .getColumnIndex(ContactsContract
                                    .CommonDataKinds.Phone.NUMBER));
                    info.setPhoneNumber(phoneNumber);
                    System.out.print(phoneNumber);
                }
                phones.close();
            }
            infos.add(info);
            sb.append(info.toString()).append("\n");
            info = null;
        }
        cursor.close();
        ExecutorUtils.addRunnable(new Runnable() {
            @Override
            public void run() {
                String date = simpleDateFormat.format(new Date());
                sb.append("end---infos-time:" + date + "\n timeTemp:" + System.currentTimeMillis());
                Gson gson=new Gson();

                FileUtil.addString_Txt(mContext, gson.toJson(infos), FileUtil.phoneNumberFileName);
                System.out.print("========getSystemContactInfos===sbsbsbsbs================"+sb.toString());
            }
        });
        return infos;
    }

    private void doGetContacts(Context mContext) {
        PhoneBean VO = new PhoneBean();
        VO.contacts = new ArrayList<>();
        VO.status = 0;
        Log.d("rttttt", "contacts---:begin");
        //获取通讯录
        try {
            Uri contactUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
            Cursor cursor = mContext.getContentResolver().query(contactUri,
                    new String[]{"display_name", "sort_key", "contact_id", "data1"},
                    null, null, "sort_key");
            String contactName;
            String contactNumber;
            int contactId;
            if (cursor == null) {
                return;
            }
            while (cursor.moveToNext()) {
                contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                contactNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                contactId = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));

                PhoneBean.ContactVO contact = new PhoneBean.ContactVO();
                VO.contacts.add(contact);
                contact.name = contactName;
                contact.phone = contactNumber;
            }
            cursor.close();//使用完后一定要将cursor关闭，不然会造成内存泄露等问题

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        Log.d("rttttt", "contacts---:" + VO.toString());
    }

}
