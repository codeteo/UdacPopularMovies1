package pop.moviesdb.popularmoviesudacity.models;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;

/**
 * Main model class to hold Review data in {@link pop.moviesdb.popularmoviesudacity.DetailsActivity}
 */

@AutoValue
public abstract class ReviewsNestedItemResult implements Parcelable {

    public abstract String id();
    public abstract String author();
    public abstract String content();
    public abstract String url();

    public static ReviewsNestedItemResult.Builder builder() {
        return new AutoValue_ReviewsNestedItemResult.Builder();
    }

    @AutoValue.Builder
    public static abstract class Builder {

        public abstract Builder setId(String id);
        public abstract Builder setAuthor(String author);
        public abstract Builder setContent(String content);
        public abstract Builder setUrl(String url);

        public abstract ReviewsNestedItemResult build();

    }

}
