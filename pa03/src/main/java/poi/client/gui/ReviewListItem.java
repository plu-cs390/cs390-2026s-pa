package poi.client.gui;

import poi.server.model.Review;

public class ReviewListItem {
    private Review review;
    public ReviewListItem(Review review) {
        this.review = review;
    }

    public Review getReview() { return review; }

    @Override
    public String toString() {
        return String.format("%d - %s", review.getStars(), review.getReview());
    }
}
