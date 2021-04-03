package assignment2.client;

import assignment2.Controller;

import assignment2.Logger;
import assignment2.Main;
import assignment2.Utility;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;

import javax.swing.*;
import java.io.File;
import java.net.InetAddress;
import java.util.List;

public class MainForm extends Controller {
    private static final String HOSTNAME_REGEX = "[a-zA-Z.-]+";

    @FXML
    public TextField txtHostname;
    @FXML
    public TextField txtPort;
    @FXML
    public Button btnConnect;
    @FXML
    public Button btnBrowse;
    @FXML
    public Button btnDownload;
    @FXML
    public Button btnUpload;
    @FXML
    public ListView<String> lstLocal;
    @FXML
    public ListView<String> lstRemote;
    @FXML
    public TextArea txtLog;

    private Logger logger;
    private FileClient client;
    private boolean connected;
    private File path;

    @Override
    public void onStart() {
        // Create logger
        logger = message -> Platform.runLater(() -> {
            String toLog = message + "\n";
            txtLog.appendText(toLog);
            System.out.print(toLog);
        });

        // Create client
        client = new FileClient(logger);
        connected = false;

        // Set default path
        // Check parameters
        if (!Main.commandLineArgs.containsKey("path")) {
            logger.log("WARNING: Startup arguments - path not defined, using local path");
            path = new File("./");
        } else {
            path = new File(Main.commandLineArgs.get("path"));
        }
        readLocalFolder();

        // Set up buttons
        btnConnect.setOnMouseClicked(event -> {
            if (!connected) {
                // Get host information
                String hostName = txtHostname.getText();
                String strPort = txtPort.getText();

                // Check if host name is valid
                try {
                    InetAddress address = InetAddress.getByName(hostName);
                    if (hostName.matches(HOSTNAME_REGEX)) {
                        logger.log(String.format("Host %s resolved to %s", hostName, address.getHostAddress()));
                    } else {
                        logger.log(String.format("IP %s resolved", address));
                    }
                } catch (Exception ex) {
                    logger.log("ERROR: Specified host is not valid");
                    return;
                }

                // Check if port is valid
                int port = 0;
                if (!Utility.isInteger(strPort) || (port = Integer.parseInt(strPort)) < 1 || port > 65535) {
                    logger.log("ERROR: Specified port is not valid (1-65535)");
                    return;
                }

                // Connect to server
                client.setHost(hostName);
                client.setPort(port);

                try {
                    // Get remote files
                    readRemoteFolder();

                    // Set as connected
                    connected = true;

                    // Enable download and upload buttons and switch connect button text
                    btnDownload.setDisable(false);
                    btnUpload.setDisable(false);
                    btnConnect.setText("Disconnect");
                } catch (Exception ex) {
                    logger.log(String.format("ERROR: %s", Utility.getStackTrace(ex)));
                }
            } else {
                // Clear remote files
                lstRemote.getItems().clear();

                // Set as disconnected
                connected = false;

                // Disable download and upload buttons and switch connect button text
                btnDownload.setDisable(true);
                btnUpload.setDisable(true);
                btnConnect.setText("Connect");
            }
        });

        btnBrowse.setOnMouseClicked(event -> {
            // Show directory selection dialog
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setInitialDirectory(path);
            path = directoryChooser.showDialog(null);
            if (path != null) {
                if (!path.isDirectory()) {
                    JOptionPane.showMessageDialog(null, "Invalid folder", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                readLocalFolder();
            }
        });

        btnDownload.setOnMouseClicked(event -> {
            String selectedFile = lstRemote.getSelectionModel().getSelectedItem();
            if (selectedFile != null) {
                File destination = new File(path, selectedFile);
                try {
                    // Download file
                    client.downloadFile(destination, selectedFile);

                    logger.log(String.format("Sent DOWNLOAD (%s) request to %s", selectedFile, client.getHost()));

                    // Get local files
                    readLocalFolder();
                } catch (Exception ex) {
                    logger.log(String.format("ERROR: %s", Utility.getStackTrace(ex)));
                }
            }
        });

        btnUpload.setOnMouseClicked(event -> {
            String selectedFile = lstLocal.getSelectionModel().getSelectedItem();
            if (selectedFile != null) {
                File file = new File(path, selectedFile);

                // Check if file exists
                if (!file.exists()) {
                    logger.log(String.format("File %s does not exist!", file.getName()));
                    readLocalFolder();
                    return;
                }

                try {
                    // Upload file
                    client.uploadFile(file);

                    logger.log(String.format("Sent UPLOAD (%s) request to %s", file.getName(), client.getHost()));

                    // Get remote files
                    readRemoteFolder();
                } catch (Exception ex) {
                    logger.log(String.format("ERROR: %s", Utility.getStackTrace(ex)));
                }
            }
        });
    }

    private void readLocalFolder() {
        logger.log("Reading local files...");

        lstLocal.getItems().clear();
        for (File file : path.listFiles()) {
            lstLocal.getItems().add(file.getName());
        }
    }

    private void readRemoteFolder() throws Exception {
        logger.log(String.format("Sent DIR request to %s", client.getHost()));

        List<String> files = client.getDirectoryListing();
        lstRemote.getItems().clear();
        lstRemote.getItems().addAll(files);
    }
}
