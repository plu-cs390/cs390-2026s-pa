package poi.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("reviews")
public class Review {
    @Id
    private String id;

    private int stars;
    private String review;
    private String poiId;

    public Review() {
        id = null;
        stars = 0;
        review = "";
        poiId = null;
    }

    public Review(int stars, String review, String poiId) {
        this.stars = stars;
        this.review = review;
        this.poiId = poiId;
        this.id = null;
    }

    @Override
    public boolean equals(Object obj) {
        if( obj == this ) return true;
        if( !(obj instanceof Review) ) return false;
        Review otherReview = (Review) obj;

        // Do not compare the database ID numbers
        return otherReview.poiId.equals(poiId) && otherReview.review.equals(review) && otherReview.stars == stars;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getPoiId() {
        return poiId;
    }

    public void setPoiId(String poiId) {
        this.poiId = poiId;
    }
}
