/**
 * Title: Assignment 2 - File Transfer
 * Desc: Use TCP to transfer files
 * Date: 4/1/2021
 *
 * Name: Muaz Rehman
 * ID:   100553376
 */

package assignment2;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.Map;

public class Main extends Application {
    public static Map<String, String> commandLineArgs;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = null;
        if (!commandLineArgs.containsKey("server")) {
            loader = new FXMLLoader(getClass().getResource("client/MainForm.fxml"));
            primaryStage.setTitle("File Transfer: Client");
        } else {
            loader = new FXMLLoader(getClass().getResource("server/MainForm.fxml"));
            primaryStage.setTitle("File Transfer: Server");
        }

        // Load form
        loader.load();

        // Create scene for stage
        Parent root = loader.getRoot();
        Scene scene = new Scene(root, 600, 400);

        primaryStage.setScene(scene);
        primaryStage.show();

        // Create handlers and trigger events
        Controller controller = loader.getController();
        controller.onStart();
        scene.getWindow().setOnCloseRequest(event -> {
            controller.onStop();
        });
    }


    public static void main(String[] args) {
        // Parse command line
        commandLineArgs = CommandLine.parse(args);
        launch(args);
    }
}
