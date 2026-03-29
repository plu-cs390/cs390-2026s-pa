package poi.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import poi.model.Poi;

public interface PoiRepository extends MongoRepository<Poi, String> { }
