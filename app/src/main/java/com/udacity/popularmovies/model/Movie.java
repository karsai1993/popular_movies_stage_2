package com.udacity.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class for storing data for each movie
 */
public class Movie implements Parcelable {
    private String mId;
    private String mTitle;
    private String mOriginalTitle;
    private String mPosterImagePathEnding;
    private byte [] mPosterByteArray;
    private String mBackdropImagePathEnding;
    private byte [] mBackdropByteArray;
    private String mOverview;
    private String mAverageVote;
    private String mReleaseDate;
    private boolean mIsOnline;

    /**
     * Constructor created to store relevant information for each movie online
     * @param id
     * @param title
     * @param originalTitle
     * @param posterImagePathEnding
     * @param backdropImagePathEnding
     * @param overview
     * @param averageVote
     * @param releaseDate
     */
    public Movie(String id,
                 String title,
                 String originalTitle,
                 String posterImagePathEnding,
                 String backdropImagePathEnding,
                 String overview,
                 String averageVote,
                 String releaseDate) {
        this.mId = id;
        this.mTitle = title;
        this.mOriginalTitle = originalTitle;
        this.mPosterImagePathEnding = posterImagePathEnding;
        this.mBackdropImagePathEnding = backdropImagePathEnding;
        this.mOverview = overview;
        this.mAverageVote = averageVote;
        this.mReleaseDate = releaseDate;
        this.mIsOnline = true;
    }

    /**
     * Constructor created to store relevant information for each movie offline
     * @param id
     * @param title
     * @param originalTitle
     * @param posterByteArray
     * @param backdropByteArray
     * @param overview
     * @param averageVote
     * @param releaseDate
     */
    public Movie(String id,
                 String title,
                 String originalTitle,
                 byte[] posterByteArray,
                 byte[] backdropByteArray,
                 String overview,
                 String averageVote,
                 String releaseDate) {
        this.mId = id;
        this.mTitle = title;
        this.mOriginalTitle = originalTitle;
        this.mPosterByteArray = posterByteArray;
        this.mBackdropByteArray = backdropByteArray;
        this.mOverview = overview;
        this.mAverageVote = averageVote;
        this.mReleaseDate = releaseDate;
        this.mIsOnline = false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt((mIsOnline ? 1 : 0));
        parcel.writeString(mId);
        parcel.writeString(mTitle);
        parcel.writeString(mOriginalTitle);
        parcel.writeString(mPosterImagePathEnding);
        parcel.writeString(mBackdropImagePathEnding);
        parcel.writeString(mOverview);
        parcel.writeString(mAverageVote);
        parcel.writeString(mReleaseDate);
        if (mPosterByteArray != null) {
            parcel.writeInt(mPosterByteArray.length);
            parcel.writeByteArray(mPosterByteArray);
        }
        if (mBackdropByteArray != null) {
            parcel.writeInt(mBackdropByteArray.length);
            parcel.writeByteArray(mBackdropByteArray);
        }
    }

    private Movie(Parcel in) {
        mIsOnline = in.readInt() == 1;
        mId = in.readString();
        mTitle = in.readString();
        mOriginalTitle = in.readString();
        mPosterImagePathEnding = in.readString();
        mBackdropImagePathEnding = in.readString();
        mOverview = in.readString();
        mAverageVote = in.readString();
        mReleaseDate = in.readString();
        if (in.dataAvail() > 0) {
            mPosterByteArray = new byte[in.readInt()];
            if (mPosterByteArray.length > 0) in.readByteArray(mPosterByteArray);
        }
        if (in.dataAvail() > 0) {
            mBackdropByteArray = new byte[in.readInt()];
            if (mBackdropByteArray.length > 0) in.readByteArray(mBackdropByteArray);
        }
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {

        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    /**
     * Function to get if the process is online
     * @return if the process is online
     */
    public boolean isOnline() {
        return mIsOnline;
    }

    /**
     * Function to set if the process is online if necessary
     * @param isOnline
     */
    public void setIsOnline(boolean isOnline) {
        this.mIsOnline = isOnline;
    }

    /**
     * Function to get the byte array of poster of the selected movie
     * @return byte array of poster of the selected movie
     */
    public byte [] getPosterByteArray() {
        return mPosterByteArray;
    }

    /**
     * Function to set the byte array of poster of the selected movie if necessary
     * @param posterByteArray
     */
    public void setPosterByteArray(byte [] posterByteArray) {
        this.mPosterByteArray = posterByteArray;
    }

    /**
     * Function to get the byte array of backdrop of the selected movie
     * @return byte array of backdrop of the selected movie
     */
    public byte [] getBackdropByteArray() {
        return mBackdropByteArray;
    }

    /**
     * Function to set the byte array of backdrop of the selected movie if necessary
     * @param backdropByteArray
     */
    public void setBackdropByteArray(byte [] backdropByteArray) {
        this.mBackdropByteArray = backdropByteArray;
    }

    /**
     * Function to return the id of selected movie
     * @return id of selected movie
     */
    public String getId() {
        return mId;
    }

    /**
     * Function to set the id of selected movie if necessary
     * @param id
     */
    public void setId(String id) {
        this.mId = id;
    }

    /**
     * Function to return the title of selected movie
     * @return title of selected movie
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Function to set the title of selected movie if necessary
     * @param title
     */
    public void setTitle(String title) {
        this.mTitle = title;
    }

    /**
     * Function to return the original title of selected movie
     * @return original title of selected movie
     */
    public String getOriginalTitle() {
        return mOriginalTitle;
    }

    /**
     * Function to set the original title of selected movie if necessary
     * @param originalTitle
     */
    public void setOriginalTitle(String originalTitle) {
        this.mOriginalTitle = originalTitle;
    }

    /**
     * Function to return the poster image url ending of selected movie
     * @return poster image url ending of selected movie
     */
    public String getPosterImagePathEnding() {
        return mPosterImagePathEnding;
    }

    /**
     * Function to set the poster image url ending of selected movie if necessary
     * @param posterImagePathEnding
     */
    public void setPosterImagePathEnding(String posterImagePathEnding) {
        this.mPosterImagePathEnding = posterImagePathEnding;
    }

    /**
     * Function to return the backdrop image url ending of selected movie
     * @return backdrop image url ending of selected movie
     */
    public String getBackdropImagePathEnding() {
        return mBackdropImagePathEnding;
    }

    /**
     * Function to set the backdrop image url ending of selected movie if necessary
     * @param backdropImagePathEnding
     */
    public void setBackdropImagePathEnding(String backdropImagePathEnding) {
        this.mBackdropImagePathEnding = backdropImagePathEnding;
    }

    /**
     * Function to return the overview of selected movie
     * @return overview of selected movie
     */
    public String getOverview() {
        return mOverview;
    }

    /**
     * Function to set the overview of selected movie if necessary
     * @param overview
     */
    public void setOverview(String overview) {
        this.mOverview = overview;
    }

    /**
     * Function to return the average vote of selected movie
     * @return average vote of selected movie
     */
    public String getAverageVote() {
        return mAverageVote;
    }

    /**
     * Function to set the average vote of selected movie if necessary
     * @param averageVote
     */
    public void setAverageVote(String averageVote) {
        this.mAverageVote = averageVote;
    }

    /**
     * Function to return the release date of selected movie
     * @return release date of selected movie
     */
    public String getReleaseDate() {
        return mReleaseDate;
    }

    /**
     * Function to set the release date of selected movie if necessary
     * @param releaseDate
     */
    public void setReleaseDate(String releaseDate) {
        this.mReleaseDate = releaseDate;
    }
}
