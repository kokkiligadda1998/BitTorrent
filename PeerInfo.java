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
	public String getUnchokedOptimisticNeighborID() {
		return unchokedOptimisticNeighborID;
	}

	public void setUnchokedOptimisticNeighborID(String unchokedOptimisticNeighborID) {
		this.unchokedOptimisticNeighborID = unchokedOptimisticNeighborID;
	}

	public boolean getDownloadSuccessFlag() {
		return downloadSuccessFlag.get();
	}

	public void setDownloadSuccessFlag(boolean value) {
		this.downloadSuccessFlag.set(value);
	}

	public ConcurrentHashMap<String, HandlePeers> getPeerLinks() {
		return peerLinks;
	}

	public ServerSocket getConnectionListener() {
		return connectionListener;
	}

	public void setConnectionListener(ServerSocket connectionListener) {
		this.connectionListener = connectionListener;
	}

	public void setTimeCounter1(Timer timeCounter1) {
		this.timeCounter1 = timeCounter1;
	}

	public void setTimeCounter2(Timer timeCounter2) {
		this.timeCounter2 = timeCounter2;
	}

	public void stopScheduledTasks() {
		System.out.println(getPeerIdentifier() + ": stopping scheduler tasks");
		timeCounter1.cancel();
		timeCounter1.purge();
		timeCounter2.cancel();
		timeCounter2.purge();
	}

	public void setInterestedPeers(Map<String, String> interestedPeers) {
		this.interestedPeers = interestedPeers;
	}

	public double getDataTransferSpeed() {
		return dataTransferSpeed;
	}

	public void setDataTransferSpeed(double dataTransferSpeed) {
		this.dataTransferSpeed = dataTransferSpeed;
	}

	public BlockingQueue<Message> getWaitingList(){
		return waitingList;
	}

	public synchronized void putFilePartitioning(int index, byte[] piece) {
		this.filePartitioning.put(index, piece);
		this.itemBit.set(index);
	}

	public Map<String, String> getInterestedPeers() {
		return interestedPeers;
	}

	public int getSelectedPeersSize(){
		return selectedPeers.size();
	}

	public void removeInterestedPeers(String peerIdentifier) {
		interestedPeers.remove(peerIdentifier);
	}

	public void putInterestedPeers(String peerIdentifier) {
		this.interestedPeers.put(peerIdentifier, peerIdentifier);
	}

	public void putPreferredPeers(String peerIdentifier) {
		selectedPeers.put(peerIdentifier, peerIdentifier);
	}

	public void setItemBit(BitSet itemBit) {
		this.itemBit = itemBit;
	}

	public ConcurrentHashMap<Integer, byte[]> getFilePartitioning() {
		return filePartitioning;
	}

	public void setFilePartitioning(ConcurrentHashMap<Integer, byte[]> filePartitioning) {
		this.filePartitioning = filePartitioning;
	}

	public ConcurrentHashMap<String, String> getPreferredNeighbours() {
		return selectedPeers;
	}

	public void setPreferredNeighbours(ConcurrentHashMap<String, String> selectedPeers) {
		this.selectedPeers = selectedPeers;
	}

	public BitSet getItemBit() {
		return itemBit;
	}

	public String getPeerIdentifier() {
		return peerIdentifier;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public int getExchangePort() {
		return exchangePort;
	}

	public boolean FileShareFlag() {
		return fileShareFlag;
	}

	public void setPeerIdentifier(String peerIdentifier) {
		this.peerIdentifier = peerIdentifier;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public void setExchangePort(int exchangePort) {
		this.exchangePort = exchangePort;
	}

	public void setFileShareFlag(boolean fileShareFlag) {
		if (fileShareFlag) {
			if (this.itemBit == null) {
				this.itemBit = new BitSet(ReadConfigFiles.getNumberOfPieces());
			}
			this.itemBit.set(0, ReadConfigFiles.getNumberOfPieces());
		}

		this.fileShareFlag = fileShareFlag;
	}

	public boolean isReceivedFileFlag() {
		return receivedFileFlag;
	}

	public void setFileReceived(boolean receivedFileFlag) {
		this.receivedFileFlag = receivedFileFlag;
	}

	public int getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(int serialNumber) {
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