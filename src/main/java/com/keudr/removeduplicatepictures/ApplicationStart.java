package com.keudr.removeduplicatepictures;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ApplicationStart extends Application {

    @Override
    public void start(Stage stage) throws IOException {
//        FXMLLoader fxmlLoader = new FXMLLoader(ApplicationStart.class.getResource("openFolder.fxml"));
//        Scene scene = new Scene(fxmlLoader.load());
//        stage.setTitle("Process pictures in folder !");
//        stage.setScene(scene);
//        stage.show();

        FXMLLoader fxmlLoader = new FXMLLoader(ApplicationStart.class.getResource("mainTable.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        PicInfoController picInfoController = fxmlLoader.getController();
        picInfoController.setStage(stage);
        stage.setTitle("Display duplicated pictures !");
        stage.setScene(scene);
        //stage.sizeToScene();
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}