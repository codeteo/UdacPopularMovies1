package pop.moviesdb.popularmoviesudacity.models;

import java.io.Serializable;

/**
 * Main model class to hold Review data in {@link pop.moviesdb.popularmoviesudacity.DetailsActivity}
 */

public class ReviewsNestedItemResult implements Serializable {

    public String id;
    public String author;
    public String content;
    public String url;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
