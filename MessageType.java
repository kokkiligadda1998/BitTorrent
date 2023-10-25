// Enum representing different message types in the peer-to-peer communication protocol
public enum MessageType {

    // Enumeration constants for different message types with their corresponding numeric values
    CHOKE(0),
    UNCHOKE(1),
    INTERESTED(2),
    NOT_INTERESTED(3),
    HAVE(4),
    BITFIELD(5),
    REQUEST(6),
    PIECE(7),
    HANDSHAKE(8);

    // Private variable to store the numeric value associated with each message type
    private final int value;

    // Constructor for MessageType enum to associate a numeric value with each message type
    MessageType(int value) {
        this.value = value;
    }

    // Getter method to retrieve the numeric value associated with a message type
    public int getValue() {
        return value;
    }

}