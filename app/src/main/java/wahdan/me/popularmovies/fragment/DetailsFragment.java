package wahdan.me.popularmovies.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import wahdan.me.popularmovies.MyLinearLayoutManager;
import wahdan.me.popularmovies.R;
import wahdan.me.popularmovies.adapter.ReviewAdapter;
import wahdan.me.popularmovies.adapter.VideoAdapter;
import wahdan.me.popularmovies.model.MovieModel;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link DetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailsFragment extends Fragment {
    final static String movieurl = "http://image.tmdb.org/t/p/w185";
    private static final String ARG_PARAM1 = "model";
    public TextView textView;
    public ImageView movieImage;
    public TextView tvYear;
    public TextView tvDuration;
    public TextView tvRate;
    public TextView tvDescription;
    Context mContext;
    VideoAdapter mVideoAdapter;
    ReviewAdapter mReviewAdapter;
    private RecyclerView rvTrailer;
    private RecyclerView rvReview;
    private MovieModel mMovieModel;


    public DetailsFragment() {
        // Required empty public constructor
    }

    public static DetailsFragment newInstance(MovieModel movieModel) {
        DetailsFragment fragment = new DetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, movieModel);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mMovieModel = (MovieModel) getArguments().getSerializable(ARG_PARAM1);

        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movie_details, container, false);
        textView = (TextView) view.findViewById(R.id.textView);
        movieImage = (ImageView) view.findViewById(R.id.movie_image);
        tvYear = (TextView) view.findViewById(R.id.tv_year);
        tvDuration = (TextView) view.findViewById(R.id.tv_duration);
        tvRate = (TextView) view.findViewById(R.id.tv_rate);
        tvDescription = (TextView) view.findViewById(R.id.tv_description);

        rvTrailer = (RecyclerView) view.findViewById(R.id.rv_Trailer);
        rvReview = (RecyclerView) view.findViewById(R.id.rc_review);
        JSONArray jsonArray = new JSONArray();

        mVideoAdapter = new VideoAdapter(mContext, jsonArray);
        mReviewAdapter = new ReviewAdapter(mContext, jsonArray);

        rvTrailer.setLayoutManager(new MyLinearLayoutManager(getActivity(), 1, false));
        rvReview.setLayoutManager(new MyLinearLayoutManager(getActivity(), 1, false));

        mContext = getActivity();
        if (mMovieModel != null) {

            initView(mMovieModel);
        }
        return view;
    }

    public void initView(MovieModel movieModel) {
        new FetchMovieTask().execute("videos");
        new FetchMovieTask().execute("reviews");
        textView.setText(movieModel.getTitle());
        Picasso.with(getActivity()).load(movieurl + movieModel.getPosterPath()).into(movieImage);
        tvRate.setText(movieModel.getVoteCount() + "/" + "10");
        tvDescription.setText(movieModel.getOverview());
        tvYear.setText(movieModel.getReleaseDate());
    }

    public class FetchMovieTask extends AsyncTask<String, Integer, JSONArray> {
        final String APPID = "api_key";
        String url = "http://api.themoviedb.org/3/movie/" + mMovieModel.getId();
        private String type = "";


        @Override
        protected JSONArray doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            type = params[0];
            Uri builtUri = Uri.parse(url + "/" + params[0])
                    .buildUpon()
                    .appendQueryParameter(APPID, getString(R.string.APPID))
                    .build();
            // Will contain the raw JSON response as a string.
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

                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }
            try {
                JSONObject result = new JSONObject(JsonStr);

                return result.getJSONArray("results");

            } catch (JSONException e) {
                e.printStackTrace();

            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray result) {
            try {

                if (type.contains("videos")) {
                    mVideoAdapter = new VideoAdapter(mContext, result);
                    rvTrailer.setAdapter(mVideoAdapter);
                } else {
                    mReviewAdapter = new ReviewAdapter(mContext, result);
                    rvReview.setAdapter(mReviewAdapter);
                }


            } catch (Exception ex) {
            }

        }
    }


}
