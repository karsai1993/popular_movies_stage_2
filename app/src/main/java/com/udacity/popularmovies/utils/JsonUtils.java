package com.udacity.popularmovies.utils;

import com.udacity.popularmovies.CommonApplicationConstants;
import com.udacity.popularmovies.model.Movie;
import com.udacity.popularmovies.model.Review;
import com.udacity.popularmovies.model.Video;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for implementing json ToDos
 */
public class JsonUtils {
    private static final String RESULTS_KEY = "results";
    private static final String BACKDROP_PATH_KEY = "backdrop_path";
    private static final String POSTER_PATH_KEY = "poster_path";
    private static final String TITLE_KEY = "title";
    private static final String ORIGINAL_TITLE_KEY = "original_title";
    private static final String OVERVIEW_KEY = "overview";
    private static final String AVERAGE_VOTE_KEY = "vote_average";
    private static final String RELEASE_DATE_KEY = "release_date";
    private static final String ID_KEY = "id";
    private static final String AUTHOR_KEY = "author";
    private static final String CONTENT_KEY = "content";
    private static final String URL_KEY = "url";
    private static final String NAME_KEY = "name";
    private static final String LINK_KEY = "key";

    /**
     * Function to give the list of Movie objects based on the json response from http request
     * @param movieJsonResponse
     * @return list of Movie objects
     * @throws JSONException
     */
    public static List<Movie> getMovieDataListFromJson(String movieJsonResponse)
            throws JSONException {
        JSONObject response = new JSONObject(movieJsonResponse);
        JSONArray results = response.getJSONArray(RESULTS_KEY);
        List<Movie> movieList = new ArrayList<>();
        for (int i = 0; i < results.length(); i++) {
            JSONObject currObject = results.getJSONObject(i);
            String id = currObject.optString(ID_KEY);
            String title = currObject.optString(TITLE_KEY);
            String originalTitle = currObject.optString(ORIGINAL_TITLE_KEY);
            String averageVote = currObject.optString(AVERAGE_VOTE_KEY);
            String posterImagePath = currObject.optString(POSTER_PATH_KEY);
            String backdropImagePath = currObject.optString(BACKDROP_PATH_KEY);
            String overview = currObject.optString(OVERVIEW_KEY);
            String releaseDate = currObject.optString(RELEASE_DATE_KEY);
            movieList.add(new Movie(
                    id,
                    title,
                    originalTitle,
                    posterImagePath,
                    backdropImagePath,
                    overview,
                    averageVote,
                    releaseDate));
        }
        return movieList;
    }

    /**
     * Function to give the list of Review objects based on the json response from http request
     * @param reviewJsonResponse
     * @return list of Review objects
     * @throws JSONException
     */
    public static List<Review> getMovieReviewListFromJson(String reviewJsonResponse)
            throws JSONException {
        JSONObject response = new JSONObject(reviewJsonResponse);
        JSONArray results = response.getJSONArray(RESULTS_KEY);
        List<Review> reviewList = new ArrayList<>();
        for (int i = 0; i < results.length(); i++) {
            JSONObject currObject = results.getJSONObject(i);
            String author = currObject.optString(AUTHOR_KEY);
            String content = currObject.optString(CONTENT_KEY);
            String url = currObject.optString(URL_KEY);
            reviewList.add(new Review(author, content, url));
        }
        return reviewList;
    }

    /**
     * Function to give the list of Video objects based on the json response from http request
     * @param videoJsonResponse
     * @return list of Video objects
     * @throws JSONException
     */
    public static List<Video> getMovieVideoListFromJson(String videoJsonResponse)
            throws JSONException {
        JSONObject response = new JSONObject(videoJsonResponse);
        JSONArray results = response.getJSONArray(RESULTS_KEY);
        List<Video> videoList = new ArrayList<>();
        for (int i = 0; i < results.length(); i++) {
            JSONObject currObject = results.getJSONObject(i);
            String name = currObject.optString(NAME_KEY);
            String key = currObject.optString(LINK_KEY);
            videoList.add(new Video(name, key));
        }
        return videoList;
    }
}
