// Class representing an Interested message in the peer-to-peer communication protocol
public class InterestedMessage extends Message {

    // Constructor for creating an Interested message
    public InterestedMessage(){
        // Call the constructor of the parent class (Message) with MessageType set to INTERESTED
        super.setMessageType(MessageType.INTERESTED);
         // Set the length of the message (1 byte for message type, no payload)
        super.setLength(1);
        // Since Interested message has no payload, set an empty string as the payload
        super.setPayload("");
    }

}