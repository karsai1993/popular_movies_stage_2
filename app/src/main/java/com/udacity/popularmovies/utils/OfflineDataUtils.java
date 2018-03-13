package com.udacity.popularmovies.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import com.udacity.popularmovies.data.FavouriteMovieListContract;
import com.udacity.popularmovies.model.Movie;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class OfflineDataUtils {

    public static List<Movie> loadOfflineDataFromDatabase(Context context) {
        Cursor cursor = context.getContentResolver().query(
                FavouriteMovieListContract.FavouriteMovieListEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        List<Movie> offlineMovieList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(FavouriteMovieListContract.FavouriteMovieListEntry.COLUMN_NAME_ID));
                String title = cursor.getString(cursor.getColumnIndex(FavouriteMovieListContract.FavouriteMovieListEntry.COLUMN_NAME_TITLE));
                String originalTitle = cursor.getString(cursor.getColumnIndex(FavouriteMovieListContract.FavouriteMovieListEntry.COLUMN_NAME_ORIGINAL_TITLE));
                byte [] posterByteArray = cursor.getBlob(cursor.getColumnIndex(FavouriteMovieListContract.FavouriteMovieListEntry.COLUMN_NAME_POSTER));
                byte [] backdropByteArray = cursor.getBlob(cursor.getColumnIndex(FavouriteMovieListContract.FavouriteMovieListEntry.COLUMN_NAME_BACKDROP));
                String overview = cursor.getString(cursor.getColumnIndex(FavouriteMovieListContract.FavouriteMovieListEntry.COLUMN_NAME_OVERVIEW));
                String averageVote = cursor.getString(cursor.getColumnIndex(FavouriteMovieListContract.FavouriteMovieListEntry.COLUMN_NAME_AVERAGE_VOTE));
                String releaseDate = cursor.getString(cursor.getColumnIndex(FavouriteMovieListContract.FavouriteMovieListEntry.COLUMN_NAME_RELEASE_DATE));

                offlineMovieList.add(new Movie(
                        id,
                        title,
                        originalTitle,
                        posterByteArray,
                        backdropByteArray,
                        overview,
                        averageVote,
                        releaseDate,
                        false
                ));
            }
        }
        return offlineMovieList;
    }

    public static byte[] convertImageToByteArray(ImageView imageView) {
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        return bos.toByteArray();
    }

    public static Bitmap convertByteArrayToImage(byte[] imageByteArray) {
        return BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
    }
}
