package design.example.com.designpro;

import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import design.example.com.designpro.service.FloatingService;
import design.example.com.designpro.util.FloatingCheckUtil;
import design.example.com.designpro.view.FloatingBuilder;
import design.example.com.designpro.view.FloatingManager;

import static design.example.com.designpro.util.FloatingCheckUtil.REQUEST_DIALOG_PERMISSION;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                FloatingCheckUtil.getPermission(this);
                return;
            }
        }
        startService(new Intent(this, FloatingService.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_DIALOG_PERMISSION) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    Toast.makeText(this, "悬浮窗开启失败", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "悬浮窗开启成功", Toast.LENGTH_SHORT).show();
                    startService(new Intent(this, FloatingService.class));
                }
            }
        }
    }
}
