package com.demo.screencapture;

import android.app.Application;
import android.graphics.Bitmap;
import android.os.StrictMode;

public class ScreenCaptureApplication extends Application {


  private Bitmap mScreenCaptureBitmap;

  @Override
  public void onCreate() {
    if (BuildConfig.DEBUG) {
      StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
              .detectDiskReads()
              .detectDiskWrites()
              .detectNetwork()   // or .detectAll() for all detectable problems
              .penaltyLog()
              .build());
      StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
              .detectLeakedSqlLiteObjects()
              .detectLeakedClosableObjects()
              .penaltyLog()
              .penaltyDeath()
              .build());
    }
    super.onCreate();
  }


  public Bitmap getmScreenCaptureBitmap() {
    return mScreenCaptureBitmap;
  }

  public void setmScreenCaptureBitmap(Bitmap mScreenCaptureBitmap) {
    this.mScreenCaptureBitmap = mScreenCaptureBitmap;
  }
}
