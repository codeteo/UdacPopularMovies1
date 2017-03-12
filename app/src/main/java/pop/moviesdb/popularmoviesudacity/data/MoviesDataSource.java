package pop.moviesdb.popularmoviesudacity.data;

import android.content.Context;
import android.database.Cursor;

import pop.moviesdb.popularmoviesudacity.models.MovieMainModel;

import static pop.moviesdb.popularmoviesudacity.data.MoviesContract.Favorites;

/**
 * Main entry point for accessing movies data.
 */

public class MoviesDataSource {

    private Context context;

    public MoviesDataSource(Context context) {
        this.context = context;
    }

    public boolean isFavorite(MovieMainModel movie) {
        boolean isFavorite = false;

        Cursor cursor = context.getContentResolver().query(
                Favorites.buildMovieUri(movie.id()),
                null,
                Favorites.COLUMN_MOVIE_ID + "=?",
                new String[]{String.valueOf(movie.id())},
                null
        );

        if (cursor != null) {
            isFavorite = cursor.getCount() != 0;
            cursor.close();
        }

        return isFavorite;
    }

}
