package com.example.xy.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;




public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {

    public static ArrayList<MovieData> popularMoviesList;
    public static RecyclerView recyclerView;
    public static MovieAdapter movieAdapter;

    private static ProgressBar mProgressBar;


    public static final String POPULAR_MOVIES_PARAM = "popular";


    public static final String TOP_RATED_MOVIES_PARAM = "top_rated";

    Snackbar snackbar;

    TextView offlineTextView;

    private Toast offlineToast;
    private static Toast noResultToast;
    private Toast noConnectionToast;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        offlineTextView = findViewById(R.id.tv_no_internet_connection);

        mProgressBar = findViewById(R.id.mProgressBar);
        recyclerView = findViewById(R.id.card_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(layoutManager);

        popularMoviesList = new ArrayList<>();
        movieAdapter = new MovieAdapter(getApplicationContext(),popularMoviesList,this);

        snackbar = Snackbar.make(findViewById(R.id.content),
                R.string.offlineText, Snackbar.LENGTH_INDEFINITE);

        offlineToast = Toast.makeText(getApplicationContext(),
                getString(R.string.reconnectString),
                Toast.LENGTH_LONG);

        noResultToast = Toast.makeText(getApplicationContext(), R.string.noResults,Toast.LENGTH_LONG);

        noConnectionToast = Toast.makeText(getApplicationContext(), R.string.noConnectivityManager,Toast.LENGTH_LONG);

        startWorking();

    }


    public void startWorking(){
        if (appIsConnectedToTheInternet()){
            if (snackbar.isShownOrQueued()){
                snackbar.dismiss();
            }
            offlineTextView.animate().alpha(0f).setDuration(2000);
            offlineTextView.setVisibility(View.INVISIBLE);

            new DownloadMovieDataTask().execute(NetworkUtils.getUrl(POPULAR_MOVIES_PARAM).toString());

        } else{

            offlineTextView.setVisibility(View.VISIBLE);
            offlineTextView.animate().alphaBy(1f).setDuration(2000);

            snackbar.setActionTextColor(ContextCompat.getColor(getApplicationContext(),
                    R.color.brightRandomColor));

            snackbar.setAction(R.string.try_again, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Re-Check if internet connection is there and if yes, call async task
                    startWorking();
                }
            }).show();
        }


    }


    public boolean appIsConnectedToTheInternet(){
        ConnectivityManager connectivityManager =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork;
        boolean isConnected = false;

        if (connectivityManager != null) {
            activeNetwork = connectivityManager.getActiveNetworkInfo();
            isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        } else{
            noConnectionToast.show();
        }


        return isConnected;

    }


    @Override
    public void onClick(MovieData movieData) {
//        Toast.makeText(this,movieTitle + "clicked!",Toast.LENGTH_LONG).show();

        Class destinationClass = DetailActivity.class;
        Intent launchDetailedScreenIntent = new Intent(this,destinationClass);

        launchDetailedScreenIntent.putExtra("movie_data",movieData);


        startActivity(launchDetailedScreenIntent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        String itemTitle = item.getTitle().toString();
        Log.i("itemTitle",itemTitle);


            if (itemId == R.id.sort_by){
                if (itemTitle.equals(getString(R.string.top_rated_movies))){
                    if (appIsConnectedToTheInternet()){
                        new DownloadMovieDataTask().execute(NetworkUtils.getUrl(TOP_RATED_MOVIES_PARAM).toString());
                        offlineTextView.animate().alpha(0f).setDuration(2000);
                        offlineTextView.setVisibility(View.INVISIBLE);
                        if (snackbar.isShownOrQueued()){
                            snackbar.dismiss();
                        }
                        item.setTitle(R.string.popular_movies);


                    } else{
                        offlineToast.show();
                    }

                } else if (itemTitle.equals(getString(R.string.popular_movies))){
                    if (appIsConnectedToTheInternet()){
                        new DownloadMovieDataTask().execute(NetworkUtils.getUrl(POPULAR_MOVIES_PARAM).toString());
                        offlineTextView.animate().alpha(0f).setDuration(2000);
                        offlineTextView.setVisibility(View.INVISIBLE);
                        if (snackbar.isShownOrQueued()){
                            snackbar.dismiss();
                        }
                        item.setTitle((R.string.top_rated_movies));
                    } else{
                        offlineToast.show();
                    }

                }


                return true;
            }


        return super.onOptionsItemSelected(item);
    }

    static class DownloadMovieDataTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);

                int data = reader.read();

                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }

                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            mProgressBar.setVisibility(View.INVISIBLE);

            if (result != null) {

                try {

                    JSONObject jsonObject = new JSONObject(result);

                    JSONArray results = jsonObject.getJSONArray("results");

                    if (!popularMoviesList.isEmpty()) {
                        popularMoviesList.clear();
                    }

                    for (int i = 0; i < results.length(); i++) {
                        JSONObject object = null;

                        object = results.getJSONObject(i);

                        String title = object.getString("title");

                        String posterPartUrl = object.getString("poster_path");
                        String actualImageUrlString = "https://image.tmdb.org/t/p/w185//" + posterPartUrl;

                        String overview = object.getString("overview");
                        String userRating = object.getString("vote_average");
                        String releaseDate = object.getString("release_date");


                        MovieData movie = new MovieData(Parcel.obtain());
                        movie.setMovieName(title);
                        movie.setMovieUrl(actualImageUrlString);
                        movie.setOverview(overview);
                        movie.setUserRating(userRating);
                        movie.setReleaseDate(releaseDate);


                        popularMoviesList.add(movie);

                    }


                    recyclerView.setAdapter(movieAdapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else{
//                If results returned were null, show toast.
                noResultToast.show();
            }

        }
    }

}


/*
if (item.getTitle().toString().equals(R.string.top_rated_movies)){
                item.setTitle(R.string.popular_movies);
                new DownloadMovieDataTask().execute(topRatedMoviesUrl);



            } else if (item.getTitle().toString().equals(R.string.popular_movies)){
                item.setTitle(R.string.top_rated_movies);
                new DownloadMovieDataTask().execute(popularMoviesUrl);

            }
 */



