package pop.moviesdb.popularmoviesudacity.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Models the response of GET "/movies/popular" service
 * in {@link pop.moviesdb.popularmoviesudacity.MoviesApiServices}
 */
public class MoviesResponse implements Serializable {

    private MoviesNestedItemResultsResponse[] results;
    private int page;

    @SerializedName("total_pages")
    private int totalPages;

    @SerializedName("total_results")
    private int totalResults;

    public MoviesNestedItemResultsResponse[] getResults() {
        return results;
    }

    public void setResults(MoviesNestedItemResultsResponse[] results) {
        this.results = results;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }
}
