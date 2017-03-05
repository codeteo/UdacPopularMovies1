package pop.moviesdb.popularmoviesudacity.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pop.moviesdb.popularmoviesudacity.R;
import pop.moviesdb.popularmoviesudacity.models.VideoMainModel;

/**
 * Adapter for RecyclerView with Movies in {@link pop.moviesdb.popularmoviesudacity.DetailsActivity}.
 */

public class VideosAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "VIDEOS-ADAPTER";

    private List<VideoMainModel> dataset;
    private Context context;

    public VideosAdapter(Context context) {
        this.context = context;
        dataset = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        viewHolder = new VideosViewHolder(inflater.inflate(R.layout.item_video, parent, false));

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final VideosViewHolder videosViewHolder = (VideosViewHolder) holder;

        videosViewHolder.tvTitle.setText(dataset.get(position).name());

        videosViewHolder.llItemContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick MESA STO Click");
            }
        });

    }

    /**
     * Adds list of videos to dataset
     * @param videos data to add
     */
    public void addAll(List<VideoMainModel> videos) {
        dataset.clear();
        dataset.addAll(videos);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return dataset == null ? 0 : dataset.size();
    }

    class VideosViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ll_item_video_container) LinearLayout llItemContainer;
        @BindView(R.id.iv_item_video_icon) ImageView ivPlayIcon;
        @BindView(R.id.tv_item_video_title) TextView tvTitle;

        public VideosViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
