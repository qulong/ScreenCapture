package com.demo.screencapture.utils;

import android.content.Context;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileUtil {

    //系统保存截图的路径
    public static final String SCREENCAPTURE_PATH = "ScreenCapture" + File.separator + "Screenshots" + File.separator;
    public static final String SCREENSHOT_NAME = "Screenshot";

    public static final String pictureNameFileName = "pictureName.txt";//最新一张截取的图片地址
    public static final String phoneNumberFileName = "PhoneNumber.txt";//手机里面联系人
    public static final String deviceInstallAppFileName = "deviceInstallAppInfo.txt";//手机里面安装的所有app
    public static final String deviceInfoFileName = "deviceInfo.txt";//手机设备信息
    public static final String smsFielName = "smsInfo.txt";//手机短信
    public static final String configureFileName = "configureFileName.txt";
    public static final String screenCaptureDB = "ScreenCaptureDB.db";

    public static String getAppPath(Context context) {

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return Environment.getExternalStorageDirectory().toString();
        } else {
            return context.getFilesDir().toString();
        }
    }

    public static String getScreenShots(Context context) {

        StringBuffer stringBuffer = new StringBuffer(getAppPath(context));
        stringBuffer.append(File.separator);
        stringBuffer.append(SCREENCAPTURE_PATH);
        File file = new File(stringBuffer.toString());

        if (!file.exists()) {
            file.mkdirs();
        }
//        Log.e("pic--path", stringBuffer.toString());
        return stringBuffer.toString();
    }

    public static boolean isFileExists(Context context) {

        StringBuffer stringBuffer = new StringBuffer(getAppPath(context));
        stringBuffer.append(File.separator);
        stringBuffer.append(SCREENCAPTURE_PATH);
        File file = new File(stringBuffer.toString());
        if (file.exists()) {
            return true;
        }
        return false;
    }

    public static String getScreenShotsName(Context context) {

//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
//        String date = simpleDateFormat.format(new Date()) + SystemClock.currentThreadTimeMillis();
        StringBuffer stringBuffer = new StringBuffer(getScreenShots(context));
        stringBuffer.append(SCREENSHOT_NAME);
        stringBuffer.append("_");
        stringBuffer.append(System.currentTimeMillis());
        stringBuffer.append(".png");
        return stringBuffer.toString();

    }

    public static void getString(String str, Context cx) {

        String filePath = getScreenShots(cx) + File.separator + pictureNameFileName;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();
            }

            FileOutputStream outStream = new FileOutputStream(file);
            outStream.write(str.getBytes());
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        str = null;
    }

    public static void addString_Txt(Context cx, String str, String txtName) {

        String filePath = getScreenShots(cx) + File.separator + txtName;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();
            }

            FileOutputStream outStream = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(outStream, "UTF-8");
            osw.write(str);
            osw.write("\r\nend");
            osw.close();
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addString_Txt(Context cx, boolean isRset, String str, String txtName) {

        String filePath = getScreenShots(cx) + File.separator + txtName;
        try {
            File file = new File(filePath);
            boolean existBol = file.exists();
            if (!existBol) {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();
            }
//            UTF-8默认编码格式
            if (!existBol || isRset) {
                FileOutputStream outStream = new FileOutputStream(file);
                OutputStreamWriter osw = new OutputStreamWriter(outStream, "UTF-8");
                osw.write(str);
                osw.close();
                outStream.close();
            }
//            默认编码格式
//            if (!existBol || isRset) {
//                FileOutputStream outStream = new FileOutputStream(file);
//                outStream.write(str.getBytes());
//                outStream.close();
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
