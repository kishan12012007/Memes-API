package com.example.memeshare;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.json.JSONException;
import org.json.JSONObject;



public class MainActivity extends AppCompatActivity {

    // Meme Image View
    ImageView memeImageView;

    // Progress Bar View
    ProgressBar progressBar;

    // Reddit meme image url
    String memeImageUrl;

    // Share Button View
    Button shareButton;

    // Next Button View
    Button nextButton;

    // Reddit meme NSFW Value (True/False)
    String nsfwValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Define ActionBar object
        ActionBar actionBar;
        actionBar = getSupportActionBar();

        // Define ColorDrawable object and parse color
        // using parseColor method
        // with color hash code as its parameter
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#BD0101"));

        // Set BackgroundDrawable
        assert actionBar != null;
        actionBar.setBackgroundDrawable(colorDrawable);

        // Find the ImageView with view ID memeImageView
        memeImageView = findViewById(R.id.memeImageView);

        // Find the ProgressBar with view ID progressBar
        progressBar = findViewById(R.id.progressBar);

        // Find the Button with view ID shareButton
        shareButton = findViewById(R.id.shareButton);

        // Find the Button with view ID nextButton
        nextButton = findViewById(R.id.nextButton);

        // Calling the loadMeme Function to initiate the first meme on screen
        loadMeme();

        // Applying On click Listener on Share Button
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, "Hey Checkout this Meme\n" + memeImageUrl);
                startActivity((Intent.createChooser(intent,"Share this meme using.....")));
            }
        });

        // Applying On click Listener on Next Button
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Setting the ProgressBar to VISIBLE
                progressBar.setVisibility(View.VISIBLE);

                // Calling the loadMeme function to display a new meme on the screen
                loadMeme();
            }
        });

    }


    private void checkInternetConnection(){
        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if(networkInfo != null && networkInfo.isConnected()){
            // Calling the loadMeme function to display a new meme on the screen
            loadMeme();
        }
        else{
            // Setting the ProgressBar to Invisible/Gone
            progressBar.setVisibility(View.GONE);

            // Setting the Meme Image to Invisible
            memeImageView.setVisibility(View.INVISIBLE);

            // Setting the Share Button to Enable == False i.e not working, if their is no Internet Connectivity
            shareButton.setEnabled(false);

            // Displaying a Toast Message with ERROR "No Internet Connection"
            Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    public void loadMeme(){
        // URL for getting random Reddit memes
        String MEME_URL = "https://meme-api.herokuapp.com/gimme";

//        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
//        progressBar.setVisibility(View.GONE);

        // Request a object response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, MEME_URL,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    // Setting the Meme Image to Invisible
                    memeImageView.setVisibility(View.VISIBLE);

                    // Setting the Share Button to Enable == True i.e working, so that the user can share the meme as soon as the Image is Fetched from the Json Object
                    shareButton.setEnabled(true);

                    // Getting the Meme Image URL String form the JSON OBJECT Response
                    memeImageUrl = response.getString("url");

                    // Getting the NSFW Value form the JSON OBJECT Response
                    nsfwValue = response.getString("nsfw");

                    // LOG TAG URL Meme Image URL
                    Log.d("URL", memeImageUrl);

                    // Using the Glide Library to load the meme image on the Screen with Listener to add Progress Bar on the delay of loading Image
                    Glide.with(MainActivity.this).load(memeImageUrl).listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            // Setting the ProgressBar to Invisible/Gone when then meme image is being load
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            // Setting the ProgressBar to Invisible/Gone when the meme image is ready to be displayed on the Screen
                            progressBar.setVisibility(View.GONE);
//                            if (nsfwValue.equals("false")){
//
//                            }
                            return false;
                        }
                    }).into(memeImageView);
                } catch (JSONException e) {
                    // LOG TAG Statement "Something Went Wrong While Fetching Data"
                    Log.d("Error", "Something Went Wrong While Fetching Data");

                    // Setting the Share Button to Enable == False i.e not working, if their is no Internet Connectivity
                    shareButton.setEnabled(false);

                    // Toast Message if something went wrong while Fetching the Data from Json Object
                    Toast.makeText(MainActivity.this, "Something Went Wrong While Fetching Data", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Calling the checkInternetConnection function to check if the Internet Connectivity is their or not
                checkInternetConnection();
            }
        });

        // Add a request (in this example, called stringRequest) to your RequestQueue.
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);

        // Other Method to add JsonObject to the Queue
//        queue.add(jsonObjectRequest);
    }

}