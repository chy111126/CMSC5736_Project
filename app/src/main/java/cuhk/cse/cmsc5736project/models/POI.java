package cuhk.cse.cmsc5736project.models;

/**
 * Tentative class for POI fragment
 * Created by TCC on 12/16/2017.
 */

public class POI {

    private String id;
    private String name;
    private String description;
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
    public void setBeacon(Beacon beacon) {
        this.beacon = beacon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
