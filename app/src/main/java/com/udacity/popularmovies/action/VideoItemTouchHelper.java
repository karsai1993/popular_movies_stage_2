package com.udacity.popularmovies.action;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.popularmovies.R;
import com.udacity.popularmovies.adapter.VideoAdapter;
import com.udacity.popularmovies.model.Video;
import com.udacity.popularmovies.utils.IntentUtils;

import java.util.List;

/**
 * https://medium.com/@kitek/recyclerview-swipe-to-delete-easier-than-you-thought-cff67ff5e5f6
 */
public class VideoItemTouchHelper implements LoaderManager.LoaderCallbacks<List<Video>>{

    private final String VIDEO_URL_BASE = "https://www.youtube.com/watch?v=";
    private final String RIGHT_SIDE = "right";
    private final String LEFT_SIDE = "left";

    private Context mContext;
    private List<Video> mVideoList;
    private VideoAdapter mVideoAdapter;
    private RecyclerView mRecyclerView;
    private LoaderManager mLoaderManager;
    private final float ALPHA_MAX = 1.0F;
    private final int LOADER_ID = 0;
    private View mActionView;
    private Drawable mActionDrawable;
    private LayoutInflater mLayoutInflater;

    public VideoItemTouchHelper (
            Context context,
            List<Video> videoList,
            VideoAdapter videoAdapter,
            RecyclerView recyclerView,
            LoaderManager loaderManager) {
        this.mContext = context;
        this.mVideoList = videoList;
        this.mVideoAdapter = videoAdapter;
        this.mRecyclerView = recyclerView;
        this.mLoaderManager = loaderManager;
    }

    public void applySwipeAction(String side, View parentView, Canvas canvas) {

        int bottomPadding = parentView.getPaddingBottom();
        int topPadding = parentView.getPaddingTop();
        int left = parentView.getLeft();
        int right = parentView.getRight();
        int top = parentView.getTop();
        int bottom = parentView.getBottom();

        mLayoutInflater = LayoutInflater.from(mContext);

        if (side.equals(RIGHT_SIDE)) {
            mActionView = mLayoutInflater.inflate(
                    R.layout.right_swipe,
                    null,
                    false);
        } else if (side.equals(LEFT_SIDE)) {
            mActionView = mLayoutInflater.inflate(
                    R.layout.left_swipe,
                    null,
                    false);
        }
        mActionDrawable = getBitmapFromView(mActionView, parentView);
        mActionDrawable.setBounds(
                left,
                top + topPadding,
                right,
                bottom - bottomPadding);
        mActionDrawable.draw(canvas);
    }

    public ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
            new ItemTouchHelper.SimpleCallback(
                    0,
                    ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

        @Override
        public boolean onMove(
                RecyclerView recyclerView,
                RecyclerView.ViewHolder viewHolder,
                RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            String videoKey = mVideoList.get(position).getKey();
            if (direction == ItemTouchHelper.LEFT) {
                IntentUtils.shareUrl(mContext, VIDEO_URL_BASE + videoKey);
            } else if (direction == ItemTouchHelper.RIGHT) {
                IntentUtils.openWebPage(mContext, VIDEO_URL_BASE + videoKey);
            }
            mLoaderManager.restartLoader(LOADER_ID,null,VideoItemTouchHelper.this);
            mRecyclerView.scrollToPosition(position);
        }

        @Override
        public void onChildDraw(
                Canvas canvas,
                RecyclerView recyclerView,
                RecyclerView.ViewHolder viewHolder,
                float dX,
                float dY,
                int actionState,
                boolean isCurrentlyActive) {

            View view = viewHolder.itemView;
            TextView textView = view.findViewById(R.id.tv_video_name);

            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                float alphaOriginalView = ALPHA_MAX - Math.abs(dX) / (float) view.getWidth();
                view.setAlpha(alphaOriginalView);
                view.setTranslationX(dX);
                if (dX > 0) {
                    applySwipeAction(RIGHT_SIDE, view, canvas);
                    textView.setBackgroundColor(Color.WHITE);
                } else if (dX < 0) {
                    applySwipeAction(LEFT_SIDE, view, canvas);
                    textView.setBackgroundColor(Color.WHITE);
                } else {
                    textView.setBackgroundColor(Color.TRANSPARENT);
                }
            } else {
                super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }
    });

    /**
     * https://stackoverflow.com/questions/2339429/android-view-getdrawingcache-returns-null-only-null/4618030#4618030
     * @param view
     * @return
     */
    private BitmapDrawable getBitmapFromView(View view, View parentView) {
        view.setDrawingCacheEnabled(true);

        view.measure(
                View.MeasureSpec.makeMeasureSpec(parentView.getWidth(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(parentView.getHeight(), View.MeasureSpec.EXACTLY)
        );
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        view.buildDrawingCache(true);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        return new BitmapDrawable(mContext.getResources(), bitmap);
    }

    @Override
    public Loader<List<Video>> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<List<Video>>(mContext) {

            @Override
            protected void onStartLoading() {
                if (mVideoList != null) {
                    deliverResult(mVideoList);
                } else {
                    forceLoad();
                }
            }

            @Nullable
            @Override
            public List<Video> loadInBackground() {
                return mVideoList;
            }

            public void deliverResult(List<Video> videoList) {
                mVideoList = videoList;
                super.deliverResult(videoList);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<Video>> loader, List<Video> data) {
        mVideoAdapter = new VideoAdapter(
                mContext,
                data
        );
        mRecyclerView.setAdapter(mVideoAdapter);
    }

    @Override
    public void onLoaderReset(Loader<List<Video>> loader) {
        mVideoAdapter = new VideoAdapter(
                mContext,
                mVideoList
        );
        mRecyclerView.setAdapter(mVideoAdapter);
    }
}
