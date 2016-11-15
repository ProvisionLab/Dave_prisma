package com.android.watercolor.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;

import com.android.watercolor.R;
import com.yalantis.ucrop.view.UCropView;

public class CropActivity extends AppCompatActivity {

    private UCropView cropImageView;
    private ImageButton rotateImageButton;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
    }
}
