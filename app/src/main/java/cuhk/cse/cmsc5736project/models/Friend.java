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
    private String mac;

    public Friend(String name, String description, String mac) {
        this.name = name;
        this.description = description;
        this.mac = mac;
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
    public boolean isSame(Friend f) {
        return mac.equals(f.mac);
    }
    public String getMAC()
    {
        return mac;
    }
}
