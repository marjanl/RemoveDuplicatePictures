package com.keudr.removeduplicatepictures;


import com.keudr.removeduplicatepictures.db.GroupedPic;
import com.keudr.removeduplicatepictures.db.PicFileDb;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;


public class PicInfoController implements Initializable {

    Stage stage;
    @FXML
    private Pane mainPane;
    @FXML
    private TableView<GroupedPic> groupByHashTable = new TableView<>();
    @FXML
    TableColumn<GroupedPic, String> md5Column;
    @FXML
    TableColumn<GroupedPic, String> countColumn;
    @FXML
    TableColumn<GroupedPic, String> sampleFileNameColumn;

    @FXML
    private TableView<PicInfo> picInfoTable = new TableView<>();
    @FXML
    TableColumn<PicInfo, String> pathColumn, hashColumn, nameColumn, fileDateColumn, picDateColumn, picOriginalDateColumn;
    @FXML
    TableColumn<PicInfo, Image> imageColumn;
    @FXML
    TableColumn<PicInfo, Button> deleteButtonColumn;
    @FXML
    Label lblCount;

    PicFileDb picFileDb;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        try {
            picFileDb = new PicFileDb();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        md5Column.setCellValueFactory(new PropertyValueFactory<>("value"));
        countColumn.setCellValueFactory(new PropertyValueFactory<>("count"));
        sampleFileNameColumn.setCellValueFactory(new PropertyValueFactory<>("sampleFileName"));
        groupByHashTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            GroupedPic selectedGroupedRow = obs.getValue();
            setSubTableValues(selectedGroupedRow);
        });
        refresh();
    }

    public void refreshTable(ActionEvent event) {
        refresh();
    }

    public void refresh(){
        try {
            groupByHashTable.setItems(FXCollections.observableArrayList(picFileDb.getGroupByData()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        lblCount.setText("found " + groupByHashTable.getItems().size() + " records");
    }


    private void setSubTableValues(GroupedPic selectedGroupedRow) {

        pathColumn.setCellValueFactory(new PropertyValueFactory<>("filePath"));
        pathColumn.setMinWidth(300);

        hashColumn.setCellValueFactory(new PropertyValueFactory<>("md5Hash"));

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        nameColumn.setMinWidth(100);
        nameColumn.setMaxWidth(100);

        fileDateColumn.setCellValueFactory(new PropertyValueFactory<>("fileCreatedFormated"));
        fileDateColumn.setMinWidth(85);
        fileDateColumn.setMaxWidth(85);
        picDateColumn.setCellValueFactory(new PropertyValueFactory<>("dateTimeFormated"));
        picDateColumn.setMinWidth(130);
        picDateColumn.setMaxWidth(130);
        picOriginalDateColumn.setCellValueFactory(new PropertyValueFactory<>("dateTimeOriginalFormated"));
        picOriginalDateColumn.setMinWidth(130);
        picOriginalDateColumn.setMaxWidth(130);

        imageColumn.setCellValueFactory(new PropertyValueFactory<>("image"));

        picInfoTable.prefHeightProperty().bind(mainPane.heightProperty());
        picInfoTable.prefWidthProperty().bind(mainPane.widthProperty());

        picInfoTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        deleteButtonColumn.setCellFactory(ActionButtonTableCell.<PicInfo>forTableColumn("Remove", (PicInfo p) -> {
            picInfoTable.getItems().remove(p);
            try {
                picFileDb.deleteByPath(p);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            File f = new File(p.filePath);
            if (f.exists()) {
                f.delete();
            }
            return p;
        }));

        try {
            picInfoTable.setItems(FXCollections.observableArrayList(picFileDb.getData(selectedGroupedRow.getValue())));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void openProcessFolder(ActionEvent event) {
        event.consume();
        System.out.println("Hello, World!");
        try {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(ApplicationStart.class.getResource("openFolder.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle("Process pictures in folder !");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
