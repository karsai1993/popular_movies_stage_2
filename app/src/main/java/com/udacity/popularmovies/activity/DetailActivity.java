package com.udacity.popularmovies.activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.udacity.popularmovies.action.VideoItemTouchHelper;
import com.udacity.popularmovies.background.AsyncTaskPhaseListener;
import com.udacity.popularmovies.CommonApplicationConstants;
import com.udacity.popularmovies.background.MovieDataProcessorAsyncTask;
import com.udacity.popularmovies.R;
import com.udacity.popularmovies.adapter.ReviewAdapter;
import com.udacity.popularmovies.adapter.VideoAdapter;
import com.udacity.popularmovies.data.FavouriteMovieListContract;
import com.udacity.popularmovies.decorator.DividerItemDecorator;
import com.udacity.popularmovies.model.Movie;
import com.udacity.popularmovies.model.Review;
import com.udacity.popularmovies.model.Video;
import com.udacity.popularmovies.utils.ImageUtils;
import com.udacity.popularmovies.utils.JsonUtils;
import com.udacity.popularmovies.utils.NetworkUtils;
import com.udacity.popularmovies.utils.OfflineDataUtils;

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
    private byte[] mBackdropByteArray;
    private String mPosterImagePathEnding;
    private byte[] mPosterByteArray;
    private String mOverview;
    private String mAverageVote;
    private String mReleaseDate;
    private boolean mIsOnline;
    private List<Review> mReviewList;
    private List<Video> mVideoList;
    private int mAsyncTaskProcessCounter;
    private Toast mToast;
    private ActionBar mActionBar;
    private Bundle mData;

    private final String DATA_NOT_AVAILABLE = "DNA";
    private final String VIDEO_LIST_LABEL_SINGLE = "Trailer:";
    private final String REVIEW_LIST_LABEL_SINGLE = "Review:";
    private final String VIDEO_LIST_LABEL_PLURAL_BASE = "Trailers (";
    private final String REVIEW_LIST_LABEL_PLURAL_BASE = "Reviews (";
    private final String LIST_LABEL_PLURAL_ENDING = "):";
    private final String RATING_MAX_COMPARATOR_TEXT = " / 10";
    private final String MESSAGE_FOR_ADDING_TO_FAVOURITE_LIST
            = " is added to list of favourites";
    private final String MESSAGE_FOR_REMOVING_FROM_FAVOURITE_LIST
            = " is removed from list of favourites";
    private final String STATE_DATA = "dataState";

    /**
     * butterknife is a third party library which is used here to binding the ids to fields easier
     * reference: http://jakewharton.github.io/butterknife/
     */
    @BindView(R.id.ll_detail_activity)
    LinearLayout movieDetailLinearLayout;
    @BindView(R.id.tv_detail_request_fetch_error)
    TextView fetchErrorTextView;
    @BindView(R.id.tv_detail_load_error)
    TextView loadErrorTextView;
    @BindView(R.id.tv_detail_request_network_error)
    TextView networkErrorTextView;
    @BindView(R.id.pb_detail_request_loading)
    ProgressBar loadingProgressBar;

    @BindView(R.id.iv_detail_activity_backdrop)
    ImageView backdropImageView;
    @BindView(R.id.iv_detail_activity_poster)
    ImageView posterImageView;
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
    @BindView(R.id.fab_favourite)
    FloatingActionButton favouriteFabButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mUnbinder = ButterKnife.bind(this);
        mActionBar = getSupportActionBar();
        receiveIntentValues();
        dataHandler();
    }

    /**
     * Function to remove the actual movie from list of favourites
     * @param id
     */
    private void removeMovieFromFavourites(String id) {
        getContentResolver().delete(
                FavouriteMovieListContract.FavouriteMovieListEntry.CONTENT_URI,
                FavouriteMovieListContract.FavouriteMovieListEntry.COLUMN_NAME_ID + " =?",
                new String[] {id}
        );
    }

    /**
     * Function to add the actual movie into the list of favourites
     * @param title
     * @param id
     * @param posterImage
     * @param backdropImage
     * @param overview
     * @param averageVote
     * @param releaseDate
     */
    private void addMovieToFavourites(
            String title,
            String id,
            byte[] posterImage,
            byte[] backdropImage,
            String overview,
            String averageVote,
            String releaseDate) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(
                FavouriteMovieListContract.FavouriteMovieListEntry.COLUMN_NAME_TITLE,
                title
        );
        contentValues.put(
                FavouriteMovieListContract.FavouriteMovieListEntry.COLUMN_NAME_ID,
                id
        );
        contentValues.put(
                FavouriteMovieListContract.FavouriteMovieListEntry.COLUMN_NAME_POSTER,
                posterImage
        );
        contentValues.put(
                FavouriteMovieListContract.FavouriteMovieListEntry.COLUMN_NAME_BACKDROP,
                backdropImage
        );
        contentValues.put(
                FavouriteMovieListContract.FavouriteMovieListEntry.COLUMN_NAME_OVERVIEW,
                overview
        );
        contentValues.put(
                FavouriteMovieListContract.FavouriteMovieListEntry.COLUMN_NAME_AVERAGE_VOTE,
                averageVote
        );
        contentValues.put(
                FavouriteMovieListContract.FavouriteMovieListEntry.COLUMN_NAME_RELEASE_DATE,
                releaseDate
        );
        getContentResolver().insert(
                FavouriteMovieListContract.FavouriteMovieListEntry.CONTENT_URI,
                contentValues);
    }

    /**
     * Function to execute the detail processor
     */
    private void dataHandler() {
        if (mIsOnline) {
            getMovieRelatedAdditionalData();
        } else {
            showMovieDetails();
            favouriteFabButton.hide();
            mActionBar.setTitle(mTitle);
            mActionBar.show();
        }
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
            favouriteFabButton.show();
            mActionBar.setTitle(mTitle);
            mActionBar.show();
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
        mData = getIntent().getExtras();
        if (mData != null) {
            Movie selectedMovie = mData.getParcelable(CommonApplicationConstants.MOVIE_DATA_KEY);
            mIsOnline = selectedMovie.isOnline();
            mId = selectedMovie.getId();
            if (mIsOnline) {
                mBackgroundImagePathEnding = selectedMovie.getBackdropImagePathEnding();
                mPosterImagePathEnding = selectedMovie.getPosterImagePathEnding();
            } else {
                mBackdropByteArray = selectedMovie.getBackdropByteArray();
                mPosterByteArray = selectedMovie.getPosterByteArray();
            }
            mTitle = selectedMovie.getTitle();
            mOriginalTitle = selectedMovie.getOriginalTitle();
            mOverview = selectedMovie.getOverview();
            mAverageVote = selectedMovie.getAverageVote();
            mReleaseDate = selectedMovie.getReleaseDate();
        } else {
            showLoadError();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBundle(STATE_DATA, mData);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mData = savedInstanceState.getBundle(STATE_DATA);
        receiveIntentValues();
        dataHandler();
    }

    /**
     * Function to set the values of all views in the layout
     * NOTE: Original title is only shown when it does not match with the title.
     * It usually occurs when the original title of the certain movie does not have English
     * original title.
     */
    private void populateUI() {
        if (mIsOnline) {
            ImageUtils.loadMovieImage(this, mPosterImagePathEnding, posterImageView, false);
            ImageUtils.loadMovieImage(this, mBackgroundImagePathEnding, backdropImageView, false);
            textViewValueHandler(averageVoteTextView, mAverageVote);
            averageVoteTextView.append(RATING_MAX_COMPARATOR_TEXT);
            textViewValueHandler(overviewTextView, mOverview);
            releaseDateHandler();
            originalTitleHandler();
            favouriteButtonHandler(this);
            initRecyclerView(this, videoListRecyclerView);
            initRecyclerView(this, reviewListRecyclerView);
        } else {
            posterImageView.setImageBitmap(OfflineDataUtils.convertByteArrayToImage(mPosterByteArray));
            backdropImageView.setImageBitmap(OfflineDataUtils.convertByteArrayToImage(mBackdropByteArray));
            textViewValueHandler(averageVoteTextView, mAverageVote);
            averageVoteTextView.append(RATING_MAX_COMPARATOR_TEXT);
            textViewValueHandler(overviewTextView, mOverview);
            releaseDateHandler();
            originalTitleHandler();
        }
    }

    /**
     * Function to handle star button
     * @param context
     */
    private void favouriteButtonHandler(final Context context) {
        if (isAlreadyMarkedAsFavourite(mId)) {
            favouriteFabButton.setBackgroundTintList(
                    ColorStateList.valueOf(getResources().getColor(R.color.colorGrey)));
        } else {
            favouriteFabButton.setBackgroundTintList(
                    ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
        }
        favouriteFabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isAlreadyMarkedAsFavourite(mId)) {
                    removeMovieFromFavourites(mId);
                    favouriteFabButton.setBackgroundTintList(
                            ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                    showToastMessage(context, mTitle + MESSAGE_FOR_REMOVING_FROM_FAVOURITE_LIST);
                } else {
                    addMovieToFavourites(
                            mTitle,
                            mId,
                            OfflineDataUtils.convertImageToByteArray(posterImageView),
                            OfflineDataUtils.convertImageToByteArray(backdropImageView),
                            mOverview,
                            mAverageVote,
                            mReleaseDate);
                    favouriteFabButton.setBackgroundTintList(
                            ColorStateList.valueOf(getResources().getColor(R.color.colorGrey)));
                    showToastMessage(context, mTitle + MESSAGE_FOR_ADDING_TO_FAVOURITE_LIST);
                }
            }
        });
    }

    /**
     * Function to show a message for the user about the status of the actual movie when star button
     * is clicked.
     * @param context
     * @param text
     */
    private void showToastMessage(Context context, String text) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(
                context,
                text,
                Toast.LENGTH_LONG);
        mToast.show();
    }

    /**
     * Function to check if the actual movie is among the favourites
     * @param id
     * @return if the actual movie is among the favourites
     */
    private boolean isAlreadyMarkedAsFavourite(String id) {
        Cursor cursor = getContentResolver().query(
                FavouriteMovieListContract.FavouriteMovieListEntry.CONTENT_URI,
                new String[] {FavouriteMovieListContract.FavouriteMovieListEntry.COLUMN_NAME_ID},
                FavouriteMovieListContract.FavouriteMovieListEntry.COLUMN_NAME_ID + " =?",
                new String[] {id},
                null
        );
        if (cursor == null) {
            return false;
        } else {
            int count = cursor.getCount();
            cursor.close();
            return count == 1;
        }
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
        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecorator(
                getResources().getDrawable(R.drawable.recyclerview_item_divider));
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setHasFixedSize(true);
    }

    /**
     * Function to handle original title text view
     */
    private void originalTitleHandler(){
        if (mTitle.equals(mOriginalTitle) || mOriginalTitle == null) {
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
        return mIsOnline;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh_detail_menu) {
            dataHandler();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Function to make the details of each movie visible
     */
    private void showMovieDetails() {
        fetchErrorTextView.setVisibility(View.GONE);
        loadErrorTextView.setVisibility(View.GONE);
        networkErrorTextView.setVisibility(View.GONE);
        movieDetailLinearLayout.setVisibility(View.VISIBLE);
    }

    /**
     * Function to make the error message referring to problem with http request visible
     */
    private void showFetchError() {
        movieDetailLinearLayout.setVisibility(View.GONE);
        networkErrorTextView.setVisibility(View.GONE);
        loadErrorTextView.setVisibility(View.GONE);
        fetchErrorTextView.setVisibility(View.VISIBLE);
        favouriteFabButton.hide();
        mActionBar.setTitle(DATA_NOT_AVAILABLE);
        mActionBar.show();
    }

    /**
     * Function to make the error message referring to problem with internet connection visible
     */
    private void showNetworkError() {
        movieDetailLinearLayout.setVisibility(View.GONE);
        fetchErrorTextView.setVisibility(View.GONE);
        loadErrorTextView.setVisibility(View.GONE);
        networkErrorTextView.setVisibility(View.VISIBLE);
        favouriteFabButton.hide();
        mActionBar.setTitle(DATA_NOT_AVAILABLE);
        mActionBar.show();
    }

    /**
     * Function to make the error message referring to problem with data loading visible
     */
    private void showLoadError() {
        movieDetailLinearLayout.setVisibility(View.GONE);
        fetchErrorTextView.setVisibility(View.GONE);
        networkErrorTextView.setVisibility(View.GONE);
        loadErrorTextView.setVisibility(View.VISIBLE);
        favouriteFabButton.hide();
        mActionBar.setTitle(DATA_NOT_AVAILABLE);
        mActionBar.show();
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
                            assignValuesToRecyclerView(
                                    CommonApplicationConstants.REQUEST_TYPE_REVIEW
                            );
                            assignValuesToRecyclerView(
                                    CommonApplicationConstants.REQUEST_TYPE_VIDEO
                            );
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
     * Function to handle video and review labels
     * @param count
     * @param labelTextView
     * @param pluralBase
     * @param single
     */
    private void handleLabel(
            int count,
            TextView labelTextView,
            String pluralBase,
            String single) {
        if (count > 1) {
            labelTextView.setText(pluralBase + count + LIST_LABEL_PLURAL_ENDING);
        } else if (count == 1) {
            labelTextView.setText(single);
        } else {
            labelTextView.setVisibility(View.GONE);
        }
    }

    /**
     * Function to assign values to the appropriate recycler view based on the input type
     * @param type
     */
    private void assignValuesToRecyclerView(String type) {
        if (type.equals(CommonApplicationConstants.REQUEST_TYPE_REVIEW)) {
            int reviewsNum = mReviewList.size();
            handleLabel(
                    reviewsNum,
                    reviewListLabelTextView,
                    REVIEW_LIST_LABEL_PLURAL_BASE,
                    REVIEW_LIST_LABEL_SINGLE);
            ReviewAdapter reviewAdapter = new ReviewAdapter(
                    DetailActivity.this,
                    mReviewList);
            reviewListRecyclerView.setAdapter(reviewAdapter);
        } else if (type.equals(CommonApplicationConstants.REQUEST_TYPE_VIDEO)) {
            int videosNum = mVideoList.size();
            handleLabel(
                    videosNum,
                    videoListLabelTextView,
                    VIDEO_LIST_LABEL_PLURAL_BASE,
                    VIDEO_LIST_LABEL_SINGLE);
            VideoAdapter videoAdapter = new VideoAdapter(
                    DetailActivity.this,
                    mVideoList);
            videoListRecyclerView.setAdapter(videoAdapter);
            new VideoItemTouchHelper(
                    DetailActivity.this,
                    mVideoList,
                    videoAdapter,
                    videoListRecyclerView,
                    getSupportLoaderManager())
                    .itemTouchHelper
                    .attachToRecyclerView(videoListRecyclerView);
        }
    }
}
