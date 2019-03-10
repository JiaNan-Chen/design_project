package design.example.com.designpro;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.util.Random;

import design.example.com.designpro.service.FloatingService;
import design.example.com.designpro.util.CommonUtil;
import design.example.com.designpro.util.FloatingCheckUtil;
import design.example.com.designpro.view.FloatingBuilder;
import design.example.com.designpro.view.FloatingManager;
import design.example.com.designpro.view.RenderClickAction;

import static design.example.com.designpro.util.FloatingCheckUtil.REQUEST_DIALOG_PERMISSION;

/**
 * @author HUYA JiaNan
 */
public class MainActivity extends AppCompatActivity {
//    Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            Random random = new Random();
//            EventBus.getDefault().post(new RenderClickAction.Point(random.nextInt(1000), random.nextInt(1000)));
//            handler.sendEmptyMessageDelayed(0x00, 3000);
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CommonUtil.checkPermission(this);
        findViewById(R.id.control).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ControlActivity.class));
            }
        });
        findViewById(R.id.remote).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RemoteActivity.class));
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                FloatingCheckUtil.getPermission(this);
                return;
            }
        }
        startService(new Intent(this, FloatingService.class));
//        handler.sendEmptyMessageDelayed(0x00, 3000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_DIALOG_PERMISSION) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    Toast.makeText(this, "悬浮窗开启失败", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "悬浮窗开启成功", Toast.LENGTH_SHORT).show();
                    startService(new Intent(this, FloatingService.class));
                }
            }
        }
    }
}
