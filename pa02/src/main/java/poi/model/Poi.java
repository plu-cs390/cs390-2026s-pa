package poi.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Represents a POI - Point of Interest.
 */
@Document("poi")
public class Poi {

    @Id
    private String id;
    private String name;
    private String address;
    private String tags;

    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPoint location;

    public Poi() {
        name = "";
        address = "";
        tags = "";
        location = null;
    }

    public Poi( String name, String address, String tags, double latitude, double longitude) {
        this.name = name;
        this.address = address;
        this.tags = tags;
        this.location = new GeoJsonPoint(longitude, latitude);
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getTags() { return tags; }

    public GeoJsonPoint getLocation() {
        return location;
    }

    public void setLocation(GeoJsonPoint location) {
        this.location = location;
    }

    /**
     * Returns whether the provided string is a substring of the
     * name or tags of this object.
     * @param search the search string
     * @return true if there is a match
     */
    public boolean match( String search ) {
        String compare = name.toLowerCase() + tags.toLowerCase();
        return compare.contains(search.toLowerCase());
    }

    @Override
    public boolean equals( Object other ) {
        if( other == this ) return true;
        if( !(other instanceof Poi) ) return false;
        Poi otherPoi = (Poi) other;

        // We don't compare the database ID
        return otherPoi.address.equals(this.address) && otherPoi.location.equals(this.location) &&
                otherPoi.name.equals(this.name) && otherPoi.tags.equals(this.tags);
    }

    @Override
    public String toString() {
        return String.format("(%d) name = '%s' add = '%s' tags = '%s' location = '%s'", id, name, address, tags, location.toString());
    }
}
