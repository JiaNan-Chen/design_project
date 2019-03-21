package design.example.com.designpro.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

/**
 * @author HUYA JiaNan
 */
public class RenderView extends FrameLayout {

    ArrayList<AbstractRender> mRenderList = new ArrayList<>();

    public RenderView(Context context) {
        this(context, null);
    }

    public RenderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RenderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint.setColor(0xff00ff00);
    }


    Paint mPaint = new Paint();

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int size = mRenderList.size();
        for (int index = 0; index < size; index++) {
            AbstractRender render = mRenderList.get(index);
            render.draw(canvas);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void drawHintPoint(RenderClickAction.Point point) {
        mRenderList.clear();
        mRenderList.add(new RenderClickAction(point));
        invalidate();
    }
}
