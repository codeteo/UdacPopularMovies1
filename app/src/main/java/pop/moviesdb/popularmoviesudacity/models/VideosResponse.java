package pop.moviesdb.popularmoviesudacity.models;

import java.io.Serializable;

/**
 * Models the response of GET "/movies/{id}/videos" service
 * in {@link pop.moviesdb.popularmoviesudacity.MoviesApiServices}
 * which fetches the videos for a specific movie.
 */

public class VideosResponse implements Serializable {

    private int id;
    private VideosNestedItemResultsResponse[] results;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public VideosNestedItemResultsResponse[] getResults() {
        return results;
    }

    public void setResults(VideosNestedItemResultsResponse[] results) {
        this.results = results;
    }
}
