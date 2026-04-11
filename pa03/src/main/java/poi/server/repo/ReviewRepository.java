package poi.server.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import poi.server.model.Review;

import java.util.List;

public interface ReviewRepository extends MongoRepository<Review,String> {
    List<Review> findAllByPoiId(String id);

    void deleteAllByPoiId(String id);
}
