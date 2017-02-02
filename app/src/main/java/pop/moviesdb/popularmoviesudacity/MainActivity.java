package pop.moviesdb.popularmoviesudacity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import pop.moviesdb.popularmoviesudacity.adapter.MoviesAdapter;
import pop.moviesdb.popularmoviesudacity.models.MoviesNestedItemResultsResponse;
import pop.moviesdb.popularmoviesudacity.models.MoviesResponse;
import pop.moviesdb.popularmoviesudacity.models.MovieMainModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MAIN-ACTIVITY";
    private static final long CONNECTION_TIMEOUT = 15;

    @BindView(R.id.rv_main_movies_list) RecyclerView rvMoviesList;
    @BindView(R.id.toolbar) Toolbar toolbar;

    OkHttpClient okHttpClient;
    Retrofit retrofit;
    MoviesApiServices apiServices;

    MoviesAdapter moviesAdapter;
    GridLayoutManager gridLayoutManager;

    List<MovieMainModel> mostPopularArrayList = new ArrayList<>();
    List<MovieMainModel> topRatedArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.setDebug(true);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        setUpNetworking();

        apiServices = retrofit.create(MoviesApiServices.class);

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

        moviesAdapter = new MoviesAdapter(this);

        gridLayoutManager = new GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        if (rvMoviesList != null) {
            rvMoviesList.setLayoutManager(gridLayoutManager);
            rvMoviesList.setAdapter(moviesAdapter);
        }

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
                return true;
            case R.id.action_top_rated:
                if (!topRatedArrayList.isEmpty()) {
                    moviesAdapter.addAll(topRatedArrayList);
                } else {
                    executeTopRatedService();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
        Log.i(TAG, "showMostPopularMovies size == " + mostPopularList.size());
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
        Log.i(TAG, "showTopRateMovies size == " + topRatedList.size());
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
