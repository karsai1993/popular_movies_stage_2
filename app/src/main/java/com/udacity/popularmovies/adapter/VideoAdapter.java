package com.udacity.popularmovies.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.udacity.popularmovies.CommonApplicationConstants;
import com.udacity.popularmovies.R;
import com.udacity.popularmovies.model.Video;
import com.udacity.popularmovies.utils.IntentUtils;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder>{

    private Context mContext;
    private List<Video> mVideoList;
    private boolean mIsSequenceNumberNecessary;

    private final String VIDEO_URL_BASE = "https://www.youtube.com/watch?v=";

    public VideoAdapter(Context context, List<Video> videoList, boolean isSequenceNumberNecessary) {
        this.mContext = context;
        this.mVideoList = videoList;
        this.mIsSequenceNumberNecessary = isSequenceNumberNecessary;
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
        String videoSequence = String.valueOf((position + 1));
        String videoName = mVideoList.get(position).getName();
        final String videoKey = mVideoList.get(position).getKey();

        if (mIsSequenceNumberNecessary) {
            holder.videoSequenceTextView
                    .setText(videoSequence + CommonApplicationConstants.DOT_CHARACTER);
        } else {
            holder.videoSequenceTextView.setVisibility(View.GONE);
        }

        holder.videoNameTextView.setText(videoName);

        holder.videoShareImageBtn.setOnClickListener(new View.OnClickListener() {
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
        });
    }

    @Override
    public int getItemCount() {
        return mVideoList.size();
    }

    class VideoViewHolder extends RecyclerView.ViewHolder {
        final TextView videoSequenceTextView;
        final TextView videoNameTextView;
        final ImageButton videoPlayImageBtn;
        final ImageButton videoShareImageBtn;

        public VideoViewHolder(View itemView) {
            super(itemView);
            videoSequenceTextView = itemView.findViewById(R.id.tv_video_sequence);
            videoNameTextView = itemView.findViewById(R.id.tv_video_name);
            videoPlayImageBtn = itemView.findViewById(R.id.ib_video_play);
            videoShareImageBtn = itemView.findViewById(R.id.ib_video_share);
        }
    }
}
