package com.codepath.apps.restclienttemplate.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class Tweet {

    //public for parceler
    public static final int MAX_TWEET_LENGTH = 280;
    public String body;
    public String createdAt;
    public User user;
    public String mediaDisplayUrl;
    public long id;


    //empty constructor needed for Parceler
    public Tweet() {
    }

    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.body = jsonObject.getString("text");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
        tweet.mediaDisplayUrl = null;
        tweet.id = jsonObject.getLong("id");
        JSONObject entities = jsonObject.getJSONObject("entities");
        if(entities.has("media")){
            JSONArray media = entities.getJSONArray("media");
            tweet.mediaDisplayUrl = media.getJSONObject(0).getString("media_url_https");
            Log.i("Tweety","got url");
        }
        return tweet;
    }

    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
        for(int i = 0; i < jsonArray.length();i++){
            tweets.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return tweets;
    }

    public long getId() {
        return id;
    }
}
