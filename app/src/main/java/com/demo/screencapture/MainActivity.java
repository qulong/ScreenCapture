package com.demo.screencapture;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.screencapture.deviceapp.ApplicationUtil;
import com.demo.screencapture.deviceapp.CheckAppInstalledUtil;
import com.demo.screencapture.deviceapp.SystemUtil;
import com.demo.screencapture.location.GPSUtils;
import com.demo.screencapture.memory.MemoryRunnable;
import com.demo.screencapture.phonesms.PhoneInfoUtils;
import com.demo.screencapture.phonesms.ReadPhoneNumberUtils;
import com.demo.screencapture.phonesms.Readsms;
import com.demo.screencapture.utils.FileUtil;
import com.demo.screencapture.utils.PermissionManager;
import com.demo.screencapture.utils.ReadAndWriterFileUtils;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainActivity extends FragmentActivity {


    public static final int REQUEST_MEDIA_PROJECTION = 18;
    TextView picPath;
    Button readPhoneNumberBtn;
    Button smsBtn;
    Button phoneBtn;
    Button button;
    Button gspBtn;
    Button installApp;
    Button cpuLogBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        picPath = findViewById(R.id.pic_path_url);
        phoneBtn = findViewById(R.id.show_device_info_phone);
        readPhoneNumberBtn = findViewById(R.id.show_device_info_read_phone);
        smsBtn = findViewById(R.id.show_device_info_read_sms);
        button = findViewById(R.id.show_device_info_sys);
        gspBtn = findViewById(R.id.show_device_info_gps);
        installApp = findViewById(R.id.show_device_info_install_app);
        cpuLogBtn = findViewById(R.id.show_device_info_cpu_log);

        oncickViews();
        //检查版本是否大于M
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean hasPermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
            if (hasPermission) {
                requestCapturePermission();
            } else {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1001);
            }
        } else {
//      requestOverlayPermission();
            requestCapturePermission();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1001:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showToast("已获取权限,可以保存图片");
                    initDB();
                    requestCapturePermission();
                } else {
                    showToast("您拒绝了写文件权限，无法保存图片");
                }
                break;
        }

    }

    public void requestCapturePermission() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            //5.0 之后才允许使用屏幕截图
            showToast("该手机不支持");
            return;
        }
        picPath.setText(FileUtil.getScreenShots(MainActivity.this));
        MediaProjectionManager mediaProjectionManager = (MediaProjectionManager)
                getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        startActivityForResult(
                mediaProjectionManager.createScreenCaptureIntent(),
                REQUEST_MEDIA_PROJECTION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_MEDIA_PROJECTION:

                if (resultCode == RESULT_OK && data != null) {
//                    intentService = data;
                    FloatWindowsService.setResultData(data);
                    startService(new Intent(getApplicationContext(), FloatWindowsService.class));
                }
//                finish();
                break;
        }

    }

    //仅在debug版本使用 悬浮按钮
    //<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
    //https://blog.csdn.net/songzi1228/article/details/82183552
    //https://blog.csdn.net/lovedou0816/article/details/79253710
    private static final int REQUEST_OVERLAY = 6666;

    private void requestOverlayPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(MainActivity.this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_OVERLAY);
            } else {

            }
        }
    }

    private void showToast(String string) {
        Toast.makeText(MainActivity.this, string, Toast.LENGTH_LONG).show();
    }

    private void initDB() {
        String path = FileUtil.getScreenShots(MainActivity.this);
        File pathFile = new File(path);
        File file = new File(path + File.separator + FileUtil.screenCaptureDB);
        try {
            if (!pathFile.exists()) {
                pathFile.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//        SQLiteDatabase  mDb = SQLiteDatabase.openOrCreateDatabase(file,null);
//        mDb.close();
    }

    private void oncickViews() {
        phoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PermissionManager.sharedInstance().requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.GET_ACCOUNTS}, PermissionManager.RequestCodeContacts, new PermissionManager.Listener() {
                    @Override
                    public void onGranted(int requestCode) {
//                        doGetContacts();
                        ReadPhoneNumberUtils.getSystemContactInfos(MainActivity.this);
//                        ReadPhoneNumberUtils.readContact(MainActivity.this);
                    }

                    @Override
                    public void onDenied(int requestCode) {

                    }

                    @Override
                    public void onAlwaysDenied(int requestCode, List<String> permissions) {

                    }
                });
            }
        });
        smsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PermissionManager.sharedInstance().requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.READ_SMS}, PermissionManager.RequestCodeSMS, new PermissionManager.Listener() {
                    @Override
                    public void onGranted(int requestCode) {
                        ExecutorUtils.addRunnable(new Runnable() {
                            @Override
                            public void run() {
                                Log.e("sms-read", "sms----------------read" + System.currentTimeMillis());
                                Gson gson = new Gson();
                                FileUtil.addString_Txt(MainActivity.this, gson.toJson(Readsms.getSmsFromPhone(MainActivity.this)), FileUtil.smsFielName);
                                Log.e("sms-read", "sms----------------read---end" + System.currentTimeMillis());
                            }
                        });
                    }

                    @Override
                    public void onDenied(int requestCode) {

                    }

                    @Override
                    public void onAlwaysDenied(int requestCode, List<String> permissions) {

                    }
                });
            }
        });
        readPhoneNumberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExecutorUtils.addRunnable(new Runnable() {
                    @Override
                    public void run() {
                        String json = ReadAndWriterFileUtils.read1(FileUtil.getScreenShots(MainActivity.this) + File.separator + FileUtil.phoneNumberFileName);
                        System.out.print(json);
                        Log.e("MMMMM", json);
                    }
                });
            }
        });

        /**
         * 手机设备信息
         * */
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionManager.sharedInstance().requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, PermissionManager.RequestCodePhone, new PermissionManager.Listener() {
                    @Override
                    public void onGranted(int requestCode) {

                        ExecutorUtils.addRunnable(new Runnable() {
                            @Override
                            public void run() {
                                SystemUtil.showSystemParameterTest(MainActivity.this);
                            }
                        });
                    }

                    @Override
                    public void onDenied(int requestCode) {

                    }

                    @Override
                    public void onAlwaysDenied(int requestCode, List<String> permissions) {

                    }
                });
            }
        });


        gspBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionManager.sharedInstance().requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PermissionManager.RequestCodeLocation, new PermissionManager.Listener() {
                    @Override
                    public void onGranted(int requestCode) {
                        GPSUtils gpsUtils = new GPSUtils(MainActivity.this);

                        GPSUtils.getAddressStr();
                        GPSUtils.getLocalCity();
                    }

                    @Override
                    public void onDenied(int requestCode) {

                    }

                    @Override
                    public void onAlwaysDenied(int requestCode, List<String> permissions) {

                    }
                });
            }
        });
        installApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExecutorUtils.addRunnable(new Runnable() {
                    @Override
                    public void run() {
                        ApplicationUtil util = ApplicationUtil.newInstance(MainActivity.this);
                        util.loadAllApp();
                    }
                });
            }
        });
        cpuLogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MemoryRunnable.getInstance().init(getApplicationContext(), 500L);
                MemoryRunnable.getInstance().start();
            }
        });
    }
}
