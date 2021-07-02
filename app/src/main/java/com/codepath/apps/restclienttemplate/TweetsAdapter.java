package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.databinding.ItemTweetBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;

import java.util.List;


public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {

    private final Context context;
    private final List<Tweet> tweets;

    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Tweet> list) {
        tweets.addAll(list);
        notifyDataSetChanged();
    }

    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final ItemTweetBinding itemTweetBinding = ItemTweetBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(itemTweetBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Tweet tweet = tweets.get(position);
        holder.bind(tweet);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemTweetBinding itemTweetBinding;

        public ViewHolder(@NonNull ItemTweetBinding tweetBinding) {
            super(tweetBinding.getRoot());
            itemTweetBinding = tweetBinding;
        }

        public void bind(Tweet tweet) {
            itemTweetBinding.tvBody.setText(tweet.body);
            itemTweetBinding.tvScreenName.setText(tweet.user.name);
            final String screenNameWithAt = "@" + tweet.user.screenName;
            itemTweetBinding.tvUserName.setText(screenNameWithAt);
            itemTweetBinding.tvRelativeTime.setText(ParseRelativeDate.getRelativeTimeAgo(tweet.createdAt));
            Glide.with(context).load(tweet.user.profileImageUrl).circleCrop().into(itemTweetBinding.ivProfileImage);
            setTweetMedia(itemTweetBinding.ivTweetMedia, tweet);
        }
    }

    private void setTweetMedia(ImageView ivTweetMedia, Tweet tweet) {
        if (tweet.mediaDisplayUrl != null) {
            ivTweetMedia.setVisibility(View.VISIBLE);
            Glide.with(context).load(tweet.mediaDisplayUrl).into(ivTweetMedia);
        } else {
            ivTweetMedia.setVisibility(View.GONE);
        }
    }
}
