package poi.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import poi.model.Review;

public interface ReviewRepository extends MongoRepository<Review,String> {}
