import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/*
 * Logger utility class designed as Singleton to
 * avoid concurrency issues
 */
public class Logger {

	// Map to store unique loggers based on peer identifiers.
    private static Map<String, Logger> loggerMap = new HashMap<>();

    // PrintWriter for writing log messages to a file.
    public PrintWriter textPrinter = null;

    // Peer's identifier for which the logger is created.
    private String peerNodeIdentifier;
	
     /**
     * Get a logger for a specific peer.
     *
     * @param peerNodeIdentifier Peer's unique identifier.
     * @return Logger instance for the peer.
     */
	public static Logger getLogger(String peerNodeIdentifier) {
		synchronized (Logger.class) {
			if (loggerMap.get(peerNodeIdentifier) == null) {
				loggerMap.put(peerNodeIdentifier, new Logger(peerNodeIdentifier));
			}
		}
		return loggerMap.get(peerNodeIdentifier);
	}

	/**
     * Constructor: Creates directories for logging
     * and initializes PrintWriter.
     *
     * @param peerNodeIdentifier Peer's unique identifier.
     */
	private Logger(String peerNodeIdentifier) {
		try {
			System.out.println("Logger for peers has started: "
					+ peerNodeIdentifier);
			this.peerNodeIdentifier = peerNodeIdentifier;
			File file = makeLogDirectoryForPeer(peerNodeIdentifier);
			initPrintWriter(file);
		}
		catch (Exception ex) {
			System.out.println("Exception: "+ ex.getMessage());
		}
	}
     /**
     * Create the log directory for a peer based on its identifier.
     *
     * @param peerNodeIdentifier Peer's unique identifier.
     * @return File object representing the log file.
     * @throws Exception if an error occurs.
     */
	private File makeLogDirectoryForPeer(String peerNodeIdentifier) throws Exception{

		String path = System.getProperty("user.dir") + File.separatorChar + "btorrent/log_peer_" + peerNodeIdentifier
				+ ".log";

		File file = new File(path);
		file.getParentFile().mkdirs();

		return file;
	}
    /**
     * Initialize the PrintWriter for writing log messages to a file.
     *
     * @param file File object representing the log file.
     * @throws IOException if an error occurs during initialization.
     */
	private void initPrintWriter(File file) throws IOException{

		file.createNewFile();
		FileOutputStream fileOutputStream = new FileOutputStream(file, false);
		textPrinter = new PrintWriter(fileOutputStream, true);
	}
    /**
     * Get the current timestamp as a string.
     *
     * @return Timestamp string.
     */
	private String getTimeStamp() {

		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		return timestamp.toString();
	}

	private void writeFile(String message) {

		synchronized (this) {
			textPrinter.println(message);
		}
	}

	 /**
     * Write a log message indicating that a peer received a 'have' message for a specific piece.
     *
     * @param fromId      Identifier of the peer from which the 'have' message is received.
     * @param pieceIndex  Index of the piece.
     */

	public void logReceivedHaveMessage(String fromId, int pieceIndex) {

		writeFile(getTimeStamp()
				+ ": Peer "
				+ peerNodeIdentifier
				+ " has obtained the 'have' message from "
				+ fromId
				+ " for this piece "
				+ pieceIndex + ".");
	}



	public void logTcpConnectionTo(String toId) {
		writeFile(getTimeStamp()
				+ ": Peer "
				+ peerNodeIdentifier
				+ " has made the connection to Peer "
				+ toId
				+ ".");
	}

	public void logTcpConnectionFrom(String fromId) {
		writeFile(getTimeStamp()
				+ ": Peer "
				+ peerNodeIdentifier
				+ " got its connection from Peer "
				+ fromId
				+ ".");
	}


	public void logNewOptimisticallyUnchokedNeighbor(String unchokedNeighbor) {

		writeFile(getTimeStamp()
				+ ": Peer "
				+ peerNodeIdentifier
				+ " now has the unchoked optimistic neighbor "
				+ unchokedNeighbor
				+ ".");
	}

	public void logUnchokingEvent(String peerId1) {

		writeFile(getTimeStamp()
				+ ": Peer "
				+ peerNodeIdentifier
				+ " is now unchoked by "
				+ peerId1
				+ ".");
	}

	public void logChokingEvent(String peerId1) {

		writeFile(getTimeStamp()
				+ ": Peer "
				+ peerNodeIdentifier
				+ " is now choked by "
				+ peerId1
				+ ".");
	}


	public void logInterestedMessageReceived(String from) {

		writeFile(getTimeStamp()
				+ ": Peer "
				+ peerNodeIdentifier
				+ " has obtained the 'interested' message from "
				+ from
				+ ".");
	}


	public void logNotInterestedMessageReceived(String from) {

		writeFile(getTimeStamp()
				+ ": Peer "
				+ peerNodeIdentifier
				+ " has obtained the 'not interested' message from "
				+ from
				+ ".");
	}


	public void logPieceDownloadComplete(String from, int pieceIndex, int numberOfPieces) {

		writeFile(getTimeStamp()
				+ ": Peer "
				+ peerNodeIdentifier
				+ " has now installed the piece "
				+ pieceIndex
				+ " from "
				+ from
				+ "."
				+ "The number of pieces it now has is "
				+ numberOfPieces);

	}

	/**
     * Write a log message indicating that a peer has completed downloading the entire file.
     */
	public void logDownloadComplete() {

		writeFile(getTimeStamp()
				+ "Peer "
				+ peerNodeIdentifier
				+ " has now installed the complete file.");
	}

}