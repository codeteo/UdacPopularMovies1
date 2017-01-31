package pop.moviesdb.popularmoviesudacity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import pop.moviesdb.popularmoviesudacity.adapter.MoviesAdapter;
import pop.moviesdb.popularmoviesudacity.models.MostPopularNestedResultResponse;
import pop.moviesdb.popularmoviesudacity.models.MostPopularResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MAIN-ACTIVITY";
    private static final long CONNECTION_TIMEOUT = 15;

    @BindView(R.id.rv_main_movies_list) RecyclerView rvMoviesList;

    OkHttpClient okHttpClient;
    Retrofit retrofit;
    MoviesApiServices apiServices;

    MoviesAdapter moviesAdapter;
    GridLayoutManager gridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.setDebug(true);
        ButterKnife.bind(this);

        setUpNetworking();

        apiServices = retrofit.create(MoviesApiServices.class);

        apiServices.getMostPopular(Constants.API_KEY).enqueue(new Callback<MostPopularResponse>() {
            @Override
            public void onResponse(Call<MostPopularResponse> call, Response<MostPopularResponse> response) {
                if (response.isSuccessful()) {
                    showMostPopularMovies(Arrays.asList(response.body().getResults()));
                }
            }

            @Override
            public void onFailure(Call<MostPopularResponse> call, Throwable t) {

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

    private void showMostPopularMovies(List<MostPopularNestedResultResponse> mostPopularList) {
        Log.i(TAG, "showMostPopularMovies size == " + mostPopularList.size());
        moviesAdapter.addAll(mostPopularList);
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
