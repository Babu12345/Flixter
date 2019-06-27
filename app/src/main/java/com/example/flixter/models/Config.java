package com.example.flixter.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Config {


    //base url for loading images

    String imageBaseUrl;
    //poster size to use when getting images
    String posterSize;

    // the backdrop size to use when fethcing images

    String backdropSize;


    public Config(JSONObject object) throws JSONException {

        JSONObject images = object.getJSONObject("images");

        imageBaseUrl = images.getString("secure_base_url");
        JSONArray posterSizeOptions = images.getJSONArray("poster_sizes");
        //use option at index 3

        posterSize = posterSizeOptions.optString(3, "w342");

        JSONArray backdropSizeOptions = images.getJSONArray("backdrop_sizes");
        backdropSize = backdropSizeOptions.optString(1,"w780");


    }

    //helper method to construct the URL

    public String getImageUrl(String size, String path){

        return String.format("%s%s%s", imageBaseUrl, size, path);// Add all three elements
    }

    public String getImageBaseUrl() {
        return imageBaseUrl;
    }

    public String getPosterSize() {
        return posterSize;
    }

    public String getBackdropSize() {
        return backdropSize;
    }
}
