package kartsev.dmitry.ru.simpleuianimation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import kartsev.dmitry.ru.simpleuianimation.views.SimpleShapeView;

public class MainActivity extends AppCompatActivity {

    private Button btnTriangle, btnCircle, btnSquare, btnRotate, btnScale, btnAlpha;
    private SimpleShapeView shape;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setButtonsBehavior();
    }

    private void initViews() {
        btnCircle = (Button) findViewById(R.id.buttonCircle);
        btnSquare = (Button) findViewById(R.id.buttonSquare);
        btnTriangle = (Button) findViewById(R.id.buttonTriangle);
        btnRotate = (Button) findViewById(R.id.buttonRotate);
        btnScale = (Button) findViewById(R.id.buttonScale);
        btnAlpha = (Button) findViewById(R.id.buttonAlpha);
        shape = (SimpleShapeView) findViewById(R.id.shapeView);
    }

    private void setButtonsBehavior() {
        final Animation animationRotateCenter = AnimationUtils.loadAnimation(
                this, R.anim.rotate);
        final Animation animationScale = AnimationUtils.loadAnimation(this, R.anim.scale);
        final Animation animationAlpha = AnimationUtils.loadAnimation(this, R.anim.alpha);

        btnSquare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shape.setShapeType(SimpleShapeView.SHAPE_SQUARE);
            }
        });

        btnTriangle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shape.setShapeType(SimpleShapeView.SHAPE_TRIANGLE);
            }
        });

        btnCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shape.setShapeType(SimpleShapeView.SHAPE_CIRCLE);
            }
        });

        btnRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shape.startAnimation(animationRotateCenter);

            }
        });

        btnScale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shape.startAnimation(animationScale);
            }
        });

        btnAlpha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shape.startAnimation(animationAlpha);
            }
        });
    }
}
