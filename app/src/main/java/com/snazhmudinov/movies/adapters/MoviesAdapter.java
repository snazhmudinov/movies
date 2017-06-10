package com.snazhmudinov.movies.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.snazhmudinov.movies.R;
import com.snazhmudinov.movies.activities.MovieActivity;
import com.snazhmudinov.movies.constans.Constants;
import com.snazhmudinov.movies.models.Movie;
import com.squareup.picasso.Picasso;
import org.parceler.Parcels;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by snazhmudinov on 5/28/17.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieHolder> {

    private List<Movie> moviesList;
    private Context mContext;

    public MoviesAdapter(List<Movie> movies, Context context) {
        moviesList = movies;
        mContext = context;
    }

    @Override
    public MovieHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.movie_rv_item, parent, false);

        return new MovieHolder(view, mContext);
    }

    @Override
    public void onBindViewHolder(MovieHolder holder, int position) {
        Movie currentMovie = moviesList.get(position);

        Picasso.with(mContext)
                .load(Constants.POSTER_BASE_URL + currentMovie.getPosterPath())
                .into(holder.mPosterView);
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }

    class MovieHolder extends RecyclerView.ViewHolder {
        private Context context;

        @BindView(R.id.poster)
        ImageView mPosterView;

        MovieHolder(View itemView, Context context) {
            super(itemView);
            this.context = context;

            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.poster)
        void openMovieDetails() {
            Movie movie = moviesList.get(getAdapterPosition());

            Intent intent = new Intent(context, MovieActivity.class);
            intent.putExtra(Constants.MOVIE_KEY, Parcels.wrap(movie));
            context.startActivity(intent);
        }
    }
}
