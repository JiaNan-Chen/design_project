package design.example.com.designpro.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import design.example.com.designpro.R;

/**
 * @author HUYA JiaNan
 */
public abstract class FloatingBuilder<T extends View> {
    Context mContext;

    public T getContentView() {
        return mContentView;
    }

    T mContentView;

    public FloatingBuilder(Context context) {
        this.mContext = context;
    }

    public abstract T createView();

    public abstract WindowManager.LayoutParams createWindowManagerParams(int left, int top) throws Exception;

    public static class RenderFloating extends FloatingBuilder<RenderView> {

        private static RenderFloating singleton;

        public static RenderFloating getInstance(Context context) {
            if (singleton == null) {
                synchronized (RenderFloating.class) {
                    if (singleton == null) {
                        singleton = new RenderFloating(context);
                    }
                }
            }
            return singleton;
        }

        public RenderFloating(Context context) {
            super(context);
        }

        @Override
        public RenderView createView() {
            if (mContentView == null) {
                mContentView = (RenderView) LayoutInflater.from(mContext).inflate(R.layout.render_layout, null);
            }
            return mContentView;
        }

        @Override
        public WindowManager.LayoutParams createWindowManagerParams(int left, int top) throws Exception {
            if (mContentView == null) {
                throw new Exception("get null content view");
            }
            Log.i(getClass().getName(), "width:" + mContentView.getWidth()
                    + "   height:" + mContentView.getMeasuredWidth());
            WindowManager.LayoutParams params = new WindowManager.LayoutParams();
            params.gravity = Gravity.TOP | Gravity.LEFT;
            params.x = left;
            params.y = top;
            params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
            params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
            params.format = PixelFormat.TRANSLUCENT;
            return params;
        }
    }
}
