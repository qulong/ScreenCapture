package com.demo.screencapture;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.os.AsyncTaskCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.WeakHashMap;

/**
 * <p>
 * 启动悬浮窗界面
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class FloatWindowsService extends Service {

    private MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;

    private static Intent mResultData = null;


    private ImageReader mImageReader;
    private WindowManager mWindowManager;
    private int mScreenWidth;
    private int mScreenHeight;
    private int mScreenDensity;

    Handler handler1 = new Handler();
    private int imgageSize = 8;
    Runnable csreenshotRunnable;
    Runnable deleteFileRunnable;
    private SaveTask mSaveTask;
    private WeakHashMap<Integer, List<String>> imgList;

    @Override
    public void onCreate() {
        super.onCreate();

//    createFloatView();

        createImageReader();
        startScreenShot();
    }

    public static Intent getResultData() {
        return mResultData;
    }

    public static void setResultData(Intent mResultData) {
        FloatWindowsService.mResultData = mResultData;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startScreenShot() {

        startVirtual();
        if (csreenshotRunnable == null) {
            csreenshotRunnable = new Runnable() {
                public void run() {
                    //capture the screen
                    startCapture();
                }
            };
        }
        handler1.postDelayed(csreenshotRunnable, 2000);
    }


    private Runnable creatDeleteFileRunnable() {
        if (deleteFileRunnable == null) {
            deleteFileRunnable = new Runnable() {
                @Override
                public void run() {
                    if (FileUtil.isFileExists(getApplicationContext())) {
                        DeleteUtils.delete(FileUtil.getScreenShots(getApplicationContext()), false, System.currentTimeMillis() - 10000);
                    }
                }
            };
        }
        return deleteFileRunnable;
    }

    private void createImageReader() {
        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mWindowManager = (WindowManager) getApplicationContext().getSystemService(getApplication().WINDOW_SERVICE);

        DisplayMetrics metrics = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(metrics);
        mScreenDensity = metrics.densityDpi;
        mScreenWidth = metrics.widthPixels;
        mScreenHeight = metrics.heightPixels;
//    mImageReader = ImageReader.newInstance(mScreenWidth, mScreenHeight, PixelFormat.RGB_888, 1);
        mImageReader = ImageReader.newInstance(mScreenWidth, mScreenHeight, PixelFormat.RGBA_8888, imgageSize);
//        mImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
//            @Override
//            public void onImageAvailable(ImageReader reader) {
//
//            }
//        },handler1);

    }

    public void startVirtual() {
        if (mMediaProjection != null) {
            virtualDisplay();
        } else {
            setUpMediaProjection();
            virtualDisplay();
        }
    }

    public void setUpMediaProjection() {
        if (mResultData == null) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            startActivity(intent);
        } else {
            mMediaProjection = getMediaProjectionManager().getMediaProjection(Activity.RESULT_OK, mResultData);
        }
    }

    private MediaProjectionManager getMediaProjectionManager() {

        return (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
    }

    private void virtualDisplay() {
        mVirtualDisplay = mMediaProjection.createVirtualDisplay("screen-mirror",
                mScreenWidth, mScreenHeight, mScreenDensity, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mImageReader.getSurface(), null, null);
    }

    private void startCapture() {
        Image image = null;
        if (imgageSize > 0) {
            try {
                image = mImageReader.acquireLatestImage();
                imgageSize--;
                Log.w("startCapture", "s=" + imgageSize);
            } catch (IllegalStateException e) {
                Log.w("startCapture", "s=" + imgageSize + "/" + e.getMessage());
                e.printStackTrace();
                restartCapture();
            }
        } else {
            restartCapture();
        }
        if (image == null) {
            startScreenShot();
        } else {
            mSaveTask = new SaveTask();
            Log.w("startCapture", "new mSaveTask");
            AsyncTaskCompat.executeParallel(mSaveTask, image);
        }
    }

    private void restartCapture() {
        Log.w("startCapture", "restartCapture");
        imgageSize = 8;
        handler1.removeCallbacks(csreenshotRunnable);
        //如果异步任务不为空 并且状态是 运行时  ，就把他取消这个加载任务
        if (mSaveTask != null && mSaveTask.getStatus() == AsyncTask.Status.RUNNING) {
            mSaveTask.cancel(true);
        }
        handler1.postDelayed(creatDeleteFileRunnable(), 1000);
    }

    public class SaveTask extends AsyncTask<Image, Void, String> {

        @Override
        protected String doInBackground(Image... params) {
            Log.w("startCapture", "thread name:" + Thread.currentThread().getId());
            if (params == null || params.length < 1 || params[0] == null) {
                Log.w("startCapture", "s=null");
                return null;
            }

            Image image = params[0];

            int width = image.getWidth();
            int height = image.getHeight();
            final Image.Plane[] planes = image.getPlanes();
            final ByteBuffer buffer = planes[0].getBuffer();
            //每个像素的间距
            int pixelStride = planes[0].getPixelStride();
            //总的间距
            int rowStride = planes[0].getRowStride();
            int rowPadding = rowStride - pixelStride * width;
            Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_4444);
            bitmap.copyPixelsFromBuffer(buffer);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);
            image.close();
            File fileImage = null;
            if (bitmap != null) {
                try {
                    fileImage = new File(FileUtil.getScreenShotsName(getApplicationContext()));
                    if (!fileImage.exists()) {
                        fileImage.createNewFile();
                    }
                    FileOutputStream out = new FileOutputStream(fileImage);
                    if (out != null) {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, out);
                        out.flush();
                        out.close();
//                        通知系统
//                        Intent media = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//                        Uri contentUri = Uri.fromFile(fileImage);
//                        media.setData(contentUri);
//                        sendBroadcast(media);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    fileImage = null;
                } catch (IOException e) {
                    e.printStackTrace();
                    fileImage = null;
                }
            }
            if (fileImage != null) {
                return fileImage.toString();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String filename) {
            super.onPostExecute(filename);
            Log.w("startCapture", "onPostExecutes=" + imgageSize + "//bitmap==" + filename);
            FileUtil.getString(filename, getApplicationContext());
            if (handler1 != null && imgageSize > 0) {
                handler1.postDelayed(csreenshotRunnable, 300);
            } else {
                startScreenShot();
            }
        }
    }


    private void tearDownMediaProjection() {
        if (mMediaProjection != null) {
            mMediaProjection.stop();
            mMediaProjection = null;
        }
    }

    private void stopVirtual() {
        if (mVirtualDisplay == null) {
            return;
        }
        mVirtualDisplay.release();
        mVirtualDisplay = null;
    }

    @Override
    public void onDestroy() {
        // to remove mFloatLayout from windowManager
        super.onDestroy();

        stopVirtual();
        tearDownMediaProjection();
        //如果异步任务不为空 并且状态是 运行时  ，就把他取消这个加载任务
        if (mSaveTask != null && mSaveTask.getStatus() == AsyncTask.Status.RUNNING) {
            mSaveTask.cancel(true);
        }
        if (handler1 != null) {
            if (csreenshotRunnable != null) {
                handler1.removeCallbacks(csreenshotRunnable);
            }
            if (deleteFileRunnable != null) {
                handler1.removeCallbacks(deleteFileRunnable);
            }
        }
    }

}
