package pop.moviesdb.popularmoviesudacity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import pop.moviesdb.popularmoviesudacity.models.MovieMainModel;

/**
 * Displays the details of a movie.
 */
public class DetailsActivity extends AppCompatActivity {

    private static final String TAG = "DETAILS-ACTIVITY";
    private static final String INTENT_MOVIE = "movie";
    private static final String KEY_MOVIE = "movie_key";

    @BindView(R.id.tb_details_toolbar) Toolbar toolbar;
    @BindView(R.id.tv_details_overview) TextView tvOverview;
    @BindView(R.id.tv_details_rating) TextView tvRating;
    @BindView(R.id.tv_details_year) TextView tvYear;
    @BindView(R.id.iv_details_backdrop) ImageView ivPoster;
    @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout collapsingToolbar;

    MovieMainModel movieModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            movieModel = getIntent().getParcelableExtra(INTENT_MOVIE);
        } else {
            movieModel = savedInstanceState.getParcelable(KEY_MOVIE);
        }

        Picasso.with(this)
                .load(createUrlForPoster(movieModel.posterPath()))
                .centerCrop()
                .fit()
                .into(ivPoster);

        collapsingToolbar.setTitle(movieModel.title());

        tvOverview.setText(movieModel.overview());
        tvRating.setText(movieModel.voteAverage());
        tvYear.setText(movieModel.releaseDate());

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_MOVIE, movieModel);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home :
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Creates full poster_path by concatenating base_url and a param for image size
     *
     * @param posterPath data from server
     * @return full poster path with base_url
     */
    private String createUrlForPoster(String posterPath) {
        return Constants.BASE_IMAGE_URL + Constants.URL_PART_IMAGE_SIZE + posterPath;
    }

}
