package kartsev.dmitry.ru.clickbuttonui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by Jag on 13.01.2017.
 */

public class PaintView extends View {
    private final int cellSize = 50;
    private Paint mPaint;

    public PaintView(Context context) {
        super(context);
        initView();
    }

    public PaintView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth(2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(Color.WHITE);

        int h = this.getHeight();
        Log.d("INFO", "Height of PaintView - " + h);
        int w = this.getWidth();
        Log.d("INFO", "Width of PaintView - " + w);

        for(int r = 0; r <= h / cellSize; ++r) {
            int y = r * cellSize;
            canvas.drawLine(0, y, w, y, mPaint);
            Log.d("INFO", "Drawing row at " + y);
        }
        for(int c = 0; c <= w / cellSize; ++c) {
            int x = c * cellSize;
            canvas.drawLine(x, 0, x, h, mPaint);
        }

        int r = (h / cellSize) + 1;
        mPaint.setAntiAlias(true);
        for(int i = 1; i < r; i += 2) {
            int xy = i * cellSize;
            canvas.drawCircle(xy, xy, cellSize, mPaint);
        }
        mPaint.setAntiAlias(false);
    }
}
