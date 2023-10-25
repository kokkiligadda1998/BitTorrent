import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The ReadConfigFiles class provides methods to read configuration files and manage peer information.
 */
public class ReadConfigFiles {

	// Configuration parameters read from the Common.cfg file
	public static int numOfPieces;
	public static int numOfPreffNeighbors;
	public static int IntervalOfUnchoke;
	public static int IntervalOfOptimistUnchoke;
	public static String fName;
	public static long fSize;
	public static int sizeOfPiece;

	// Peer information stored in a ConcurrentHashMap
	private static ConcurrentHashMap<String, PeerInfo> peerMap = new ConcurrentHashMap<>();

	/**
     * Gets the peer state for a given peer ID from the peerMap.
     * param id Peer ID
     * return PeerInfo object representing the peer's state
     */
	public static PeerInfo getPeerState(String id) {
		return peerMap.get(id);
	}

	/**
     * Gets the map containing all peer information.
     * return ConcurrentHashMap containing peer information
     */
	public static Map<String, PeerInfo> getPeers() {
		return peerMap;
	}

	 /**
     * Gets the total number of peers in the system.
     * return Number of peers
     */
	public static int numberOfPeers() {
		return peerMap.size();
	}

	public static int getNumberOfPieces() {
		return numOfPieces;
	}

	public static void setNumberOfPieces(int numOfPieces) {
		ReadConfigFiles.numOfPieces = numOfPieces;
	}

	public static int getNumberOfPreferredNeighbors() {
		return numOfPreffNeighbors;
	}

	public static void setNumberOfPreferredNeighbors(int numOfPreffNeighbors) {
		ReadConfigFiles.numOfPreffNeighbors = numOfPreffNeighbors;
	}

	public static int getUnchokingInterval() {
		return IntervalOfUnchoke;
	}

	public static void setUnchokingInterval(int IntervalOfUnchoke) {
		ReadConfigFiles.IntervalOfUnchoke = IntervalOfUnchoke;
	}

	public static int getOptimisticUnchokingInterval() {
		return IntervalOfOptimistUnchoke;
	}

	public static void setOptimisticUnchokingInterval(int IntervalOfOptimistUnchoke) {
		ReadConfigFiles.IntervalOfOptimistUnchoke = IntervalOfOptimistUnchoke;
	}

	public static String getFileName() {
		return fName;
	}

	public static void setFileName(String fName) {
		ReadConfigFiles.fName = fName;
	}

	public static long getFileSize() {
		return fSize;
	}

	public static void setFileSize(long fSize) {
		ReadConfigFiles.fSize = fSize;
	}

	public static int getPieceSize() {
		return sizeOfPiece;
	}

	public static void setPieceSize(int sizeOfPiece) {
		ReadConfigFiles.sizeOfPiece = sizeOfPiece;
	}

	/**
     * Reads peer information from the PeerInfo.cfg file and populates the peerMap.
     */
	public static void setPeerMapFromProperties() {
		Scanner scan = null;
		int counter = 1;
		try {
			scan = new Scanner(new File(System.getProperty("user.dir") + File.separatorChar + "PeerInfo.cfg"));
			while (scan.hasNextLine()) {
				String arr[] = scan.nextLine().split(" ");
				PeerInfo peersInfo = new PeerInfo();
				peersInfo.setSequenceId(counter++);
				peersInfo.setPeerId(arr[0]);
				peersInfo.setHostName(arr[1]);
				peersInfo.setPort(Integer.parseInt(arr[2]));
				if (arr[3].equals("1")) {
					peersInfo.setHasSharedFile(true);
				}
				else {
					peersInfo.setHasSharedFile(false);
				}
				peerMap.put(arr[0], peersInfo);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			scan.close();
		}
	}

	/**
     * Reads configuration settings from Common.cfg and initializes the class variables.
     */
	public static void setStateFromConfigFiles() {

		Properties props = new Properties();
		try {
			FileInputStream fis = new FileInputStream(System.getProperty("user.dir") + File.separatorChar + "Common.cfg");
			props.load(fis);
		}
		catch (IOException ex) {
			throw new RuntimeException("File not found : " + ex.getMessage());
		}

		// Initialize class variables from the configuration settings
		fName = props.get("FileName").toString();
		fSize = Long.parseLong(props.get("FileSize").toString());
		numOfPreffNeighbors = Integer.parseInt(props.get("NumberOfPreferredNeighbors").toString());
		IntervalOfOptimistUnchoke = Integer.parseInt(props.get("UnchokingInterval").toString());
		sizeOfPiece = Integer.parseInt(props.getProperty("PieceSize"));
		IntervalOfUnchoke = Integer.parseInt(props.getProperty("OptimisticUnchokingInterval"));
        numOfPieces = (int)Math.ceil((double)fSize / sizeOfPiece);
        System.out.println("Number of pieces: " + numOfPieces);
		System.out.println(System.getProperty("user.dir") + File.separatorChar + ReadConfigFiles.fName);
		setPeerMapFromProperties();

	}

	/**
     * Checks if all peers have downloaded the complete file.
     * return true if all peers have complete file, false otherwise
     */
	public static synchronized boolean hasAllPeersDownloadedFile() {
		for (PeerInfo peerState: peerMap.values()) {
			if (peerState.getBitField().nextClearBit(0) != numOfPieces) {
				System.out.println(peerState.getPeerId() + " has incomplete file, so not exiting");
				return false;
			}
		}
		return true;
	}

}