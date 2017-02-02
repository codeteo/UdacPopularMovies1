package pop.moviesdb.popularmoviesudacity.events;

import pop.moviesdb.popularmoviesudacity.models.MovieMainModel;

/**
 * Event to notify MainActivity there was a click on an ImageView
 * and it should open {@link pop.moviesdb.popularmoviesudacity.DetailsActivity}.
 * We use Otto Event Bus to avoid "tight" coupling between modules and succeed a
 * more modular/decoupled architecture.
 */
public class OpenDetailsActivityEvent {

    private MovieMainModel movieModel;

    public OpenDetailsActivityEvent(MovieMainModel movieModel) {
        this.movieModel = movieModel;
    }

    public MovieMainModel getMovieModel() {
        return movieModel;
    }

    public void setMovieModel(MovieMainModel movieModel) {
        this.movieModel = movieModel;
    }
}
