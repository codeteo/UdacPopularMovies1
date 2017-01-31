package pop.moviesdb.popularmoviesudacity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setUpRetrofit();

        apiServices = retrofit.create(MoviesApiServices.class);

        apiServices.getMostPopular(Constants.API_KEY).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.i(TAG, "onResponse mesa");
                if (response.isSuccessful()) {
                    Log.i(TAG, "onResponse Success");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });

    }

    private void setUpRetrofit() {
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
