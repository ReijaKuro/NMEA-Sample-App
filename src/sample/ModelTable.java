package sample;

public class ModelTable {


    public int getSat() {
        return sat;
    }

    public void setSat(int sat) {
        this.sat = sat;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getAlt() {
        return alt;
    }

    public void setAlt(double alt) {
        this.alt = alt;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public ModelTable( double time, double lat, double lon, double alt, int sat) {
        this.time = time;
        this.lat = lat;
        this.lon = lon;
        this.alt = alt;
        this.sat = sat;

    }

    int sat;
    double lat,lon,alt, time;


}
