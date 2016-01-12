package wahdan.me.popularmovies.fragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import wahdan.me.popularmovies.R;
import wahdan.me.popularmovies.adapter.MoviesAdpater;
import wahdan.me.popularmovies.db.MovieDb;
import wahdan.me.popularmovies.helper.OnSortMoviesChange;
import wahdan.me.popularmovies.model.MovieModel;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements OnSortMoviesChange {
    public static final String DB_NAEM = "MOVIES";
    LinearLayoutManager mLayoutManager;
    String resalt;
    MoviesAdpater moviesAdpater;
    ArrayList<MovieModel> m;
    MovieDb mMovieDb;

    public MainActivityFragment() {

    }

    public static MainActivityFragment newInstance(ArrayList<MovieModel> m) {
        MainActivityFragment fragment = new MainActivityFragment();
        Bundle args = new Bundle();
        args.putSerializable("movie", m);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String sort = "popularity.desc";
        MovieDb movieDb = new MovieDb(getActivity());
        m = new ArrayList<MovieModel>();


        View view = inflater.inflate(R.layout.fragment_main, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        int coulom = getActivity().getResources().getInteger(R.integer.number_colom);
        LinearLayoutManager layoutManager = new GridLayoutManager(getActivity(), coulom);
        moviesAdpater = new MoviesAdpater(getActivity(), m, movieDb);

        // recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(moviesAdpater);
        if (getArguments() != null) {
            sort = getArguments().getString("sort");

            Log.d("MainActivityFragment", "m.size():" + m.size());
        }
        if (!isNetworkConnected())
            getfromDb();
        else if (sort.contains("fav")) {
            getfromDb();

        } else
            new FetchMovieTask().execute(sort);


        return view;
    }

    protected boolean isNetworkConnected() {
        try {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            return (mNetworkInfo == null) ? false : true;

        } catch (NullPointerException e) {
            return false;

        }
    }
    @Override
    public void onChangeSortMovies(String type) {
        if (type.contains("fav"))
            getfromDb();
        else
            new FetchMovieTask().execute(type);
    }

    public void getfromDb() {
        mMovieDb = new MovieDb(getActivity());
        m = (ArrayList<MovieModel>) mMovieDb.getAllMovie();
        moviesAdpater.updateMovieList(m);
        moviesAdpater.notifyDataSetChanged();
    }

    public class FetchMovieTask extends AsyncTask<String, Integer, ArrayList<MovieModel>> {
        final String Movie_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
        final String SORTED_BY = "sort_by";
        final String APPID = "api_key";
        String url = "http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=98d5636d628896097020058a401fc8d3";
        String url2 = "http://api.themoviedb.org/3/discover/movie?sort_by=vote_avarage&api_key=98d5636d628896097020058a401fc8d3";

        /**
         * Prepare the weather high/lows for presentation.
         */

        @Override
        protected ArrayList<MovieModel> doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            Uri builtUri = Uri.parse(Movie_BASE_URL)
                    .buildUpon()
                    .appendQueryParameter(SORTED_BY, params[0])
                    .appendQueryParameter(APPID, getString(R.string.APPID))
                    .build();
            String JsonStr = null;

            try {

                URL url = new URL(builtUri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    return null;
                }
                JsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e("", e.getMessage());
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("", "Error closing stream", e);
                    }
                }
            }
            try {
                JSONObject result = new JSONObject(JsonStr);

                return new Gson().fromJson(result.get("results").toString(), new TypeToken<List<MovieModel>>() {
                }.getType());

            } catch (JSONException e) {
                e.printStackTrace();

            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<MovieModel> result) {
            try {
                m = result;
                moviesAdpater.updateMovieList(result);
                moviesAdpater.notifyDataSetChanged();
            } catch (Exception ex) {
                Log.d("FetchMovieTask", "ex:" + ex);
            }

        }
    }

}
