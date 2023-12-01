public class PieceMessage extends Message {

    // The index of the piece being sent
    private int reference;

    // Constructor for creating a PieceMessage
    public PieceMessage(byte[] messageData, int reference) {
        // Set the index of the piece
        this.reference = reference;
        
        // Set the message type to PIECE
        super.setTypeOfMessage(MessageType.PIECE);
        
        // Calculate and set the length of the message
        super.setMessageSize(5 + messageData.length);
        
        // Set the payload (data) of the message, which is the actual piece data
        super.setData(messageData);
    }

    // Get the index of the piece being sent
    public int getIndex() {
        return this.reference;
    }
}