import java.net.ServerSocket;
import java.util.BitSet;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class PeerInfo {

	private int serialNumber;  // A unique identifier for the peer.
    private String peerIdentifier;  // Peer's identifier.
    private String deviceName;  // Peer's device name or hostname.
    private int exchangePort;  // Port for exchanging data.
    private boolean fileShareFlag;  // Flag indicating whether the peer shares a file.
    private boolean receivedFileFlag = false;  // Flag indicating whether the peer has received a file.
    private BitSet itemBit;  // A BitSet representing the availability of file pieces.
    private ConcurrentHashMap<Integer, byte[]> filePartitioning;  // Mapping of file pieces.
    private ConcurrentHashMap<String, String> selectedPeers = new ConcurrentHashMap<>();  // Preferred neighbors.
    private String unchokedOptimisticNeighborID;  // ID of the optimistically unchoked neighbor.
    private Map<String, String> interestedPeers = new ConcurrentHashMap<>();  // Peers interested in this peer's data.
    private ConcurrentHashMap<String, HandlePeers> peerLinks = new ConcurrentHashMap<>();  // Connections to other peers.
    private BlockingQueue<Message> waitingList = new LinkedBlockingQueue<>();  // Queue for messages.
    private double dataTransferSpeed = 0;  // Data transfer speed.
    private Timer timeCounter1;  // Timer for preferred neighbors.
    private Timer timeCounter2;  // Timer for optimistically unchoked neighbor.
    private ServerSocket connectionListener;  // ServerSocket for incoming connections.
    private AtomicBoolean downloadSuccessFlag = new AtomicBoolean(false);  // Flag for download completion.


	// Getters and setters for various attributes...
	public String getOptimisticUnchokedPeerId() {
		return unchokedOptimisticNeighborID;
	}

	public void setOptimisticUnchokedPeerId(String unchokedOptimisticNeighborID) {
		this.unchokedOptimisticNeighborID = unchokedOptimisticNeighborID;
	}

	public boolean getDownloadComplete() {
		return downloadSuccessFlag.get();
	}

	public void setDownloadComplete(boolean value) {
		this.downloadSuccessFlag.set(value);
	}

	public ConcurrentHashMap<String, HandlePeers> getConnections() {
		return peerLinks;
	}

	public ServerSocket getServerSocket() {
		return connectionListener;
	}

	public void setServerSocket(ServerSocket connectionListener) {
		this.connectionListener = connectionListener;
	}

	public void setTimer1(Timer timeCounter1) {
		this.timeCounter1 = timeCounter1;
	}

	public void setTimer2(Timer timeCounter2) {
		this.timeCounter2 = timeCounter2;
	}

	public void stopScheduledTasks() {
		System.out.println(getPeerId() + ": stopping scheduler tasks");
		timeCounter1.cancel();
		timeCounter1.purge();
		timeCounter2.cancel();
		timeCounter2.purge();
	}

	public void setInterestedNeighbours(Map<String, String> interestedPeers) {
		this.interestedPeers = interestedPeers;
	}

	public double getDataRate() {
		return dataTransferSpeed;
	}

	public void setDataRate(double dataTransferSpeed) {
		this.dataTransferSpeed = dataTransferSpeed;
	}

	public BlockingQueue<Message> getQueue(){
		return waitingList;
	}

	public synchronized void putFileSplitMap(int index, byte[] piece) {
		this.filePartitioning.put(index, piece);
		this.itemBit.set(index);
	}

	public Map<String, String> getInterestedNeighbours() {
		return interestedPeers;
	}

	public int preferredNeighboursCount(){
		return selectedPeers.size();
	}

	public void removeInterestedNeighbours(String peerIdentifier) {
		interestedPeers.remove(peerIdentifier);
	}

	public void putInterestedNeighbours(String peerIdentifier) {
		this.interestedPeers.put(peerIdentifier, peerIdentifier);
	}

	public void putPreferredNeighbours(String peerIdentifier) {
		selectedPeers.put(peerIdentifier, peerIdentifier);
	}

	public void setBitField(BitSet itemBit) {
		this.itemBit = itemBit;
	}

	public ConcurrentHashMap<Integer, byte[]> getFileSplitMap() {
		return filePartitioning;
	}

	public void setFileSplitMap(ConcurrentHashMap<Integer, byte[]> filePartitioning) {
		this.filePartitioning = filePartitioning;
	}

	public ConcurrentHashMap<String, String> getPreferredNeighbours() {
		return selectedPeers;
	}

	public void setPreferredNeighbours(ConcurrentHashMap<String, String> selectedPeers) {
		this.selectedPeers = selectedPeers;
	}

	public BitSet getBitField() {
		return itemBit;
	}

	public String getPeerId() {
		return peerIdentifier;
	}

	public String getHostName() {
		return deviceName;
	}

	public int getPort() {
		return exchangePort;
	}

	public boolean isHasSharedFile() {
		return fileShareFlag;
	}

	public void setPeerId(String peerIdentifier) {
		this.peerIdentifier = peerIdentifier;
	}

	public void setHostName(String deviceName) {
		this.deviceName = deviceName;
	}

	public void setPort(int exchangePort) {
		this.exchangePort = exchangePort;
	}

	public void setHasSharedFile(boolean fileShareFlag) {
		if (fileShareFlag) {
			if (this.itemBit == null) {
				this.itemBit = new BitSet(ReadConfigFiles.getNumberOfPieces());
			}
			this.itemBit.set(0, ReadConfigFiles.getNumberOfPieces());
		}

		this.fileShareFlag = fileShareFlag;
	}

	public boolean isFileReceived() {
		return receivedFileFlag;
	}

	public void setFileReceived(boolean receivedFileFlag) {
		this.receivedFileFlag = receivedFileFlag;
	}

	public int getSequenceId() {
		return serialNumber;
	}

	public void setSequenceId(int serialNumber) {
		this.serialNumber = serialNumber;
	}
// Constructor to initialize the peer's information.
	public PeerInfo(String peerIdentifier, String deviceName, int exchangePort, boolean fileShareFlag) {
		this.peerIdentifier = peerIdentifier;
		this.deviceName = deviceName;
		this.exchangePort = exchangePort;
		this.fileShareFlag = fileShareFlag;
	}
// Constructor to initialize the peer's information.
	public PeerInfo(){
		this.itemBit = new BitSet(ReadConfigFiles.getNumberOfPieces());
	}

	@Override
	public String toString() {
		return "PeerState{" +
				"serialNumber=" + serialNumber +
				", peerIdentifier='" + peerIdentifier + '\'' +
				", deviceName='" + deviceName + '\'' +
				", exchangePort=" + exchangePort +
				", fileShareFlag=" + fileShareFlag +
				", receivedFileFlag=" + receivedFileFlag +
				'}';
	}
}