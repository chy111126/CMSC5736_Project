package cuhk.cse.cmsc5736project.models;

import java.io.Serializable;

public class Beacon implements Serializable
{
    private String uuid;
    private int major;
    private int minor;
    private int rssi;
    private double path_loss_exponent;
    double one_meter_power = 0;
    double pos_x = 0,pos_y = 0;

    public int scanTimes = 0;
    public double prevRSSIAvg = 0;

    public boolean isSameBeacon(Beacon b)
    {
        return uuid.equals(b.uuid) && major == b.major && minor == b.minor;
    }
    public void setUUID(String uuid)
    {
        this.uuid = uuid;
    }
    public void setMajor(int major)
    {
        this.major = major;
    }
    public void setMinor(int minor)
    {
        this.minor = minor;
    }
    public void setRSSI(int rssi)
    {
        this.rssi = rssi;
    }
    public void setPathLossExponent(double path_loss_exponent)
    {
        this.path_loss_exponent = path_loss_exponent;
    }
    public void setOneMeterPower(double one_meter_power)
    {
        this.one_meter_power = one_meter_power;
    }
    public void setPos(double x,double y)
    {
        this.pos_x = x;
        this.pos_y = y;
    }

    public String getUUID()
    {
        return uuid;
    }

    public int getMinor(){
        return minor;
    }
    public int getMajor()
    {
        return major;
    }
    public int getRSSI()
    {
        return rssi;
    }
    public double getPos_x(){return pos_x;}
    public double getPos_y(){return pos_y;}

    public double getDistance() {
        // Get distance using RSSI power
        return calDistance(this.rssi);
    }

    public double calDistance(double power)
    {
        return Math.pow((one_meter_power/power),(1/path_loss_exponent));
    }


    // ----- Proximity method -----
    public static int PROXIMITY_VERY_CLOSE = 0;
    public static int PROXIMITY_CLOSE = 1;
    public static int PROXIMITY_FAR = 2;
    public static int PROXIMITY_UNDETERMINED = -1;

    public int getProximity(){
        // From RSSI value, determine how close the user is to the POI
        if(getRSSI() < -90) {
            return PROXIMITY_FAR;
        } else if(getRSSI() < -70) {
            return PROXIMITY_CLOSE;
        } else if (getRSSI() < -50) {
            return PROXIMITY_VERY_CLOSE;
        }
        return PROXIMITY_UNDETERMINED;
    }
}
