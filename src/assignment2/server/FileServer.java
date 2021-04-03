package assignment2.server;

import assignment2.Logger;
import assignment2.Utility;
import assignment2.network.TcpServer;

import java.io.*;
import java.net.Socket;
import java.util.Objects;

public class FileServer extends TcpServer {
    private Logger logger;
    private File root;

    public FileServer(Logger logger, File root, int port) {
        super(port);
        this.logger = logger;
        this.root = root;
    }

    @Override
    public void onServerStarted() {
        logger.log("Server started listening on port " + super.getPort());
    }

    @Override
    public void onServerStopped() {
        logger.log("Server stopped listening on port " + super.getPort());
    }

    @Override
    public void onClientConnected(Socket socket) {
        try {
            logger.log("Connection from " + socket.getRemoteSocketAddress());

            // Open streams
            InputStreamReader input = new InputStreamReader(socket.getInputStream());
            OutputStreamWriter output = new OutputStreamWriter(socket.getOutputStream());

            BufferedReader reader = new BufferedReader(input);
            BufferedWriter writer = new BufferedWriter(output);

            // Read command
            String readLine = reader.readLine();
            String command = readLine;
            if (readLine.isEmpty()) {
                // Close streams
                writer.close();
                reader.close();
                output.close();
                input.close();

                // Close connection
                socket.close();

                return;
            }
            if (readLine.contains(" ")) {
                command = readLine.substring(0, readLine.indexOf(" "));
            }

            if (Objects.equals(command, "DIR")) {
                logger.log("DIR request from " + socket.getRemoteSocketAddress());
                for (File file : root.listFiles()) {
                    writer.write(file.getName());
                    writer.newLine();
                }
            } else if (Objects.equals(command, "UPLOAD")) {
                // NOTE: This is a security risk
                String fileName = readLine.substring(readLine.indexOf(" "));

                logger.log("UPLOAD (" + fileName + ") request from " + socket.getRemoteSocketAddress());

                // Open file stream
                FileWriter fileWriter = new FileWriter(fileName);
                int b = -1;
                while ((b = reader.read()) != -1) {
                    fileWriter.write(b);
                }
                fileWriter.close();
            } else if (Objects.equals(command, "DOWNLOAD")) {
                // NOTE: This is a security risk
                String fileName = readLine.substring(readLine.indexOf(" "));

                logger.log("DOWNLOAD (" + fileName + ") request from " + socket.getRemoteSocketAddress());

                // Open file stream
                FileReader fileReader = new FileReader(fileName);
                int b = -1;
                while ((b = fileReader.read()) != -1) {
                    writer.write(b);
                }
                fileReader.close();
            }

            // Close streams
            writer.close();
            reader.close();
            output.close();
            input.close();
        } catch (Exception ex) {
            logger.log(String.format("ERROR: %s", Utility.getStackTrace(ex)));
        } finally {
            // Close connection
            try {
                socket.close();
            } catch (Exception ex) {
                logger.log(String.format("ERROR: %s", Utility.getStackTrace(ex)));
            }
        }
    }
}
