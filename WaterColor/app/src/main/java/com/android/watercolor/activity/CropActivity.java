package com.android.watercolor.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.watercolor.R;
import com.yalantis.ucrop.view.UCropView;

public class CropActivity extends AppCompatActivity {

    public static final String URI = "imageUri";
    private static final String TAG = CropActivity.class.getSimpleName();

    private Uri uri;
    private UCropView cropView;
    private ImageButton rotateClockwiseImageButton;
    private ImageButton rotateCounterClockwiseImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView titleTextView = (TextView) toolbar.findViewById(R.id.toolbar_title);
        titleTextView.setText(R.string.crop);

        TextView backTextView = (TextView) toolbar.findViewById(R.id.toolbar_left_text);
        backTextView.setVisibility(View.VISIBLE);
        backTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (getIntent().getExtras() != null) {
            uri = (Uri) getIntent().getExtras().get(URI);
            Log.d(TAG, "Uri " + uri);
        }

        cropView = (UCropView) findViewById(R.id.ucropView);

        rotateClockwiseImageButton = (ImageButton) findViewById(R.id.rotate_clock);
        rotateCounterClockwiseImageButton = (ImageButton) findViewById(R.id.rotate_counter_clock);

        rotateClockwiseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        rotateCounterClockwiseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);

        MenuItem item = menu.findItem(R.id.action_next).setVisible(true);
        item.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CropActivity.this, FilterActivity.class);
                startActivity(intent);
            }
        });

        return true;
    }
}
