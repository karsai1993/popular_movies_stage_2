package com.udacity.popularmovies;

import android.os.AsyncTask;

/**
 * Class for implementing background process for http request
 * It is separated for the purpose to have a more maintainable code.
 * source: http://www.jameselsey.co.uk/blogs/techblog/extracting-out-your-asynctasks-into-separate-classes-makes-your-code-cleaner/
 */
public class MovieDataProcessorAsyncTask extends AsyncTask<String, Void, String> {

    private AsyncTaskPhaseListener<String> mListener;

    public MovieDataProcessorAsyncTask(AsyncTaskPhaseListener<String> listener) {
        this.mListener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mListener.onTaskBegin();
    }

    @Override
    protected String doInBackground(String... args) {
        if (args.length == 0) {
            return null;
        }
        return mListener.onTaskProcess(args);
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        mListener.onTaskComplete(result);
    }
}
