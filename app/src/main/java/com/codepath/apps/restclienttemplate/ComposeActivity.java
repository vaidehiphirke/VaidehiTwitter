package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.codepath.apps.restclienttemplate.databinding.ActivityComposeBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {

    public static final int COMPOSE_REQUEST_CODE = 20;
    private static final String TAG = "ComposeActivity";

    private EditText composeText;
    private Button composeButton;
    private TextInputLayout composeTextLayout;
    private TwitterClient client;

    private ActivityComposeBinding composeBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        composeBinding = ActivityComposeBinding.inflate(getLayoutInflater());
        setContentView(composeBinding.getRoot());

        client = TwitterApplication.getRestClient(this);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.compose_label));
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff1da1f2")));

        composeTextLayout = composeBinding.layoutEtCompose;
        composeText = composeBinding.etCompose;
        composeButton = composeBinding.btnCompose;

        composeTextLayout.setCounterMaxLength(Tweet.MAX_TWEET_LENGTH);

        composeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleTweetPublishing();
            }
        });
    }

    private void handleTweetPublishing() {
        final String tweetText = composeText.getText().toString();
        if (tweetText.length() == 0) {
            Toast.makeText(ComposeActivity.this, getString(R.string.compose_error_empty), Toast.LENGTH_SHORT).show();
        } else if (tweetText.length() > Tweet.MAX_TWEET_LENGTH) {
            Toast.makeText(ComposeActivity.this, getString(R.string.compose_error_long), Toast.LENGTH_SHORT).show();
        } else {
            client.publishTweet(tweetText, new TweetJsonHttpResponseHandler());
        }
    }

    private class TweetJsonHttpResponseHandler extends JsonHttpResponseHandler {
        @Override
        public void onSuccess(int statusCode, Headers headers, JSON json) {
            Log.i(TAG, "onSuccess publishing tweet");
            try {
                final Tweet tweet = Tweet.fromJson(json.jsonObject);
                Log.i(TAG, "Published tweet says " + tweet.body);
                final Intent intent = new Intent();
                intent.putExtra("tweet", Parcels.wrap(tweet));
                setResult(RESULT_OK, intent);
                finish();
            } catch (JSONException e) {
                Log.i(TAG, "Published tweet says " + e);
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
            Log.i(TAG, "onFailure publishing tweet" + response, throwable);
        }

    }
}
