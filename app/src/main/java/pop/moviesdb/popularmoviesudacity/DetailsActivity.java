package pop.moviesdb.popularmoviesudacity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import pop.moviesdb.popularmoviesudacity.models.MovieMainModel;
import pop.moviesdb.popularmoviesudacity.models.VideoDatasetModel;
import pop.moviesdb.popularmoviesudacity.models.VideoMainModel;
import pop.moviesdb.popularmoviesudacity.models.VideosNestedItemResultsResponse;
import pop.moviesdb.popularmoviesudacity.models.VideosResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Displays the details of a movie.
 */
public class DetailsActivity extends AppCompatActivity {

    private static final String TAG = "DETAILS-ACTIVITY";
    private static final String INTENT_MOVIE = "movie";
    private static final String KEY_MOVIE = "movie_key";
    private static final String KEY_VIDEOS = "video_key";
    private static final long CONNECTION_TIMEOUT = 15;

    @BindView(R.id.tb_details_toolbar) Toolbar toolbar;
    @BindView(R.id.tv_details_overview) TextView tvOverview;
    @BindView(R.id.tv_details_rating) TextView tvRating;
    @BindView(R.id.tv_details_year) TextView tvYear;
    @BindView(R.id.iv_details_backdrop) ImageView ivPoster;
    @BindView(R.id.collapsing_toolbar) CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.rv_details_video_list) RecyclerView rvVideoList;

    OkHttpClient okHttpClient;
    Retrofit retrofit;
    MoviesApiServices apiServices;

    MovieMainModel movieModel;

    private List<VideoMainModel> videoList = new ArrayList<>();
    private VideoDatasetModel videoListDataset;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setUpNetworking();

        apiServices = retrofit.create(MoviesApiServices.class);

        if (savedInstanceState == null) {
            movieModel = getIntent().getParcelableExtra(INTENT_MOVIE);

            executeVideoRequest();

        } else {
            movieModel = savedInstanceState.getParcelable(KEY_MOVIE);
            videoListDataset = savedInstanceState.getParcelable(KEY_VIDEOS);
            if (videoListDataset == null) {
                executeVideoRequest();
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
                }
            }

            @Override
            public void onFailure(Call<VideosResponse> call, Throwable t) {

            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_MOVIE, movieModel);
        outState.putParcelable(KEY_VIDEOS, videoListDataset);
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
