package com.raiyanshahid.letstalk;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

//This class is used in recyclerview for overLaping the top to another recycler view, which is used in Dashboard
public class OverlapDecoration extends RecyclerView.ItemDecoration {

    private final static int vertOverlap = -100;

    @Override
    public void getItemOffsets (Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        final int itemPosition = parent.getChildAdapterPosition(view);
        if (itemPosition == 0) {
            return; }
        outRect.set(0, vertOverlap, 0, 0);
    }
}
