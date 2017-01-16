package kartsev.dmitry.ru.clickbuttonui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by Jag on 13.01.2017.
 */

public class ClickCountButton extends Button {
    private int mClickCount = 0;

    public ClickCountButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ClickCountButton);

        int clickCount = a.getInt(R.styleable.ClickCountButton_clickCount, 0);

        mClickCount = clickCount;
        a.recycle();
    }

    public ClickCountButton(Context context) {
        super(context);
    }

    public int getClickCount() {
        return mClickCount;
    }

    public void resetClickCount() {
        mClickCount = 0;
    }

    public boolean performClick() {
        mClickCount++;
        return super.performClick();
    }
}
