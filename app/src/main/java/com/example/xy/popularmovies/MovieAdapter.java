package com.example.xy.popularmovies;

import android.content.Context;
import android.os.Parcel;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private Context context;
    private ArrayList<MovieData> movieDataArrayList;

    public final MovieAdapterOnClickHandler mClickHandler;



    public MovieAdapter(Context context, ArrayList<MovieData> movieDataArrayList,MovieAdapterOnClickHandler mClickHandler){
        this.context = context;
        this.movieDataArrayList = movieDataArrayList;
        this.mClickHandler = mClickHandler;
    }

    public interface MovieAdapterOnClickHandler{
        void onClick(MovieData movieData);
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutId = R.layout.row_layout;

        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutId,parent,shouldAttachToParentImmediately);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {

//        Setting movie name
//        holder.tv_movie_name.setText(movieDataArrayList.get(position).getMovieName());

//        Setting image using Picasso
        Picasso.with(context)
                .load(movieDataArrayList.get(position).getMovieUrl())
                .into(holder.img_movie);


    }

    @Override
    public int getItemCount() {
        return movieDataArrayList.size();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView tv_movie_name;
        private ImageView img_movie;

        public MovieViewHolder(View itemView) {
            super(itemView);

//            tv_movie_name = (TextView) itemView.findViewById(R.id.tv_movie_name);
            img_movie = itemView.findViewById(R.id.img_movie);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            MovieData movieData = new MovieData(Parcel.obtain());

            String movieTitle = movieDataArrayList.get(adapterPosition).getMovieName();
            movieData.setMovieName(movieTitle);

            String movieImageUrl = movieDataArrayList.get(adapterPosition).getMovieUrl();
            movieData.setMovieUrl(movieImageUrl);

            String movieOverview = movieDataArrayList.get(adapterPosition).getOverview();
            movieData.setOverview(movieOverview);

            String userRating = movieDataArrayList.get(adapterPosition).getUserRating();
            movieData.setUserRating(userRating);

            String releaseDate = movieDataArrayList.get(adapterPosition).getReleaseDate();
            movieData.setReleaseDate(releaseDate);


            mClickHandler.onClick(movieData);

        }
    }
}
