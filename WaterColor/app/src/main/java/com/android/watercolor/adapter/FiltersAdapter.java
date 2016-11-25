package com.android.watercolor.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.watercolor.R;
import com.android.watercolor.model.Filter;

import java.util.ArrayList;

/**
 * Created by Evgeniy on 25.11.2016.
 */

public class FiltersAdapter extends RecyclerView.Adapter<FiltersAdapter.ViewHolder> {

    private ArrayList<Filter> filters;
    private Context context;
    private int lastCheckedPosition = -1;

    public FiltersAdapter(Context context, ArrayList<Filter> filters) {
        this.filters = filters;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.filter_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.filterImageView.setSelected(position == lastCheckedPosition);
        holder.checkImageView.setVisibility(position == lastCheckedPosition ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return filters.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView filterImageView;
        private ImageView checkImageView;

        ViewHolder(View itemView) {
            super(itemView);

            filterImageView = (ImageView) itemView.findViewById(R.id.filter_image);
            checkImageView = (ImageView) itemView.findViewById(R.id.check);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lastCheckedPosition = getAdapterPosition();
                    notifyItemRangeChanged(0, filters.size());
                }
            });
        }
    }
}
