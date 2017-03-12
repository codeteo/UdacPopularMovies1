package pop.moviesdb.popularmoviesudacity.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Models the response of GET "/movies/{id}/reviews" service
 */

public class ReviewsResponse implements Serializable {

    private int id;
    private int page;

    private ReviewsNestedItemResult[] results;

    @SerializedName("total_pages")
    private int totalPages;

    @SerializedName("total_results")
    private int totalResults;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public ReviewsNestedItemResult[] getResults() {
        return results;
    }

    public void setResults(ReviewsNestedItemResult[] results) {
        this.results = results;
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
