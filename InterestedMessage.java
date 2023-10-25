public class InterestedMessage extends Message {

    public InterestedMessage(){
        super.setMessageType(MessageType.INTERESTED);
        super.setLength(1);
        super.setPayload("");
    }

}
