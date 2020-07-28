package db;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.*;

public class MySQLAccess {

    public static void connectDB (String url){
        try (Connection conn = DriverManager.getConnection(url)){
            System.out.println("connected");
        }catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }





    public static void addEntry(String url, String name) {
        try (Connection conn = DriverManager.getConnection(url)) {
            PreparedStatement stmt = conn.prepareStatement(String.format("INSERT INTO %s (timestamp,latitude,longitude,altitude,satellitenanzahl) VALUES (?,?,?,?,?)", name));
            JsonObject jsonObject = new JsonParser().parse(new FileReader("D:/Uni/rojek/output.geojson")).getAsJsonObject();

            System.out.println(jsonObject);
            System.out.println(jsonObject.get("testy").getAsJsonArray().get(1).getAsJsonObject().get("properties").getAsJsonObject().get("latitude").getAsDouble());
            //stmt.setDouble(1,jsonObject.get("testy").getAsJsonArray().get(0).getAsJsonObject().get("properties").getAsJsonObject().get("Altitude").getAsDouble());

            String s = jsonObject.get("testy").getAsJsonArray().toString();
            //jsonObject.get("testy").getAsJsonArray();

            System.out.println(jsonObject.get("testy").getAsJsonArray().size());
            //int i = 0, j = 1; i >= 0 && j >=1; i++ , j++
            JsonArray jsonArray = jsonObject.get("testy").getAsJsonArray();

            for (int i = 0; i < jsonArray.size() ; i++ ){
                stmt.setDouble(1,jsonArray.get(i).getAsJsonObject().get("properties").getAsJsonObject().get("UTC").getAsDouble());
                stmt.setDouble(2,jsonArray.get(i).getAsJsonObject().get("properties").getAsJsonObject().get("latitude").getAsDouble());
                stmt.setDouble(3,jsonArray.get(i).getAsJsonObject().get("properties").getAsJsonObject().get("longitude").getAsDouble());
                stmt.setDouble(4,jsonArray.get(i).getAsJsonObject().get("properties").getAsJsonObject().get("Altitude").getAsDouble());
                stmt.setInt(5,jsonArray.get(i).getAsJsonObject().get("properties").getAsJsonObject().get("Satellite Number").getAsInt());
                stmt.executeUpdate();
                System.out.println(stmt.getUpdateCount());
            }

            //stmt.executeUpdate();
            //System.out.println(stmt.getUpdateCount());
        }catch (SQLException | ArrayIndexOutOfBoundsException  | FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void getEntries(String url, String name) {
        try (Connection conn = DriverManager.getConnection(url)) {
            String sql = "SELECT * FROM " + name;
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery(sql);
            while (resultSet.next()) {
                double latitude = resultSet.getDouble("latitude");
                double longitude = resultSet.getDouble("longitude");
                System.out.println(String.format("%6.3f , %6.3f", latitude, longitude));
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void createNewTable(String url, String name) {
        String sql = "CREATE TABLE IF NOT EXISTS " + name + " (timestamp double precision,latitude double precision, longitude double precision,altitude double precision, satellitenanzahl INT)";
        try (Connection conn = DriverManager.getConnection(url)) {
            Statement stmt = conn.createStatement();
            stmt.execute(sql);


        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

    }

    public static void dropTable(String url, String name) {
        String sql = "DROP TABLE IF EXISTS " + name;
        try (Connection conn = DriverManager.getConnection(url)) {
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }





    public static void main(String... args) {
        String url = "jdbc:mysql://localhost/java_rojek?serverTimezone=UTC&user=root&password=.sWAG2014";
        String name = "daten";
        MySQLAccess.connectDB(url);
        MySQLAccess.dropTable(url,name);
        MySQLAccess.createNewTable(url,name);
        //MySQLAccess.addEntry(url,name);
       // MySQLAccess.getEntries(url, name);


    }
}
