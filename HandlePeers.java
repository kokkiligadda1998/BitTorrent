import java.io.*;
import java.net.Socket;
import java.util.BitSet;

public class HandlePeers implements Runnable {

    private Socket peerSocket = null; // The socket for connecting to the peer
    private PeerInfo peerInfo = null; // The information of the current peer
    private ObjectInputStream inputstream; // The input stream for receiving messages from the peer
    private ObjectOutputStream outputstream; // The output stream for sending messages to the peer
    private Logger l; // The logger for recording events
    private String remotePID; // The peer ID of the remote peer
    private long startTime; // The start time of the connection
    private long stopTime; // The stop time of the connection
    private boolean running = false; // A flag to indicate if the connection is running

    // The constructor that takes a socket and a peer info as parameters
    public HandlePeers(Socket peerSocket, PeerInfo peerInfo) {
        this.peerSocket = peerSocket;
        this.peerInfo = peerInfo;
        this.l = Logger.getLogger(peerInfo.getPeerId()); //// Initialize the logger with the current peer ID
    }

    //// A setter method for setting the remote peer ID
    public void setRemotePeerId(String remotePeerId) {
        this.remotePID = remotePeerId;
    }

    // The run method that is executed when the thread is started
    @Override
    public void run() {
        try {
            running = true; // Set the running flag to true
            outputstream = new ObjectOutputStream(peerSocket.getOutputStream()); // Initialize the output stream

            sendMessage(new HandshakeMessage(this.peerInfo.getPeerId())); // Send a handshake message to the remote peer

            Message receivedMsg = null;
            while (running) { // While the connection is running
                receivedMsg = receiveMessage(); // Receive a message from the remote peer
                System.out.println(this.peerInfo.getPeerId() + ": Received message type: " +
                        receivedMsg.getMessageType().name() + " from " + this.remotePID + ", message: " +
                        receivedMsg.toString()); // Print the message details

                switch (receivedMsg.getMessageType()) { // Switch on the message type
                    case HANDSHAKE: { // If the message is a handshake message
                        processHandshake(receivedMsg); // Process the handshake message
                        break;
                    }
                    case BITFIELD: { // If the message is a bitfield message
                        processBitField(receivedMsg); // Process the bitfield message
                        break;
                    }
                    case INTERESTED: { // If the message is an interested message
                        processInterested(); // Process the interested message
                        break;
                    }
                    case NOT_INTERESTED: { // If the message is a not interested message
                        processNotInterested(); // Process the not interested message
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
        } catch (Exception ex) {
            System.out.println(this.peerInfo.getPeerId() + ": Exiting Handlepeers because of " + ex.getStackTrace()[0]);
            stop(); // Stop the connection if an exception occurs
        }
    }

    private void processInterested() {

        l.logInterestedMessageReceived(remotePID); // Log that an interested message was received

        this.peerInfo.putInterestedNeighbours(remotePID); // Add the remote peer to the list of interested neighbours

    }

    private void processNotInterested() {
        l.logNotInterestedMessageReceived(remotePID); // Log that a not interested message was received
        this.peerInfo.removeInterestedNeighbours(remotePID); // Remove the remote peer from the list of interested
                                                             // neighbours
        if (ReadConfigFiles.hasAllPeersDownloadedFile()) {
            stopAllConnections(); // Stop all connections if all peers have downloaded the file
        }
    }

    private void processBitField(Message msg) {
        BitFieldMessage bitFieldMsg = (BitFieldMessage) msg;

        ReadConfigFiles.getPeers().get(remotePID).setBitField(bitFieldMsg.getPayload());

        int interestingPieceIndex = getNextInterestingPieceIndex(bitFieldMsg.getPayload(), this.peerInfo.getBitField());

        if (interestingPieceIndex == -1) {
            NotInterestedMessage notInterestedMessage = new NotInterestedMessage();
        } else {
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
        } else {
            dataRate = 0;
        }
        ReadConfigFiles.getPeers().get(remotePID).setDataRate(dataRate);
    }

    // This method returns the index of the next interesting piece that the remote
    // peer has and the current peer does not have
    private int getNextInterestingPieceIndex(BitSet remote, BitSet current) {
        BitSet interestingPieces = new BitSet(); // Create a new bit set for storing the interesting pieces
        interestingPieces.or(remote); // Perform a logical OR operation with the remote bit set
        interestingPieces.andNot(current); // Perform a logical AND NOT operation with the current bit set
        return interestingPieces.nextSetBit(0); // Return the index of the first set bit in the interesting pieces bit set
    }

    // This method processes the handshake message from the remote peer
    private void processHandshake(Message response) {
        HandshakeMessage hm = (HandshakeMessage) response; // Cast the message to a handshake message object
        this.remotePID = hm.getPeerId(); // Get the peer ID from the handshake message
        if (ReadConfigFiles.getPeers().containsKey(remotePID)) { // If the peer ID is valid and present in the config file
            System.out.println(remotePID + " validated!"); // Print a validation message
        } else { // If the peer ID is invalid or not present in the config file
            System.out.println(remotePID + " invalid!"); // Print an invalidation message
            return; // Return from the method without further processing
        }
        if (Integer.parseInt(this.peerInfo.getPeerId()) < Integer.parseInt(this.remotePID)) { // If the current peer ID is smaller than the remote peer ID

            l.logTcpConnectionFrom(this.remotePID); // Log that a TCP connection was established from the remote peer
            this.peerInfo.getConnections().put(this.remotePID, this); // Add the remote peer and this handler to the connections map of the current peer
        }
        BitFieldMessage bitfieldMsg = new BitFieldMessage(this.peerInfo.getBitField()); // Create a new bitfield message with the current peer's bitfield

        sendMessage(bitfieldMsg); // Send the bitfield message to the remote peer
    }

    // This method receives a message from the remote peer using the input stream
    public Message receiveMessage() throws IOException, ClassNotFoundException {
        if (inputstream == null) { // If the input stream is not initialized yet
            inputstream = new ObjectInputStream(peerSocket.getInputStream()); // Initialize the input stream with the ocket's input stream
        }
        return (Message) inputstream.readObject(); // Return a message object by reading from the input stream

    }

    // This method sends a message to the remote peer using the output stream
    public synchronized void sendMessage(Message msg) {
        System.out.println(
                this.peerInfo.getPeerId() + ": Sending " + msg.getMessageType().name() + " message: " + msg.toString()); // Print a message details before sending it                                                                                               
        try {
            outputstream.writeObject(msg); // Write a message object to the output stream
            outputstream.flush(); // Flush the output stream to ensure that all bytes are sent
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // This method stops all connections with other peers by calling their stop
    // methods
    public void stopAllConnections() {
        for (HandlePeers peerConnectionHandler : this.peerInfo.getConnections().values()) {
            peerConnectionHandler.stop(); // Stop each connection handler in the connections map of the current peer
        }
    }

    // This method stops this connection with a single peer by closing all resources
    // and setting running flag to false
    public void stop() {
        try {
            System.out.println(this.peerInfo.getPeerId() + ": Stopping tasks");
            this.peerInfo.stopScheduledTasks(); // Stop any scheduled tasks for this connection handler
            this.peerInfo.getServerSocket().close(); // Close the server socket for this connection handler
            running = false; // Set running flag to false
            outputstream.close(); // Close output stream
            inputstream.close(); // Close input stream
            System.out.println(this.peerInfo.getPeerId() + ": Stopped tasks");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
