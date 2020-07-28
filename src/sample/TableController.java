package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class TableController implements Initializable {

    @FXML
    private TableView<ModelTable> table;


    @FXML
    private javafx.scene.control.TableColumn<ModelTable, String> col_time;

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
    }



    public void setMainApp(Main main) {
    }
}
