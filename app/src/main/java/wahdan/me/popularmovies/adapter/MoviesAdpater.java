package wahdan.me.popularmovies.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import wahdan.me.popularmovies.R;
import wahdan.me.popularmovies.db.MovieDb;
import wahdan.me.popularmovies.helper.onMovieSelectionChange;
import wahdan.me.popularmovies.model.MovieModel;

/**
 * Created by ahmedwahdan on 12/16/15.
 */
public class MoviesAdpater extends RecyclerView.Adapter<MoviesAdpater.MovieviewHolder> {
    final static String movieurl = "http://image.tmdb.org/t/p/w185";
    MovieDb mMovieDb;
    ArrayList<MovieModel> movieList;
    Context mContext;

    public MoviesAdpater(Context context, ArrayList<MovieModel> movieList, MovieDb movieDb) {
        mMovieDb = movieDb;
        this.movieList = movieList;
        this.mContext = context;
    }

    @Override
    public MovieviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_list_item, null);
        MovieviewHolder movieviewHolder = new MovieviewHolder(view);

        return movieviewHolder;
    }

    public void updateMovieList(ArrayList<MovieModel> data) {

        movieList = data;
        notifyItemRangeChanged(0, movieList.size());
    }

    @Override
    public void onBindViewHolder(final MovieviewHolder holder, final int position) {
        final MovieModel movieModel = mMovieDb.getMovie(movieList.get(position).getId());
        if (movieModel != null) {
            holder.favoriteImage.setImageResource(R.drawable.ic_favorite_pressed);
        }
        Log.d("MoviesAdpater", "position:" + position);
        Log.d("MoviesAdpater", movieurl + (movieList.get(position).getPosterPath()));
        holder.movieTitle.setText(movieList.get(position).getTitle());
        Picasso.with(mContext).load(movieurl + (movieList.get(position).getPosterPath())).into(holder.movieImage);
        holder.favoriteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (movieModel == null) {
                    mMovieDb.addMovie(movieList.get(position));
                    holder.favoriteImage.setImageResource(R.drawable.ic_favorite_pressed);

                } else {
                    mMovieDb.deleteMovie(movieList.get(position));
                    holder.favoriteImage.setImageResource(R.drawable.ic_favorite_normal);

                }
            }
        });


    }


    @Override
    public int getItemCount() {
        return movieList.size();
    }

    class MovieviewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView movieTitle;
        ImageView movieImage;
        ImageView favoriteImage;

        public MovieviewHolder(View itemView) {

            super(itemView);
            movieImage = (ImageView) itemView.findViewById(R.id.movie_image);
            movieTitle = (TextView) itemView.findViewById(R.id.movie_title);
            favoriteImage = (ImageView) itemView.findViewById(R.id.favorite_button);
            movieImage.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {

            if (v.getId() == R.id.movie_image) {
                Toast.makeText(mContext, " onClick : ", Toast.LENGTH_SHORT).show();
                onMovieSelectionChange listener = (onMovieSelectionChange) mContext;
                listener.OnSelectionChanged(movieList.get(getAdapterPosition()));
            }

        }

    }
}

