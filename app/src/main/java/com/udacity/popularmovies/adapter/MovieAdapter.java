package com.udacity.popularmovies.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.udacity.popularmovies.R;
import com.udacity.popularmovies.model.Movie;
import com.udacity.popularmovies.utils.ImageUtils;
import com.udacity.popularmovies.utils.OfflineDataUtils;

import java.util.List;

/**
 * Class for implementing adapter for movie list
 */
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private final Context mContext;
    private final List<Movie> mMovieList;
    private final ListItemClickListener mOnClickListener;
    private final boolean mIsOnline;

    public MovieAdapter(Context context, List<Movie> moviesList, boolean isOnline) {
        this.mContext = context;
        this.mMovieList = moviesList;
        this.mIsOnline = isOnline;
        this.mOnClickListener = (ListItemClickListener) context;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int movieListItemId = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(movieListItemId, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        if (mIsOnline) {
            String posterImagePathEnding = mMovieList.get(position).getPosterImagePathEnding();
            ImageUtils.loadMovieImage(mContext, posterImagePathEnding, holder.movieImageView, true);
        } else {
            holder.movieImageView.setImageBitmap(OfflineDataUtils.convertByteArrayToImage(mMovieList.get(position).getPosterByteArray()));
        }
    }

    @Override
    public int getItemCount() {
        return mMovieList.size();
    }

    /**
     * Class for implementing view holder of adapter
     */
    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        final ImageView movieImageView;

        public MovieViewHolder(View itemView) {
            super(itemView);

            movieImageView = itemView.findViewById(R.id.iv_movie_list_item);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int index = getAdapterPosition();
            mOnClickListener.onListItemClick(index);
        }
    }

    public interface ListItemClickListener {
        void onListItemClick(int index);
    }
}
