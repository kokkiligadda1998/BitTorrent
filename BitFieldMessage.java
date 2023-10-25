import java.util.BitSet;

// Class representing a BitField message in the peer-to-peer communication protocol
public class BitFieldMessage extends Message {

    // Constructor for creating a BitField message
    public BitFieldMessage(BitSet bitField) {
        // Call the constructor of the parent class (Message) with MessageType set to BITFIELD
        super.setMessageType(MessageType.BITFIELD);
         // Set the payload of the message to the provided BitSet
        Object payload = bitField;
        // Calculate and set the length of the message (1 byte for message type + size of BitSet in bytes)
        super.setLength(1 + bitField.size());
        // Set the payload of the message
        super.setPayload(payload);
    }

    // Getter method to retrieve the BitSet payload from the BitFieldMessage
    public BitSet getPayload() {
        // Cast the payload to BitSet and return it
        return (BitSet) super.getPayload();
    }
}
