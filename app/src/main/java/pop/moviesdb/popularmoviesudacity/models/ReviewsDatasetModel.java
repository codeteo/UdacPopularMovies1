package pop.moviesdb.popularmoviesudacity.models;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;

import java.util.List;

/**
 *
 */

@AutoValue
public abstract class ReviewsDatasetModel implements Parcelable {

    public abstract List<ReviewsNestedItemResult> reviewsList();

    public static ReviewsDatasetModel.Builder builder() {
        return new AutoValue_ReviewsDatasetModel.Builder();
    }

    @AutoValue.Builder
    public static abstract class Builder {

        public abstract Builder setReviewsList(List<ReviewsNestedItemResult> videoList);

        public abstract ReviewsDatasetModel build();

    }

}

