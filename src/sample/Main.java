package sample;

import com.sothawo.mapjfx.Projection;
import db.MySQLAccess;
import gnss.NMEA2GeoJSON;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.Downloader;
import tools.SimpleGeoJSON;

public class Main extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;


    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    @Override
    public void start(Stage primaryStage) throws Exception {
        logger.info("starting DemoApp");
        String fxmlFile = "OSMSample.fxml";
        logger.debug("loading fxml file {}", fxmlFile);
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent rootNode = fxmlLoader.load(getClass().getResourceAsStream(fxmlFile));
        logger.trace("stage loaded");

        final Controller controller = fxmlLoader.getController();
        final Projection projection = getParameters().getUnnamed().contains("wgs84")
                ? Projection.WGS_84 : Projection.WEB_MERCATOR;
        controller.initMapAndControls(projection);

        Scene scene = new Scene(rootNode);
        logger.trace("scene created");

        primaryStage.setTitle("sothawo mapjfx demo application");
        primaryStage.setScene(scene);
        logger.trace("showing scene");
        primaryStage.show();

        logger.debug("application start method finished.");
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }



    public static void main(String[] args) throws Exception{

        String inputPath = "D:/Uni/rojek/sample.nmea";
        String outputPath = "D:/Uni/rojek/output.geojson";
        new NMEA2GeoJSON(inputPath, outputPath, SimpleGeoJSON.TYPE_POINT).run();

        String url = "jdbc:mysql://localhost/java_rojek?serverTimezone=UTC&user=root&password=";
        String name = "daten";
        MySQLAccess.connectDB(url);
        MySQLAccess.dropTable(url,name);
        MySQLAccess.createNewTable(url,name);
        MySQLAccess.addEntry(url,name);

        launch(args);

        // greift auf Downloader zu und gibt Addresse
        Downloader.download("https://www.geodaetische-messtechnik.de/data/sample.nmea");
       


    }
}
