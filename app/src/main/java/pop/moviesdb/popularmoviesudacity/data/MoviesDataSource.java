package pop.moviesdb.popularmoviesudacity.data;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

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
        Log.i("DataSource", "movieID == " + movie.id());

        Cursor cursor = context.getContentResolver().query(
                Favorites.CONTENT_URI,
                null,
                Favorites.COLUMN_MOVIE_ID + "=?",
                new String[]{String.valueOf(movie.id())},
                null
        );

        if (cursor != null) {
            isFavorite = cursor.getCount() != 0;
            cursor.close();
        }

        Log.i("DataSource", "isFavorite : " + isFavorite);

        return isFavorite;
    }

}
