package kartsev.dmitry.ru.imageasyncload;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import it.sephiroth.android.library.picasso.Picasso;

public class MainActivity extends AppCompatActivity implements OnAsyncTaskComplete {
    ImageView imageView;
    Spinner imageChooser;
    CheckBox checkboxLoadType;
    ProgressBar loadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initSpinner();
        setSpinnerBehavior();
        setCheckboxBehavior();
    }

    private void initViews() {

        imageView = (ImageView) findViewById(R.id.imageView);
        imageChooser = (Spinner) findViewById(R.id.spinnerImgChooser);
        checkboxLoadType = (CheckBox) findViewById(R.id.checkBox);
        loadingIndicator = (ProgressBar) findViewById(R.id.progressBar);
        loadingIndicator.setVisibility(ProgressBar.INVISIBLE);
    }

    private void initSpinner() {
        ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(this, R.array.imagesVariants,
                android.R.layout.simple_spinner_item);
        imageChooser.setAdapter(adapter);
    }

    private void setSpinnerBehavior() {
        final String[] urls = getResources().getStringArray(R.array.imagesUrls);
        imageChooser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long selectedId) {
                if(checkboxLoadType.isChecked()) {
                    Picasso.with(MainActivity.this)
                            .load(urls[position])
                            .placeholder(R.drawable.loading)
                            .resize(400, 600)
                            .into(imageView);
                } else {
                    LoadImageAsync loadTask = new LoadImageAsync(MainActivity.this, MainActivity.this);
                    loadTask.execute(urls[position]);
                    loadingIndicator.setVisibility(ProgressBar.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setCheckboxBehavior() {
        checkboxLoadType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingIndicator.setVisibility(ProgressBar.INVISIBLE);
                imageChooser.setSelection(imageChooser.getSelectedItemPosition());
            }
        });
    }

    @Override
    public void onAsyncTaskComplete(Bitmap bitmap) {
        loadingIndicator.setVisibility(ProgressBar.INVISIBLE);
        imageView.setImageBitmap(bitmap);
    }
}
