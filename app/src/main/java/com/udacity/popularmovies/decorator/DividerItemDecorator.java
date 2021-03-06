package com.udacity.popularmovies.decorator;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Class for creating a decorator element for video and review recyclerviews.
 * The key responsibilities of this decorator are the followings:
 * - creating divider element between two items
 * - avoiding placing the divider after the last item
 * The main source of the code implementation:
 * https://stackoverflow.com/questions/46215810/recyclerview-remove-divider-decorator-after-the-last-item
 */

public class DividerItemDecorator extends RecyclerView.ItemDecoration {
    private Drawable mDivider;


    public DividerItemDecorator (Drawable divider) {
        this.mDivider = divider;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int dividerLeft = parent.getPaddingLeft();
        int dividerRight = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();
        for (int i = 0; i <= childCount - 2; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int dividerTop = child.getBottom() + params.bottomMargin;
            int dividerBottom = dividerTop + mDivider.getIntrinsicHeight();

            mDivider.setBounds(dividerLeft, dividerTop, dividerRight, dividerBottom);
            mDivider.draw(c);
        }
    }
}
