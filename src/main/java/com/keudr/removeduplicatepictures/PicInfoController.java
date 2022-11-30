package com.keudr.removeduplicatepictures;

import com.keudr.removeduplicatepictures.db.GroupedByHash;
import com.keudr.removeduplicatepictures.db.GroupedPic;
import com.keudr.removeduplicatepictures.db.PicFileDb;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class PicInfoController implements Initializable{

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
    private TableView<PicInfo> picInfoTable = new TableView<>();
    @FXML
    TableColumn<PicInfo, String> pathColumn, hashColumn, nameColumn, fileDateColumn, picDateColumn, picOriginalDateColumn;
    @FXML
    TableColumn<PicInfo, Image> imageColumn;

    public void setStage(Stage stage) {
        this.stage=stage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        md5Column.setCellValueFactory(new PropertyValueFactory<>("value"));
        countColumn.setCellValueFactory(new PropertyValueFactory<>("count"));
        groupByHashTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            GroupedPic selectedGroupedRow = obs.getValue();
            setSubTableValues(selectedGroupedRow);
        });
        try {
            groupByHashTable.setItems(FXCollections.observableArrayList(new GroupedByHash(ApplicationStart.conn).getData()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
        try {
            picInfoTable.setItems(FXCollections.observableArrayList(new PicFileDb(ApplicationStart.conn).getData(selectedGroupedRow.getValue())));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
