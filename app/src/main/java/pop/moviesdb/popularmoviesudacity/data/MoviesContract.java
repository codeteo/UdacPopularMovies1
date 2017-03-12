package pop.moviesdb.popularmoviesudacity.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Contract class for Database.
 */

public class MoviesContract {

    public static final String AUTHORITY = "pop.moviesdb.popularmoviesudacity";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // This is the path for "favorites" table
    public static final String PATH_FAVORITE = "favorites";

    // To prevent someone from accidentally instantiate the Contract class.
    private MoviesContract() { }

    public static final class Favorites implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FAVORITE)
                .build();

        /**
         * Builds Uri for single movie items
         */
        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(Favorites.CONTENT_URI, id);
        }

        public static final String TABLE_NAME = "favorites";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TITLE = "title";

    }

    public static long getIdFromUri(Uri uri) {
        return Long.parseLong(uri.getPathSegments().get(1));
    }

}
