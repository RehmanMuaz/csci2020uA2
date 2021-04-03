package assignment2.server;

import assignment2.Controller;
import assignment2.Logger;
import assignment2.Main;
import assignment2.Utility;
import javafx.application.Platform;
import javafx.fxml.FXML;

import javafx.scene.control.*;

import java.io.File;

public class MainForm extends Controller {
    private Logger logger;
    private FileServer server;
    private File path;
    private int port;

    @FXML
    public TextArea txtLog;

    @Override
    public void onStart() {
        // Create logger
        logger = message -> Platform.runLater(() -> {
            String toLog = message + "\n";
            txtLog.appendText(toLog);
            System.out.print(toLog);
        });

        // Check parameters
        if (!Main.commandLineArgs.containsKey("path")) {
            logger.log("ERROR: Startup arguments - path not defined");
            return;
        }
        if (!Main.commandLineArgs.containsKey("port")) {
            logger.log("ERROR: Startup arguments - port not defined");
            return;
        }

        path = new File(Main.commandLineArgs.get("path"));
        if (!path.exists() || !path.isDirectory()) {
            logger.log("ERROR: Specified path does not exist or is not a directory");
            return;
        }

        String strPort = Main.commandLineArgs.get("port");
        if (!Utility.isInteger(strPort) || (port = Integer.parseInt(strPort)) < 1 || port > 65535) {
            logger.log("ERROR: Specified port is not valid (1-65535)");
            return;
        }

        logger.log("Starting server...");

        // Create server
        server = new FileServer(logger, path, port);

        try {
            // Start server
            server.start();
        } catch (Exception ex) {
            logger.log(String.format("ERROR: %s", Utility.getStackTrace(ex)));
        }
    }

    @Override
    public void onStop() {
        // Stop server
        try {
            server.stop();
            logger.log("Server stopped");
        } catch (Exception ex) {
            logger.log(String.format("ERROR: %s", Utility.getStackTrace(ex)));
        }
    }
}
