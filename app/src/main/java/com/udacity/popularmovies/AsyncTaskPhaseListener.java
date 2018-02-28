package com.udacity.popularmovies;

/**
 * This interface is needed to create a callback mechanism to be able to abstract the Asynctask
 * in separate classes
 * source: http://www.jameselsey.co.uk/blogs/techblog/extracting-out-your-asynctasks-into-separate-classes-makes-your-code-cleaner/
 * @param <String>
 */
public interface AsyncTaskPhaseListener<String> {
    void onTaskBegin();
    String onTaskProcess(String... args);
    void onTaskComplete(String result);
}
