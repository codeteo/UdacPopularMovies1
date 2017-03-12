package pop.moviesdb.popularmoviesudacity.data;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

import pop.moviesdb.popularmoviesudacity.models.MovieMainModel;

import static pop.moviesdb.popularmoviesudacity.data.MoviesContract.Favorites;

/**
 * Main entry point for accessing movies data.
 */

public class MoviesDataSource {

    private Context context;

    public MoviesDataSource(Context context) {
        this.context = context;
        new MoviesDbHelper(context);
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

    public ArrayList<MovieMainModel> getFavorites() {
        ArrayList<MovieMainModel> favoritesArrayList = new ArrayList<>();

        Cursor cursor = context.getContentResolver().query(Favorites.CONTENT_URI,
                null, null, null, null);

        if (cursor != null) {
            if (cursor.moveToNext()) {
                do {
                    int movieId = cursor.getInt(cursor.getColumnIndex(Favorites.COLUMN_MOVIE_ID));
                    String title = cursor.getString(cursor.getColumnIndex(Favorites.COLUMN_TITLE));
                    String posterPath = cursor.getString(cursor.getColumnIndex(Favorites.COLUMN_POSTER_PATH));
                    String overview = cursor.getString(cursor.getColumnIndex(Favorites.COLUMN_OVERVIEW));
                    String releaseDate = cursor.getString(cursor.getColumnIndex(Favorites.COLUMN_RELEASE_DATE));
                    String voteAverage = cursor.getString(cursor.getColumnIndex(Favorites.COLUMN_VOTE_AVERAGE));

                    MovieMainModel movieMainModel = MovieMainModel.builder()
                            .setId(movieId)
                            .setTitle(title)
                            .setPosterPath(posterPath)
                            .setOverview(overview)
                            .setReleaseDate(releaseDate)
                            .setVoteAverage(voteAverage)
                            .build();

                    favoritesArrayList.add(movieMainModel);

                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return favoritesArrayList;
    }

}
