import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class IncomingConnectionHandler implements Runnable{

    private PeerInfo peerState;

    public IncomingConnectionHandler(PeerInfo peerState) {
        this.peerState = peerState;
    }

    @Override
    public void run() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(peerState.getPort());
            this.peerState.setServerSocket(serverSocket);
            while (true) {
                System.out.println("Peer Id " + peerState.getPeerId() + " accepting connections");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Connection is established to " + peerState.getPort() + " from " + clientSocket.getRemoteSocketAddress());
                Thread t = new Thread(new HandlePeers(clientSocket, peerState));
                t.start();
            }
        }
        catch (Exception e) {

            System.out.println(this.peerState.getPeerId() + ": Exiting IncomingConnectionHandler!");
        }
        finally {
            try {
                serverSocket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
}
