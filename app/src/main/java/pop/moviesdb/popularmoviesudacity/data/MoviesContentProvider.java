package pop.moviesdb.popularmoviesudacity.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

import static pop.moviesdb.popularmoviesudacity.data.MoviesContract.AUTHORITY;
import static pop.moviesdb.popularmoviesudacity.data.MoviesContract.PATH_FAVORITE;

/**
 * ContentProvider class for our app to provide access to database's operations.
 */

public class MoviesContentProvider extends ContentProvider {

    public static final int FAVORITES = 100;
    public static final int FAVORITES_WITH_ID = 101;

    private MoviesDbHelper moviesDbHelper;

    public static final UriMatcher uriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        // "favorites" directory
        uriMatcher.addURI(AUTHORITY, PATH_FAVORITE, FAVORITES);
        // single item from "favorites"
        uriMatcher.addURI(AUTHORITY, PATH_FAVORITE + "/#", FAVORITES_WITH_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {

        Context context = getContext();
        moviesDbHelper = new MoviesDbHelper(context);

        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
