package pop.moviesdb.popularmoviesudacity;

import pop.moviesdb.popularmoviesudacity.models.MoviesResponse;
import pop.moviesdb.popularmoviesudacity.models.VideosResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Retrofit service for api calls
 */
public interface MoviesApiServices {

    @GET("movie/popular")
    Call<MoviesResponse> getMostPopular(@Query("api_key") String apiKey);

    @GET("movie/top_rated")
    Call<MoviesResponse> getTopRated(@Query("api_key") String apiKey);

    @GET("movie/{id}/videos")
    Call<VideosResponse> getVideos(@Path("id") int movieId, @Query("api_key") String apiKey);

    @GET("movie/{id}/reviews")
    Call<Void> getReviews(@Path("id") int movieId, @Query("api_key") String apiKey);

}
