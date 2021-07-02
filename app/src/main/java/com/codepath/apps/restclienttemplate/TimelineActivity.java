package com.codepath.apps.restclienttemplate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.codepath.apps.restclienttemplate.databinding.ActivityTimelineBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.TweetDao;
import com.codepath.apps.restclienttemplate.models.TweetWithUser;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity {

    public static final String TAG = "TimelineActivity";

    private TwitterClient client;
    private RecyclerView rvTweets;
    private List<Tweet> tweets;
    private TweetsAdapter adapter;
    private TweetDao tweetDao;

    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActivityTimelineBinding timelineBinding = ActivityTimelineBinding.inflate(getLayoutInflater());
        setContentView(timelineBinding.getRoot());

        client = TwitterApplication.getRestClient(this);

        tweetDao = ((TwitterApplication) getApplicationContext()).getMyDatabase().tweetDao();

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("  Your Timeline");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setLogo(R.drawable.twitter_32);
        actionBar.setDisplayUseLogoEnabled(true);

        rvTweets = timelineBinding.rvTweets;
        tweets = new ArrayList<>();
        adapter = new TweetsAdapter(this, tweets);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvTweets.setLayoutManager(linearLayoutManager);
        rvTweets.setAdapter(adapter);

        // swipe to refresh
        swipeContainer = timelineBinding.swipeContainer;
        swipeContainer.setOnRefreshListener(this::populateHomeTimeline);
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        // infinite scroll
        final EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                client.getMoreTweets(tweets.get(tweets.size() - 1).getId(), new LoadTweetsJsonHttpResponseHandler());
            }
        };
        rvTweets.addOnScrollListener(scrollListener);

        // query for existing tweets in DB
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "Showing data from database");
                List<TweetWithUser> tweetWithUsers = tweetDao.recentItems();
                List<Tweet> tweetsFromDB = TweetWithUser.getTweetList(tweetWithUsers);
                adapter.clear();
                adapter.addAll(tweetsFromDB);
            }
        });

        populateHomeTimeline();
    }

    private class LoadTweetsJsonHttpResponseHandler extends JsonHttpResponseHandler {
        @Override
        public void onSuccess(int statusCode, Headers headers, JSON json) {
            final JSONArray jsonArray = json.jsonArray;
            try {
                adapter.addAll(Tweet.fromJsonArray(jsonArray));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
            Log.i(TAG, "loadMoreTweets onFailure");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.compose) {
            final Intent intent = new Intent(this, ComposeActivity.class);
            startActivityForResult(intent, ComposeActivity.COMPOSE_REQUEST_CODE);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == ComposeActivity.COMPOSE_REQUEST_CODE && resultCode == RESULT_OK) {
            final Tweet tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));
            tweets.add(0, tweet);
            adapter.notifyItemInserted(0);
            rvTweets.smoothScrollToPosition(0);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void populateHomeTimeline() {
        client.getHomeTimeline(new HomeTimelineJsonHttpResponseHandler());
    }

    private class HomeTimelineJsonHttpResponseHandler extends JsonHttpResponseHandler {
        @Override
        public void onSuccess(int statusCode, Headers headers, JSON json) {
            Log.i(TAG, "onSuccess");
            final JSONArray jsonArray = json.jsonArray;
            try {
                adapter.clear();
                final List<Tweet> tweetsFromNetwork = Tweet.fromJsonArray(jsonArray);
                tweets.addAll(tweetsFromNetwork);
                adapter.notifyDataSetChanged();
                swipeContainer.setRefreshing(false);
                savingDataIntoDatabase(tweetsFromNetwork);
            } catch (JSONException e) {
                Log.e(TAG, "Json exception", e);
            }
        }

        @Override
        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
            Log.i(TAG, "onFailure home timeline" + throwable, throwable);
        }
    }


    private void savingDataIntoDatabase(List<Tweet> tweetsFromNetwork) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "Saving data into database");
                final List<User> usersFromNetwork = User.fromJsonTweetArray(tweetsFromNetwork);
                tweetDao.insertModel(usersFromNetwork.toArray(new User[0]));
                tweetDao.insertModel(tweetsFromNetwork.toArray(new Tweet[0]));
            }
        });
    }

    public void onLogoutButton(View view) {
        client.clearAccessToken(); // forget who's logged in
        final Intent i = new Intent(this, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }
}