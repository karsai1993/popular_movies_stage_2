package com.udacity.popularmovies.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.udacity.popularmovies.CommonApplicationConstants;
import com.udacity.popularmovies.R;
import com.udacity.popularmovies.model.Video;
import com.udacity.popularmovies.utils.ImageUtils;
import com.udacity.popularmovies.utils.IntentUtils;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder>{

    private Context mContext;
    private List<Video> mVideoList;

    //private final String VIDEO_URL_BASE = "https://www.youtube.com/watch?v=";

    public VideoAdapter(Context context, List<Video> videoList) {
        this.mContext = context;
        this.mVideoList = videoList;
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int videoListItemId = R.layout.video_list_item;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(videoListItemId, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {
        String videoName = mVideoList.get(position).getName();
        String videoKey = mVideoList.get(position).getKey();

        ImageUtils.loadVideoImage(mContext, videoKey, holder.videoPosterImageView);

        holder.videoNameTextView.setText(videoName);

        /*holder.videoShareImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentUtils.shareUrl(mContext,VIDEO_URL_BASE + videoKey);
            }
        });

        holder.videoPlayImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentUtils.openWebPage(mContext, VIDEO_URL_BASE + videoKey);
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return mVideoList.size();
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder{
        final ImageView videoPosterImageView;
        final TextView videoNameTextView;
        //final ImageButton videoPlayImageBtn;
        //final ImageButton videoShareImageBtn;
        private boolean mIsClicked;
        private ScaleGestureDetector mDetector;
        private final String DEBUG_TAG = "VideoAdapter";

        public VideoViewHolder(View itemView) {
            super(itemView);
            videoPosterImageView = itemView.findViewById(R.id.iv_video_poster);
            videoNameTextView = itemView.findViewById(R.id.tv_video_name);
            //videoPlayImageBtn = itemView.findViewById(R.id.ib_video_play);
            //videoShareImageBtn = itemView.findViewById(R.id.ib_video_share);
        }
    }
}
