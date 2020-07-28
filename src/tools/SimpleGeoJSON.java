package tools;

import gnss.GPGGA;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import java.util.ArrayList;

public class SimpleGeoJSON {

    public static final String TYPE_POINT = "Point";
    public static final String TYPE_LINE = "LineString";
    public static final String TYPE_POLYGON = "Polygon";

    private final JsonArrayBuilder FEATURES = Json.createArrayBuilder();
    private final JsonObjectBuilder PUNKT = Json.createObjectBuilder();

    public SimpleGeoJSON() {
    }

    public void addFeature(GPGGA position, String type) {
        if (!type.equals(SimpleGeoJSON.TYPE_POINT))
            return;
        JsonObjectBuilder point = Json.createObjectBuilder();
        point.add("properties", Json.createObjectBuilder().add("UTC", position.getUtcTime()).add("Altitude",position.getAlt()).add("Satellite Number",position.getSatNum()).add("latitude",position.getLatitude()).add("longitude",position.getLongitude()));
        FEATURES.add(point);
    }


    public void addFeature(ArrayList<GPGGA> positions, String type) {
        if (!type.equals(SimpleGeoJSON.TYPE_LINE) && !type.equals(SimpleGeoJSON.TYPE_POLYGON))
            return;
        JsonObjectBuilder line = Json.createObjectBuilder();

        line.add("properties", Json.createObjectBuilder().add("stroke", "#ff0000").add("stroke-width", 5));
        JsonArrayBuilder coordinates = Json.createArrayBuilder();
        for (GPGGA position : positions)
            coordinates.add(Json.createArrayBuilder().add(position.getLongitude()).add(position.getLatitude()));
        /*if (type.equals(SimpleGeoJSON.TYPE_POLYGON)) {
            coordinates.add(Json.createArrayBuilder().add(positions.get(0).getLongitude()).add(positions.get(0).getLatitude()));
            line.add("geometry", Json.createObjectBuilder().add("type", type).add("coordinates", Json.createArrayBuilder().add(coordinates)));
        }
        if (type.equals(SimpleGeoJSON.TYPE_LINE))
            line.add("geometry", Json.createObjectBuilder().add("type", type).add("coordinates", Json.createArrayBuilder().add(coordinates)));
        FEATURES.add(line);*/
    }

    @Override
    public String toString() {
        return Json.createObjectBuilder().add("testy",FEATURES).build().toString();
    }


}
