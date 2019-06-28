package com.example.flixter;

import android.os.Bundle;
import android.util.Log;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.flixter.models.Movie;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;


public class MovieDetailsActivity extends YouTubeBaseActivity {

    Movie movie;

    @Nullable @BindView(R.id.tvTitle) TextView tvTitle;
    @Nullable @BindView(R.id.tvOverview) TextView tvOverview;
    @Nullable @BindView(R.id.rbVoteAverage) RatingBar rbVoteAverage;
    String trailerID;

    AsyncHttpClient client;
    YouTubePlayerView playerView;



    @Override
    protected void onCreate(Bundle savedInstanceState){


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        ButterKnife.bind(this);

//        tvTitle = (TextView) findViewById(R.id.tvTitle);
//        tvOverview = (TextView) findViewById(R.id.tvOverview);
//        rbVoteAverage = (RatingBar) findViewById(R.id.rbVoteAverage);

        // unwrap the movie passed in via intent using it's simple name as a key

        movie = (Movie) Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));
        String movieName = movie.getTitle();


        Log.d("MovieDetailsActivity", String.format("Showing details for '%s'", movie.getTitle()));



        // unwrap the movie passed in via intent, using its simple name as a key
        movie = (Movie) Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));
        Log.d("MovieDetailsActivity", String.format("Showing details for '%s'", movie.getTitle()));

        // set the title and overview
        tvTitle.setText(movie.getTitle());
        tvOverview.setText(movie.getOverview());

        // vote average is 0..10, convert to 0..5 by dividing by 2
        float voteAverage = movie.getVoteAverage().floatValue();
        rbVoteAverage.setRating(voteAverage = voteAverage > 0 ? voteAverage / 2.0f : voteAverage);

        Toast.makeText(MovieDetailsActivity.this, String.format("Accessing Movie details for %s", movieName), Toast.LENGTH_SHORT).show();


        //---------------------


        String url = "https://api.themoviedb.org/3" + "/movie/" + movie.getId() + "/videos";

        // set the request parameters

        RequestParams params = new RequestParams();
        params.put("api_key", getString(R.string.api_key));

        client = new AsyncHttpClient();

        client.get(url,params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //loads the result into movie
                try {
                    JSONArray results = response.getJSONArray("results");
                    trailerID = results.getJSONObject(0).getString("key");


                    getVideo();


                } catch (JSONException e) {

                    trailerID = "2";
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                trailerID = "3";

            }
        });





    }

    private void getVideo() {
        Toast.makeText(MovieDetailsActivity.this, String.format("TrailerID is %s", trailerID), Toast.LENGTH_SHORT).show();

        // resolve the player view from the layout
        playerView = (YouTubePlayerView) findViewById(R.id.player);

        // initialize with API key stored in secrets.xml
        //Uses the youtube API key
        playerView.initialize(getString(R.string.api_key2), new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                YouTubePlayer youTubePlayer, boolean b) {
                // do any work here to cue video, play video, etc.
                youTubePlayer.cueVideo(trailerID);
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                YouTubeInitializationResult youTubeInitializationResult) {
                // log the error
                Log.e("MovieTrailerActivity", "Error initializing YouTube player");
            }
        });
    }


}
