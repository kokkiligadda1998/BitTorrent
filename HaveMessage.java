public class HaveMessage extends Message {

    /**
     * Constructor for creating a Have message.
     *
     * @param reference The piece index being announced.
     */
    public HaveMessage(Integer reference) {
        // Call the superclass constructor to set the message type, length, and payload.
        super.setTypeOfMessage(MessageType.HAVE);
        super.setMessageSize(5);  // Length of a Have message is 5 bytes.
        super.setData(reference);  // Set the payload to the piece index being announced.
    }
}
