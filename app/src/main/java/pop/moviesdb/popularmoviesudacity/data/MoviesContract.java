package pop.moviesdb.popularmoviesudacity.data;

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

    public static final class Favorites implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FAVORITE)
                .build();

        public static final String TABLE_NAME = "favorites";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER_PATH = "poster_path";

    }

}
