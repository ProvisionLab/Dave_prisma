package com.android.watercolor.activity;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.watercolor.R;
import com.android.watercolor.adapter.FiltersAdapter;
import com.android.watercolor.model.Filter;
import com.android.watercolor.utils.FilterItemDecoration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Calendar;

public class FilterActivity extends AppCompatActivity {

    private ImageView squareImageView;
    private RecyclerView filterListRecyclerView;
    private Uri cameraImageUri;
    private Uri imageUri;
    private ImageButton instagramShareButton;
    private ImageButton facebookShareButton;
    private ImageButton whatsappShareButton;
    private ImageButton downloadButton;

    private static final int DOWNLOAD_NOTIFICATION_ID_DONE = 911;
    private static final String TAG = FilterActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        if (getIntent().getData() != null) {
            cameraImageUri = getIntent().getData();
        }

        if (getIntent().getData() != null) {
            imageUri = getIntent().getData();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView titleTextView = (TextView) toolbar.findViewById(R.id.toolbar_title);
        titleTextView.setText(R.string.edit);

        TextView backTextView = (TextView) toolbar.findViewById(R.id.toolbar_left_text);
        backTextView.setVisibility(View.VISIBLE);
        backTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        squareImageView = (ImageView) findViewById(R.id.processed_image);

        if (cameraImageUri != null) {
            squareImageView.setImageURI(cameraImageUri);
        } else if (imageUri != null) {
            squareImageView.setImageURI(imageUri);
        }

        filterListRecyclerView = (RecyclerView) findViewById(R.id.filter_list);
        filterListRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        filterListRecyclerView.setLayoutManager(layoutManager);

        ArrayList<Filter> filters = new ArrayList<>();
        filters.add(new Filter("1"));
        filters.add(new Filter("1"));
        filters.add(new Filter("1"));
        filters.add(new Filter("1"));
        filters.add(new Filter("1"));
        filters.add(new Filter("1"));
        filters.add(new Filter("1"));
        filters.add(new Filter("1"));
        filters.add(new Filter("1"));
        filters.add(new Filter("1"));

        FiltersAdapter filtersAdapter = new FiltersAdapter(this, filters);
        filterListRecyclerView.addItemDecoration(new FilterItemDecoration(10));
        filterListRecyclerView.setAdapter(filtersAdapter);

        instagramShareButton = (ImageButton) findViewById(R.id.instagram_share);
        instagramShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        facebookShareButton = (ImageButton) findViewById(R.id.facebook_share);
        facebookShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        whatsappShareButton = (ImageButton) findViewById(R.id.whatsapp_share);
        whatsappShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        downloadButton = (ImageButton) findViewById(R.id.download);
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (imageUri != null) {
                        saveCroppedImage(imageUri);
                    } else {
                        saveCroppedImage(cameraImageUri);
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Error " + e.getMessage());
                }
            }
        });
    }

    private void saveCroppedImage(Uri croppedFileUri) throws IOException {
        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "WaterColor");
        String filename = String.format("%d_%s", Calendar.getInstance().getTimeInMillis(), croppedFileUri.getLastPathSegment());

        File saveFile = new File(directory, filename);

        FileInputStream inStream = new FileInputStream(new File(croppedFileUri.getPath()));
        FileOutputStream outStream = new FileOutputStream(saveFile);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();

        showNotification(saveFile);
    }

    private void showNotification(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(file), "image/*");

        NotificationCompat.Builder notification = new NotificationCompat.Builder(this);

        notification
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.notification_image_saved_click_to_preview))
                .setTicker(getString(R.string.notification_image_saved))
                .setOngoing(false)
                .setContentIntent(PendingIntent.getActivity(this, 0, intent, 0))
                .setAutoCancel(true);
        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).notify(DOWNLOAD_NOTIFICATION_ID_DONE, notification.build());
    }
}
