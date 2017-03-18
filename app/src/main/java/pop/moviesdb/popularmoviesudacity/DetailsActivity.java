package pop.moviesdb.popularmoviesudacity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import pop.moviesdb.popularmoviesudacity.adapter.ReviewsAdapter;
import pop.moviesdb.popularmoviesudacity.adapter.VideosAdapter;
import pop.moviesdb.popularmoviesudacity.data.MoviesDataSource;
import pop.moviesdb.popularmoviesudacity.events.OpenYoutubeVideoEvent;
import pop.moviesdb.popularmoviesudacity.models.MovieMainModel;
import pop.moviesdb.popularmoviesudacity.models.ReviewsDatasetModel;
import pop.moviesdb.popularmoviesudacity.models.ReviewsNestedItemResult;
import pop.moviesdb.popularmoviesudacity.models.ReviewsResponse;
import pop.moviesdb.popularmoviesudacity.models.VideoDatasetModel;
import pop.moviesdb.popularmoviesudacity.models.VideoMainModel;
import pop.moviesdb.popularmoviesudacity.models.VideosNestedItemResultsResponse;
import pop.moviesdb.popularmoviesudacity.models.VideosResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static pop.moviesdb.popularmoviesudacity.data.MoviesContract.Favorites;

/**
 * Displays the details of a movie.
 */
public class DetailsActivity extends BaseActivity {

    private static final String TAG = "DETAILS-ACTIVITY";
    private static final String INTENT_MOVIE = "movie";
    private static final String INTENT_IS_FAVORITE = "is_favorite";
    private static final String INTENT_REFRESH_FAVORITE_LIST = "refresh_favorite";
    private static final String KEY_MOVIE = "movie_key";
    private static final String KEY_VIDEOS = "videos_key";
    private static final String KEY_REVIEWS = "reviews_key";
    private static final String KEY_REMOVED_FROM_DB = "removed_from_db";
    private static final String KEY_ENTERED_FROM_FAV_GRID = "entered_from_fav_grid";
    private static final long CONNECTION_TIMEOUT = 15;
    private static final String YOUTUBE_BASE_URL = "http://www.youtube.com/watch?v=";


    @BindView(R.id.tb_details_toolbar) Toolbar toolbar;
    @BindView(R.id.tv_details_overview) TextView tvOverview;
    @BindView(R.id.tv_details_rating) TextView tvRating;
    @BindView(R.id.tv_details_year) TextView tvYear;
    @BindView(R.id.iv_details_backdrop) ImageView ivPoster;
    @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.rv_details_video_list) RecyclerView rvVideoList;
    @BindView(R.id.rv_details_reviews_list) RecyclerView rvReviewsList;
    @BindView(R.id.fab_details_add_favorite) FloatingActionButton fabAddFavorite;

    private OkHttpClient okHttpClient;
    private Retrofit retrofit;
    private MoviesApiServices apiServices;

    private MovieMainModel movieModel;

    private List<VideoMainModel> videoList = new ArrayList<>();
    private VideoDatasetModel videoListDataset;
    private List<ReviewsNestedItemResult> reviewsList = new ArrayList<>();
    private ReviewsDatasetModel reviewsListDataset;
    private VideosAdapter videosAdapter;
    private ReviewsAdapter reviewsAdapter;
    private LinearLayoutManager linearLayoutManager;
    private LinearLayoutManager reviewLinearLayoutManager;

    private MoviesDataSource moviesDataSource;
    private boolean isFavorite = false;
    // Declares whether we opened this Activity from the grid of favorite movies
    private boolean isEnteredFromFavoriteGrid = false;
    private boolean isFavoriteRemovedFromDB = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setUpNetworking();

        apiServices = retrofit.create(MoviesApiServices.class);

        videosAdapter = new VideosAdapter(this);
        reviewsAdapter = new ReviewsAdapter(this);

        if (savedInstanceState == null) {
            movieModel = getIntent().getParcelableExtra(INTENT_MOVIE);
            isEnteredFromFavoriteGrid = getIntent().getBooleanExtra(INTENT_IS_FAVORITE, false);

            executeVideoRequest();
            executeReviewRequest();

        } else {
            movieModel = savedInstanceState.getParcelable(KEY_MOVIE);
            videoListDataset = savedInstanceState.getParcelable(KEY_VIDEOS);
            reviewsListDataset = savedInstanceState.getParcelable(KEY_REVIEWS);
            isEnteredFromFavoriteGrid = savedInstanceState.getBoolean(KEY_ENTERED_FROM_FAV_GRID);
            isFavoriteRemovedFromDB = savedInstanceState.getBoolean(KEY_REMOVED_FROM_DB);

            if (videoListDataset == null) {
                executeVideoRequest();
            } else {
                setVideosAdapter();
            }
            if (reviewsListDataset == null) {
                executeReviewRequest();
            } else {
                setReviewsAdapter();
            }
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

        moviesDataSource = new MoviesDataSource(this);
        isFavorite = moviesDataSource.isFavorite(movieModel);
        if (isFavorite) {
            fabAddFavorite.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
        }

        fabAddFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = movieModel.id();
                String title = movieModel.title();
                String posterPath = movieModel.posterPath();
                String overview = movieModel.overview();
                String voteAverage = movieModel.voteAverage();
                String releaseDate = movieModel.releaseDate();

                ContentValues contentValues = new ContentValues();

                contentValues.put(Favorites.COLUMN_MOVIE_ID, id);
                contentValues.put(Favorites.COLUMN_TITLE, title);
                contentValues.put(Favorites.COLUMN_POSTER_PATH, posterPath);
                contentValues.put(Favorites.COLUMN_OVERVIEW, overview);
                contentValues.put(Favorites.COLUMN_VOTE_AVERAGE, voteAverage);
                contentValues.put(Favorites.COLUMN_RELEASE_DATE, releaseDate);

                isFavorite = !isFavorite;

                if (isFavorite) {
                    Uri uri = getContentResolver().insert(Favorites.CONTENT_URI, contentValues);
                    if (uri != null) {
                        fabAddFavorite.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
                    }
                } else {
                    int rowsDeleted = getContentResolver().delete(Favorites.buildMovieUri(id),
                            null, new String[]{Favorites.COLUMN_MOVIE_ID + "=" + id});
                    if (rowsDeleted > 0) {
                        fabAddFavorite.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
                        isFavoriteRemovedFromDB = true;
                    }
                }

            }
        });

    }

    @Override
    public void onBackPressed() {
        finishActivity();
        super.onBackPressed();
    }

    private void executeReviewRequest() {
        apiServices.getReviews(movieModel.id(), Constants.API_KEY).enqueue(new Callback<ReviewsResponse>() {
            @Override
            public void onResponse(Call<ReviewsResponse> call, Response<ReviewsResponse> response) {
                if (response.isSuccessful()) {
                    Collections.addAll(reviewsList, response.body().getResults());
                }

                reviewsListDataset = ReviewsDatasetModel.builder()
                        .setReviewsList(reviewsList)
                        .build();

                setReviewsAdapter();

            }

            @Override
            public void onFailure(Call<ReviewsResponse> call, Throwable t) {

            }
        });
    }

    private void executeVideoRequest() {
        apiServices.getVideos(movieModel.id() , Constants.API_KEY).enqueue(new Callback<VideosResponse>() {
            @Override
            public void onResponse(Call<VideosResponse> call, Response<VideosResponse> response) {
                if (response.isSuccessful()) {
                    for (VideosNestedItemResultsResponse nestedItem: response.body().getResults()){
                        VideoMainModel videoMainModel = VideoMainModel.builder()
                                .setId(nestedItem.getId())
                                .setKey(nestedItem.getKey())
                                .setName(nestedItem.getName())
                                .setType(nestedItem.getType())
                                .build();

                        videoList.add(videoMainModel);
                    }

                    videoListDataset = VideoDatasetModel.builder()
                            .setVideoList(videoList)
                            .build();

                    setVideosAdapter();
                }
            }

            @Override
            public void onFailure(Call<VideosResponse> call, Throwable t) {

            }
        });
    }

    private void setVideosAdapter() {
        videosAdapter.addAll(videoListDataset.videoList());

        linearLayoutManager = new LinearLayoutManager(this);
        rvVideoList.setLayoutManager(linearLayoutManager);
        rvVideoList.setAdapter(videosAdapter);
    }

    private void setReviewsAdapter() {
        reviewsAdapter.addAll(reviewsListDataset.reviewsList());

        reviewLinearLayoutManager = new LinearLayoutManager(this);
        rvReviewsList.setLayoutManager(reviewLinearLayoutManager);
        rvReviewsList.setAdapter(reviewsAdapter);
    }

    @Subscribe
    public void onOpenYoutubeVideoEventReceived(OpenYoutubeVideoEvent event) {
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(YOUTUBE_BASE_URL + event.getVideoMainModel().key()));
        startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_MOVIE, movieModel);
        outState.putParcelable(KEY_VIDEOS, videoListDataset);
        outState.putParcelable(KEY_REVIEWS, reviewsListDataset);
        outState.putBoolean(KEY_ENTERED_FROM_FAV_GRID, isEnteredFromFavoriteGrid);
        outState.putBoolean(KEY_REMOVED_FROM_DB, isFavoriteRemovedFromDB);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home :
                finishActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* Finishes Activity and if DetailsActivity started from favorite lists adds some Extras  */
    private void finishActivity() {
        if (isEnteredFromFavoriteGrid) {
            Intent intent = new Intent();
            intent.putExtra(INTENT_REFRESH_FAVORITE_LIST, isFavoriteRemovedFromDB);
            setResult(RESULT_OK, intent);
            finish();
        } else {
            finish();
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

    private void setUpNetworking() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .build();

        retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

}
