package design.example.com.designpro;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ControlActivity extends AppCompatActivity {
    ImageView mImageView;

    boolean isFirstInit = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        mImageView = findViewById(R.id.video_img);
        EventBus.getDefault().register(this);
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    OkHttpClient okHttpClient = new OkHttpClient();
                    final Request request = new Request.Builder().url("http://203.195.224.187/DesignPro/getShot")
                            .method("GET", null).cacheControl(CacheControl.FORCE_NETWORK).build();
//                    okHttpClient.newCall(request).enqueue(new Callback() {
//                        @Override
//                        public void onFailure(Call call, IOException e) {
//
//                        }
//
//                        @Override
//                        public void onResponse(Call call, Response response) throws IOException {
//                            try {
//                                JSONObject o = new JSONObject(response.body().string());
//                                EventBus.getDefault().post(new MyShotData(o.getString("img")));
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });
                    try {

                        Response response = okHttpClient.newCall(request).execute();
                        JSONObject o = new JSONObject(response.body().string());
                        EventBus.getDefault().post(new MyShotData(o.getString("img")));
//
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
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
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    int mShotWidth, mShotHeight, mImageWidth, mImageHeight;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showShot(MyShotData data) {
        byte[] decodedString = Base64.decode(data.data, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        if (isFirstInit) {
            mShotWidth = decodedByte.getWidth();
            mShotHeight = decodedByte.getHeight();
            mImageWidth = mImageView.getWidth();
            mImageHeight = mImageView.getHeight();
            mImageView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        if ((mShotWidth * 1.0f / mShotHeight) < (mImageWidth * 1.0f / mImageHeight)) {
                            float realWidth = mShotWidth * 1.0f * mImageHeight / mShotHeight;
                            float currentX = event.getX();
                            float currentY = event.getY();
                            float pecentX, pecentY;
                            pecentX = (currentX - (mImageWidth - realWidth) * 1.0f / 2) / realWidth;
                            pecentY = currentY / mImageHeight;
                            Log.i("point control", "X:" + pecentX + "    Y:" + pecentY);
                            OkHttpClient okHttpClient = new OkHttpClient();
                            final Request request = new Request.Builder().url("http://203.195.224.187/DesignPro/uploadLocation?x=" + pecentX + "&&y=" + pecentY)
                                    .method("GET", null).cacheControl(CacheControl.FORCE_NETWORK).build();
                            okHttpClient.newCall(request).enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {

                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {

                                }
                            });
                        }
                    }
                    return true;
                }
            });
            isFirstInit = false;
        }
        mImageView.setImageBitmap(decodedByte);
    }

    public static class MyShotData {
        String data;

        public MyShotData(String data) {
            this.data = data;
        }
    }
}
