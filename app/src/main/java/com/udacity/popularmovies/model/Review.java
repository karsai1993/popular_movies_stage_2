package com.udacity.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class for storing data for each review
 */
public class Review implements Parcelable {
    private String mAuthor;
    private String mContent;
    private String mUrl;

    /**
     * Constructor created to store relevant information for each review
     * @param mAuthor
     * @param mContent
     * @param mUrl
     */
    public Review(String mAuthor, String mContent, String mUrl) {
        this.mAuthor = mAuthor;
        this.mContent = mContent;
        this.mUrl = mUrl;
    }

    /**
     * Function to return the author of the review
     * @return author of review
     */
    public String getAuthor() {
        return mAuthor;
    }

    /**
     * Function to set the author of the review if necessary
     * @param author
     */
    public void setAuthor(String author) {
        this.mAuthor = author;
    }

    /**
     * Function to return the content of the review
     * @return content of review
     */
    public String getContent() {
        return mContent;
    }

    /**
     * Function to set the content of review if necessary
     * @param content
     */
    public void setContent(String content) {
        this.mContent = content;
    }

    /**
     * Function to get the url of the review
     * @return url of review
     */
    public String getUrl() {
        return mUrl;
    }

    /**
     * Function to set the url of review if necessary
     * @param url
     */
    public void setUrl(String url) {
        this.mUrl = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mAuthor);
        parcel.writeString(mContent);
        parcel.writeString(mUrl);
    }

    private Review(Parcel in) {
        mAuthor = in.readString();
        mContent = in.readString();
        mUrl = in.readString();
    }

    public static final Parcelable.Creator<Review> CREATOR = new Parcelable.Creator<Review>() {

        @Override
        public Review createFromParcel(Parcel parcel) {
            return new Review(parcel);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };
}
