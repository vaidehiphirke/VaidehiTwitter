package com.codepath.apps.restclienttemplate.models;

import android.util.Log;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
@Entity(foreignKeys = @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "userId"))

public class Tweet {

    public static final int MAX_TWEET_LENGTH = 280;

    // fields must be public for Parceler
    @ColumnInfo
    @PrimaryKey
    public long id;
    @ColumnInfo
    public String body;
    @ColumnInfo
    public String createdAt;
    // ignore user object because Room doesn't allow directly accessing class objects
    @Ignore
    public User user;
    // instead use userId as ForeignKey
    @ColumnInfo
    public long userId;
    @ColumnInfo
    public String mediaDisplayUrl;

    // empty constructor needed for Parceler
    public Tweet() {
    }

    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        final Tweet tweet = new Tweet();
        tweet.body = jsonObject.getString("text");
        tweet.createdAt = jsonObject.getString("created_at");
        final User user = User.fromJson(jsonObject.getJSONObject("user"));
        tweet.user = user;
        tweet.userId = user.id;
        tweet.mediaDisplayUrl = null;
        tweet.id = jsonObject.getLong("id");
        final JSONObject entities = jsonObject.getJSONObject("entities");
        if (entities.has("media")) {
            final JSONArray media = entities.getJSONArray("media");
            tweet.mediaDisplayUrl = media.getJSONObject(0).getString("media_url_https");
            Log.i("MediaDisplay (Tweet)", "Got url");
        }
        return tweet;
    }

    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        final List<Tweet> tweets = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            tweets.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return tweets;
    }

    public long getId() {
        return id;
    }
}
