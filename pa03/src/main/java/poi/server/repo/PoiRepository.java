package poi.server.repo;

import org.springframework.data.geo.Distance;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.repository.MongoRepository;
import poi.server.model.Poi;

import java.util.List;

public interface PoiRepository extends MongoRepository<Poi, String> {

    List<Poi> findByLocationNear(GeoJsonPoint p, Distance d);

}
