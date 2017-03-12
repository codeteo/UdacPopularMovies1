package pop.moviesdb.popularmoviesudacity.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pop.moviesdb.popularmoviesudacity.R;
import pop.moviesdb.popularmoviesudacity.models.ReviewsNestedItemResult;

/**
 * Adapter for RecyclerView with Movies in {@link pop.moviesdb.popularmoviesudacity.DetailsActivity}.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "REVIEWS-ADAPTER";

    private List<ReviewsNestedItemResult> dataset;
    private Context context;

    public ReviewsAdapter(Context context) {
        this.context = context;
        dataset = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        viewHolder = new VideosViewHolder(inflater.inflate(R.layout.item_review, parent, false));

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final VideosViewHolder videosViewHolder = (VideosViewHolder) holder;

        videosViewHolder.tvAuthor.setText(dataset.get(position).author());
        videosViewHolder.tvContent.setText(dataset.get(position).content());

    }

    /**
     * Adds list of videos to dataset
     * @param reviews data to add
     */
    public void addAll(List<ReviewsNestedItemResult> reviews) {
        dataset.clear();
        dataset.addAll(reviews);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return dataset == null ? 0 : dataset.size();
    }

    class VideosViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_item_review_author) TextView tvAuthor;
        @BindView(R.id.tv_item_review_content) TextView tvContent;

        public VideosViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}

