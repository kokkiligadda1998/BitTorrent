import java.net.Socket;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;

public class Execute implements Runnable{
	private PeerInfo peerState;
	private Logger logger;

	public Execute(String peerId) {
		ReadConfigFiles.setStateFromConfigFiles();
		this.peerState = ReadConfigFiles.getPeerState(peerId);
		this.logger = Logger.getLogger(peerId);
	}

	public void init() {
		FileManager.makeFilesAndDirectories(this.peerState.getPeerId());
		if (peerState.isHasSharedFile()) {
			System.out.println("Shared file found with :"+ peerState.getPeerId());
			this.peerState.setFileSplitMap(FileManager.splitFile());
		}
		else {
			this.peerState.setFileSplitMap(new ConcurrentHashMap<>());
		}
		System.out.println("Peer ID :"+ peerState.getPeerId());
		System.out.println(peerState);

		// accept incoming connections
		Thread t = new Thread(() -> IncomingPeers(peerState));
		t.start();

		try {
			t.join();
			System.out.println(this.peerState.getPeerId() + ": Exiting PeerProcessExecutor");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		init();
	}

    public void IncomingPeers(PeerInfo peerState)
    {
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