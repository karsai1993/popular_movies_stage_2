package com.udacity.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

import com.udacity.popularmovies.CommonApplicationConstants;

public class FavouriteMovieListContract {

    public static final String AUTHORITY = "com.udacity.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_FAVOURITE_MOVIE_LIST = "favourite_movie_list";

    public static final class  FavouriteMovieListEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVOURITE_MOVIE_LIST).build();

        public static final String TABLE_NAME = "favourite_movie_list";
        public static final String COLUMN_NAME_TITLE = CommonApplicationConstants.TITLE_KEY;
        public static final String COLUMN_NAME_ID = CommonApplicationConstants.ID_KEY;
        public static final String COLUMN_NAME_BACKDROP
                = CommonApplicationConstants.BACKDROP_PATH_KEY;
        public static final String COLUMN_NAME_POSTER = CommonApplicationConstants.POSTER_PATH_KEY;
        public static final String COLUMN_NAME_ORIGINAL_TITLE
                = CommonApplicationConstants.ORIGINAL_TITLE_KEY;
        public static final String COLUMN_NAME_OVERVIEW = CommonApplicationConstants.OVERVIEW_KEY;
        public static final String COLUMN_NAME_AVERAGE_VOTE
                = CommonApplicationConstants.AVERAGE_VOTE_KEY;
        public static final String COLUMN_NAME_RELEASE_DATE
                = CommonApplicationConstants.RELEASE_DATE_KEY;

    }
}
