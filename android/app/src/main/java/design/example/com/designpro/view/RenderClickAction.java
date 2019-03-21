package design.example.com.designpro.view;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * @author HUYA JiaNan
 */
public class RenderClickAction implements AbstractRender {
    public static final int COMMON_RADIUS = 30;
    public static final int COMMON_COLOR = 0xff00ffff;

    private Paint mPaint = new Paint();

    public static class Point {
        public int x;
        public int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private Point mPoint;

    public RenderClickAction(Point point) {
        mPoint = point;
        mPaint.setColor(COMMON_COLOR);
        mPaint.setAntiAlias(true);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawCircle(mPoint.x, mPoint.y, COMMON_RADIUS, mPaint);
    }
}
