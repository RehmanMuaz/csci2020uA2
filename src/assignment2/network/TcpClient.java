package assignment2.network;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class TcpClient {
    private String host;
    private int port;
    private Socket socket;
    private boolean connected;

    public TcpClient() {
        host = "";
        port = 0;
        connected = false;
    }

    public TcpClient(String host, int port) {
        this.host = host;
        this.port = port;
        connected = false;
    }

    // Establishes TCP Connection
    public void connect() throws Exception {
        if (connected) {
            socket.close();
        }

        socket = new Socket(host, port);
        connected = true;

        // Handle connection
        onConnected(socket);
    }

    // Disconnects TCP Connection
    public void disconnect() throws Exception {
        if (!connected) {
            return;
        }

        connected = false;
        socket.close();

        onDisconnected();
    }

    // Returns host
    public String getHost() {
        return host;
    }

    // Sets host
    public void setHost(String host) {
        this.host = host;
    }

    // Returns port
    public int getPort() {
        return port;
    }

    // Sets port
    public void setPort(int port) {
        this.port = port;
    }

    // Returns connection status
    public boolean isConnected() {
        return connected;
    }

    public InputStream getInputStream() throws Exception {
        return socket.getInputStream();
    }

    public OutputStream getOutputStream() throws Exception {
        return socket.getOutputStream();
    }

    public void onConnected(Socket socket) {}
    public void onDisconnected() {}
}
