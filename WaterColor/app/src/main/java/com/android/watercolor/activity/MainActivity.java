package com.android.watercolor.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.android.watercolor.R;
import com.android.watercolor.utils.SharedPreferencesStub;
import com.android.watercolor.widget.CameraPreview;
import com.android.watercolor.widget.SquaredFrameLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

public class MainActivity extends AppCompatActivity {

    public static final int SELECT_IMAGE_CODE = 1111;
    public static final String SELECTED_IMAGE_PATH = "selectedImagePath";
    public static final String CAMERA_IMAGE_PATH = "cameraImagePath";
    public static final String CAMERA_ID = "cameraId";

    private CameraPreview cameraPreview;
    private SquaredFrameLayout squaredFrame;
    private ImageButton cameraFlashImageButton;
    private ImageButton takePictureImageButton;
    private ImageButton cameraSwitchImageButton;
    private Button openGalleryButton;
    private int currentCameraId;

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        squaredFrame = (SquaredFrameLayout) findViewById(R.id.camera_preview);

        cameraFlashImageButton = (ImageButton) findViewById(R.id.flash_light);
        takePictureImageButton = (ImageButton) findViewById(R.id.take_photo);
        cameraSwitchImageButton = (ImageButton) findViewById(R.id.switch_camera);
        openGalleryButton = (Button) findViewById(R.id.open_gallery);

        PackageManager packageManager = getPackageManager();
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
            cameraSwitchImageButton.setEnabled(false);
        }

        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            cameraFlashImageButton.setEnabled(false);
        }

        cameraFlashImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        takePictureImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraPreview.getCamera().takePicture(null, null, pictureCallback);
            }
        });

        cameraSwitchImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraPreview.stop();
                squaredFrame.removeView(cameraPreview);

                currentCameraId = cameraPreview.getCameraId();

                if (currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
                } else {
                    currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
                }

                createCameraPreview(currentCameraId);
            }
        });

        openGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, SELECT_IMAGE_CODE);
            }
        });
    }

    private void createCameraPreview(int cameraId) {
        cameraPreview = new CameraPreview(this, cameraId, CameraPreview.LayoutMode.FitToParent);
        RelativeLayout.LayoutParams previewLayoutParams =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

        squaredFrame.addView(cameraPreview, 0, previewLayoutParams);
    }

    private Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (pictureFile == null) {
                Log.d(TAG, "Error creating media file, check storage permissions");
                return;
            }

            try {

                Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);

                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                Bitmap rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);

                FileOutputStream fos = new FileOutputStream(pictureFile);
                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();
            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }
        }
    };

    private File getOutputMediaFile(int type) {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "WaterColor");
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "failed to create directory");
            return null;
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + timeStamp + ".jpg");
            Intent intent = new Intent(this, FilterActivity.class);
            intent.putExtra(CAMERA_IMAGE_PATH, mediaFile.getPath());
            startActivity(intent);
        } else {
            return null;
        }
        return mediaFile;
    }

    @Override
    protected void onResume() {
        super.onResume();

        cameraPreview = new CameraPreview(this, SharedPreferencesStub.getData(this, CAMERA_ID, 0), CameraPreview.LayoutMode.FitToParent);
        RelativeLayout.LayoutParams previewLayoutParams =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

        squaredFrame.addView(cameraPreview, 0, previewLayoutParams);
    }

    @Override
    protected void onPause() {
        super.onPause();

        super.onPause();
        cameraPreview.stop();
        squaredFrame.removeView(cameraPreview);
        cameraPreview = null;
        SharedPreferencesStub.saveData(this, CAMERA_ID, currentCameraId);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_IMAGE_CODE && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            Log.d(TAG, imageUri.toString());
            Intent intent = new Intent(this, CropActivity.class);
            intent.putExtra(SELECTED_IMAGE_PATH, imageUri);
            startActivity(intent);
        }
    }
}
