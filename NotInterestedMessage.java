public class NotInterestedMessage extends Message {

    public NotInterestedMessage(){
        super.setMessageType(MessageType.NOT_INTERESTED);
        super.setLength(1);
        super.setPayload("");
    }

}
