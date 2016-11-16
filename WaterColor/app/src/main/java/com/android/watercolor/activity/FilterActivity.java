package com.android.watercolor.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.android.watercolor.R;

import static com.android.watercolor.activity.MainActivity.CAMERA_IMAGE_PATH;
import static com.android.watercolor.activity.MainActivity.IMAGE_URI;

public class FilterActivity extends AppCompatActivity {

    private ImageView squareImageView;
    private RecyclerView filterListRecyclerView;
    private String imagePath;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        if (getIntent().getExtras() != null) {
            imagePath = getIntent().getExtras().getString(CAMERA_IMAGE_PATH);
            imageUri = (Uri) getIntent().getExtras().get(IMAGE_URI);
        }

        squareImageView = (ImageView) findViewById(R.id.image);
        filterListRecyclerView = (RecyclerView) findViewById(R.id.filter_list);

        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            squareImageView.setImageBitmap(bitmap);
        } else if (imageUri != null) {
            squareImageView.setImageURI(imageUri);
        }
    }
}
