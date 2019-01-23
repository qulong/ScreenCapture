package com.demo.screencapture.deviceapp;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.demo.screencapture.MainActivity;
import com.demo.screencapture.phonesms.PhoneInfoUtils;
import com.demo.screencapture.utils.FileUtil;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;

/**
 * File Name:
 * Version:V1.0
 * Author:qulonglong
 * Date:2019/1/17
 * 获取手机基本信息(厂商、型号等参数)
 * 设备信息获取实现图
 * 获取手机设备 宽、高、IMEI 信息
 * 获取手机厂商名、产品名、手机品牌、手机型号、主板名、设备名
 * 获取手机硬件名、SDK版本、android版本 、语言支持、默认语言
 * 获取 SD 卡存储信息
 */

public class SystemUtil {
    /**
     * 获取当前手机屏幕宽高
     */
    public static String getDeviceWidth(Context ctx) {
        return ctx.getResources().getDisplayMetrics().toString();
    }

  public static int[] getDeviceWH(Context ctx) {
        return new int[]{ctx.getResources().getDisplayMetrics().widthPixels,ctx.getResources().getDisplayMetrics().heightPixels};
    }

    /**
     * 获取当前手机系统版本号 8.0.0
     */
    public static String getSystemVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 获取当前手机系统sdk 26
     */
    public static String getSystemSDK_INT() {
        return "" + Build.VERSION.SDK_INT;
    }

    /**
     * 获取手机型号
     */
    public static String getSystemModel() {
        return Build.MODEL;
    }

    /**
     * 获取手机主板
     */
    public static String getSystemBOARD() {
        return Build.BOARD;
    }

    /**
     * 获取手机厂商
     */
    public static String getDeviceMANUFACTURER() {
        return Build.MANUFACTURER;
    }

    /**
     * 获取手机品牌
     */
    public static String getDeviceBrand() {
        return Build.BRAND;
    }

    /**
     * 需要“android.permission.READ_PHONE_STATE”权限
     * 硬件序列号
     */
    public static String getDeviceSerial() {
        return Build.getSerial();
    }

    /**
     * 获取当前手机系统语言。
     *
     * @return 返回当前系统语言。例如：当前设置的是“中文-中国”，则返回“zh-CN”
     */
    public static String getSystemLanguage() {
        return Locale.getDefault().getLanguage();
    }

    /**
     * 获取当前系统上的语言列表(Locale列表)
     *
     * @return 语言列表
     */
    public static Locale[] getSystemLanguageList() {
        return Locale.getAvailableLocales();
    }

    /**
     * 获取手机IMEI(需要“android.permission.READ_PHONE_STATE”权限)
     *
     * @return 手机IMEI
     */
    public static String getIMEI(Context ctx) {
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Activity.TELEPHONY_SERVICE);
        if (tm != null) {//&&tm.hasCarrierPrivileges()
            //tm.getDeviceId();
            return tm.getMeid();
        }
        return null;
    }

    /**
     * 获取手机IMSI号
     */
    public static String getIMSI(Context context) {
        TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imsi = mTelephonyMgr.getSubscriberId();

        return imsi;
    }

    /**
     * 设备序列号
     */
    public static String getANDROID_ID(Context ctx) {
        String androidId = Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID);

        return androidId;
    }

    /**
     * SD 卡是否存在
     */
    public static boolean isSDCardMount() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取手机MAC地址
     * 从 Android M 开始，无法再通过第三方 API 获得本地设备 MAC 地址（例如，WLAN 和蓝牙）。
     * WifiInfo.getMacAddress() 方法和 BluetoothAdapter.getDefaultAdapter().getAddress()
     * 方法都会返回 02:00:00:00:00:00。
     * google 已经限制 无法得到真实Mac
     * https://developer.android.com/training/articles/user-data-ids
     * android.permission.ACCESS_WIFI_STATE
     */
    public static String getMacAddress(Context ctx) {
        String result = "";
        WifiManager wifiManager = (WifiManager) ctx.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        result = wifiInfo.getMacAddress();
        return "手机macAdd:" + result;
    }

    /**
     * 手机CPU信息
     */
    public static String getCpuInfo() {
        String str1 = "/proc/cpuinfo";
        String str2 = "";
        String[] cpuInfo = {"", ""}; //1-cpu型号 //2-cpu频率
        String[] arrayOfString;
        try {
            FileReader fr = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
            str2 = localBufferedReader.readLine();
            arrayOfString = str2.split("\\s+");
            for (int i = 2; i < arrayOfString.length; i++) {
                cpuInfo[0] = cpuInfo[0] + arrayOfString[i] + " ";
            }
            str2 = localBufferedReader.readLine();
            arrayOfString = str2.split("\\s+");
            cpuInfo[1] += arrayOfString[2];
            localBufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "CPU型号:" + cpuInfo[0] + "CPU频率：" + cpuInfo[1];
    }

    /**
     * 权限
     * <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
     *
     *  获取移动网络
     */

    public static String getIPAddress1(Context context) {
        NetworkInfo info = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
                try {
                    //Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }

                    }
                } catch (SocketException e) {

                }
            }

        } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());//得到IPV4地址
            return ipAddress;
        } else {
            //当前无网络连接,请在设置中打开网络
        }
        return null;
    }

    /**
     * 将得到的int类型的IP转换为String类型
     *      
     */
    private static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." + ((ip >> 8) & 0xFF) + "." + ((ip >> 16) & 0xFF) + "." + (ip >> 24 & 0xFF);
    }

    public static String getIP(Context ctx) {
        String ip=null;
        ConnectivityManager conMann = (ConnectivityManager)
                ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobileNetworkInfo = conMann.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifiNetworkInfo = conMann.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mobileNetworkInfo.isConnected()) {
            ip = getLocalIpAddress();
            System.out.println("本地ip-----" + ip);
        } else if (wifiNetworkInfo.isConnected()) {
            WifiManager wifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            ip = intIP2StringIP(ipAddress);
            System.out.println("wifi_ip地址为------" + ip);
        }
        return ip;
    }

    private static String getLocalIpAddress() {
        try {
            String ipv4;
            ArrayList<NetworkInterface> nilist = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface ni : nilist) {
                ArrayList<InetAddress> ialist = Collections.list(ni.getInetAddresses());
                for (InetAddress address : ialist) {

//                    if (!address.isLoopbackAddress() && InetAddressUtils.isIPv4Address(ipv4=address.getHostAddress()))API 19 以前可用
                    if (!address.isLoopbackAddress() && address instanceof Inet4Address) {
                        ipv4 = address.getHostAddress();
                        return ipv4;
                    }
                }

            }

        } catch (SocketException ex) {
            Log.e("localip", ex.toString());
        }
        return null;
    }

    private static String getlocalIp() {
//        String ip;

        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()) {
//	    	                	ip=inetAddress.getHostAddress();
                        System.out.println("ip==========" + inetAddress.getHostAddress());
                        return inetAddress.getHostAddress();

                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("WifiIpAddress", ex.toString());
        }
        return null;
    }

    /**
     * 获取手机运营商
     */

    public static String getOperatorName(Context context) {
        /*
         * 中国为460
         * getSimOperatorName()就可以直接获取到运营商的名字
         * 也可以使用IMSI获取，getSimOperator()，然后根据返回值判断，例如"46000"为移动
         * IMSI相关链接：http://baike.baidu.com/item/imsi
         * 中国移动系统使用00、02、04、07，
         * 中国联通GSM系统使用01、06、09，
         * 中国电信CDMA系统使用03、05、电信4G使用11，
         * 中国铁通系统使用20。
         */
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        // getSimOperatorName就可以直接获取到运营商的名字
        return telephonyManager.getSimOperatorName();
    }

    public static void showSystemParameterTest(Context ctx) {
        String TAG = "系统参数：";
        Log.e(TAG, "手机屏幕参数：" + SystemUtil.getDeviceWidth(ctx));
        Log.e(TAG, "手机厂商：" + SystemUtil.getDeviceMANUFACTURER());
        Log.e(TAG, "手机语言：" + SystemUtil.getSystemLanguage());
        Log.e(TAG, "手机品牌：" + SystemUtil.getDeviceBrand());
        Log.e(TAG, "手机型号：" + SystemUtil.getSystemModel());
        Log.e(TAG, "Android系统版本号：" + SystemUtil.getSystemVersion());
        Log.e(TAG, "系统SDK：" + getSystemSDK_INT());
        Log.e(TAG, "手机IMEI：" + SystemUtil.getIMEI(ctx));
        Log.e(TAG, "手机IMSI：" + SystemUtil.getIMSI(ctx));
        Log.e(TAG, "手机ANDROID_ID：" + SystemUtil.getANDROID_ID(ctx));
        Log.e(TAG, "手机MacAddress：Google限制了无法得到真实地址" + getMacAddress(ctx));
        Log.e(TAG, "手机Cpu：" + getCpuInfo());
        Log.e(TAG, "手机硬件序列号：" + getDeviceSerial());
        Log.e(TAG, "手机获取移动网络IP：" + getIPAddress1(ctx));
        Log.e(TAG, "手机IP-methed2：" + getIP(ctx));
        Log.e(TAG, "手机主板：" + getSystemBOARD());
        Log.e(TAG, "mobile运营商：" + getOperatorName(ctx));

        StringBuffer sb = new StringBuffer("系统参数：\n");
        sb.append("手机屏幕参数：" + SystemUtil.getDeviceWidth(ctx));
        sb.append("\n手机厂商：" + SystemUtil.getDeviceMANUFACTURER());
        sb.append("\n手机语言：" + SystemUtil.getSystemLanguage());
        sb.append("\n手机品牌：" + SystemUtil.getDeviceBrand());
        sb.append("\n手机型号：" + SystemUtil.getSystemModel());
        sb.append("\nAndroid系统版本号：" + SystemUtil.getSystemVersion());
        sb.append("\n系统SDK：" + getSystemSDK_INT());
        sb.append("\n手机IMEI：" + SystemUtil.getIMEI(ctx));
        sb.append("\n手机IMSI：" + SystemUtil.getIMSI(ctx));
        sb.append("\n手机ANDROID_ID：" + SystemUtil.getANDROID_ID(ctx));
        sb.append("\n手机MacAddress：Google限制了无法得到真实地址" + getMacAddress(ctx));
        sb.append("\n手机硬件序列号：" + getDeviceSerial());
        sb.append("\n手机IP：" + getIP(ctx));
        sb.append("\n手机主板：" + getSystemBOARD());
        sb.append("\nmobile运营商：" + getOperatorName(ctx));
        PhoneInfoUtils phoneInfoUtils=new PhoneInfoUtils(ctx);
        sb.append(phoneInfoUtils.getPhoneInfo());
        FileUtil.addString_Txt(ctx,sb.toString(),FileUtil.deviceInfoFileName);
    }
}
