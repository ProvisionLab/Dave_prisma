package com.android.watercolor.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import com.android.watercolor.R;
import com.theartofdev.edmodo.cropper.CropImageView;

import static com.android.watercolor.activity.MainActivity.SELECTED_IMAGE_PATH;

public class CropActivity extends AppCompatActivity {

    private CropImageView cropImageView;
    private ImageButton rotateImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);

        Uri imageUri = (Uri) getIntent().getExtras().get(SELECTED_IMAGE_PATH);

        cropImageView = (CropImageView) findViewById(R.id.cropImageView);
        cropImageView.setImageUriAsync(imageUri);

        rotateImageButton = (ImageButton) findViewById(R.id.rotate_photo);

        rotateImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropImageView.rotateImage(90);
            }
        });
    }
}
