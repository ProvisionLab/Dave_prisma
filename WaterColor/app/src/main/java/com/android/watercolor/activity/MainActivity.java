package com.android.watercolor.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.android.watercolor.R;
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

    public static final String CAMERA_IMAGE_PATH = "cameraImagePath";
    public static final int PICTURE_SIZE = 1080;
    public static final int PICTURE_ROTATE = 90;

    private CameraPreview cameraPreview;
    private SquaredFrameLayout squaredFrame;
    private ImageButton takePictureImageButton;
    private ImageButton openGalleryButton;

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        squaredFrame = (SquaredFrameLayout) findViewById(R.id.camera_preview);

        takePictureImageButton = (ImageButton) findViewById(R.id.take_photo);
        openGalleryButton = (ImageButton) findViewById(R.id.open_gallery);

        takePictureImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraPreview.getCamera().takePicture(null, null, pictureCallback);
            }
        });

        openGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PickImageActivity.class);
                startActivity(intent);
            }
        });
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

                Bitmap bitmap = processImage(data, camera);

                FileOutputStream fos = new FileOutputStream(pictureFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();
            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }
        }
    };

    private Bitmap processImage(byte[] data, Camera camera) throws IOException {

        int width = camera.getParameters().getPictureSize().width;
        int height = camera.getParameters().getPictureSize().height;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);

        int croppedWidth = (width > height) ? height : width;
        int croppedHeight = (width > height) ? height : width;

        Matrix matrix = new Matrix();

        matrix.postRotate(PICTURE_ROTATE);
        Bitmap cropped = Bitmap.createBitmap(bitmap, 0, 0, croppedWidth, croppedHeight, matrix, true);
        bitmap.recycle();

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(cropped, PICTURE_SIZE, PICTURE_SIZE, true);
        cropped.recycle();

        return scaledBitmap;
    }

    private File getOutputMediaFile(int type) {
        File mediaStorageDir = getWaterColorDirectory();

        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "failed to create directory");
            return null;
        }

        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + getFilename() + ".jpg");
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
        createCameraPreview(0);
    }

    private void createCameraPreview(int cameraId) {
        cameraPreview = new CameraPreview(this, cameraId, CameraPreview.LayoutMode.FitToParent);
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
    }

    private String getFilename() {
        return new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    }

    private File getWaterColorDirectory() {
        return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "WaterColor");
    }
}
