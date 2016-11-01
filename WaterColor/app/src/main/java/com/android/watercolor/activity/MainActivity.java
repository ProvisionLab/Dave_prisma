package com.android.watercolor.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.android.watercolor.R;
import com.android.watercolor.widget.CameraPreview;

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

    private Camera camera;
    private CameraPreview cameraPreview;
    private FrameLayout cameraPreviewFrameLayout;
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

        camera = getCameraInstance();
        setCameraDisplayOrientation(this, currentCameraId, camera);
        cameraPreview = new CameraPreview(this, camera);
        cameraPreviewFrameLayout = (FrameLayout) findViewById(R.id.camera_preview);
        cameraPreviewFrameLayout.addView(cameraPreview);

        cameraFlashImageButton = (ImageButton) findViewById(R.id.flash_light);
        takePictureImageButton = (ImageButton) findViewById(R.id.take_photo);
        cameraSwitchImageButton = (ImageButton) findViewById(R.id.switch_camera);
        openGalleryButton = (Button) findViewById(R.id.open_gallery);

        PackageManager packageManager = getPackageManager();
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
            cameraSwitchImageButton.setEnabled(false);
        }

        cameraFlashImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        takePictureImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.takePicture(null, null, pictureCallback);
            }
        });

        cameraSwitchImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cameraPreview != null) {
                    camera.stopPreview();
                }
                camera.release();

                if (currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
                } else {
                    currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
                }
                camera = Camera.open(currentCameraId);

                try {
                    camera.setPreviewDisplay(cameraPreview.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                setCameraDisplayOrientation(MainActivity.this, currentCameraId, camera);
                camera.startPreview();
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

    public static void setCameraDisplayOrientation(Activity activity, int cameraId, Camera camera) {
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        //int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        // do something for phones running an SDK before lollipop
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate the mirror
        } else { // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }

        camera.setDisplayOrientation(result);
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
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
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
            finish();
        } else {
            return null;
        }
        return mediaFile;
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

    public Camera getCameraInstance() {
        Camera c = null;
        try {
            currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
            c = Camera.open(currentCameraId);
        } catch (Exception e) {
            Log.d(TAG, "Camera is not available");
        }
        return c;
    }
}
