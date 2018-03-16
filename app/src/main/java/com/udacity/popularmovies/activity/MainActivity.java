package com.udacity.popularmovies.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.udacity.popularmovies.ListItemClickListener;
import com.udacity.popularmovies.background.AsyncTaskPhaseListener;
import com.udacity.popularmovies.CommonApplicationConstants;
import com.udacity.popularmovies.background.MovieDataProcessorAsyncTask;
import com.udacity.popularmovies.R;
import com.udacity.popularmovies.adapter.MovieAdapter;
import com.udacity.popularmovies.model.Movie;
import com.udacity.popularmovies.utils.JsonUtils;
import com.udacity.popularmovies.utils.NetworkUtils;
import com.udacity.popularmovies.utils.OfflineDataUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Class for implementing starter point of the application
 */
public class MainActivity extends AppCompatActivity implements SharedPreferences
        .OnSharedPreferenceChangeListener, ListItemClickListener {

    /**
     * butterknife is a third party library which is used here to binding the ids to fields easier
     * reference: http://jakewharton.github.io/butterknife/
     */
    @BindView(R.id.rv_movies)
    RecyclerView movieRecyclerView;
    @BindView(R.id.tv_movie_request_fetch_error)
    TextView fetchErrorTextView;
    @BindView(R.id.tv_movie_request_network_error)
    TextView networkErrorTextView;
    @BindView(R.id.tv_empty_favourite_list_message)
    TextView emptyFavouriteListMessageTextView;
    @BindView(R.id.pb_movie_request_loading)
    ProgressBar loadingProgressBar;

    private List<Movie> mMovieList;
    private Unbinder mUnbinder;
    private boolean mIsOnline;
    private ActionBar mActionBar;

    private final int COLUMN_NUMBER = 2;
    private final String LABEL_PART_FAVOURITES = " - Favourites";
    private final String LABEL_PART_TOP_RATED = " - Highest Rated";
    private final String LABEL_PART_MOST_POPULAR = " - Most Popular";
    private final String STATE_MOVIE_LIST = "movieListState";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUnbinder = ButterKnife.bind(this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, COLUMN_NUMBER);
        layoutManager.setAutoMeasureEnabled(true);
        movieRecyclerView.setLayoutManager(layoutManager);
        movieRecyclerView.setHasFixedSize(true);
        applySortingOrderBasedOnSharedPreferences();
    }

    /**
     * Function to register shared preferences listener, and to launch the sorting execution
     */
    private void applySortingOrderBasedOnSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        executeSortingBasedOnPreference(sharedPreferences);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    /**
     * Function to execute http request based on sorting requirement including network checking
     * @param sharedPreferences
     */
    private void executeSortingBasedOnPreference(SharedPreferences sharedPreferences) {
        String sortingOrderPreferenceValue = sharedPreferences.getString(
                getString(R.string.pref_sort_order_key),
                getString(R.string.pref_sort_order_by_popularity_value)
        );
        mActionBar = getSupportActionBar();
        if (sortingOrderPreferenceValue.equals(getString(R.string.pref_sort_order_by_favourites_value))) {
            mIsOnline = false;
            mActionBar.setTitle(getString(R.string.app_name) + LABEL_PART_FAVOURITES);
        } else {
            mIsOnline = true;
            if (sortingOrderPreferenceValue.equals(getString(R.string.pref_sort_order_by_popularity_value))) {
                mActionBar.setTitle(getString(R.string.app_name) + LABEL_PART_MOST_POPULAR);
            } else {
                mActionBar.setTitle(getString(R.string.app_name) + LABEL_PART_TOP_RATED);
            }
        }
        mActionBar.show();
        if (!mIsOnline) {
            mMovieList = OfflineDataUtils.loadOfflineDataFromDatabase(this);
            if (mMovieList.size() == 0) {
                showEmptyFavouriteListMessage();
            } else {
                MovieAdapter movieAdapter = new MovieAdapter(this, mMovieList, false);
                movieRecyclerView.setAdapter(movieAdapter);
            }
        } else {
            if (!NetworkUtils.isNetworkAvailable(this)) {
                showNetworkError();
            } else {
                showMovieList();
                new MovieDataProcessorAsyncTask(new MovieListDataProcessorAsyncTaskPhaseListenerImpl())
                        .execute(sortingOrderPreferenceValue);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        int numOfMovies = mMovieList.size();
        Movie [] movieArray = new Movie[numOfMovies];
        for (int i = 0; i < numOfMovies; i++) {
            movieArray[i] = mMovieList.get(i);
        }
        outState.putParcelableArray(STATE_MOVIE_LIST, movieArray);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Movie [] movieArray = (Movie[]) savedInstanceState.getParcelableArray(STATE_MOVIE_LIST);
        List<Movie> movieList = new ArrayList<>();
        for (Movie movie : movieArray) {
            movieList.add(movie);
        }
        mMovieList = movieList;
        applySortingOrderBasedOnSharedPreferences();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mIsOnline) {
            getMenuInflater().inflate(R.menu.main_menu_online, menu);
        } else {
            getMenuInflater().inflate(R.menu.main_menu_offline, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsActivityIntent = new Intent(
                    MainActivity.this,
                    SettingsActivity.class);
            startActivity(settingsActivityIntent);
        }
        if (id == R.id.action_refresh_main_menu) {
            applySortingOrderBasedOnSharedPreferences();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_sort_order_key))) {
            executeSortingBasedOnPreference(sharedPreferences);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
        mUnbinder.unbind();
    }

    /**
     * Function to make the poster of each movie visible
     */
    private void showMovieList() {
        fetchErrorTextView.setVisibility(View.INVISIBLE);
        networkErrorTextView.setVisibility(View.INVISIBLE);
        emptyFavouriteListMessageTextView.setVisibility(View.INVISIBLE);
        movieRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * Function to make the error message referring to problem with http request visible
     */
    private void showFetchError() {
        movieRecyclerView.setVisibility(View.INVISIBLE);
        networkErrorTextView.setVisibility(View.INVISIBLE);
        emptyFavouriteListMessageTextView.setVisibility(View.INVISIBLE);
        fetchErrorTextView.setVisibility(View.VISIBLE);
    }

    /**
     * Function to make the error message referring to problem with internet connection visible
     */
    private void showNetworkError() {
        movieRecyclerView.setVisibility(View.INVISIBLE);
        fetchErrorTextView.setVisibility(View.INVISIBLE);
        emptyFavouriteListMessageTextView.setVisibility(View.INVISIBLE);
        networkErrorTextView.setVisibility(View.VISIBLE);
    }

    /**
     * Function to make the message referring to empty favourite list visible
     */
    private void showEmptyFavouriteListMessage() {
        movieRecyclerView.setVisibility(View.INVISIBLE);
        fetchErrorTextView.setVisibility(View.INVISIBLE);
        networkErrorTextView.setVisibility(View.INVISIBLE);
        emptyFavouriteListMessageTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onListItemClick(int index) {
        Movie selectedMovie = mMovieList.get(index);
        Intent detailActivityIntent = new Intent(MainActivity.this,
                DetailActivity.class);
        detailActivityIntent.putExtra(CommonApplicationConstants.MOVIE_DATA_KEY,
                selectedMovie);
        startActivity(detailActivityIntent);
    }

    /**
     * Class to implement AsyncTaskPhaseListener for the case of Movie list data
     */
    public class MovieListDataProcessorAsyncTaskPhaseListenerImpl
            implements AsyncTaskPhaseListener<String> {

        @Override
        public void onTaskBegin() {
            loadingProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public String onTaskProcess(String... args) {
            URL movieRequestUrl = NetworkUtils.buildUrlForMovieListRequest(args[0]);
            try {
                return NetworkUtils.getResponseFromUrl(movieRequestUrl);
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
                    mMovieList = JsonUtils.getMovieDataListFromJson(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    if (mMovieList != null) {
                        showMovieList();
                        MovieAdapter movieAdapter = new MovieAdapter(
                                MainActivity.this,
                                mMovieList,
                                true);
                        movieRecyclerView.setAdapter(movieAdapter);
                    } else {
                        showFetchError();
                    }
                }
            } else {
                showFetchError();
            }
        }
    }
}
