package poi.bootstrap;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.stereotype.Component;
import poi.model.Poi;
import poi.repo.PoiRepository;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * This is run (onApplicationEvent) when the server starts.  It populates the database
 * with data from the file (if the collection is empty).
 */
@Component
public class PoiBootstrap {

    private PoiRepository repo;
    public PoiBootstrap(PoiRepository repo ) {
        this.repo = repo;
    }

    @EventListener
    public void onApplicationStart(ApplicationReadyEvent event) {
        // Do nothing if the database is not empty
        if( repo.count() != 0 ) { return; }

        // Otherwise, fill with data from the file
        String fileName = "places.txt";
        System.out.println("Loading: " + fileName);
        List<Poi> poiList = new ArrayList<>();
        try( Scanner scan = new Scanner( new FileInputStream(fileName)) ) {
            scan.useDelimiter("[\\t\\n]");  // Tab separated fields
            while( scan.hasNext() ) {
                String name = scan.next();
                String address = scan.next();
                String tags = scan.next();
                double lat = scan.nextDouble();
                double longitude = scan.nextDouble();
                poiList.add( new Poi( name, address, tags, lat,longitude ) );
            }
        } catch(FileNotFoundException e) {
            System.out.println("Unable to load: " + fileName);
            e.printStackTrace();
        }

        // Save all to database
        repo.saveAll(poiList);
    }
}
