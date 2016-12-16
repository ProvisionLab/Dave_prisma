package com.android.watercolor.activity;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.watercolor.R;
import com.android.watercolor.adapter.FiltersAdapter;
import com.android.watercolor.model.Filter;
import com.android.watercolor.utils.FilterItemDecoration;
import com.android.watercolor.utils.ItemClickListener;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Calendar;

import static com.android.watercolor.utils.Constants.SEND_IMAGE_URL;

public class FilterActivity extends AppCompatActivity implements ItemClickListener {

    private ImageView squareImageView;
    private RecyclerView filterListRecyclerView;
    private Uri imageUri;
    private byte[] imageByteArray;
    private String filteredImage;
    private ImageButton instagramShareButton;
    private ImageButton facebookShareButton;
    private ImageButton whatsappShareButton;
    private ImageButton downloadButton;
    private RelativeLayout progressLayout;

    private static final int SAVE_IMAGE = 111;
    private static final String TAG = FilterActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

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
        progressLayout = (RelativeLayout) findViewById(R.id.progress_layout);

        if (imageUri != null) {
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
        filtersAdapter.setClickListener(this);

        instagramShareButton = (ImageButton) findViewById(R.id.instagram_share);
        instagramShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getPackageManager().getLaunchIntentForPackage("com.instagram.android");
                if (intent != null) {
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.setPackage("com.instagram.android");
                    shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                    shareIntent.setType("image/*");
                    startActivity(shareIntent);
                } else {
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setData(Uri.parse("market://details?id=" + "com.instagram.android"));
                    startActivity(intent);
                }
            }
        });

        facebookShareButton = (ImageButton) findViewById(R.id.facebook_share);
        facebookShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FacebookSdk.sdkInitialize(getApplicationContext());
                ShareDialog shareDialog = new ShareDialog(FilterActivity.this);
                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    ShareLinkContent content = new ShareLinkContent.Builder()
                            .setContentTitle("WaterColor")
                            .setContentDescription("WaterColor picture")
                            .setImageUrl(imageUri)
                            .setContentUrl(Uri.parse("WaterColor site"))
                            .build();

                    shareDialog.show(content);
                }
            }
        });

        whatsappShareButton = (ImageButton) findViewById(R.id.whatsapp_share);
        whatsappShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                sendIntent.setPackage("com.whatsapp");
                sendIntent.setType("image/*");
                startActivity(sendIntent);
            }
        });

        downloadButton = (ImageButton) findViewById(R.id.download);
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Log.d(TAG, "Cropped " + imageUri);
                    if (imageUri != null) {
                        saveCroppedImage(imageUri);
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Error " + e.getMessage());
                }
            }
        });
    }

    private void saveCroppedImage(Uri croppedFileUri) throws IOException {
        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), getString(R.string.app_name));
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
                .setSmallIcon(R.drawable.download)
                .setContentIntent(PendingIntent.getActivity(this, 0, intent, 0))
                .setAutoCancel(true);
        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).notify(SAVE_IMAGE, notification.build());
    }

    @Override
    public void onClick(View view, int position) {
        progressLayout.setVisibility(View.VISIBLE);
        new SendImageAsyncTask().execute();
    }

    private String sendImageRequest() {
        String attachmentName = "image";
        String crlf = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        try {
            HttpURLConnection httpUrlConnection = null;
            URL url = new URL(SEND_IMAGE_URL);
            httpUrlConnection = (HttpURLConnection) url.openConnection();
            httpUrlConnection.setUseCaches(false);
            httpUrlConnection.setDoOutput(true);
            httpUrlConnection.setRequestMethod("POST");
            httpUrlConnection.setRequestProperty("Connection", "Keep-Alive");
            httpUrlConnection.setRequestProperty("Cache-Control", "no-cache");
            httpUrlConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            httpUrlConnection.setConnectTimeout(2000);

            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            imageByteArray = baos.toByteArray();

            DataOutputStream request = new DataOutputStream(httpUrlConnection.getOutputStream());
            request.writeBytes(twoHyphens + boundary + crlf);
            request.writeBytes("Content-Disposition: form-data; name=\"" + attachmentName + "\"" + crlf);
            request.writeBytes("Content-Type: image/jpeg" + crlf);
            request.writeBytes(crlf);
            request.write(imageByteArray);
            request.writeBytes(crlf);
            request.writeBytes(twoHyphens + boundary + crlf);
            request.writeBytes("Content-Disposition: form-data; name=\"testingName\"" + crlf);
            request.writeBytes(crlf);
            request.writeBytes(twoHyphens + boundary + twoHyphens);
            request.flush();
            request.close();

            InputStream responseStream = new BufferedInputStream(httpUrlConnection.getInputStream());
            BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(responseStream));
            String line = "";
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = responseStreamReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            responseStreamReader.close();
            String response = stringBuilder.toString();
            Log.d(TAG, "Response " + response);
            return response;
        } catch (IOException e) {
            Log.d(TAG, "Error " + e.toString());
            return null;
        }
    }

    private class SendImageAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            filteredImage = sendImageRequest();
            return filteredImage;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressLayout.setVisibility(View.GONE);
        }
    }
}
