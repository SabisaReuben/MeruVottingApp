package com.example.mustapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerLineDivider extends RecyclerView.ItemDecoration {

    private Drawable mDrawable;
    public RecyclerLineDivider(Context context) {
        //mDrawable=context.getResources().getDrawable(R.drawable.recycler_dividor);
        mDrawable= ContextCompat.getDrawable(context, R.drawable.recycler_dividor);

    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() -parent.getPaddingRight();
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params  = (RecyclerView.LayoutParams)
                    child.getLayoutParams();
            int top = child.getBottom()+params.bottomMargin;
            int bottom = top + mDrawable.getIntrinsicHeight();
            mDrawable.setBounds(left,top,right,bottom);
            mDrawable.draw(c);

        }

    }
}
