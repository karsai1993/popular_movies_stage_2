package com.udacity.popularmovies.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import com.udacity.popularmovies.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

/**
 * Class for implementing network ToDos
 */
public class NetworkUtils {

    private static final String REQUEST_BASE_URL = "https://api.themoviedb.org/3/movie/";
    private static final String API_KEY_PARAM = "api_key";

    /**
     * Function to create the necessary url for the http request
     * @param sortingOrderValue
     * @return url of http request
     */
    public static URL buildUrlForMovieListRequest(String sortingOrderValue) {
        Uri builtUri = Uri.parse(REQUEST_BASE_URL + sortingOrderValue)
                .buildUpon()
                .appendQueryParameter(API_KEY_PARAM, BuildConfig.MOVIE_API_KEY)
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * Function to create the necessary url for the http request
     * @param urlEnding
     * @return
     */
    public static URL buildUrlForMovieDetailsRequest(String urlEnding) {
        Uri builtUri = Uri.parse(REQUEST_BASE_URL + urlEnding)
                .buildUpon()
                .appendQueryParameter(API_KEY_PARAM, BuildConfig.MOVIE_API_KEY)
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * Function to execute http request, and to store the response
     * @param url
     * @return response of http request
     * @throws IOException
     */
    public static String getResponseFromUrl(URL url) throws IOException {
        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");
            if(scanner.hasNext()) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    /**
     * Function to check if the device is connected to the internet
     * Advised page: https://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-times-out
     * @param context
     * @return whether the device has internet connection
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo =
                connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
