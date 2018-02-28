package com.udacity.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class for storing data for each video
 */
public class Video implements Parcelable{
    private String mName;
    private String mKey;

    /**
     * Constructor created to store relevant information for each video
     * @param mName
     * @param mKey
     */
    public Video(String mName, String mKey) {
        this.mName = mName;
        this.mKey = mKey;
    }

    /**
     * Function to return the name of the video
     * @return name of video
     */
    public String getName() {
        return mName;
    }

    /**
     * Function to set the name of the video if necessary
     * @param name
     */
    public void setName(String name) {
        this.mName = name;
    }

    /**
     * Function to return the youtube key of the video
     * @return key of video
     */
    public String getKey() {
        return mKey;
    }

    /**
     * Function to set the key of the video if necessary
     * @param key
     */
    public void setKey(String key) {
        this.mKey = key;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mName);
        parcel.writeString(mKey);
    }

    private Video(Parcel in) {
        mName = in.readString();
        mKey = in.readString();
    }

    public static final Parcelable.Creator<Video> CREATOR = new Parcelable.Creator<Video>() {

        @Override
        public Video createFromParcel(Parcel parcel) {
            return new Video(parcel);
        }

        @Override
        public Video[] newArray(int size) {
            return new Video[size];
        }
    };
}
