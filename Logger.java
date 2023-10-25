import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/***
 * Logger utility class designed as Singleton to
 * avoid concurrency issues
 */
public class Logger {

	private static Map<String, Logger> map = new HashMap<>();

	public PrintWriter printWriter = null;

	private String peerId;

	public static Logger getLogger(String peerId) {
		synchronized (Logger.class) {
			if (map.get(peerId) == null) {
				map.put(peerId, new Logger(peerId));
			}
		}
		return map.get(peerId);
	}

	/**
	 * Constructor: Creates directories for logging
	 * and initializes PrintWriter
	 */
	private Logger(String peerId) {
		try {
			System.out.println("Logger instantiated for peer: "
					+ peerId);
			this.peerId = peerId;
			File file = makeLogDirectoryForPeer(peerId);
			initPrintWriter(file);
		}
		catch (Exception ex) {
			System.out.println("Exception "+ ex.getMessage());
		}
	}

	private File makeLogDirectoryForPeer(String peerId) throws Exception{

		String path = System.getProperty("user.dir") + File.separatorChar + "btorrent/log_peer_" + peerId
				+ ".log";

		File file = new File(path);
		file.getParentFile().mkdirs();

		return file;
	}

	private void initPrintWriter(File file) throws IOException{

		file.createNewFile();
		FileOutputStream fileOutputStream = new FileOutputStream(file, false);
		printWriter = new PrintWriter(fileOutputStream, true);
	}

	private String getTimeStamp() {

		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		return timestamp.toString();
	}

	private void writeFile(String message) {

		synchronized (this) {
			printWriter.println(message);
		}
	}

	public void logReceivedHaveMessage(String fromId, int pieceIndex) {

		writeFile(getTimeStamp()
				+ ": Peer "
				+ peerId
				+ " received the 'have' message from "
				+ fromId
				+ " for the piece "
				+ pieceIndex + ".");
	}



	public void logTcpConnectionTo(String toId) {
		writeFile(getTimeStamp()
				+ ": Peer "
				+ peerId
				+ " makes a connection to Peer "
				+ toId
				+ ".");
	}

	public void logTcpConnectionFrom(String fromId) {
		writeFile(getTimeStamp()
				+ ": Peer "
				+ peerId
				+ " is connected from Peer "
				+ fromId
				+ ".");
	}


	public void logNewOptimisticallyUnchokedNeighbor(String unchokedNeighbor) {

		writeFile(getTimeStamp()
				+ ": Peer "
				+ peerId
				+ " has the optimistically unchoked neighbor "
				+ unchokedNeighbor
				+ ".");
	}

	public void logUnchokingEvent(String peerId1) {

		writeFile(getTimeStamp()
				+ ": Peer "
				+ peerId
				+ " is unchoked by "
				+ peerId1
				+ ".");
	}

	public void logChokingEvent(String peerId1) {

		writeFile(getTimeStamp()
				+ ": Peer "
				+ peerId
				+ " is choked by "
				+ peerId1
				+ ".");
	}


	public void logInterestedMessageReceived(String from) {

		writeFile(getTimeStamp()
				+ ": Peer "
				+ peerId
				+ " received the 'interested' messaging from "
				+ from
				+ ".");
	}


	public void logNotInterestedMessageReceived(String from) {

		writeFile(getTimeStamp()
				+ ": Peer "
				+ peerId
				+ " received the 'not interested' messaging from "
				+ from
				+ ".");
	}


	public void logPieceDownloadComplete(String from, int pieceIndex, int numberOfPieces) {

		writeFile(getTimeStamp()
				+ ": Peer "
				+ peerId
				+ " has downloaded the piece "
				+ pieceIndex
				+ " from "
				+ from
				+ "."
				+ "Now the number of pieces it has is "
				+ numberOfPieces);

	}

	public void logDownloadComplete() {

		writeFile(getTimeStamp()
				+ "Peer "
				+ peerId
				+ " has downloaded the complete file.");
	}

}