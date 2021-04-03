package assignment2.network;

import java.io.IOException;
import java.net.*;

public abstract class TcpServer {
    private int port;
    private ServerSocket socket;
    private Thread thread;
    private boolean listening;

    public TcpServer(int port) {
        this.port = port;
        listening = false;
    }

    public void start() throws Exception {
        if (listening) {
            throw new UnsupportedOperationException("Server is already started");
        }

        socket = new ServerSocket(port);
        thread = new Thread(() -> {
            while (listening) {
                try {
                    // Accept socket
                    Socket socket = this.socket.accept();

                    // Handle connection
                    new Thread(() -> {
                        onClientConnected(socket);
                    }).start();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        listening = true;
        thread.start();

        onServerStarted();
    }

    public void stop() throws Exception {
        if (!listening) {
            throw new UnsupportedOperationException("Server is already stopped");
        }

        listening = false;
        socket.close();
        thread.join();

        onServerStopped();
    }

    // Returns port
    public int getPort() {
        return port;
    }

    // Sets port
    public void setPort(int port) {
        this.port = port;
    }

    // Returns listening status
    public boolean isListening() {
        return listening;
    }

    public void onServerStarted() {}
    public void onServerStopped() {}
    public abstract void onClientConnected(Socket socket);
}
