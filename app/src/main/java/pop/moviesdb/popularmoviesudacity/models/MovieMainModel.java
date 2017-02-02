package pop.moviesdb.popularmoviesudacity.models;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;

/**
 * Main model for items in {@link pop.moviesdb.popularmoviesudacity.MainActivity}.
 * It also implements Parcelable interface so it is immutable
 */
@AutoValue
public abstract class MovieMainModel implements Parcelable {

    public abstract String title();
    public abstract String posterPath();
    public abstract String overview();
    public abstract String voteAverage();
    public abstract String releaseDate();

    public static Builder builder() {
        return new AutoValue_MovieMainModel.Builder();
    }

    @AutoValue.Builder
    public static abstract class Builder {

        public abstract Builder setTitle(String title);
        public abstract Builder setPosterPath(String posterPath);
        public abstract Builder setOverview(String overview);
        public abstract Builder setVoteAverage(String voteAverage);
        public abstract Builder setReleaseDate(String releaseDate);

        public abstract MovieMainModel build();

    }

}
