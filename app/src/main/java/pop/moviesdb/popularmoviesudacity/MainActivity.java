package pop.moviesdb.popularmoviesudacity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import pop.moviesdb.popularmoviesudacity.adapter.MoviesAdapter;
import pop.moviesdb.popularmoviesudacity.events.OpenDetailsActivityEvent;
import pop.moviesdb.popularmoviesudacity.models.MovieMainModel;
import pop.moviesdb.popularmoviesudacity.models.MoviesNestedItemResultsResponse;
import pop.moviesdb.popularmoviesudacity.models.MoviesResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * MainActivity is the main screen of the app with a grid that displays
 * either the "Most Popular" or the "Top Rated" movies, fetched with a network request
 * from <a href="https://www.themoviedb.org/">www.themoviedb.org</a> web site.
 * On toolbar there's an overflow menu (three dots) where user can select either <i>"most popular"</i>
 * or <i>"top rated"</i> movies.
 * Data are saved to an ArrayList of Parcelable objects with the use of Google's AutoValue library
 * and AutoValue extensions that help to easily create immutable objects that implement Parcelable interface
 * without the boileplate.
 */
public class MainActivity extends BaseActivity {

    private static final String TAG = "MAIN-ACTIVITY";
    private static final String MOST_POPULAR_KEY = "most_popular";
    private static final String TOP_RATED_KEY = "top_rated";
    private static final String CURRENTLY_DISPLAYED_KEY = "currently_displayed";
    private static final String INTENT_MOVIE = "movie";
    private static final long CONNECTION_TIMEOUT = 15;

    @BindView(R.id.rv_main_movies_list) RecyclerView rvMoviesList;
    @BindView(R.id.toolbar) Toolbar toolbar;

    OkHttpClient okHttpClient;
    Retrofit retrofit;
    MoviesApiServices apiServices;

    MoviesAdapter moviesAdapter;
    GridLayoutManager gridLayoutManager;

    ArrayList<MovieMainModel> mostPopularArrayList = new ArrayList<>();
    ArrayList<MovieMainModel> topRatedArrayList = new ArrayList<>();

    // keeps state of currently displayed data, initially we display "Most Popular"
    private boolean isMostPopularDisplayed = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        setUpNetworking();

        moviesAdapter = new MoviesAdapter(this);

        gridLayoutManager = new GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        if (rvMoviesList != null) {
            rvMoviesList.setLayoutManager(gridLayoutManager);
            rvMoviesList.setAdapter(moviesAdapter);
        }

        apiServices = retrofit.create(MoviesApiServices.class);

        if (savedInstanceState == null) {

            apiServices.getMostPopular(Constants.API_KEY).enqueue(new Callback<MoviesResponse>() {
                @Override
                public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                    if (response.isSuccessful()) {
                        showMostPopularMovies(Arrays.asList(response.body().getResults()));
                    }
                }

                @Override
                public void onFailure(Call<MoviesResponse> call, Throwable t) {

                }
            });

        } else {
            Log.i(TAG, "onCreate State is NOT NULL");
            mostPopularArrayList = savedInstanceState.getParcelableArrayList(MOST_POPULAR_KEY);
            topRatedArrayList = savedInstanceState.getParcelableArrayList(TOP_RATED_KEY);

            isMostPopularDisplayed = savedInstanceState.getBoolean(CURRENTLY_DISPLAYED_KEY);

            // insert mostPopular to Adapter
            if (isMostPopularDisplayed) {
                moviesAdapter.addAll(mostPopularArrayList);
            } else {
                moviesAdapter.addAll(topRatedArrayList);
            }
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(MOST_POPULAR_KEY, mostPopularArrayList);
        outState.putParcelableArrayList(TOP_RATED_KEY, topRatedArrayList);
        outState.putBoolean(CURRENTLY_DISPLAYED_KEY, isMostPopularDisplayed);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_most_popular:
                if (!mostPopularArrayList.isEmpty()) {
                    moviesAdapter.addAll(mostPopularArrayList);
                }
                isMostPopularDisplayed = true;
                return true;
            case R.id.action_top_rated:
                if (!topRatedArrayList.isEmpty()) {
                    moviesAdapter.addAll(topRatedArrayList);
                } else {
                    executeTopRatedService();
                }
                isMostPopularDisplayed = false;
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * When a click happens on a Grid's movie item we start {@link DetailsActivity}
     * and we send through Extras an object with detail info about the movie clicked.
     */
    @Subscribe
    public void onOpenDetailsActivityEventReceived(OpenDetailsActivityEvent event) {
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra(INTENT_MOVIE, event.getMovieModel());
        startActivity(intent);
    }

    private void executeTopRatedService() {
        apiServices.getTopRated(Constants.API_KEY).enqueue(new Callback<MoviesResponse>() {
            @Override
            public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                if (response.isSuccessful()) {
                    showTopRatedMovies(Arrays.asList(response.body().getResults()));
                }
            }

            @Override
            public void onFailure(Call<MoviesResponse> call, Throwable t) {

            }
        });
    }

    private void showMostPopularMovies(List<MoviesNestedItemResultsResponse> mostPopularList) {
        for (MoviesNestedItemResultsResponse mostPopularMovie : mostPopularList){

            MovieMainModel mostPopularModel = MovieMainModel.builder()
                    .setTitle(mostPopularMovie.getOriginal_title())
                    .setOverview(mostPopularMovie.getOverview())
                    .setPosterPath(mostPopularMovie.getPoster_path())
                    .setReleaseDate(mostPopularMovie.getRelease_date())
                    .setVoteAverage(mostPopularMovie.getVote_average())
                    .build();

            mostPopularArrayList.add(mostPopularModel);

        }

        moviesAdapter.addAll(mostPopularArrayList);
    }

    private void showTopRatedMovies(List<MoviesNestedItemResultsResponse> topRatedList) {
        for (MoviesNestedItemResultsResponse topRatedMovie : topRatedList){

            MovieMainModel topRatedModel = MovieMainModel.builder()
                    .setTitle(topRatedMovie.getOriginal_title())
                    .setOverview(topRatedMovie.getOverview())
                    .setPosterPath(topRatedMovie.getPoster_path())
                    .setReleaseDate(topRatedMovie.getRelease_date())
                    .setVoteAverage(topRatedMovie.getVote_average())
                    .build();

            topRatedArrayList.add(topRatedModel);

        }

        moviesAdapter.addAll(topRatedArrayList);
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
