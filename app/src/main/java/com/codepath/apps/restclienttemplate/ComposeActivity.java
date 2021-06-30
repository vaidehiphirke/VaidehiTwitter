package com.codepath.apps.restclienttemplate;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.codepath.apps.restclienttemplate.databinding.ActivityComposeBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.google.android.material.textfield.TextInputLayout;

public class ComposeActivity extends AppCompatActivity {

    private static final String TAG = "ComposeActivity";

    EditText composeText;
    Button composeButton;
    TextInputLayout composeTextLayout;
    TwitterClient client;

    ActivityComposeBinding composeBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        composeBinding = ActivityComposeBinding.inflate(getLayoutInflater());
        setContentView(composeBinding.getRoot());

        client = TwitterApplication.getRestClient(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.compose_label));

        composeTextLayout = composeBinding.layoutEtCompose;
        composeText = composeBinding.etCompose;
        composeButton = composeBinding.btnCompose;

        composeTextLayout.setCounterMaxLength(Tweet.MAX_TWEET_LENGTH);

        composeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tweetText = composeText.getText().toString();
                if (tweetText.length() == 0) {
                    Toast.makeText(ComposeActivity.this, getString(R.string.compose_error_empty), Toast.LENGTH_SHORT).show();
                } else if (tweetText.length() > Tweet.MAX_TWEET_LENGTH) {
                    Toast.makeText(ComposeActivity.this, getString(R.string.compose_error_long), Toast.LENGTH_SHORT).show();
                } else {
                    //todo
                }
            }
        });
    }
}
