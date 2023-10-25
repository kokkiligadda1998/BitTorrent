import java.io.*;
import java.net.Socket;
import java.util.BitSet;

public class HandlePeers implements Runnable{

    private Socket peerSocket = null;
    private PeerInfo peerState = null;
    private ObjectInputStream is;
    private ObjectOutputStream os;
    private Logger logger;
    private String remotePeerId;
    private long startTime;
    private long stopTime;
    private boolean running = false;

    public HandlePeers(Socket peerSocket, PeerInfo peerState) {
        this.peerSocket = peerSocket;
        this.peerState = peerState;
        this.logger = Logger.getLogger(peerState.getPeerId());
    }

    public void setRemotePeerId(String remotePeerId) {
        this.remotePeerId = remotePeerId;
    }

    @Override
    public void run() {
        try
        {
            running = true;
            os = new ObjectOutputStream(peerSocket.getOutputStream());

            sendMessage(new HandshakeMessage(this.peerState.getPeerId()));

            Message receivedMsg = null;
            while (running) {
                receivedMsg = receiveMessage();
                System.out.println(this.peerState.getPeerId() + ": Received message type: " +
                        receivedMsg.getMessageType().name() + " from " + this.remotePeerId + ", message: " +
                        receivedMsg.toString());

                switch (receivedMsg.getMessageType()) {
                    case HANDSHAKE: {
                        processHandshake(receivedMsg);
                        break;
                    }
                    case BITFIELD: {
                        processBitField(receivedMsg);
                        break;
                    }
                    case INTERESTED: {
                        processInterested();
                        break;
                    }
                    case NOT_INTERESTED: {
                        processNotInterested();
                        break;
                    }
                    case REQUEST: {
                        // TODO: process request
                        break;
                    }
                    case PIECE: {
                        // TODO: process piece
                        break;
                    }
                    case HAVE: {
                        // TODO: process have
                        break;
                    }
                    case CHOKE: {
                        // TODO: process choke
                        break;
                    }
                    case UNCHOKE: {
                        // TODO: process choke
                        break;
                    }
                    default:
                }
            }
        }
        catch (Exception ex)
        {
            System.out.println(this.peerState.getPeerId() + ": Exiting PeerConnectionHandler because of " + ex.getStackTrace()[0]);
            stop();
        }
    }


    private void processInterested() {

        logger.logInterestedMessageReceived(remotePeerId);

        this.peerState.putInterestedNeighbours(remotePeerId);

    }

    private void processNotInterested() {
        logger.logNotInterestedMessageReceived(remotePeerId);
        this.peerState.removeInterestedNeighbours(remotePeerId);
        if (ReadConfigFiles.hasAllPeersDownloadedFile()) {
            stopAllConnections();
        }
    }

    private void processBitField(Message message){
        BitFieldMessage bitFieldMessage = (BitFieldMessage) message;

        ReadConfigFiles.getPeers().get(remotePeerId).setBitField(bitFieldMessage.getPayload());

        int interestingPieceIndex = getNextInterestingPieceIndex(bitFieldMessage.getPayload(), this.peerState.getBitField());

        if (interestingPieceIndex == -1) {
            NotInterestedMessage notInterestedMessage = new NotInterestedMessage();
        }
        else {
            InterestedMessage interestedMessage = new InterestedMessage();
            sendMessage(interestedMessage);
            RequestMessage requestMessage = new RequestMessage(interestingPieceIndex);
            startTime = System.currentTimeMillis();
            sendMessage(requestMessage);
        }

    }

    private void setDataRate(int size) {
        double dataRate;
        if (Math.abs(stopTime - startTime) > 0) {
            dataRate = size / (stopTime - startTime);
        }
        else {
            dataRate = 0;
        }

        System.out.println("Setting data rate " + dataRate);
        ReadConfigFiles.getPeers().get(remotePeerId).setDataRate(dataRate);
    }

    private int getNextInterestingPieceIndex(BitSet remote, BitSet current) {
        BitSet interestingPieces = new BitSet();
        interestingPieces.or(remote);
        interestingPieces.andNot(current);
        return interestingPieces.nextSetBit(0);
    }



    private void processHandshake(Message response) {
        HandshakeMessage handshakeMessage = (HandshakeMessage) response;
        this.remotePeerId = handshakeMessage.getPeerId();
        if (ReadConfigFiles.getPeers().containsKey(remotePeerId)) {
            System.out.println(remotePeerId + " validated!");
        }
        else {
            System.out.println(remotePeerId + " invalid!");
            return;
        }
        if (Integer.parseInt(this.peerState.getPeerId()) < Integer.parseInt(this.remotePeerId)) {
            logger.logTcpConnectionFrom(this.remotePeerId);
            this.peerState.getConnections().put(this.remotePeerId, this);
        }
        BitFieldMessage bitfieldMessage = new BitFieldMessage(this.peerState.getBitField());
        sendMessage(bitfieldMessage);
    }

    public Message receiveMessage() throws IOException, ClassNotFoundException {
        if (is == null) {
            is = new ObjectInputStream(peerSocket.getInputStream());
        }
        return (Message) is.readObject();

    }

    public synchronized void sendMessage(Message message) {
        System.out.println(this.peerState.getPeerId() + ": Sending " + message.getMessageType().name() + " message: " + message.toString());
        try {
            os.writeObject(message);
            os.flush();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void stopAllConnections() {
        for (HandlePeers peerConnectionHandler: this.peerState.getConnections().values()) {
            peerConnectionHandler.stop();
        }
    }

    public void stop() {
        try {
            System.out.println(this.peerState.getPeerId() + ": Stopping tasks");
            this.peerState.stopScheduledTasks();
            this.peerState.getServerSocket().close();
            running = false;
            os.close();
            is.close();
            System.out.println(this.peerState.getPeerId() + ": Stopped tasks");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
