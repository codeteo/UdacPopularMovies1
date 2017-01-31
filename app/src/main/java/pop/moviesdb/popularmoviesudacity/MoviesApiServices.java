package pop.moviesdb.popularmoviesudacity;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Retrofit service for api calls
 */
public interface MoviesApiServices {

    @GET("movie/popular")
    Call<Void> getMostPopular(@Query("api_key") String apiKey);

}
