package pop.moviesdb.popularmoviesudacity.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pop.moviesdb.popularmoviesudacity.Constants;
import pop.moviesdb.popularmoviesudacity.R;
import pop.moviesdb.popularmoviesudacity.models.MovieMainModel;

/**
 * Adapter for RecyclerView in {@link pop.moviesdb.popularmoviesudacity.MainActivity}
 * displays movie's poster + title
 */
public class MoviesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "MOVIES-ADAPTER";

    private List<MovieMainModel> dataset;
    private Context context;

    public MoviesAdapter(Context context) {
        this.context = context;
        dataset = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
         RecyclerView.ViewHolder viewHolder;
         LayoutInflater inflater = LayoutInflater.from(parent.getContext());

         viewHolder = new MoviesViewHolder(inflater.inflate(R.layout.item_movie, parent, false));

         return viewHolder;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final MoviesViewHolder moviesViewHolder = (MoviesViewHolder) holder;

        moviesViewHolder.tvTitle.setText(dataset.get(position).title());

        String posterPath = createUrlForPoster(dataset.get(position).posterPath());

        Picasso.with(context)
                .load(posterPath)
                .centerCrop()
                .fit()
                .into(moviesViewHolder.ivImage);

    }

    /**
     * Creates full poster_path by concatenating base_url and a param for image size
     * @param posterPath data from server
     * @return full poster path with base_url
     */
    private String createUrlForPoster(String posterPath) {
        return Constants.BASE_IMAGE_URL + Constants.URL_PART_IMAGE_SIZE + posterPath;
    }

    /**
     * Adds list of movies to dataset
     * @param movies data to add
     */
    public void addAll(List<MovieMainModel> movies) {
        dataset.clear();
        dataset.addAll(movies);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return dataset == null ? 0 : dataset.size();
    }

    class MoviesViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_item_movie_image) ImageView ivImage;
        @BindView(R.id.tv_item_movie_title) TextView tvTitle;

        public MoviesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
