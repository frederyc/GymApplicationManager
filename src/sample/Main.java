package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    private static final String database = "jdbc:sqlite:database.db";

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
        primaryStage.getIcons().add(new Image("icon.png"));
        primaryStage.setTitle("BodyFlex Gym Management System");
        primaryStage.setScene(new Scene(root, 1920, 1017));
        primaryStage.setMaximized(true);
        primaryStage.setFullScreen(true);
        primaryStage.show();

    }

    public static String getDatabase() { return database; }

    public static void main(String[] args) {
        launch(args);
    }
}
