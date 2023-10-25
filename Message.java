import java.io.Serializable;

public abstract class Message implements Serializable {

    // The size of the message
    private int messageSize;

    // The payload (data) of the message
    private Object data;

    // Get the length (size) of the message
    public int getLength() {
        return messageSize;
    }

    // Set the length (size) of the message
    public void setLength(int messageSize) {
        this.messageSize = messageSize;
    }

    // Get the payload (data) of the message
    public Object getPayload() {
        return data;
    }

    // Set the payload (data) of the message
    public void setPayload(Object data) {
        this.data = data;
    }

    // Generate a string representation of the message
    public String toString() {
        // The format is messageSize + messageType + data
        return Integer.toString(this.messageSize) + getMessageType().getValue() + this.data;
    }

    // The type of the message
    private MessageType typeOfMessage = null;

    // Get the type of the message
    public MessageType getMessageType() {
        return typeOfMessage;
    }

    // Set the type of the message
    public void setMessageType(MessageType typeOfMessage) {
        this.typeOfMessage = typeOfMessage;
    }
}
