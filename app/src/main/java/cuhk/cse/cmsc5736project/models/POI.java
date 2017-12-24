package cuhk.cse.cmsc5736project.models;

import android.graphics.PointF;

/**
 * Tentative class for POI fragment
 * Created by TCC on 12/16/2017.
 */

public class POI {

    // Metadata
    private String id;
    private String name;
    private String description;
    private boolean isBookmarked;
    private PointF location;
    private Beacon beacon;

    public POI(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public String getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Beacon getBeacon() {
        return this.beacon;
    }

    public void setBeacon(Beacon beacon) {
        this.beacon = beacon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isBookmarked() {
        return isBookmarked;
    }

    public void setBookmarked(boolean bookmarked) {
        isBookmarked = bookmarked;
    }

    public PointF getPosition() {
        return location;
    }

    public void setPosition(double pos_x, double pos_y) {
        this.location = new PointF((float)pos_x, (float)pos_y);
    }
}
