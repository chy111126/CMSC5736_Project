package cuhk.cse.cmsc5736project;
public class Beacon
{
    private String uuid;
    private int major;
    private int minor;
    private int rssi;
    private double path_loss_exponent;
    double one_meter_power = 0;
    double pos_x = 0,pos_y = 0;
    boolean isSameBeacon(Beacon b)
    {
        return uuid.equals(b.uuid) && major == b.major && minor == b.minor;
    }
    void setUUID(String uuid)
    {
        this.uuid = uuid;
    }
    void setMajor(int major)
    {
        this.major = major;
    }
    void setMinor(int minor)
    {
        this.minor = minor;
    }
    void setRSSI(int rssi)
    {
        this.rssi = rssi;
    }
    void setPathLossExponent(double path_loss_exponent)
    {
        this.path_loss_exponent = path_loss_exponent;
    }
    void setOneMeterPower(double one_meter_power)
    {
        this.one_meter_power = one_meter_power;
    }
    void setPos(double x,double y)
    {
        this.pos_x = x;
        this.pos_y = y;
    }

    String getUUID()
    {
        return uuid;
    }

    int getMinor(){
        return minor;
    }
    int  getMajor()
    {
        return major;
    }
    int getRSSI()
    {
        return rssi;
    }

    double calDistance(int power)
    {
        return Math.pow((one_meter_power/power),(1/one_meter_power));
    }
}
