package wahdan.me.popularmovies.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import wahdan.me.popularmovies.R;

/**
 * Created by ahmedwahdan on 1/8/16.
 */
public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoItemHolder> {
    JSONArray videoList;
    Context mContext;

    public VideoAdapter(Context mContext, JSONArray videoList) {
        this.mContext = mContext;
        this.videoList = videoList;
    }

    @Override
    public VideoItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_item, null);
        VideoItemHolder videoItemHolder = new VideoItemHolder(view);

        return videoItemHolder;
    }

    @Override
    public void onBindViewHolder(VideoItemHolder holder, int position) {
        holder.tvVideo.setText("video: " + position);


    }

    public void updateVideoList(JSONArray videoList) {
        this.videoList = videoList;
        notifyItemRangeChanged(0, videoList.length());

    }

    @Override
    public int getItemCount() {
        Log.d("VideoAdapter", "videoList.length():" + videoList.length());
        return videoList.length();
    }

    class VideoItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Button tvVideo;


        public VideoItemHolder(View view) {
            super(view);
            tvVideo = (Button) view.findViewById(R.id.tv_video);
            tvVideo.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            String id = "";
            try {
                JSONObject videoObject = videoList.getJSONObject(getAdapterPosition());
                id = videoObject.getString(id);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v=" + id));
            mContext.startActivity(intent);
            Log.d("onClick ", "" + getAdapterPosition() + " " + videoList);
        }
    }
}
