package com.codepath.apps.restclienttemplate;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class ParseRelativeDate {

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;
    public static final String TAG = "ParseRelativeDate";
    private static final String TWITTER_TIME_DOT = "• ";

    public static String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        try {
            long time = sf.parse(rawJsonDate).getTime();
            long now = System.currentTimeMillis();

            final long diff = now - time;
            if (diff < MINUTE_MILLIS) {
                return TWITTER_TIME_DOT + diff / SECOND_MILLIS + "s";
            }  else if (diff < 50 * MINUTE_MILLIS) {
                return TWITTER_TIME_DOT + diff / MINUTE_MILLIS + "m";
            } else if (diff < 24 * HOUR_MILLIS) {
                return TWITTER_TIME_DOT + diff / HOUR_MILLIS + "h";
            } else {
                return TWITTER_TIME_DOT + diff / DAY_MILLIS + "d";
            }
        } catch (ParseException e) {
            Log.i(TAG, "getRelativeTimeAgo failed");
            e.printStackTrace();
        }

        return "";
    }
}
