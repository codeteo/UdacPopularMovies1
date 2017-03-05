package pop.moviesdb.popularmoviesudacity.events;

import pop.moviesdb.popularmoviesudacity.models.VideoMainModel;

/**
 * Event to notify {@link pop.moviesdb.popularmoviesudacity.DetailsActivity} there was a click
 * on a Video list item and it should open video in Youtube.
 * We use Otto Event Bus to avoid "tight" coupling between modules and succeed a
 * modular/decoupled architecture.
 */

public class OpenYoutubeVideoEvent {

    private VideoMainModel videoMainModel;

    public OpenYoutubeVideoEvent(VideoMainModel videoMainModel) {
        this.videoMainModel = videoMainModel;
    }

    public VideoMainModel getVideoMainModel() {
        return videoMainModel;
    }

    public void setVideoMainModel(VideoMainModel videoMainModel) {
        this.videoMainModel = videoMainModel;
    }
}
