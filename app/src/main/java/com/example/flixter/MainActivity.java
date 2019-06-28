package com.example.flixter;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flixter.models.Config;
import com.example.flixter.models.Movie;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    // constants
    // the base URL for the API
    public final static String API_BASE_URL = "https://api.themoviedb.org/3";

    //API_KEY parameter name

    public final static String API_KEY_PARAM = "api_key";


    //tag for logging this activity


    public final static String TAG = "MovieListActivity";
    // instance fields
    AsyncHttpClient client;

//    //base url for loading images
//
//    String imageBaseUrl;
//    //poster size to use when getting images
//    String posterSize;

    //track the list of currently playing movies
    ArrayList<Movie> movies;

    // the recycler view

    @BindView(R.id.rvMovies) RecyclerView rvMovies;

    // the adapter wired to the recycler view
    MovieAdapter adapter;


    //image configuration
    Config config ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //init the client
        client = new AsyncHttpClient();

        //init the list of movies
        movies = new ArrayList<>();

        adapter = new MovieAdapter(movies);

        // resolve the recycler view and connect a layout
//        rvMovies = findViewById(R.id.rvMovies);
        ButterKnife.bind(MainActivity.this);

        rvMovies.setLayoutManager(new LinearLayoutManager(this));
        rvMovies.setAdapter(adapter);



        //get the configuration for the app creation
        getConfiguration();

//        // get the Now playing movie list
//        getNowPlaying();

    }


    private void getNowPlaying(){
        //create the url
        String url = API_BASE_URL + "/movie/now_playing";

        // set the request parameters

        RequestParams params = new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.api_key));

        client.get(url,params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //loads the result into movie
                try {
                    JSONArray results = response.getJSONArray("results");

                    // pass every movie into the list

                    for (int i = 0;i <results.length(); i++){
                        Movie movie = new Movie(results.getJSONObject(i));
                        movies.add(movie);

                        // notify adapter that a row was added
                        adapter.notifyItemInserted(movies.size() - 1);

                    }

                    Log.i(TAG, String.format("Loaded %s movies", results.length()));
                } catch (JSONException e) {
                    logError("Failed to parse the data from now playing", e,true);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                logError("Failed to get the data from now playing", throwable,true);
            }
        });


    }

    private void getConfiguration(){
        //create the url
        String url = API_BASE_URL + "/configuration";

        // set the request parameters

        RequestParams params = new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.api_key));

        // call Get request and expect a JSON object
        client.get(url, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //get image base url

                try {
//                    JSONObject images = response.getJSONObject("images");
//
//                    imageBaseUrl = images.getString("secure_base_url");
//                    JSONArray posterSizeOptions = images.getJSONArray("poster_sizes");
//                    //use option at index 3
//
//                    posterSize = posterSizeOptions.optString(3, "w342");

                    // get the configuration information from the new class
                    config = new Config(response);


                    Log.i(TAG,String.format("Loaded configuration with imageBaseURL %s and posterSize %s"
                            , config.getImageBaseUrl(),
                            config.getPosterSize()));

                    // pass config to the adapter
                    adapter.setConfig(config);

                    // get the Now playing movie list


                    getNowPlaying();
                } catch (JSONException e) {
                    logError("Parsing Error", e, true);
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                logError("Failed getting configuration", throwable, true);
            }
        });

    }


    // handle errors, log and alert user
    private void logError(String message,Throwable error, boolean alertUser){
        Log.e(TAG, message, error);


        if (alertUser){
            // show a long toast with error message

            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
    }
}
