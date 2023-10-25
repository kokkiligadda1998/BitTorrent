// Class representing an Unchoke message in the peer-to-peer communication protocol
public class UnchokeMessage extends Message {

    // Constructor for creating an Unchoke message
    public UnchokeMessage(){
        // Call the constructor of the parent class (Message) with MessageType set to UNCHOKE
        super.setMessageType(MessageType.UNCHOKE);
        // Set the length of the message (1 byte for message type, no payload)
        super.setLength(1);
        // Since Unchoke message has no payload, set an empty string as the payload
        super.setPayload("");
    }

}
