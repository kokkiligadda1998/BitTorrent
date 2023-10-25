public class HaveMessage extends Message{

    public HaveMessage(Integer index) {
        super.setMessageType(MessageType.HAVE);
        super.setLength(5);
        super.setPayload(index);
    }
}
