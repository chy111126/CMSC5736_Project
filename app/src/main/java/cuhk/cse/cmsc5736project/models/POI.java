package cuhk.cse.cmsc5736project.models;

/**
 * Tentative class for POI fragment
 * Created by TCC on 12/16/2017.
 */

public class POI {

    String name;
    String description;
    Beacon beacon;

    public POI(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
