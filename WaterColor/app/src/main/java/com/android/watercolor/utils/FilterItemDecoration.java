package com.android.watercolor.utils;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Evgeniy on 25.11.2016.
 */

public class FilterItemDecoration extends RecyclerView.ItemDecoration {

    private int itemOffset;

    public FilterItemDecoration(int itemOffset) {
        this.itemOffset = itemOffset;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        outRect.set(0, 0, itemOffset, 0);
    }
}
