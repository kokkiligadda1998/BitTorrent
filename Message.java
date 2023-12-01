import java.io.Serializable;

public abstract class Message implements Serializable {

    // The size of the message
    private int messageSize;

    // The payload (data) of the message
    private Object data;

    // Get the length (size) of the message
    public int getMessageSize() {
        return messageSize;
    }

    // Set the length (size) of the message
    public void setMessageSize(int messageSize) {
        this.messageSize = messageSize;
    }

    // Get the payload (data) of the message
    public Object getData() {
        return data;
    }

    // Set the payload (data) of the message
    public void setData(Object data) {
        this.data = data;
    }

    // Generate a string representation of the message
    public String toString() {
        // The format is messageSize + messageType + data
        return Integer.toString(this.messageSize) + getTypeOfMessage().getValue() + this.data;
    }

    // The type of the message
    private MessageType typeOfMessage = null;

    // Get the type of the message
    public MessageType getTypeOfMessage() {
        return typeOfMessage;
    }

    // Set the type of the message
    public void setTypeOfMessage(MessageType typeOfMessage) {
        this.typeOfMessage = typeOfMessage;
    }
}
