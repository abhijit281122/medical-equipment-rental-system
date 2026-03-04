package com.equipment.rental.equipmentrentalsystem2025;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MachineTestApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/equipment/rental/equipmentrentalsystem2025/RentMachinePage.fxml")
        );

        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.setTitle("Machine Master Test");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}