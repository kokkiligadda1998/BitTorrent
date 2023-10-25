public class ChokeMessage extends Message {

    public ChokeMessage(){
        super.setMessageType(MessageType.CHOKE);
        super.setLength(1);
        super.setPayload("");
    }

}
