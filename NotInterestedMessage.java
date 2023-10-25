// Class representing a Not Interested message in the peer-to-peer communication protocol
public class NotInterestedMessage extends Message {

    // Constructor for creating a Not Interested message
    public NotInterestedMessage(){
        // Call the constructor of the parent class (Message) with MessageType set to NOT_INTERESTED
        super.setMessageType(MessageType.NOT_INTERESTED);
        // Set the length of the message (1 byte for message type, no payload)
        super.setLength(1);
        // Since Not Interested message has no payload, set an empty string as the payload
        super.setPayload("");
    }

}
