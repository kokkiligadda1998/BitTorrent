// Class representing a Choke message in the peer-to-peer communication protocol
public class ChokeMessage extends Message {

    // Constructor for creating a Choke message
    public ChokeMessage(){
        // Call the constructor of the parent class (Message) with MessageType set to CHOKE
        super.setMessageType(MessageType.CHOKE);
        // Set the length of the message (1 byte for message type, no payload)
        super.setLength(1);
         // Since Choke message has no payload, set an empty string as the payload
        super.setPayload("");
    }

}
