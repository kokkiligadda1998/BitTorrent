import java.io.Serializable;

public abstract class Message implements Serializable {

    private int length;
    private Object payload;

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }

    public String toString(){
        return Integer.toString(this.length) + getMessageType().getValue() + this.payload;
    }
    private MessageType messageType = null;

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }
}
