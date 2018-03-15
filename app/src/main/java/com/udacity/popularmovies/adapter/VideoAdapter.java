package com.udacity.popularmovies.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.udacity.popularmovies.ListItemClickListener;
import com.udacity.popularmovies.R;
import com.udacity.popularmovies.model.Video;
import com.udacity.popularmovies.utils.ImageUtils;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder>{

    private Context mContext;
    private List<Video> mVideoList;
    private ListItemClickListener mOnClickListener;

    public VideoAdapter(Context context, List<Video> videoList) {
        this.mContext = context;
        this.mOnClickListener = (ListItemClickListener) context;
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
        Video currVideo = mVideoList.get(position);
        String videoName = currVideo.getName();
        String videoKey = currVideo.getKey();

        ImageUtils.loadVideoImage(mContext, videoKey, holder.videoPosterImageView);

        holder.videoNameTextView.setText(videoName);
    }

    @Override
    public int getItemCount() {
        return mVideoList.size();
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        final ImageView videoPosterImageView;
        final TextView videoNameTextView;

        public VideoViewHolder(View itemView) {
            super(itemView);
            videoPosterImageView = itemView.findViewById(R.id.iv_video_poster);
            videoNameTextView = itemView.findViewById(R.id.tv_video_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mOnClickListener.onListItemClick(getAdapterPosition());
        }
    }
}
