package com.example.xy.popularmovies;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    private TextView movieTitle,userRatingValue,movieReleaseDate,movieOverview;
    private ImageView moviePoster;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        moviePoster = findViewById(R.id.moviePosterImageView);
        movieTitle = findViewById(R.id.movieTitleTextView);

        userRatingValue = findViewById(R.id.movieRatingValueTextView);
        movieReleaseDate = findViewById(R.id.releaseDateValueTextView);

        movieOverview = findViewById(R.id.movieOverviewValueTextView);



        Intent intentThatInvokedThisActivity = getIntent();

        if (intentThatInvokedThisActivity.hasExtra("movie_data")){

            MovieData movieData = (MovieData) intentThatInvokedThisActivity.getParcelableExtra("movie_data");
            setupUI(movieData);
        }
    }

    private void setupUI(MovieData movieData){
        Picasso.with(this)
                .load(movieData.getMovieUrl())
                .resize(410,690)
                .into(moviePoster);

        movieTitle.setText(movieData.getMovieName());

        userRatingValue.setText(movieData.getUserRating());

        movieReleaseDate.setText(movieData.getReleaseDate());

        movieOverview.setText(movieData.getOverview());

    }
}
