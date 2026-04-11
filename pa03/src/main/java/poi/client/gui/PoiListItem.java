package poi.client.gui;

import poi.server.model.Poi;

public class PoiListItem {

    private Poi poi;

    public PoiListItem( Poi p ) {
        this.poi = p;
    }

    public Poi getPoi() { return poi; }

    @Override
    public String toString() {
        return String.format("%s (%s) - (%.6f, %.6f)", poi.getName(), poi.getTags(),
                poi.getLocation().getX(), poi.getLocation().getY());
    }
}
