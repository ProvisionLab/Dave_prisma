package com.android.watercolor.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.watercolor.R;
import com.android.watercolor.adapter.FiltersAdapter;
import com.android.watercolor.model.Filter;
import com.android.watercolor.utils.FilterItemDecoration;

import java.util.ArrayList;

import static com.android.watercolor.activity.MainActivity.CAMERA_IMAGE_PATH;

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

        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            squareImageView.setImageBitmap(bitmap);
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
    }
}
