package pop.moviesdb.popularmoviesudacity.models;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;

import java.util.List;

/**
 * Model to store dataset of Videos {@link VideoMainModel}
 * for {@link pop.moviesdb.popularmoviesudacity.DetailsActivity}
 */
@AutoValue
public abstract class VideoDatasetModel implements Parcelable{

    public abstract List<VideoMainModel> videoList();

    public static VideoDatasetModel.Builder builder() {
        return new AutoValue_VideoDatasetModel.Builder();
    }

    @AutoValue.Builder
    public static abstract class Builder {

        public abstract Builder setVideoList(List<VideoMainModel> videoList);

        public abstract VideoDatasetModel build();

    }

}
