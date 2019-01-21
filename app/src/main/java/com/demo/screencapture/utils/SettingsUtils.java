package com.demo.screencapture.utils;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * File Name:SettingsUtils
 * Version:V1.0
 * Author:qulonglong
 * Date:2018/12/5
 */


public class SettingsUtils {
   private static MyLogger myLogger = MyLogger.getLogger(SettingsUtils.class.getName());

   //跳转到设置界面 ---begin
   public static boolean gotoPermissionSetting(Application application,Context context) {
       boolean success = true;
       Intent intent = new Intent();
       intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
       String packageName = AppUtils.getPackageName(application);

       OSUtils.ROM romType = OSUtils.getRomType();
       switch (romType) {
           case EMUI: // 华为
               intent.putExtra("packageName", packageName);
               intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity"));
               break;
           case Flyme: // 魅族
               intent.setAction("com.meizu.safe.security.SHOW_APPSEC");
               intent.addCategory(Intent.CATEGORY_DEFAULT);
               intent.putExtra("packageName", packageName);
               break;
           case MIUI: // 小米
               String rom = getMiuiVersion();
               if ("V6".equals(rom) || "V7".equals(rom)) {
                   intent.setAction("miui.intent.action.APP_PERM_EDITOR");
                   intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
                   intent.putExtra("extra_pkgname", packageName);
               } else if ("V8".equals(rom) || "V9".equals(rom)) {
                   intent.setAction("miui.intent.action.APP_PERM_EDITOR");
                   intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
                   intent.putExtra("extra_pkgname", packageName);
               } else {
                   intent = getAppDetailsSettingsIntent(packageName);
               }
               break;
           case Sony: // 索尼
               intent.putExtra("packageName", packageName);
               intent.setComponent(new ComponentName("com.sonymobile.cta", "com.sonymobile.cta.SomcCTAMainActivity"));
               break;
           case ColorOS: // OPPO
               intent.putExtra("packageName", packageName);
               intent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.PermissionManagerActivity"));
               break;
           case EUI: // 乐视
               intent.putExtra("packageName", packageName);
               intent.setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.PermissionAndApps"));
               break;
           case LG: // LG
               intent.setAction("android.intent.action.MAIN");
               intent.putExtra("packageName", packageName);
               ComponentName comp = new ComponentName("com.android.settings", "com.android.settings.Settings$AccessLockSummaryActivity");
               intent.setComponent(comp);
               break;
           case SamSung: // 三星
           case SmartisanOS: // 锤子
               gotoAppDetailSetting(packageName, context);
               break;
           default:
               intent.setAction(Settings.ACTION_SETTINGS);
               myLogger.i("没有适配该机型, 跳转普通设置界面");
               success = false;
               break;
       }
       try {
           context.startActivity(intent);
       } catch (Exception e) {
           e.printStackTrace();
           // 跳转失败, 前往普通设置界面
           gotoSetting(context);
           success = false;
           myLogger.i("无法跳转权限界面, 开始跳转普通设置界面");
       }
       return success;
   }

   private static Intent getAppDetailsSettingsIntent(String packageName) {
       return new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
               .setData(Uri.parse("package:" + packageName))
               .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
   }

   private static void gotoAppDetailSetting(String packageName, Context context) {
       context.startActivity(getAppDetailsSettingsIntent(packageName));
   }

   private static void gotoSetting(Context context) {
       context.startActivity(getSettingIntent());
   }

   private static Intent getSettingIntent() {
       return new Intent(Settings.ACTION_SETTINGS)
               .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
   }

   private static String getMiuiVersion() {
       String propName = "ro.miui.ui.version.name";
       String line;
       BufferedReader input = null;
       try {
           Process p = Runtime.getRuntime().exec("getprop " + propName);
           input = new BufferedReader(
                   new InputStreamReader(p.getInputStream()), 1024);
           line = input.readLine();
           input.close();
       } catch (IOException ex) {
           ex.printStackTrace();
           return null;
       } finally {
           if (input != null) {
               try {
                   input.close();
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
       }
       return line;
   }
}
