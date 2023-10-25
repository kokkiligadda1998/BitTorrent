public class RequestMessage extends Message{

    public RequestMessage(Integer index) {
        super.setMessageType(MessageType.REQUEST);
        super.setLength(5);
        super.setPayload(index);
    }
}