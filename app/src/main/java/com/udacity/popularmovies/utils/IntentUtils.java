package com.udacity.popularmovies.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.ShareCompat;

/**
 * Class for implementing explicit intents
 */
public class IntentUtils {

    /**
     * Function to implement explicit intent for opening a web page
     * @param context
     * @param url
     */
    public static void openWebPage (Context context, String url) {
        Uri webPage = Uri.parse(url);
        Intent openWebPageIntent = new Intent(Intent.ACTION_VIEW, webPage);
        if (openWebPageIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(openWebPageIntent);
        }
    }

    /**
     * Function to implement explicit intent for sharing url
     * @param context
     * @param textToShare
     */
    public static void shareUrl(Context context, String textToShare) {
        String mimeType = "text/plain";
        String title = "Let's share!";
        Activity activity = (Activity) context;
        Intent shareIntent = ShareCompat.IntentBuilder
                .from(activity)
                .setChooserTitle(title)
                .setType(mimeType)
                .setText(textToShare)
                .createChooserIntent();
        if (shareIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(shareIntent);
        }
    }
}
