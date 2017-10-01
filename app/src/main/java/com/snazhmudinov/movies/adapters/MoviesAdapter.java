package com.snazhmudinov.movies.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.facebook.drawee.view.SimpleDraweeView;
import com.snazhmudinov.movies.R;
import com.snazhmudinov.movies.constans.Constants;
import com.snazhmudinov.movies.models.Movie;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by snazhmudinov on 5/28/17.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieHolder> {

    public interface MovieInterface {
        void onMovieSelected(Movie movie, boolean isLocalImage);
    }

    private List<Movie> moviesList;
    private Context mContext;
    private boolean isLocalImage = false;
    public MovieInterface movieInterface;

    public MoviesAdapter(List<Movie> movies, Context context) {
        moviesList = movies;
        mContext = context;
    }

    @Override
    public MovieHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.movie_rv_item, parent, false);

        return new MovieHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieHolder holder, int position) {
        Movie currentMovie = moviesList.get(position);
        Uri uri = isLocalImage ? Uri.parse(currentMovie.getSavedFilePath()) :
                Uri.parse(Constants.POSTER_BASE_URL + currentMovie.getPosterPath());

        holder.mPosterView.setImageURI(uri);
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }

    class MovieHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.poster)
        SimpleDraweeView mPosterView;

        MovieHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.poster)
        void openMovieDetails() {
            Movie movie = moviesList.get(getAdapterPosition());
            if (movieInterface != null) {
                movieInterface.onMovieSelected(movie, isLocalImage);
            }
        }
    }

    public void setLocalImage(boolean value) {
        isLocalImage = value;
    }
}
