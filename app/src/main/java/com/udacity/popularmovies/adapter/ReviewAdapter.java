package com.udacity.popularmovies.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.udacity.popularmovies.R;
import com.udacity.popularmovies.model.Review;
import com.udacity.popularmovies.utils.IntentUtils;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>{

    private Context mContext;
    private List<Review> mReviewList;

    public ReviewAdapter(Context context, List<Review> reviewList) {
        this.mContext = context;
        this.mReviewList = reviewList;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int reviewListItemId = R.layout.review_list_item;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(reviewListItemId, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ReviewViewHolder holder, int position) {
        Review currReview = mReviewList.get(position);
        String authorName = currReview.getAuthor();
        String content = currReview.getContent();
        final String url = currReview.getUrl();

        SpannableStringBuilder str = new SpannableStringBuilder(
                holder.reviewHeaderTextView.getText().toString()
        );
        str.setSpan(
                new UnderlineSpan(),
                0,
                holder.reviewHeaderTextView.getText().toString().length(),
                0);
        holder.reviewHeaderTextView.setText(str);

        holder.reviewAuthorTextView.setText(authorName);

        holder.reviewContentTextView.setText(content);

        holder.reviewOpenImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentUtils.openWebPage(mContext, url);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mReviewList.size();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder {
        final TextView reviewHeaderTextView;
        final TextView reviewAuthorTextView;
        final ImageButton reviewOpenImageBtn;
        final TextView reviewContentTextView;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            reviewHeaderTextView = itemView.findViewById(R.id.tv_review_header);
            reviewAuthorTextView = itemView.findViewById(R.id.tv_review_author);
            reviewOpenImageBtn = itemView.findViewById(R.id.ib_review_open);
            reviewContentTextView = itemView.findViewById(R.id.tv_review_content);
        }
    }
}
