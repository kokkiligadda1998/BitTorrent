import java.util.BitSet;

// Class representing a BitField message in the peer-to-peer communication protocol
public class BitFieldMessage extends Message {

    // Constructor for creating a BitField message
    public BitFieldMessage(BitSet bitField) {
        // Call the constructor of the parent class (Message) with MessageType set to BITFIELD
        super.setTypeOfMessage(MessageType.BITFIELD);
         // Set the payload of the message to the provided BitSet
        Object payload = bitField;
        // Calculate and set the length of the message (1 byte for message type + size of BitSet in bytes)
        super.setMessageSize(1 + bitField.size());
        // Set the payload of the message
        super.setData(payload);
    }

    // Getter method to retrieve the BitSet payload from the BitFieldMessage
    public BitSet getData() {
        // Cast the payload to BitSet and return it
        return (BitSet) super.getData();
    }
}
