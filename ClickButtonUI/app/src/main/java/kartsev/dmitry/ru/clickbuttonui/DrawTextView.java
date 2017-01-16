package kartsev.dmitry.ru.clickbuttonui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Jag on 13.01.2017.
 */

public class DrawTextView extends TextView {
    private Paint mPaint = null;
    private final int mTextSize = 45;
    private final String mText = "Android";

    public DrawTextView(Context context) {
        super(context);
        this.initView();
    }

    public DrawTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initView();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int textWidth = (int) mPaint.measureText(mText);
        int width = this.getWidth();
        int height = this.getHeight();

        /*canvas.drawColor(Color.WHITE);
        int x = (width - textWidth) / 2;
        int y = (height + mTextSize) / 2;

        canvas.drawText(mText, x, y, mPaint);*/

        canvas.drawColor(Color.WHITE);

        float l = (width / 2) - (textWidth / 2);
        float t = (height / 2);
        float r = l + textWidth;
        float b = t + height / 4;

		/* Создаем прямоугольник по координатам */
        RectF rect = new RectF(l,t,r,b);
        /*mPaint.setAlpha(25);
        canvas.drawRect(rect, mPaint);*/
        Path path = new Path();
        path.addArc(rect, 180, 180);
        mPaint.setAlpha(255);
        canvas.drawTextOnPath(mText, path, 0, 0, mPaint);
    }

    private void initView() {
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth(2);
        mPaint.setTextSize(mTextSize);
        mPaint.setAntiAlias(true);
    }
}
