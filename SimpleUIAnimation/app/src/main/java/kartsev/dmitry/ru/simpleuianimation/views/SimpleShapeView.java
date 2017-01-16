package kartsev.dmitry.ru.simpleuianimation.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

import kartsev.dmitry.ru.simpleuianimation.R;

/**
 * Created by Jag on 15.01.2017.
 */

public class SimpleShapeView extends View {
    public static final String SHAPE_TRIANGLE = "triangle";
    public static final String SHAPE_CIRCLE = "circle";
    public static final String SHAPE_SQUARE = "square";
    private Paint mPaint = null;
    private String shapeType = null;
    private int shapeSize = 0;
    private String shapeAnimation = null;
    private int backgroundColor = 0;

    public SimpleShapeView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SimpleShapeView);
        try {
            this.shapeType = a.getString(R.styleable.SimpleShapeView_shape_type);
            this.shapeSize = a.getInt(R.styleable.SimpleShapeView_shape_size, 0);
            if (this.shapeSize > this.getHeight()) {
                this.shapeSize /= 1.5;
            }
            this.shapeAnimation = a.getString(R.styleable.SimpleShapeView_shape_animation);

            mPaint = new Paint();
            mPaint.setColor(a.getColor(R.styleable.SimpleShapeView_shape_color, 0));
            mPaint.setAntiAlias(true);
            backgroundColor = a.getColor(R.styleable.SimpleShapeView_shape_background_color, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int x = this.getWidth() / 2;
        int y = this.getHeight() / 2;

        if ((!shapeType.isEmpty()) && (backgroundColor != 0)) {
            canvas.drawColor(backgroundColor);

            switch (shapeType) {
                case SHAPE_CIRCLE:
                    canvas.drawCircle(x, y, shapeSize / 2, mPaint);
                    break;
                case SHAPE_TRIANGLE:
                    drawTriangle(x - (shapeSize / 2), y + (shapeSize / 2), shapeSize,
                            shapeSize, false, mPaint, canvas);
                    break;
                case SHAPE_SQUARE:
                    canvas.drawRect(x - (shapeSize / 2), y - (shapeSize / 2), x + (shapeSize / 2),
                            y + (shapeSize / 2), mPaint);
                    break;
            }
        }
    }

    private void drawTriangle(int x, int y, int width, int height, boolean inverted, Paint paint, Canvas canvas) {

        Point p1 = new Point(x, y);
        int pointX = x + width / 2;
        int pointY = inverted ? y + height : y - height;

        Point p2 = new Point(pointX, pointY);
        Point p3 = new Point(x + width, y);


        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(p1.x, p1.y);
        path.lineTo(p2.x, p2.y);
        path.lineTo(p3.x, p3.y);
        path.close();

        canvas.drawPath(path, paint);
    }

    public boolean setShapeType(String type) {
        type = type.toLowerCase();
        if ((type.equals(SHAPE_CIRCLE)) || (type.equals(SHAPE_SQUARE)) || (type.equals(SHAPE_TRIANGLE))) {
            this.shapeType = type;
            this.invalidate();
            return true;
        }

        return false;
    }
}
