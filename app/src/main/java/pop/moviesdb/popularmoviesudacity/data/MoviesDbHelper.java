package pop.moviesdb.popularmoviesudacity.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;

import static android.provider.BaseColumns._ID;
import static pop.moviesdb.popularmoviesudacity.data.MoviesContract.*;

/**
 * Helper class for our database.
 */

public class MoviesDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 6;

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_FAVORITES_TABLE = "CREATE TABLE " +
                Favorites.TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                Favorites.COLUMN_MOVIE_ID + " INTEGER NOT NULL," +
                Favorites.COLUMN_TITLE + " TEXT NOT NULL," +
                Favorites.COLUMN_POSTER_PATH + " TEXT NOT NULL," +
                Favorites.COLUMN_OVERVIEW+ " TEXT NOT NULL," +
                Favorites.COLUMN_VOTE_AVERAGE + " TEXT NOT NULL," +
                Favorites.COLUMN_RELEASE_DATE + " TEXT NOT NULL" +
                ");";

        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITES_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Favorites.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    /**
     * Helper method to check if db exists
     */
    public static boolean doesDatabaseExist(Context context) {
        File dbFile = context.getDatabasePath(DATABASE_NAME);
        return dbFile.exists();
    }

}
