package com.demo.screencapture;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.demo.screencapture.utils.FileUtil;
import com.demo.screencapture.vo.ServiceInnerVO;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.WeakHashMap;
import java.util.concurrent.Executors;

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
    private GestureDetector mGestureDetector;
    private WindowManager.LayoutParams mLayoutParams;
    ImageView  mFloatView;
    @Override
    public void onCreate() {
        super.onCreate();

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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createImageReader();
        createFloatView();
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                startScreenShot();
            }
        },1000);
        return super.onStartCommand(intent, flags, startId);
    }

    private void startScreenShot() {

        startVirtual();
        if (csreenshotRunnable == null) {
            csreenshotRunnable = new Runnable() {
                public void run() {
                    //capture the screen
                    startCapture(mImageReader);
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
                        ExecutorUtils.delete(FileUtil.getScreenShots(getApplicationContext()), false, System.currentTimeMillis() - 10000);
                    }
                }
            };
        }
        return deleteFileRunnable;
    }

    private void createImageReader() {
//        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mWindowManager = (WindowManager) getApplicationContext().getSystemService(getApplication().WINDOW_SERVICE);

        DisplayMetrics metrics = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(metrics);
        mScreenDensity = metrics.densityDpi;
        mScreenWidth = metrics.widthPixels;
        mScreenHeight = metrics.heightPixels;
//    mImageReader = ImageReader.newInstance(mScreenWidth, mScreenHeight, PixelFormat.RGB_888, 1);

        imageReaderinit();
    }

    private void imageReaderinit() {
        mImageReader = ImageReader.newInstance(mScreenWidth, mScreenHeight, PixelFormat.RGBA_8888, 8);
//
//        mImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
//            @Override
//            public void onImageAvailable(ImageReader reader) {
//                startCapture(reader);
//                Log.e("onImageAvailable", "-----onImageAvailable-------");
//            }
//        }, getBackgroundHandler());
//       Image image= mImageReader.acquireLatestImage();
//       if (image!=null){
//           image.close();
//       }
    }

    //在后台线程里保存文件
    Handler backgroundHandler;

    private Handler getBackgroundHandler() {
        if (backgroundHandler == null) {
            HandlerThread backgroundThread =
                    new HandlerThread("catwindow", android.os.Process
                            .THREAD_PRIORITY_BACKGROUND);
            backgroundThread.start();
            backgroundHandler = new Handler(backgroundThread.getLooper());
        }
        return backgroundHandler;
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
        if (mMediaProjection == null) {
            mMediaProjection = getMediaProjectionManager().getMediaProjection(Activity.RESULT_OK, mResultData);
        }
        if (mImageReader == null) {
            mImageReader = ImageReader.newInstance(mScreenWidth, mScreenHeight, PixelFormat.RGBA_8888, 8);
        }
        //E/nulll: ddd:truemMediaProjection=truemImageReader;false
        Log.e("mResultData", "mResultData:" + (mResultData == null) + "mMediaProjection=" + (mMediaProjection == null) + "mImageReader;" + (mImageReader == null));
        Log.e("nulll", "ddd:" + (mResultData == null) + "mMediaProjection=" + (mMediaProjection == null) + "mImageReader;" + (mImageReader == null));
        mVirtualDisplay = mMediaProjection.createVirtualDisplay("screen-mirror",
                mScreenWidth, mScreenHeight, mScreenDensity, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mImageReader.getSurface(), null, null);
    }

    private void startCapture(ImageReader mImageReader) {
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
//            AsyncTaskCompat.executeParallel(mSaveTask, image);
            mSaveTask.execute(image);
//            mSaveTask.executeOnExecutor(Executors.newSingleThreadExecutor(), image);
        }

    }

    private void restartCapture() {
        Log.w("startCapture", "restartCapture");
        mImageReader.discardFreeBuffers();
        imgageSize = 8;
        handler1.removeCallbacks(csreenshotRunnable);
        //如果异步任务不为空 并且状态是 运行时  ，就把他取消这个加载任务
        if (mSaveTask != null && mSaveTask.getStatus() == AsyncTask.Status.RUNNING) {
            mSaveTask.cancel(true);
        }
        stopVirtual();
        tearDownMediaProjection();
        mImageReader.close();
        mImageReader = null;
        csreenshotRunnable = null;
        handler1.postDelayed(creatDeleteFileRunnable(), 5000);
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
//                ImageReader.newInstance(mScreenWidth, mScreenHeight, PixelFormat.RGBA_8888, 8);
                startScreenShot();
            }
        }, 500);
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

            FileUtil.getString(filename.replace(FileUtil.getAppPath(getApplicationContext()), "/sdcard"), getApplicationContext());
            if (handler1 != null && imgageSize > 0) {
                handler1.postDelayed(csreenshotRunnable, 500);
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
    private void createFloatView() {
        mGestureDetector = new GestureDetector(getApplicationContext(), new FloatGestrueTouchListener());
        mLayoutParams = new WindowManager.LayoutParams();
//        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);


        mLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        mLayoutParams.format = PixelFormat.RGBA_8888;
        // 设置Window flag
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        mLayoutParams.x = mScreenWidth;
        mLayoutParams.y = 100;
        mLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;


        mFloatView = new ImageView(getApplicationContext());
//        mFloatView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_imagetool_crop));
        mWindowManager.addView(mFloatView, mLayoutParams);
    }
    private class FloatGestrueTouchListener implements GestureDetector.OnGestureListener {
        int lastX, lastY;
        int paramX, paramY;

        @Override
        public boolean onDown(MotionEvent event) {
            lastX = (int) event.getRawX();
            lastY = (int) event.getRawY();
            paramX = mLayoutParams.x;
            paramY = mLayoutParams.y;
            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
//            startScreenShot();
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            int dx = (int) e2.getRawX() - lastX;
            int dy = (int) e2.getRawY() - lastY;
            mLayoutParams.x = paramX + dx;
            mLayoutParams.y = paramY + dy;
            // 更新悬浮窗位置
            mWindowManager.updateViewLayout(mFloatView, mLayoutParams);
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    }
}
