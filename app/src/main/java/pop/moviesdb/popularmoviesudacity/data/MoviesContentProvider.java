package pop.moviesdb.popularmoviesudacity.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import pop.moviesdb.popularmoviesudacity.data.MoviesContract.Favorites;

import static pop.moviesdb.popularmoviesudacity.data.MoviesContract.AUTHORITY;
import static pop.moviesdb.popularmoviesudacity.data.MoviesContract.PATH_FAVORITE;

/**
 * ContentProvider class for our app to provide access to database's operations.
 */

public class MoviesContentProvider extends ContentProvider {

    private static final String TAG = "CONTENT-PROVIDER";

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
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor returnCursor;
        Log.i(TAG, "query uri === " + uri.toString());
        switch (uriMatcher.match(uri)) {
            case FAVORITES_WITH_ID :
                returnCursor = moviesDbHelper.getReadableDatabase().query(
                        Favorites.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                Log.i(TAG, "query FAV_WITH_ID");
                break;
            case FAVORITES :
                returnCursor = moviesDbHelper.getReadableDatabase().query(
                        Favorites.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                Log.i(TAG, "query FAVORITES");
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri : " + uri);
        }

        returnCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return returnCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final SQLiteDatabase db = moviesDbHelper.getWritableDatabase();

        int match = uriMatcher.match(uri);

        Uri returnUri;

        switch (match) {
            case FAVORITES:
                long id = db.insert(Favorites.TABLE_NAME, null, contentValues);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(Favorites.CONTENT_URI, id);
                } else {
                    throw new SQLException("Failed to insert row into : " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri : " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = moviesDbHelper.getWritableDatabase();

        int rowsDeleted = 0;

        switch (uriMatcher.match(uri)) {
            case FAVORITES_WITH_ID :
                long id = MoviesContract.getIdFromUri(uri);
                rowsDeleted = db.delete(Favorites.TABLE_NAME, Favorites.COLUMN_MOVIE_ID + "=?", new String[]{String.valueOf(id)});
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri : " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
