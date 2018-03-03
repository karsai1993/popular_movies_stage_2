package com.udacity.popularmovies.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.udacity.popularmovies.AsyncTaskPhaseListener;
import com.udacity.popularmovies.CommonApplicationConstants;
import com.udacity.popularmovies.MovieDataProcessorAsyncTask;
import com.udacity.popularmovies.R;
import com.udacity.popularmovies.adapter.ReviewAdapter;
import com.udacity.popularmovies.adapter.VideoAdapter;
import com.udacity.popularmovies.model.Movie;
import com.udacity.popularmovies.model.Review;
import com.udacity.popularmovies.model.Video;
import com.udacity.popularmovies.utils.ImageUtils;
import com.udacity.popularmovies.utils.JsonUtils;
import com.udacity.popularmovies.utils.NetworkUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Class for implementing detail view of each movie
 */
public class DetailActivity extends AppCompatActivity {

    private Unbinder mUnbinder;
    private String mId;
    private String mTitle;
    private String mOriginalTitle;
    private String mBackgroundImagePathEnding;
    private String mPosterImagePathEnding;
    private String mOverview;
    private String mAverageVote;
    private String mReleaseDate;
    private List<Review> mReviewList;
    private List<Video> mVideoList;
    private int mAsyncTaskProcessCounter;
    private Toast mToast;

    private final String DATA_NOT_AVAILABLE = "DNA";
    private final String VIDEO_LIST_LABEL_SINGLE = "Trailer:";
    private final String REVIEW_LIST_LABEL_SINGLE = "Review:";
    private final String VIDEO_LIST_LABEL_PLURAL_BASE = "Trailers (";
    private final String REVIEW_LIST_LABEL_PLURAL_BASE = "Reviews (";
    private final String LIST_LABEL_PLURAL_ENDING = "):";
    private final String RATING_MAX_COMPARATOR_TEXT = " / 10";
    private final String MESSAGE_FOR_ADDING_TO_FAVOURITE_LIST = " is added to list of favourites";
    private final String MESSAGE_FOR_REMOVING_FROM_FAVOURITE_LIST = " is removed from list of favourites";

    /**
     * butterknife is a third party library which is used here to binding the ids to fields easier
     * reference: http://jakewharton.github.io/butterknife/
     */
    @BindView(R.id.sv_detail_activity)
    ScrollView movieDetailScrollView;
    @BindView(R.id.tv_detail_request_fetch_error)
    TextView fetchErrorTextView;
    @BindView(R.id.tv_detail_request_network_error)
    TextView networkErrorTextView;
    @BindView(R.id.pb_detail_request_loading)
    ProgressBar loadingProgressBar;

    @BindView(R.id.iv_detail_activity_backdrop)
    ImageView backdropImageView;
    @BindView(R.id.iv_detail_activity_poster)
    ImageView posterImageView;
    @BindView(R.id.tv_title)
    TextView titleTextView;
    @BindView(R.id.tv_original_title)
    TextView originalTitleTextView;
    @BindView(R.id.tv_original_title_label)
    TextView originalTitleLabelTextView;
    @BindView(R.id.tv_average_vote)
    TextView averageVoteTextView;
    @BindView(R.id.tv_release_date)
    TextView releaseDateTextView;
    @BindView(R.id.tv_overview)
    TextView overviewTextView;
    @BindView(R.id.tv_video_label)
    TextView videoListLabelTextView;
    @BindView(R.id.rv_videos)
    RecyclerView videoListRecyclerView;
    @BindView(R.id.tv_review_label)
    TextView reviewListLabelTextView;
    @BindView(R.id.rv_reviews)
    RecyclerView reviewListRecyclerView;
    @BindView(R.id.ib_favourite)
    ImageButton favouriteImageButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mUnbinder = ButterKnife.bind(this);
        receiveIntentValues();
        getMovieRelatedAdditionalData();
        populateUI();
    }

    /**
     * Function to get the review and video information
     */
    private void getMovieRelatedAdditionalData() {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            showNetworkError();
        } else {
            showMovieDetails();
            mAsyncTaskProcessCounter = 0;
            new MovieDataProcessorAsyncTask(new MovieDetailsProcessorAsyncTaskPhaseListenerImpl())
                    .execute(CommonApplicationConstants.REQUEST_TYPE_REVIEW);
            new MovieDataProcessorAsyncTask(new MovieDetailsProcessorAsyncTaskPhaseListenerImpl())
                    .execute(CommonApplicationConstants.REQUEST_TYPE_VIDEO);
        }
    }

    /**
     * Function to get the intent values from parent class, and to store them in appropriate fields
     */
    private void receiveIntentValues() {
        Bundle data = getIntent().getExtras();
        Movie selectedMovie = data.getParcelable(CommonApplicationConstants.MOVIE_DATA_KEY);
        mId = selectedMovie.getId();
        mBackgroundImagePathEnding = selectedMovie.getBackdropImagePathEnding();
        mPosterImagePathEnding = selectedMovie.getPosterImagePathEnding();
        mTitle = selectedMovie.getTitle();
        mOriginalTitle = selectedMovie.getOriginalTitle();
        mOverview = selectedMovie.getOverview();
        mAverageVote = selectedMovie.getAverageVote();
        mReleaseDate = selectedMovie.getReleaseDate();
    }

    /**
     * Function to set the values of all views in the layout
     * NOTE: Original title is only shown when it does not match with the title.
     * It usually occurs when the original title of the certain movie does not have English
     * original title.
     */
    private void populateUI() {
        ImageUtils.loadImage(this, mPosterImagePathEnding, posterImageView);
        ImageUtils.loadImage(this, mBackgroundImagePathEnding, backdropImageView);
        textViewValueHandler(titleTextView, mTitle);
        textViewValueHandler(averageVoteTextView, mAverageVote);
        averageVoteTextView.append(RATING_MAX_COMPARATOR_TEXT);
        textViewValueHandler(overviewTextView, mOverview);
        releaseDateHandler();
        originalTitleHandler();
        favouriteButtonHandler(this, mTitle, mId);
        initRecyclerView(this, videoListRecyclerView);
        initRecyclerView(this, reviewListRecyclerView);
    }

    private void favouriteButtonHandler(final Context context, final String title, final String id) {
        if (isAlreadyMarkedAsFavourite(id)) {
            favouriteImageButton.setBackground(
                    getResources().getDrawable(R.drawable.image_button_shape_red));
        } else {
            favouriteImageButton.setBackground(
                    getResources().getDrawable(R.drawable.image_button_shape_green));
        }
        favouriteImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isAlreadyMarkedAsFavourite(id)) {
                    favouriteImageButton.setBackground(
                            getResources().getDrawable(R.drawable.image_button_shape_green));
                    showToastMessage(context, mToast, title + MESSAGE_FOR_REMOVING_FROM_FAVOURITE_LIST);
                } else {
                    favouriteImageButton.setBackground(
                            getResources().getDrawable(R.drawable.image_button_shape_red));
                    showToastMessage(context, mToast, title + MESSAGE_FOR_ADDING_TO_FAVOURITE_LIST);
                }
            }
        });
    }

    private void showToastMessage(Context context, Toast toast, String text) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(
                context,
                text,
                Toast.LENGTH_LONG);
        toast.show();
    }

    private boolean isAlreadyMarkedAsFavourite(String id) {
        return false;
    }

    /**
     * Function to handle text view values based on the corresponding field values
     * @param textView
     * @param value
     */
    private void textViewValueHandler(TextView textView, String value) {
        if (value.isEmpty()) {
            textView.setText(DATA_NOT_AVAILABLE);
        } else {
            textView.setText(value);
        }
    }

    /**
     * Function to initialize the recycler view
     * @param context
     * @param recyclerView
     */
    private void initRecyclerView(Context context, RecyclerView recyclerView) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
    }

    /**
     * Function to handle original title text view
     */
    private void originalTitleHandler(){
        if (mTitle.equals(mOriginalTitle)) {
            originalTitleLabelTextView.setVisibility(View.GONE);
            originalTitleTextView.setVisibility(View.GONE);
        } else if (mOriginalTitle.isEmpty()) {
            originalTitleTextView.setText(DATA_NOT_AVAILABLE);
        } else {
            originalTitleTextView.setText(mOriginalTitle);
        }
    }

    /**
     * Function to provide a more readable date format for each movie including the
     * weekday, day, month and year of its release.
     */
    private void releaseDateHandler() {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat outputFormat =
                new SimpleDateFormat("EEE, d MMM yyyy", Locale.US);
        try {
            Date releaseDate = inputFormat.parse(mReleaseDate);
            String formattedDate = outputFormat.format(releaseDate);
            releaseDateTextView.setText(formattedDate);
        } catch (ParseException e) {
            textViewValueHandler(releaseDateTextView, mReleaseDate);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh_detail_menu) {
            getMovieRelatedAdditionalData();
            populateUI();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Function to make the details of each movie visible
     */
    private void showMovieDetails() {
        fetchErrorTextView.setVisibility(View.INVISIBLE);
        networkErrorTextView.setVisibility(View.INVISIBLE);
        movieDetailScrollView.setVisibility(View.VISIBLE);
    }

    /**
     * Function to make the error message referring to problem with http request visible
     */
    private void showFetchError() {
        movieDetailScrollView.setVisibility(View.INVISIBLE);
        networkErrorTextView.setVisibility(View.INVISIBLE);
        fetchErrorTextView.setVisibility(View.VISIBLE);
    }

    /**
     * Function to make the error message referring to problem with internet connection visible
     */
    private void showNetworkError() {
        movieDetailScrollView.setVisibility(View.INVISIBLE);
        fetchErrorTextView.setVisibility(View.INVISIBLE);
        networkErrorTextView.setVisibility(View.VISIBLE);
    }

    /**
     * Class to implement AsyncTaskPhaseListener for the cases of Movie Details (reviews, videos)
     */
    public class MovieDetailsProcessorAsyncTaskPhaseListenerImpl
            implements AsyncTaskPhaseListener<String> {
        private final String REVIEW_REQUEST_URL_PART = "/reviews";
        private final String VIDEO_REQUEST_URL_PART = "/videos";

        private String mRequestType;

        @Override
        public void onTaskBegin() {
            loadingProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public String onTaskProcess(String... args) {
            mRequestType = args[0];
            String urlEnding = "";
            if (mRequestType.equals(CommonApplicationConstants.REQUEST_TYPE_REVIEW)) {
                urlEnding = mId + REVIEW_REQUEST_URL_PART;
            } else if (mRequestType.equals(CommonApplicationConstants.REQUEST_TYPE_VIDEO)) {
                urlEnding = mId + VIDEO_REQUEST_URL_PART;
            }
            if (urlEnding.isEmpty()) {
                return null;
            }
            URL movieDetailsRequestUrl = NetworkUtils.buildUrlForMovieDetailsRequest(urlEnding);
            try {
                return NetworkUtils.getResponseFromUrl(movieDetailsRequestUrl);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public void onTaskComplete(String result) {
            loadingProgressBar.setVisibility(View.INVISIBLE);
            if (result != null) {
                try {
                    if (mRequestType.equals(CommonApplicationConstants.REQUEST_TYPE_REVIEW)) {
                        mReviewList = JsonUtils.getMovieReviewListFromJson(result);
                    } else if (mRequestType.equals(CommonApplicationConstants.REQUEST_TYPE_VIDEO)) {
                        mVideoList = JsonUtils.getMovieVideoListFromJson(result);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    mAsyncTaskProcessCounter ++;
                    if (mAsyncTaskProcessCounter == 2) {
                        if (mReviewList != null && mVideoList != null) {
                            showMovieDetails();
                            assignValuesToRecyclerView(CommonApplicationConstants.REQUEST_TYPE_REVIEW);
                            assignValuesToRecyclerView(CommonApplicationConstants.REQUEST_TYPE_VIDEO);
                        } else {
                            showFetchError();
                        }
                    }
                }
            } else {
                showFetchError();
            }
        }
    }

    /**
     * Function to check if there is a need for sequence number
     * @param count
     * @param labelTextView
     * @param pluralBase
     * @param single
     * @return
     */
    private boolean checkIfSequenceNumberNecessary(
            int count,
            TextView labelTextView,
            String pluralBase,
            String single) {
        if (count > 1) {
            labelTextView.setText(pluralBase + count + LIST_LABEL_PLURAL_ENDING);
            return true;
        } else if (count == 1) {
            labelTextView.setText(single);
            return false;
        } else {
            labelTextView.setVisibility(View.GONE);
            return false;
        }
    }

    /**
     * Function to assign values to the appropriate recycler view based on the input type
     * @param type
     */
    private void assignValuesToRecyclerView(String type) {
        if (type.equals(CommonApplicationConstants.REQUEST_TYPE_REVIEW)) {
            int reviewsNum = mReviewList.size();
            boolean isReviewSequenceNumberNecessary = checkIfSequenceNumberNecessary(
                    reviewsNum,
                    reviewListLabelTextView,
                    REVIEW_LIST_LABEL_PLURAL_BASE,
                    REVIEW_LIST_LABEL_SINGLE
            );
            ReviewAdapter reviewAdapter = new ReviewAdapter(
                    DetailActivity.this,
                    mReviewList,
                    isReviewSequenceNumberNecessary);
            reviewListRecyclerView.setAdapter(reviewAdapter);
        } else if (type.equals(CommonApplicationConstants.REQUEST_TYPE_VIDEO)) {
            int videosNum = mVideoList.size();
            boolean isVideoSequenceNumberNecessary = checkIfSequenceNumberNecessary(
                    videosNum,
                    videoListLabelTextView,
                    VIDEO_LIST_LABEL_PLURAL_BASE,
                    VIDEO_LIST_LABEL_SINGLE);
            VideoAdapter videoAdapter = new VideoAdapter(
                    DetailActivity.this,
                    mVideoList,
                    isVideoSequenceNumberNecessary);
            videoListRecyclerView.setAdapter(videoAdapter);
        }
    }
}
