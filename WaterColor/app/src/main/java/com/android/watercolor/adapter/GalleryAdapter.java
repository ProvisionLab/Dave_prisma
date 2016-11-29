package com.android.watercolor.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.watercolor.R;
import com.android.watercolor.activity.CropActivity;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

import static com.yalantis.ucrop.UCrop.EXTRA_ASPECT_RATIO_X;
import static com.yalantis.ucrop.UCrop.EXTRA_ASPECT_RATIO_Y;
import static com.yalantis.ucrop.UCrop.EXTRA_INPUT_URI;
import static com.yalantis.ucrop.UCrop.EXTRA_OUTPUT_URI;
import static com.yalantis.ucrop.UCrop.Options.EXTRA_COMPRESSION_FORMAT_NAME;
import static com.yalantis.ucrop.UCrop.Options.EXTRA_COMPRESSION_QUALITY;

/**
 * Created by Evgeniy on 23.11.2016.
 */

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private ArrayList<String> images;
    private Context context;

    private static final String FILE_NAME = "croppedImage.jpg";

    public GalleryAdapter(ArrayList<String> images, Context context) {
        this.images = images;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Uri uri = Uri.fromFile(new File(images.get(position)));
        Glide.with(context).load(uri).override(300, 220).centerCrop().crossFade().into(holder.imageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, CropActivity.class);
                Bundle cropOptionsBundle = new Bundle();
                cropOptionsBundle.putParcelable(EXTRA_INPUT_URI, uri);
                cropOptionsBundle.putParcelable(EXTRA_OUTPUT_URI, Uri.fromFile(new File(context.getCacheDir(), FILE_NAME)));
                cropOptionsBundle.putFloat(EXTRA_ASPECT_RATIO_X, 1);
                cropOptionsBundle.putFloat(EXTRA_ASPECT_RATIO_Y, 1);

                cropOptionsBundle.putString(EXTRA_COMPRESSION_FORMAT_NAME, Bitmap.CompressFormat.JPEG.name());
                cropOptionsBundle.putInt(EXTRA_COMPRESSION_QUALITY, 100);

                intent.setClass(context, CropActivity.class);
                intent.putExtras(cropOptionsBundle);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;

        ViewHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.gallery_image);
        }
    }
}
