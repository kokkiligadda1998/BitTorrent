// Class representing a Request message in the peer-to-peer communication protocol
public class RequestMessage extends Message{

    // Constructor for creating a Request message with a specific piece index
    public RequestMessage(Integer index) {
        // Call the constructor of the parent class (Message) with MessageType set to REQUEST
        super.setMessageType(MessageType.REQUEST);
        // Set the length of the message (1 byte for message type + 4 bytes for the piece index)
        super.setLength(5);
        // Set the payload of the message to the provided piece index
        super.setPayload(index);
    }
}