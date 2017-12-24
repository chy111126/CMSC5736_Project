package cuhk.cse.cmsc5736project.models;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by TCC on 12/16/2017.
 */

public class Friend implements Serializable {

    private String name;
    private String mac;
    private String description;
    private Beacon beacon;
    private POI nearestLocation;
    private Date lastUpdatedDate;

    public Friend(String mac,  String name) {
        this.mac = mac;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setLastUpdated(Date lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
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

    public Date getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public Beacon getBeacon() {
        return beacon;
    }
    public void setBeacon(Beacon beacon) {
        this.beacon = beacon;
    }
    public void setNearPOI(POI nearestLocation)
    {
        this.nearestLocation = nearestLocation;
    }
}
