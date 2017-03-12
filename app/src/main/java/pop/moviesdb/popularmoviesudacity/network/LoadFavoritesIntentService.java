package pop.moviesdb.popularmoviesudacity.network;

import android.app.IntentService;
import android.content.Intent;

import java.util.ArrayList;

import pop.moviesdb.popularmoviesudacity.data.MoviesDataSource;
import pop.moviesdb.popularmoviesudacity.events.BusProvider;
import pop.moviesdb.popularmoviesudacity.events.LoadFavoritesFinishedEvent;
import pop.moviesdb.popularmoviesudacity.models.MovieMainModel;

/**
 * Service class to execute the load of user's favorites movies
 * from db in a background thread.
 */

public class LoadFavoritesIntentService extends IntentService {

    private static final String TAG = "LOAD-FAVORITES-DB";

    public LoadFavoritesIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        MoviesDataSource moviesDataSource = new MoviesDataSource(this);
        ArrayList<MovieMainModel> favoritesArrayList = moviesDataSource.getFavorites();

        BusProvider.getInstance()
                .post(new LoadFavoritesFinishedEvent(favoritesArrayList));
    }
}
