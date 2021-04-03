package assignment2.client;

import assignment2.Logger;
import assignment2.network.TcpClient;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileClient {
    private Logger logger;
    private TcpClient client;

    public FileClient(Logger logger) {
        this.logger = logger;
        this.client = new TcpClient();
    }

    // Returns Directory
    public List<String> getDirectoryListing() throws Exception {
        List<String> result = new ArrayList<>();

        // Try to connect to server
        client.connect();

        // Send data
        OutputStreamWriter output = new OutputStreamWriter(client.getOutputStream());
        BufferedWriter writer = new BufferedWriter(output);
        writer.write("DIR\n");

        // Flush buffer
        writer.flush();

        // Read response
        InputStreamReader input = new InputStreamReader(client.getInputStream());
        BufferedReader reader = new BufferedReader(input);
        Scanner scanner = new Scanner(reader);
        while (scanner.hasNextLine()) {
            String fileName = scanner.nextLine();
            result.add(fileName);
        }

        // Close streams
        reader.close();
        writer.close();
        input.close();
        output.close();

        // Close connection
        client.disconnect();

        return result;
    }

    // Uploads selected file
    public void uploadFile(File file) throws Exception {
        // Try to connect to server
        client.connect();

        // Send data
        OutputStreamWriter output = new OutputStreamWriter(client.getOutputStream());
        BufferedWriter writer = new BufferedWriter(output);
        writer.write(String.format("UPLOAD %s\n", file.getName()));

        // Read file and write to stream
        FileReader fileReader = new FileReader(file);
        int b = -1;
        while ((b = fileReader.read()) != -1) {
            writer.write(b);
        }
        fileReader.close();

        // Flush buffer
        writer.flush();

        // Close streams
        writer.close();
        output.close();

        // Close connection
        client.disconnect();
    }

    // Downloads selected file
    public void downloadFile(File destinationFile, String fileName) throws Exception {
        // Try to connect to server
        client.connect();

        // Send data
        OutputStreamWriter output = new OutputStreamWriter(client.getOutputStream());
        BufferedWriter writer = new BufferedWriter(output);
        writer.write(String.format("DOWNLOAD %s\n", fileName));

        // Flush buffer
        writer.flush();

        // Read stream and write to file
        InputStreamReader input = new InputStreamReader(client.getInputStream());

        // Read file and write to stream
        FileWriter fileWriter = new FileWriter(destinationFile);
        int b = -1;
        while ((b = input.read()) != -1) {
            fileWriter.write(b);
        }
        fileWriter.close();

        // Close streams
        writer.close();
        input.close();
        output.close();

        // Close connection
        client.disconnect();
    }

    // Returns host
    public String getHost() {
        return client.getHost();
    }

    // Sets host
    public void setHost(String host) {
        client.setHost(host);
    }

    // Returns port
    public int getPort() {
        return client.getPort();
    }

    // Sets port
    public void setPort(int port) {
        client.setPort(port);
    }
}
