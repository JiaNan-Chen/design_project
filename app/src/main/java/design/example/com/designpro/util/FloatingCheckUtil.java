package design.example.com.designpro.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

public class FloatingCheckUtil {

    public static int REQUEST_DIALOG_PERMISSION = 0x8520;

    public static void getPermission(Activity activity) {
        int sdkVersion = Build.VERSION.SDK_INT;
        //8.0以上
        if (sdkVersion >= Build.VERSION_CODES.O) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            activity.startActivityForResult(intent, REQUEST_DIALOG_PERMISSION);
        }
        //6.0-8.0
        else if (sdkVersion >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(activity)) {
                //没有悬浮窗权限,跳转申请
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(Uri.parse("package:" + activity.getPackageName()));
                activity.startActivityForResult(intent, REQUEST_DIALOG_PERMISSION);
            }
        }
    }
}
