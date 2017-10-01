package com.snazhmudinov.movies.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.snazhmudinov.movies.R;
import com.snazhmudinov.movies.constans.Constants;
import com.snazhmudinov.movies.interfaces.MovieInterface;
import com.snazhmudinov.movies.models.Movie;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by snazhmudinov on 5/28/17.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieHolder> {

    private List<Movie> moviesList;
    private Context mContext;
    private boolean isLocalImage = false;
    public MovieInterface movieInterface;

    public static final int GRID_MODE = 0;
    public static final int LIST_MODE = 1;
    private int mMode = 0;
    private int mCurrentSelection = 0;

    public MoviesAdapter(List<Movie> movies, Context context) {
        moviesList = movies;
        mContext = context;
    }

    @Override
    public MovieHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = null;
        switch(viewType) {
            case GRID_MODE:
                view = inflater.inflate(R.layout.movie_grid_item, parent, false);
                break;

            case LIST_MODE:
                view = inflater.inflate(R.layout.movie_list_item, parent, false);
                break;
        }


        return new MovieHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieHolder holder, int position) {
        Movie currentMovie = moviesList.get(position);
        Uri uri = isLocalImage ? Uri.parse(currentMovie.getPosterPath()) :
                Uri.parse(Constants.POSTER_BASE_URL + currentMovie.getPosterPath());

        holder.mPosterView.setImageURI(uri);

        if (mMode == LIST_MODE) {
            final TextView tv = (TextView) holder.itemView.findViewById(R.id.movie_title);
            tv.setText(moviesList.get(position).getTitle());
            holder.itemView.setSelected(position == mCurrentSelection);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mMode;
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }

    class MovieHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.poster)
        SimpleDraweeView mPosterView;

        MovieHolder(final View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Movie movie = moviesList.get(getAdapterPosition());
                    if (movieInterface != null) {
                        mCurrentSelection = moviesList.indexOf(movie);
                        movieInterface.onMovieSelected(movie, isLocalImage);
                        notifyDataSetChanged();
                    }
                }
            });
        }
    }

    public void setLocalImage(boolean value) {
        isLocalImage = value;
    }

    public void setMode(int mode) {
        mMode = mode;
    }

    public void setSelectionIndex(int index) {
        mCurrentSelection = index;
    }
}
