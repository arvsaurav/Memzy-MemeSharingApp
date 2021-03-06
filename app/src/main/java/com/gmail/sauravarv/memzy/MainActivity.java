package com.gmail.sauravarv.memzy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.service.chooser.ChooserTarget;
import android.service.chooser.ChooserTargetService;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity
{
    ImageView memeImage;
    Button nextButton, shareButton;
    ProgressBar progressBar;
    // We have added memeImageUrl because when we will share meme then we have to share this url and we are going to use this url outside loadMeme method.
    String memeImageUrl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        memeImage = (ImageView)findViewById(R.id.memeImage);
        nextButton = (Button)findViewById(R.id.nextButton);
        shareButton = (Button)findViewById(R.id.shareButton);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        loadMeme();
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                loadMeme();
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //here we will use implicit intent.
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, "Hey! Check out this meme "+memeImageUrl);
                Intent shareIntent = Intent.createChooser(i, "Share this meme using...");
                startActivity(shareIntent);
            }
        });

    }

    private void loadMeme()
    {
        progressBar.setVisibility(View.VISIBLE);
        String url ="https://meme-api.herokuapp.com/gimme";
        // Request a json object response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        try
                        {
                            //storing memeImage url
                            memeImageUrl = response.getString("url");

                            //we are adding listner to glide so that we can set the visibility of progress bar.
                            Glide.with(MainActivity.this).load(memeImageUrl).listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource)
                                {
                                    progressBar.setVisibility(View.GONE);
                                    Toast toast = Toast.makeText(MainActivity.this, "An error occurred!", Toast.LENGTH_LONG);
                                    toast.setGravity(Gravity.CENTER,0, 0);
                                    toast.show();
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource)
                                {
                                    progressBar.setVisibility(View.GONE);
                                    return false;
                                }
                            }).into(memeImage);

                        }
                        catch (JSONException e)
                        {
                            Toast toast = Toast.makeText(MainActivity.this, "ERROR!", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER,0, 0);
                            toast.show();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Toast toast = Toast.makeText(MainActivity.this, "No data received from the server!", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER,0, 0);
                        toast.show();
                    }
                });
        // Adding request using singleton pattern
        //Using Companion as MySingletonK is a kotlin class file.
        MySingletonK.Companion.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    @Override
    public void onBackPressed()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Really Exit?").setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                MainActivity.super.onBackPressed();
            }
        }).setNegativeButton("Cancel", null)
        .setCancelable(false);
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.about:
                Intent i = new Intent(MainActivity.this, about.class);
                startActivity(i);
                return true;

            case R.id.contact:
                Intent j = new Intent(MainActivity.this, contact.class);
                startActivity(j);
                return true;
        }
        return true;
    }
}
