package simulator.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;



public class SimulatorApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader();
            BorderPane root =
                    (BorderPane)loader.load(getClass().getResource("SimulatorGUI.fxml").openStream());
            root.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
            primaryStage.setScene(new Scene(root));
            primaryStage.getIcons().add(new Image("file:Images/CompanyLogo.png"));
            primaryStage.setTitle("Pandemic Simulator");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}