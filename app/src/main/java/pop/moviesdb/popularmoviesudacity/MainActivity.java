package pop.moviesdb.popularmoviesudacity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import pop.moviesdb.popularmoviesudacity.events.LoadFavoritesFinishedEvent;
import pop.moviesdb.popularmoviesudacity.events.OpenDetailsActivityEvent;
import pop.moviesdb.popularmoviesudacity.models.MovieMainModel;
import pop.moviesdb.popularmoviesudacity.models.MoviesNestedItemResultsResponse;
import pop.moviesdb.popularmoviesudacity.models.MoviesResponse;
import pop.moviesdb.popularmoviesudacity.network.LoadFavoritesIntentService;
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
 * without the boilerplate.
 */
public class MainActivity extends BaseActivity {

    private static final String TAG = "MAIN-ACTIVITY";
    private static final String MOST_POPULAR_KEY = "most_popular";
    private static final String TOP_RATED_KEY = "top_rated";
    private static final String FAVORITES_KEY = "favorites";
    private static final String CURRENTLY_DISPLAYED_KEY = "currently_displayed";
    private static final String INTENT_MOVIE = "movie";
    private static final String INTENT_IS_FAVORITE = "is_favorite";
    private static final String INTENT_REFRESH_FAVORITE_LIST = "refresh_favorite";
    private static final long CONNECTION_TIMEOUT = 15;
    private static final int REQUEST_CODE = 1;

    @BindView(R.id.rv_main_movies_list) RecyclerView rvMoviesList;
    @BindView(R.id.toolbar) Toolbar toolbar;

    private OkHttpClient okHttpClient;
    private Retrofit retrofit;
    private MoviesApiServices apiServices;

    private MoviesAdapter moviesAdapter;
    private GridLayoutManager gridLayoutManager;

    private ArrayList<MovieMainModel> mostPopularArrayList = new ArrayList<>();
    private ArrayList<MovieMainModel> topRatedArrayList = new ArrayList<>();
    private ArrayList<MovieMainModel> favoritesArrayList = new ArrayList<>();

    // keeps state of currently displayed data, initially we display "Most Popular"
    // 0 for MostPopular, 1 for TopRated and 2 for favorites
    private int isCurrentlyDisplayed = 0;

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

            executeMostPopularService();

        } else {
            mostPopularArrayList = savedInstanceState.getParcelableArrayList(MOST_POPULAR_KEY);
            topRatedArrayList = savedInstanceState.getParcelableArrayList(TOP_RATED_KEY);
            favoritesArrayList = savedInstanceState.getParcelableArrayList(FAVORITES_KEY);

            isCurrentlyDisplayed = savedInstanceState.getInt(CURRENTLY_DISPLAYED_KEY);

            // insert mostPopular to Adapter
            if (isCurrentlyDisplayed == 0) {
                moviesAdapter.addAll(mostPopularArrayList);
            } else if (isCurrentlyDisplayed == 1){
                moviesAdapter.addAll(topRatedArrayList);
            } else {
                moviesAdapter.addAll(favoritesArrayList);
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                boolean isDeleted = data.getBooleanExtra(INTENT_REFRESH_FAVORITE_LIST, false);
                if (isDeleted) {
                    // clear and re-query DB
                    favoritesArrayList.clear();
                    moviesAdapter.notifyDataSetChanged();
                    loadFavoritesFromDatabase();
                }
            }
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(MOST_POPULAR_KEY, mostPopularArrayList);
        outState.putParcelableArrayList(TOP_RATED_KEY, topRatedArrayList);
        outState.putParcelableArrayList(FAVORITES_KEY, favoritesArrayList);
        outState.putInt(CURRENTLY_DISPLAYED_KEY, isCurrentlyDisplayed);
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
                isCurrentlyDisplayed = 0;
                return true;
            case R.id.action_top_rated:
                if (!topRatedArrayList.isEmpty()) {
                    moviesAdapter.addAll(topRatedArrayList);
                } else {
                    executeTopRatedService();
                }
                isCurrentlyDisplayed = 1;
                return true;
            case R.id.action_favorites:
                if (!favoritesArrayList.isEmpty()) {
                    moviesAdapter.addAll(favoritesArrayList);
                } else {
                    loadFavoritesFromDatabase();
                }
                isCurrentlyDisplayed = 2;
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadFavoritesFromDatabase() {
        Intent intent = new Intent(this, LoadFavoritesIntentService.class);
        startService(intent);
    }

    @Subscribe
    public void onLoadFavoritesFinishedEventReceived(LoadFavoritesFinishedEvent event) {
        favoritesArrayList = event.getFavoritesArrayList();
        moviesAdapter.addAll(favoritesArrayList);
    }

    /**
     * When a click happens on a Grid's movie item we start {@link DetailsActivity}
     * and we send through Extras an object with detail info about the movie clicked.
     */
    @Subscribe
    public void onOpenDetailsActivityEventReceived(OpenDetailsActivityEvent event) {
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra(INTENT_MOVIE, event.getMovieModel());
        intent.putExtra(INTENT_IS_FAVORITE, isCurrentlyDisplayed == 2);
        startActivityForResult(intent, REQUEST_CODE);
    }

    private void executeMostPopularService() {
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
                    .setId(Integer.valueOf(mostPopularMovie.getId()))
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
                    .setId(Integer.valueOf(topRatedMovie.getId()))
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
