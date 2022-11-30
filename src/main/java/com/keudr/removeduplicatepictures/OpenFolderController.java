package com.keudr.removeduplicatepictures;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class OpenFolderController{

    @FXML
    private Button openFolders;
    File selectedDirectory;
    Alert alert = new Alert(Alert.AlertType.NONE);

    @FXML
    void openFolder(ActionEvent event) {

        Stage stage = (Stage) openFolders.getScene().getWindow();

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Open folder");
        directoryChooser.showDialog(stage);

        selectedDirectory =
                directoryChooser.showDialog(stage);
        if (selectedDirectory != null) {
            System.out.println("mam folder:" + selectedDirectory.getAbsolutePath());
            openFolders.setText(selectedDirectory.getAbsolutePath());
        }
    }

    @FXML
    void processFolder(ActionEvent event){
        if(selectedDirectory == null){
            alert.setAlertType(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("set folder for processing first");
            // show the dialog
            alert.show();
        }else {
            System.out.println("procesiram");

            try {
                ProcessFolder processFolder = new ProcessFolder(selectedDirectory);
                processFolder.process();
            } catch (IOException e) {
                alert.setAlertType(Alert.AlertType.ERROR);
                alert.setTitle("Error while procesing folder " + selectedDirectory.getAbsolutePath());
                alert.setContentText("Exception:" + e.getLocalizedMessage());
                // show the dialog
                alert.show();
            } catch (SQLException e) {
                e.printStackTrace();
                alert.setAlertType(Alert.AlertType.ERROR);
                alert.setTitle("Error while procesing folder " + selectedDirectory.getAbsolutePath());
                alert.setContentText("Exception:" + e.getLocalizedMessage());
                // show the dialog
                alert.show();
            }

        }
    }

}
