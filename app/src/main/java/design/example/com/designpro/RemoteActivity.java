package design.example.com.designpro;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import design.example.com.designpro.util.CommonUtil;

public class RemoteActivity extends AppCompatActivity {
    private static final int REQUEST_MEDIA_PROJECTION = 10086;

    MediaProjectionManager mMediaProjectionManager;

    MediaProjection mMediaProjection;

    ImageReader mImageReader;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote);
        //1、首先获取MediaProjectionManager
        mMediaProjectionManager = (MediaProjectionManager) getApplicationContext().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_MEDIA_PROJECTION) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(getApplication(), "开启录制视频成功" +
                        "向服务器发送", Toast.LENGTH_SHORT).show();

                mMediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data);

                DisplayMetrics metrics = new DisplayMetrics();
                Display display = getWindowManager().getDefaultDisplay();
                if (Build.VERSION.SDK_INT < 17) {
                    display.getMetrics(metrics);
                } else {
                    display.getRealMetrics(metrics);
                }

                int screenWidth = metrics.widthPixels;
                int screenHeight = metrics.heightPixels;
                int densityDpi = metrics.densityDpi;

                mImageReader = ImageReader.newInstance(screenWidth, screenHeight, PixelFormat.RGBA_8888, 1);

                mMediaProjection.createVirtualDisplay("shot_image", screenWidth,
                        screenHeight,
                        densityDpi, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mImageReader
                                .getSurface(), null, null);
                startCapture();
            } else {
                Toast.makeText(getApplication(), "开启录制视频失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startCapture() {
        Image image = mImageReader.acquireLatestImage();
        if (image == null) {
            startCapture();
            return;
        }
        int width = image.getWidth();
        int height = image.getHeight();
        final Image.Plane[] planes = image.getPlanes();
        final ByteBuffer buffer = planes[0].getBuffer();
        int pixelStride = planes[0].getPixelStride();
        int rowStride = planes[0].getRowStride();
        int rowPadding = rowStride - pixelStride * width;
        Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(buffer);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);
        if (bitmap != null) {
            //发送
            saveBitmap("screen_shot.png", bitmap);
        }
        image.close();
    }

    /**
     * 保存方法
     */
    public void saveBitmap(String picName, Bitmap bm) {
        File f = new File(CommonUtil.getExternalPath() + "DesignPro", picName);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
