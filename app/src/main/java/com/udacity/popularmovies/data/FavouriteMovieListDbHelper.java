package com.udacity.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FavouriteMovieListDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "favourite_movie_list.db";
    private static final int DATABASE_VERSION = 2;

    public FavouriteMovieListDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_FAVOURITE_MOVIE_LIST_TABLE = "CREATE TABLE " +
                FavouriteMovieListContract.FavouriteMovieListEntry.TABLE_NAME + " (" +
                FavouriteMovieListContract.FavouriteMovieListEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                FavouriteMovieListContract.FavouriteMovieListEntry.COLUMN_NAME_TITLE + " TEXT NOT NULL," +
                FavouriteMovieListContract.FavouriteMovieListEntry.COLUMN_NAME_ID + " TEXT NOT NULL," +
                FavouriteMovieListContract.FavouriteMovieListEntry.COLUMN_NAME_BACKDROP + " BLOB NOT NULL," +
                FavouriteMovieListContract.FavouriteMovieListEntry.COLUMN_NAME_POSTER + " BLOB NOT NULL," +
                FavouriteMovieListContract.FavouriteMovieListEntry.COLUMN_NAME_ORIGINAL_TITLE + " TEXT," +
                FavouriteMovieListContract.FavouriteMovieListEntry.COLUMN_NAME_OVERVIEW + " TEXT NOT NULL," +
                FavouriteMovieListContract.FavouriteMovieListEntry.COLUMN_NAME_AVERAGE_VOTE + " TEXT NOT NULL," +
                FavouriteMovieListContract.FavouriteMovieListEntry.COLUMN_NAME_RELEASE_DATE + " TEXT NOT NULL" +
                ");";
        sqLiteDatabase.execSQL(SQL_CREATE_FAVOURITE_MOVIE_LIST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavouriteMovieListContract.FavouriteMovieListEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
