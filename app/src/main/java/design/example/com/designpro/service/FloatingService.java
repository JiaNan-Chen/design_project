package design.example.com.designpro.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import design.example.com.designpro.view.FloatingBuilder;
import design.example.com.designpro.view.FloatingManager;

public class FloatingService extends Service {
    FloatingBuilder.RenderFloating mRenderFloating = FloatingBuilder.RenderFloating.getInstance(this);

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            boolean b = FloatingManager.getInstance(this).addView(mRenderFloating.createView(), mRenderFloating.createWindowManagerParams(0, 0), null);
            Log.i("floatingservice", b+"");
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}

