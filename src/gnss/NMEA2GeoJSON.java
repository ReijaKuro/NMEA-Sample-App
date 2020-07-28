package gnss;

import tools.SimpleGeoJSON;

import java.io.*;
import java.util.ArrayList;

public class NMEA2GeoJSON {

    private final FileInputStream fileInputStream;
    private final FileOutputStream fileOutputStream;
    private final SimpleBufferedReader sbr;
    //private final BufferedReader br;
    //private final BufferedWriter bw;
    private final SimpleGeoJSON geojson = new SimpleGeoJSON();
    private final ArrayList<GPGGA> positions = new ArrayList();
    private final String TYPE;

    public NMEA2GeoJSON(String input, String output, String type) throws IOException, SecurityException {
        this.fileInputStream = new FileInputStream(new File(input));
        this.fileOutputStream = new FileOutputStream(new File(output));
        this.sbr = new SimpleBufferedReader(new File(input));
        //this.br = new BufferedReader(new FileReader(input));
        //this.bw = new BufferedWriter(new FileWriter(input));
        this.TYPE = type;
    }

    public void run() {
        String line;
        Object data = null;
        while ((line = sbr.readLine()) != null) {
            System.out.println(line);
            data = NMEA.parseNMEA(line);
            if (data != null)
                if (data instanceof GPGGA) {
                    if (this.TYPE.equals(SimpleGeoJSON.TYPE_POINT))
                        geojson.addFeature((GPGGA) data, this.TYPE);
                    if (this.TYPE.equals(SimpleGeoJSON.TYPE_LINE) || this.TYPE.equals(SimpleGeoJSON.TYPE_POLYGON))
                        positions.add((GPGGA) data);
                }
        }
        if (this.TYPE.equals(SimpleGeoJSON.TYPE_LINE) || this.TYPE.equals(SimpleGeoJSON.TYPE_POLYGON))
            geojson.addFeature(positions, this.TYPE);
        try {
            //this.bw.write(geojson.toString());
            this.fileOutputStream.write(geojson.toString().getBytes());
            this.fileOutputStream.close();
            this.fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
