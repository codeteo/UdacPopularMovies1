package pop.moviesdb.popularmoviesudacity.events;

import java.util.ArrayList;

import pop.moviesdb.popularmoviesudacity.models.MovieMainModel;

/**
 * Event to notify {@link pop.moviesdb.popularmoviesudacity.MainActivity}
 * from {@link pop.moviesdb.popularmoviesudacity.network.LoadFavoritesIntentService}
 * that load has finished and also passes the data.
 */

public class LoadFavoritesFinishedEvent {

    private ArrayList<MovieMainModel> favoritesArrayList;

    public LoadFavoritesFinishedEvent(ArrayList<MovieMainModel> favoritesArrayList) {
        this.favoritesArrayList = favoritesArrayList;
    }

    public ArrayList<MovieMainModel> getFavoritesArrayList() {
        return favoritesArrayList;
    }

    public void setFavoritesArrayList(ArrayList<MovieMainModel> favoritesArrayList) {
        this.favoritesArrayList = favoritesArrayList;
    }
}
