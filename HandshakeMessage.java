// Class representing a Handshake message in the peer-to-peer communication protocol
public class HandshakeMessage extends Message {

    // The fixed header for the handshake message
    private final String header = "P2PBITTORRENT";
    // Fixed zero bits in the handshake message
    private final String ZERO_BITS = "0000000000";
    // Peer ID of the sender/receiver of the handshake message
    private String peerId;

    // Constructor for creating a Handshake message with a specific peer ID
    public HandshakeMessage(String peerId) {
        // Call the constructor of the parent class (Message) with MessageType set to HANDSHAKE
        this.setMessageType(MessageType.HANDSHAKE);
        // Set the peer ID for this handshake message
        this.peerId = peerId;
    }

    // Method to convert the Handshake message to a string representation
    public String toString(){
        // Concatenate the header, zero bits, and peer ID to form the handshake message string
        return this.header + this.ZERO_BITS + this.peerId;
    }

    // Getter method to retrieve the peer ID from the Handshake message
    public String getPeerId(){
        return peerId;
    }

    // Method to validate the received peer ID in the handshake message
    public boolean validate(String peerId) {
        // Compare the received peer ID with the expected peer ID in the handshake message
        // Return true if the peer ID is valid, false otherwise
        return header == "P2PBITTORRENT" && peerId == this.peerId;
    }
}
