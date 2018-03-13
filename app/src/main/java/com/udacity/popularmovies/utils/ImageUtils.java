package com.udacity.popularmovies.utils;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.udacity.popularmovies.R;

/**
 * Class for implementing image ToDos
 */
public class ImageUtils {

    private static final String IMAGE_PATH_BASE_URL = "http://image.tmdb.org/t/p/w185/";
    private static final String VIDEO_PATH_URL_START = "https://i.ytimg.com/vi/";
    private static final String VIDEO_PATH_URL_END = "/hqdefault.jpg";

    /**
     * Function to load images from http source
     * @param context
     * @param pathEnding
     * @param imageView
     */
    public static void loadMovieImage(
            Context context,
            String pathEnding,
            ImageView imageView,
            boolean shouldFit) {
        /**
         * picasso is a third party library which is used to making the image appear more smoothly
         * reference: http://square.github.io/picasso/
         * IMPORTANT to mention here: Images for cases of waiting (placeholder) and error(error)
         * are the followings: ic_icons8_load_24 and ic_icons8_error_24. Both of them were
         * downloaded from a free source: https://icons8.com/material-icons/,
         * then Android Asset Studio (https://romannurik.github.io/AndroidAssetStudio/) was used.
         */
        if (shouldFit) {
            Picasso.with(context)
                    .load(IMAGE_PATH_BASE_URL + pathEnding)
                    .placeholder(R.drawable.ic_icons8_load_24)
                    .error(R.drawable.ic_icons8_error_24)
                    .fit()
                    .into(imageView);
        } else {
            Picasso.with(context)
                    .load(IMAGE_PATH_BASE_URL + pathEnding)
                    .placeholder(R.drawable.ic_icons8_load_24)
                    .error(R.drawable.ic_icons8_error_24)
                    .into(imageView);
        }
    }

    public static void loadVideoImage(
            Context context,
            String videoKey,
            ImageView imageView) {
        Picasso.with(context)
                .load(VIDEO_PATH_URL_START + videoKey + VIDEO_PATH_URL_END)
                .placeholder(R.drawable.ic_icons8_load_24)
                .error(R.drawable.ic_icons8_error_24)
                .into(imageView);
    }
}
