public class UnchokeMessage extends Message {

    public UnchokeMessage(){
        super.setMessageType(MessageType.UNCHOKE);
        super.setLength(1);
        super.setPayload("");
    }

}