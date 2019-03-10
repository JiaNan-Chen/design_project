package design.example.com.designpro.view;

import android.content.Context;
import android.view.View;
import android.view.WindowManager;


/**
 * 悬浮窗管理类
 */
public class FloatingManager implements View.OnClickListener {
    private static WindowManager mWindowManager;
    private static FloatingManager mInstance;
    private Context mContext;
    private int mStatusBarHeight;

    public interface FloatingAction {
        void before();

        void after();
    }

    public static FloatingManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (FloatingManager.class) {
                if (mInstance == null) {
                    mInstance = new FloatingManager(context);
                }
            }
        }
        return mInstance;
    }

    private FloatingManager(Context context) {
        mContext = context;
        //获得WindowManager对象
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            mStatusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }
    }

    public boolean addView(View view, WindowManager.LayoutParams params, View.OnTouchListener touchListener) {
        try {
            view.setOnTouchListener(touchListener);
            mWindowManager.addView(view, params);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean removeView(View view, FloatingAction action) {
        if (action != null) {
            action.before();
        }
        boolean flag = removeView(view);
        if (action != null) {
            action.after();
        }
        return flag;
    }

    public boolean removeView(View view) {
        try {
            mWindowManager.removeView(view);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 更新悬浮窗参数
     *
     * @param view
     * @param params
     * @return
     */
    public boolean updateView(View view, WindowManager.LayoutParams params) {
        try {
            mWindowManager.updateViewLayout(view, params);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getScreenHeight() {
        return mWindowManager.getDefaultDisplay().getHeight();
    }

    public int getScreenWidth() {
        return mWindowManager.getDefaultDisplay().getWidth();
    }

    @Override
    public void onClick(View v) {

    }
}
