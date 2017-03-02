package pop.moviesdb.popularmoviesudacity.models;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;

/**
 * Main model class to hold data about the videos in {@link pop.moviesdb.popularmoviesudacity.DetailsActivity}
 */

@AutoValue
public abstract class VideoMainModel implements Parcelable {

    public abstract String id();
    public abstract String key();
    public abstract String name();
    public abstract String type();


    public static VideoMainModel.Builder builder() {
        return new AutoValue_VideoMainModel.Builder();
    }

    @AutoValue.Builder
    public static abstract class Builder {

        public abstract Builder setId(String id);
        public abstract Builder setKey(String key);
        public abstract Builder setName(String name);
        public abstract Builder setType(String type);

        public abstract VideoMainModel build();

    }


}
