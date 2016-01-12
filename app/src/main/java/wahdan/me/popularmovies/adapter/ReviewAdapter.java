package wahdan.me.popularmovies.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import wahdan.me.popularmovies.R;

/**
 * Created by ahmedwahdan on 1/8/16.
 */
public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewItemHolder> {
    JSONArray reviewList;
    Context mContext;

    public ReviewAdapter(Context mContext, JSONArray reviewList) {
        this.mContext = mContext;
        this.reviewList = reviewList;
    }

    @Override
    public ReviewItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item, null);

        return new ReviewItemHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewItemHolder holder, int position) {
        try {
            JSONObject jsonObject = reviewList.getJSONObject(position);
            holder.tvUser.setText(jsonObject.getString("author"));
            holder.tvReview.setText(jsonObject.getString("content"));

        } catch (JSONException e) {
            Log.d("ReviewAdapter", "e:" + e);
            e.printStackTrace();
        }


    }

    public void updateReviewList(JSONArray videoList) {
        this.reviewList = videoList;
        notifyDataSetChanged();

    }

    @Override
    public int getItemCount() {
        if (reviewList != null) {
            return reviewList.length();

        } else return 0;
    }

    class ReviewItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvUser;
        TextView tvReview;


        public ReviewItemHolder(View view) {
            super(view);
            tvUser = (TextView) view.findViewById(R.id.User);
            tvReview = (TextView) view.findViewById(R.id.user_review);

        }


        @Override
        public void onClick(View v) {
            String id = "";
            try {
                JSONObject videoObject = reviewList.getJSONObject(getAdapterPosition());
                id = videoObject.getString(id);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v=" + id));
            mContext.startActivity(intent);
            Log.d("onClick ", "" + getAdapterPosition() + " " + reviewList);
        }
    }
}
