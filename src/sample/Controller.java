package sample;

import com.sothawo.mapjfx.*;
import com.sothawo.mapjfx.event.MapLabelEvent;
import com.sothawo.mapjfx.event.MapViewEvent;
import com.sothawo.mapjfx.event.MarkerEvent;
import com.sothawo.mapjfx.offline.OfflineCache;
import javafx.animation.AnimationTimer;
import javafx.animation.Transition;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Controller for the FXML defined code.
 *
 * @author P.J. Meisch (pj.meisch@sothawo.com).
 */
public class Controller implements Initializable{

    /** logger for the class. */
    private static final Logger logger = LoggerFactory.getLogger(Controller.class);





    /** some coordinates from around town. */

    private static final Coordinate coordBeuth = new Coordinate(52.5450798, 13.3497509);

    private static final Coordinate coordGermanyNorth = new Coordinate(55.05863889, 8.417527778);
    private static final Coordinate coordGermanySouth = new Coordinate(47.27166667, 10.17405556);
    private static final Coordinate coordGermanyWest = new Coordinate(51.0525, 5.866944444);
    private static final Coordinate coordGermanyEast = new Coordinate(51.27277778, 15.04361111);
    private static final Extent extentGermany = Extent.forCoordinates(coordGermanyNorth, coordGermanySouth, coordGermanyWest, coordGermanyEast);

    /** default zoom value. */
    private static final int ZOOM_DEFAULT = 14;

    /** the markers. */

    private final Marker markerBeuth;
    private final Marker markerClick;

    ArrayList<Marker> markerList = new ArrayList<>();
    private HashMap<String, Marker> markersCreatedOnClick = new HashMap<>();

    /** the labels. */

    private final MapLabel labelClick;

    @FXML
    /** button to set the map's zoom. */
    private Button buttonZoom;

    /** the MapView containing the map */
    @FXML
    private MapView mapView;

    /** the box containing the top controls, must be enabled when mapView is initialized */
    @FXML
    private HBox topControls;

    /** Slider to change the zoom value */
    @FXML
    private Slider sliderZoom;

    /** Accordion for all the different options */
    @FXML
    private Accordion leftControls;

    /** section containing the location button */
    @FXML
    private TitledPane optionsLocations;


    /** button to set the map's center */
    @FXML
    private Button buttonBeuth;


    /** for editing the animation duration */
    @FXML
    private TextField animationDuration;

    /** the BIng Maps API Key. */
    @FXML
    private TextField bingMapsApiKey;

    /** Label to display the current center */
    @FXML
    private Label labelCenter;

    /** Label to display the current extent */
    @FXML
    private Label labelExtent;

    /** Label to display the current zoom */
    @FXML
    private Label labelZoom;

    /** label to display the last event. */
    @FXML
    private Label labelEvent;

    /** RadioButton for MapStyle OSM */
    @FXML
    private RadioButton radioMsOSM;

    /** RadioButton for MapStyle Stamen Watercolor */
    @FXML
    private RadioButton radioMsSTW;

    /** RadioButton for MapStyle Bing Roads */
    @FXML
    private RadioButton radioMsBR;

    /** RadioButton for MapStyle Bing Roads - dark */
    @FXML
    private RadioButton radioMsCd;

    /** RadioButton for MapStyle Bing Roads - grayscale */
    @FXML
    private RadioButton radioMsCg;

    /** RadioButton for MapStyle Bing Roads - light */
    @FXML
    private RadioButton radioMsCl;

    /** RadioButton for MapStyle Bing Aerial */
    @FXML
    private RadioButton radioMsBA;

    /** RadioButton for MapStyle Bing Aerial with Label */
    @FXML
    private RadioButton radioMsBAwL;

    /** RadioButton for MapStyle WMS. */
    @FXML
    private RadioButton radioMsWMS;

    /** RadioButton for MapStyle XYZ */
    @FXML
    private RadioButton radioMsXYZ;

    /** ToggleGroup for the MapStyle radios */
    @FXML
    private ToggleGroup mapTypeGroup;



    @FXML
    private CheckBox alleMarker;





    /** Check button for BEUTH marker */
    @FXML
    private CheckBox checkBeuth;

    /** Check button for click marker */
    @FXML
    private CheckBox checkClickMarker;



    /** params for the WMS server. */
    private WMSParam wmsParam = new WMSParam()
            .setUrl("http://ows.terrestris.de/osm/service?")
            .addParam("layers", "OSM-WMS");

    private XYZParam xyzParams = new XYZParam()
            .withUrl("https://server.arcgisonline.com/ArcGIS/rest/services/World_Topo_Map/MapServer/tile/{z}/{y}/{x})")
            .withAttributions(
                    "'Tiles &copy; <a href=\"https://services.arcgisonline.com/ArcGIS/rest/services/World_Topo_Map/MapServer\">ArcGIS</a>'");
   

    public Controller( ) throws SQLException {

        // a couple of markers using the provided ones

        // no position for click marker yet
        markerClick = Marker.createProvided(Marker.Provided.ORANGE).setVisible(false);

        // a marker with a custom icon
        markerBeuth = Marker.createProvided(Marker.Provided.GREEN).setPosition(coordBeuth)
                .setVisible(true);

        labelClick = new MapLabel("click!", 10, -10).setVisible(false).setCssClass("orange-label");


        markerClick.attachLabel(labelClick);
 
    }



    /**
     * called after the fxml is loaded and all objects are created. This is not called initialize any more,
     * because we need to pass in the projection before initializing.
     *
     * @param projection
     *     the projection to use in the map.
     */
    public void initMapAndControls(Projection projection) {
        logger.trace("begin initialize");

        // init MapView-Cache
        final OfflineCache offlineCache = mapView.getOfflineCache();
        final String cacheDir = System.getProperty("java.io.tmpdir") + "/mapjfx-cache";
//        logger.info("using dir for cache: " + cacheDir);
//        try {
//            Files.createDirectories(Paths.get(cacheDir));
//            offlineCache.setCacheDirectory(cacheDir);
//            offlineCache.setActive(true);
//        } catch (IOException e) {
//            logger.warn("could not activate offline cache", e);
//        }

        // set the custom css file for the MapView
        mapView.setCustomMapviewCssURL(getClass().getResource("/custom_mapview.css"));

        leftControls.setExpandedPane(optionsLocations);

        // set the controls to disabled, this will be changed when the MapView is intialized
        setControlsDisable(true);

        // wire up the location buttons

        buttonBeuth.setOnAction(event -> mapView.setCenter(coordBeuth));


        logger.trace("location buttons done");

        // wire the zoom button and connect the slider to the map's zoom
        buttonZoom.setOnAction(event -> mapView.setZoom(ZOOM_DEFAULT));
        sliderZoom.valueProperty().bindBidirectional(mapView.zoomProperty());


        // bind the map's center and zoom properties to the corresponding labels and format them
        labelCenter.textProperty().bind(Bindings.format("center: %s", mapView.centerProperty()));
        labelZoom.textProperty().bind(Bindings.format("zoom: %.0f", mapView.zoomProperty()));
        logger.trace("options and labels done");

        // watch the MapView's initialized property to finish initialization
        mapView.initializedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                afterMapIsInitialized();
            }
        });

        // observe the map type radiobuttons
        mapTypeGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            logger.debug("map type toggled to {}", newValue.toString());
            MapType mapType = MapType.OSM;
            if (newValue == radioMsOSM) {
                mapType = MapType.OSM;
            } else if (newValue == radioMsBR) {
                mapType = MapType.BINGMAPS_ROAD;
            } else if (newValue == radioMsCd) {
                mapType = MapType.BINGMAPS_CANVAS_DARK;
            } else if (newValue == radioMsCg) {
                mapType = MapType.BINGMAPS_CANVAS_GRAY;
            } else if (newValue == radioMsCl) {
                mapType = MapType.BINGMAPS_CANVAS_LIGHT;
            } else if (newValue == radioMsBA) {
                mapType = MapType.BINGMAPS_AERIAL;
            } else if (newValue == radioMsBAwL) {
                mapType = MapType.BINGMAPS_AERIAL_WITH_LABELS;
            } else if (newValue == radioMsWMS) {
                mapView.setWMSParam(wmsParam);
                mapType = MapType.WMS;
            } else if (newValue == radioMsXYZ) {
                mapView.setXYZParam(xyzParams);
                mapType = MapType.XYZ;
            }
            mapView.setBingMapsApiKey(bingMapsApiKey.getText());
            mapView.setMapType(mapType);
        });

        setupEventHandlers();

        // add the graphics to the checkboxes

        alleMarker.setGraphic(
                new ImageView(new Image(markerClick.getImageURL().toExternalForm(), 16.0, 16.0, true, true)));
        checkBeuth.setGraphic(
                new ImageView(new Image(markerBeuth.getImageURL().toExternalForm(), 16.0, 16.0, true, true)));
        checkClickMarker.setGraphic(
                new ImageView(new Image(markerClick.getImageURL().toExternalForm(), 16.0, 16.0, true, true)));

        // bind the checkboxes to the markers visibility

        try { sqldaten(url,name);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        for (Marker marker : markerList)
        {

            alleMarker.selectedProperty().bindBidirectional(marker.visibleProperty());
        }

        checkBeuth.selectedProperty().bindBidirectional(markerBeuth.visibleProperty());
        checkClickMarker.selectedProperty().bindBidirectional(markerClick.visibleProperty());
        logger.trace("marker checks done");





        // finally initialize the map view
        logger.trace("start map initialization");
        mapView.initialize(Configuration.builder()
                .projection(projection)
                .showZoomControls(false)
                .build());
        logger.debug("initialization finished");

        long animationStart = System.nanoTime();
        new AnimationTimer() {
            @Override
            public void handle(long nanoSecondsNow) {
                if (markerBeuth.getVisible()) {
                    // every 100ms, increase the rotation of the markerBEUTH by 9 degrees, make a turn in 4 seconds
                    long milliSecondsDelta = (nanoSecondsNow - animationStart) / 1_000_000;
                    long numSteps = milliSecondsDelta / 100;
                    int angle = (int) ((numSteps * 9) % 360);
                    if (markerBeuth.getRotation() != angle) {
                        markerBeuth.setRotation(angle);
                    }
                }
            }
        }.start();
    }

    /**
     * initializes the event handlers.
     */
    private void setupEventHandlers() {
        // add an event handler for singleclicks, set the click marker to the new position when it's visible
        mapView.addEventHandler(MapViewEvent.MAP_CLICKED, event -> {
            event.consume();
            final Coordinate newPosition = event.getCoordinate().normalize();
            labelEvent.setText("Event: map clicked at: " + newPosition);
            if (markerClick.getVisible()) {
                final Coordinate oldPosition = markerClick.getPosition();
                if (oldPosition != null) {
                    animateClickMarker(oldPosition, newPosition);
                } else {
                    markerClick.setPosition(newPosition);
                    // adding can only be done after coordinate is set
                    mapView.addMarker(markerClick);
                }
            }
        });

        mapView.addEventHandler(MarkerEvent.MARKER_CLICKED, event -> {
            event.consume();
            Marker marker = markersCreatedOnClick.remove(event.getMarker().getId());
            if (null != marker) {
                mapView.removeMarker(marker);
            }
        });

        // add an event handler for MapViewEvent#MAP_EXTENT and set the extent in the map
        mapView.addEventHandler(MapViewEvent.MAP_EXTENT, event -> {
            event.consume();
            mapView.setExtent(event.getExtent());
        });

        // add an event handler for extent changes and display them in the status label
        mapView.addEventHandler(MapViewEvent.MAP_BOUNDING_EXTENT, event -> {
            event.consume();
            labelExtent.setText(event.getExtent().toString());
        });

        mapView.addEventHandler(MapViewEvent.MAP_RIGHTCLICKED, event -> {
            event.consume();
            labelEvent.setText("Event: map right clicked at: " + event.getCoordinate());
        });
        mapView.addEventHandler(MarkerEvent.MARKER_CLICKED, event -> {
            event.consume();
            labelEvent.setText("Event: marker clicked: " + event.getMarker().getId());
        });
        mapView.addEventHandler(MarkerEvent.MARKER_RIGHTCLICKED, event -> {
            event.consume();
            labelEvent.setText("Event: marker right clicked: " + event.getMarker().getId());
        });
        mapView.addEventHandler(MapLabelEvent.MAPLABEL_CLICKED, event -> {
            event.consume();
            labelEvent.setText("Event: label clicked: " + event.getMapLabel().getText());
        });
        mapView.addEventHandler(MapLabelEvent.MAPLABEL_RIGHTCLICKED, event -> {
            event.consume();
            labelEvent.setText("Event: label right clicked: " + event.getMapLabel().getText());
        });

        mapView.addEventHandler(MapViewEvent.MAP_POINTER_MOVED, event -> {
            logger.debug("pointer moved to " + event.getCoordinate());
        });

        logger.trace("map handlers initialized");
    }

    private void animateClickMarker(Coordinate oldPosition, Coordinate newPosition) {
        // animate the marker to the new position
        final Transition transition = new Transition() {
            private final Double oldPositionLongitude = oldPosition.getLongitude();
            private final Double oldPositionLatitude = oldPosition.getLatitude();
            private final double deltaLatitude = newPosition.getLatitude() - oldPositionLatitude;
            private final double deltaLongitude = newPosition.getLongitude() - oldPositionLongitude;

            {
                setCycleDuration(Duration.seconds(1.0));
                setOnFinished(evt -> markerClick.setPosition(newPosition));
            }

            @Override
            protected void interpolate(double v) {
                final double latitude = oldPosition.getLatitude() + v * deltaLatitude;
                final double longitude = oldPosition.getLongitude() + v * deltaLongitude;
                markerClick.setPosition(new Coordinate(latitude, longitude));
            }
        };
        transition.play();
    }

    /**
     * shows a new polygon with the coordinate from the added.
     *
     * @param event
     *     event with coordinates
     */


    /**
     * enables / disables the different controls
     *
     * @param flag
     *     if true the controls are disabled
     */
    private void setControlsDisable(boolean flag) {
        topControls.setDisable(flag);
        leftControls.setDisable(flag);
    }

    public void sqldaten(String url, String name) throws SQLException {
        try (Connection conn = DriverManager.getConnection(url)) {
            String sql = "SELECT * FROM " + name;
            Statement stmt = conn.createStatement();
            ResultSet resultSet = stmt.executeQuery(sql);


            while (resultSet.next()) {
                double latitude = resultSet.getDouble("latitude");
                double longitude = resultSet.getDouble("longitude");
                Marker newMarker = Marker.createProvided(Marker.Provided.BLUE).setPosition(new Coordinate(latitude,longitude)).setVisible(
                        false);
                markerList.add(newMarker);







            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * finishes setup after the mpa is initialzed
     *
     */

    String url = "jdbc:mysql://localhost/java_rojek?serverTimezone=UTC&user=root&password=.sWAG2014";
    String name = "daten";

    private void afterMapIsInitialized() {
        logger.trace("map intialized");
        logger.debug("setting center and enabling controls...");
        // start at the beuth with default zoom
        mapView.setZoom(ZOOM_DEFAULT);
        mapView.setCenter(coordBeuth);
        // add the markers to the map - they are still invisible

        mapView.addMarker(markerBeuth);

        for (Marker m : markerList)
        {
            mapView.addMarker(m);
        }




        // now enable the controls
        setControlsDisable(false);
    }

    /**
     * load a coordinateLine from the given uri in lat;lon csv format
     *
     * @param url
     *     url where to load from
     * @return optional CoordinateLine object
     * @throws java.lang.NullPointerException
     *     if uri is null
     */
    private Optional<CoordinateLine> loadCoordinateLine(URL url) {
        try (
                Stream<String> lines = new BufferedReader(
                        new InputStreamReader(url.openStream(), StandardCharsets.UTF_8)).lines()
        ) {
            return Optional.of(new CoordinateLine(
                    lines.map(line -> line.split(";")).filter(array -> array.length == 2)
                            .map(values -> new Coordinate(Double.valueOf(values[0]), Double.valueOf(values[1])))
                            .collect(Collectors.toList())));
        } catch (IOException | NumberFormatException e) {
            logger.error("load {}", url, e);
        }
        return Optional.empty();
    }

        public void addMarkerOnClick(Double latitude, Double longitude){
            Marker newMarker = Marker.createProvided(Marker.Provided.BLUE).setPosition(new Coordinate(latitude,longitude)).setVisible(
                    true);
            markerList.add(newMarker);
            mapView.addMarker(newMarker);
            markersCreatedOnClick.put(newMarker.getId(), newMarker);

        }




        @FXML
        private TableView<ModelTable> table;


        @FXML
        private javafx.scene.control.TableColumn<ModelTable, Double> col_time;

        @FXML
        private javafx.scene.control.TableColumn<ModelTable, Double> col_lat;

        @FXML
        private javafx.scene.control.TableColumn<ModelTable, Double> col_lon;

        @FXML
        private javafx.scene.control.TableColumn<ModelTable, Double> col_alt;

        @FXML
        private javafx.scene.control.TableColumn<ModelTable, Integer> col_sat;





        ObservableList<ModelTable> oblist = FXCollections.observableArrayList();


        @Override
        public void initialize(URL url, ResourceBundle resourceBundle) {

            try {

                Connection con = DBConnector.getConnection();
                ResultSet rs = con.createStatement().executeQuery("SELECT * from java_rojek.daten");

                while (rs.next()){
                    oblist.add(new ModelTable(rs.getDouble("timestamp"),
                            rs.getDouble("latitude"),rs.getDouble("longitude"),
                            rs.getDouble("altitude"),rs.getInt("satellitenanzahl")));
                }

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

            col_time.setCellValueFactory(new PropertyValueFactory<>("time"));
            col_lat.setCellValueFactory(new PropertyValueFactory<>("lat"));
            col_lon.setCellValueFactory(new PropertyValueFactory<>("lon"));
            col_alt.setCellValueFactory(new PropertyValueFactory<>("alt"));
            col_sat.setCellValueFactory(new PropertyValueFactory<>("sat"));


            table.setItems(oblist);
            table.setRowFactory(tv ->{
                TableRow<ModelTable> row = new TableRow<>();
                row.setOnMouseClicked(mouseEvent -> {
                    if(! row.isEmpty()){
                        ModelTable mt = row.getItem();
                        Double latitude = mt.getLat();
                        Double longitude = mt.getLon();
                        addMarkerOnClick(latitude,longitude);
                    }
                });

                return row;
            });
        }



        public void setMainApp(Main main) {
        }
}