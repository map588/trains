package trainController.HWImpl;

import javax.bluetooth.LocalDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BluetoothTransceiver {
    private static final String UUID_STRING = "1101";
    private static final String SERVER_NAME = "BluetoothTransceiver";

    private StreamConnectionNotifier serverConnection;
    private StreamConnection clientConnection;
    private OutputStream outputStream;
    private InputStream inputStream;
    private volatile boolean isRunning = true;

    public void startServer() {
        Thread serverThread = new Thread(() -> {
            try {
                // Open a local server socket
                LocalDevice localDevice = LocalDevice.getLocalDevice();
                UUID uuid = new UUID(UUID_STRING, true);
                String connectionURL = "btspp://localhost:" + uuid.toString() + ";name=" + SERVER_NAME;
                serverConnection = (StreamConnectionNotifier) Connector.open(connectionURL);
                while (isRunning) {
                    try {
                        // Wait for a client connection
                        System.out.println("Waiting for client connection...");
                        clientConnection = serverConnection.acceptAndOpen();
                        System.out.println("Client connected.");

                        // Get input and output streams
                        outputStream = clientConnection.openOutputStream();
                        inputStream = clientConnection.openInputStream();

                        // Start receiving data
                        startReceivingData();
                    } catch (IOException e) {
                        System.out.println("Error accepting connection: " + e.getMessage());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                closeConnection();
            }
        });

        serverThread.start();
    }

    public void connectToServer(String serverURL) {
        try {
            // Connect to the server
            clientConnection = (StreamConnection) Connector.open(serverURL);

            // Get input and output streams
            outputStream = clientConnection.openOutputStream();
            inputStream = clientConnection.openInputStream();

            System.out.println("Connected to the server.");

            // Start receiving data
            startReceivingData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendData(String data) {
        try {
            if (outputStream != null) {
                // Write data
                outputStream.write(data.getBytes());
                outputStream.flush();
                System.out.println("Data sent: " + data);
            } else {
                System.out.println("No active connection. Connect to the server or wait for a client connection.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startReceivingData() {
        Thread receivingThread = new Thread(() -> {
            try {
                byte[] buffer = new byte[1024];
                while (isRunning) {
                    int bytesRead = inputStream.read(buffer);
                    if (bytesRead > 0) {
                        String receivedData = new String(buffer, 0, bytesRead);
                        System.out.println("Received data: " + receivedData);
                        // Process the received data as needed
                    }
                }
            } catch (IOException e) {
                System.out.println("Error receiving data: " + e.getMessage());
            }
        });

        receivingThread.start();
    }

    public void stopTransceiver() {
        isRunning = false;
        closeConnection();
    }

    private void closeConnection() {
        try {
            if (inputStream != null) {
                inputStream.close();
                inputStream = null;
            }
            if (outputStream != null) {
                outputStream.close();
                outputStream = null;
            }
            if (clientConnection != null) {
                clientConnection.close();
                clientConnection = null;
            }
            if (serverConnection != null) {
                serverConnection.close();
                serverConnection = null;
            }
            System.out.println("Connection closed.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}