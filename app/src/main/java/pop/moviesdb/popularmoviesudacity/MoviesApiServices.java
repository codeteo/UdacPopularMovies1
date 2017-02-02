package pop.moviesdb.popularmoviesudacity;

import pop.moviesdb.popularmoviesudacity.models.MoviesResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Retrofit service for api calls
 */
public interface MoviesApiServices {

    @GET("movie/popular")
    Call<MoviesResponse> getMostPopular(@Query("api_key") String apiKey);

    @GET("movie/top_rated")
    Call<MoviesResponse> getTopRated(@Query("api_key") String apiKey);

}
