package cuhk.cse.cmsc5736project.models;

import java.util.Date;

/**
 * Created by TCC on 12/16/2017.
 */

public class Friend {

    private String name;
    private String description;
    private Date lastUpdatedDate;
    private String nearestLocation;
    private Beacon beacon;

    public Friend(String name, String description) {
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
