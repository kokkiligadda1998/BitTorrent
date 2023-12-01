import java.net.Socket;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;

public class Execute implements Runnable {
    private PeerInfo peerInfo;
    private Logger logger;

    public Execute(String peerId) {
        // Initialize the peer information and logger based on the peer ID
        ReadConfigFiles.setStateFromConfigFiles();
        this.peerInfo = ReadConfigFiles.getPeerState(peerId);
        this.logger = Logger.getLogger(peerId);
    }

    public void init() {
        // Create necessary files and directories for the peer
        FileManager.makeFilesAndDirectories(this.peerInfo.getPeerIdentifier());

        if (peerInfo.FileShareFlag()) {
            // If the peer has a shared file, split the file into chunks
            System.out.println("Shared file: " + peerInfo.getPeerIdentifier());
            this.peerInfo.setFilePartitioning(FileManager.splitFile());
        } else {
            // If the peer does not have a shared file, initialize an empty map
            this.peerInfo.setFilePartitioning(new ConcurrentHashMap<>());
        }

        // Display the peer's ID and information
        System.out.println("Peer ID: " + peerInfo.getPeerIdentifier());
        System.out.println(peerInfo);

        // Create a thread to handle incoming peer connections
        Thread t = new Thread(() -> IncomingPeers(peerInfo));
        t.start();

        try {
            // Wait for the incoming peer handling thread to finish
            t.join();
            System.out.println(this.peerInfo.getPeerIdentifier() + ": Exiting Execute");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        // Entry point for the Execute class
        init();
    }

    public void IncomingPeers(PeerInfo peerInfo) {
        ServerSocket serverSocket = null;
        try {
            // Create a server socket to accept incoming connections
            serverSocket = new ServerSocket(peerInfo.getExchangePort());
            this.peerInfo.setConnectionListener(serverSocket);

            while (true) {
                // Accept incoming client connections
                Socket clientSocket = serverSocket.accept();
                System.out.println("Connection established to " + peerInfo.getExchangePort() + " from " + clientSocket.getRemoteSocketAddress());

                // Create a thread to handle the communication with the connected peer
                Thread t = new Thread(new HandlePeers(clientSocket, peerInfo));
                t.start();
            }
        } catch (Exception e) {
            // Handle exceptions and display an error message
            System.out.println(this.peerInfo.getPeerIdentifier() + ": Exiting IncomingPeers Function!");
        } finally {
            try {
                // Close the server socket
                serverSocket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
}