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

    public void setBeacon(Beacon beacon) {
        this.beacon = beacon;
    }

    public void setNearPOI(POI nearestLocation)
    {
        this.nearestLocation = nearestLocation;
    }

    public POI getNearestLocation() {
        return nearestLocation;
    }


    // ----- Friend proximity method -----
    public static int PROXIMITY_VERY_CLOSE = 0;
    public static int PROXIMITY_CLOSE = 1;
    public static int PROXIMITY_FAR = 2;
    public static int PROXIMITY_UNDETERMINED = 3;

    public int getProximityToCurrentUserPos(double x, double y) {
        if(getNearestLocation() == null) {
            return PROXIMITY_UNDETERMINED;
        }

        // Distance calculation
        double friendX = getNearestLocation().getBeacon().getPos_x();
        double friendY = getNearestLocation().getBeacon().getPos_y();
        double dist = Math.sqrt(Math.pow(friendX - x, 2) + Math.pow(friendY - y, 2));

        // TODO: Determine threshold for proximity
        if(dist <= 10) {
            return PROXIMITY_VERY_CLOSE;
        } else if(dist <= 30) {
            return PROXIMITY_CLOSE;
        } else {
            return PROXIMITY_FAR;
        }

    }
}
