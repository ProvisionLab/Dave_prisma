package com.android.watercolor.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.android.watercolor.R;

import static com.android.watercolor.activity.MainActivity.CAMERA_IMAGE_PATH;

public class FilterActivity extends AppCompatActivity {

    private ImageView squareImageView;
    private RecyclerView filterListRecyclerView;
    private String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        if (getIntent().getExtras() != null) {
            imagePath = getIntent().getExtras().getString(CAMERA_IMAGE_PATH);
        }

        squareImageView = (ImageView) findViewById(R.id.image);
        filterListRecyclerView = (RecyclerView) findViewById(R.id.filter_list);

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        squareImageView.setImageBitmap(bitmap);
    }
}
