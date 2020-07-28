package gnss;

public class GPGGA {
    public final static String KEY = "$GPGGA";
    public final static int FS_INVALID = 0;
    public final static int FS_GPS_FIX = 1;
    public final static int FS_DGPS_FIX = 2;
    public final static int FS_ESTIMATED_FIX = 6;
    // time of position fix
    private String utcTime,noSV, alt;
    // latitude (lat), longitude (long), horizontal dilution of precision (hdop),
    // altitude in meters (alt), height of geoid in meters (altRef)
    private double lat, lng, hdop,  altRef;
    // position fix status (fs), number of satellites (noSV), dgps reference station id (dgps)
    private int fs, dgps;

    public GPGGA() {
    }

    public GPGGA(String nmea) throws Error {
        String[] split = nmea.split(",");
        if (!split[0].equals("$GPGGA"))
            throw new Error("is not GPGGA format");
        this.setUtcTime(split[1]);
        this.setLatitude(split[2], split[3]);
        this.setLongitude(split[4], split[5]);
        this.setSatNum(split[7]);
        this.setAlt(split[9]);

    }

    public void setFixStatus(String fix) throws Exception {
        switch (Integer.parseInt(fix)) {
            case FS_INVALID:
            case FS_DGPS_FIX:
            case FS_ESTIMATED_FIX:
            case FS_GPS_FIX:
                this.fs = Integer.parseInt(fix);
            default:
                throw new Exception("unknown fix status");
        }
    }

    public int getFixStatus() {
        return this.fs;
    }

    public void setLatitude(String lat, String dir) {
        this.lat = NMEA.convert(lat);
        if (dir.equals("S"))
            this.lat *= -1;
    }

    public double getLatitude() {
        return this.lat;
    }

    public void setLongitude(String lng, String dir) {
        this.lng = NMEA.convert(lng);
        if (dir.equals("W"))
            this.lng *= -1;
    }

    public double getLongitude() {
        return this.lng;
    }

    public String getSatNum(){return this.noSV;}
    public void setSatNum(String noSV){this.noSV = noSV;}

    public String getAlt(){return this.alt; }
    public void setAlt(String alt){this.alt = alt;}

    public void setUtcTime(String utc) {
        this.utcTime = utc;
    }

    public String getUtcTime() {
        return this.utcTime;
    }

    @Override
    public String toString() {
        return String.format("{utc: %s, lat: %f, lng: %f, hdop: %f, alt: %f, altRef: %f, fs: %d, noSV: %d, dgps: %d}",
                this.utcTime, this.lat, this.lng, this.hdop, this.alt, this.altRef, this.fs, this.noSV, this.dgps);
    }
}





