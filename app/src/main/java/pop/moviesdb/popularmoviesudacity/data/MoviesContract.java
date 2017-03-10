package pop.moviesdb.popularmoviesudacity.data;

import android.provider.BaseColumns;

/**
 * Contract class for Database.
 */

public class MoviesContract {

    public static final class Favorites implements BaseColumns {

        public static final String TABLE_NAME = "favorites";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER_PATH = "poster_path";

    }

}
