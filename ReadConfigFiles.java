import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ReadConfigFiles {

	public static int numberOfPieces;
	public static int numberOfPreferredNeighbors;
	public static int unchokingInterval;
	public static int optimisticUnchokingInterval;
	public static String fileName;
	public static long fileSize;
	public static int pieceSize;

	private static ConcurrentHashMap<String, PeerInfo> peers = new ConcurrentHashMap<>();

	public static PeerInfo getPeerState(String id) {
		return peers.get(id);
	}

	public static Map<String, PeerInfo> getPeers() {
		return peers;
	}

	public static int numberOfPeers() {
		return peers.size();
	}

	public static int getNumberOfPieces() {
		return numberOfPieces;
	}

	public static void setNumberOfPieces(int numberOfPieces) {
		ReadConfigFiles.numberOfPieces = numberOfPieces;
	}

	public static int getNumberOfPreferredNeighbors() {
		return numberOfPreferredNeighbors;
	}

	public static void setNumberOfPreferredNeighbors(int numberOfPreferredNeighbors) {
		ReadConfigFiles.numberOfPreferredNeighbors = numberOfPreferredNeighbors;
	}

	public static int getUnchokingInterval() {
		return unchokingInterval;
	}

	public static void setUnchokingInterval(int unchokingInterval) {
		ReadConfigFiles.unchokingInterval = unchokingInterval;
	}

	public static int getOptimisticUnchokingInterval() {
		return optimisticUnchokingInterval;
	}

	public static void setOptimisticUnchokingInterval(int optimisticUnchokingInterval) {
		ReadConfigFiles.optimisticUnchokingInterval = optimisticUnchokingInterval;
	}

	public static String getFileName() {
		return fileName;
	}

	public static void setFileName(String fileName) {
		ReadConfigFiles.fileName = fileName;
	}

	public static long getFileSize() {
		return fileSize;
	}

	public static void setFileSize(long fileSize) {
		ReadConfigFiles.fileSize = fileSize;
	}

	public static int getPieceSize() {
		return pieceSize;
	}

	public static void setPieceSize(int pieceSize) {
		ReadConfigFiles.pieceSize = pieceSize;
	}

	public static void setPeerMapFromProperties() {
		Scanner sc = null;
		int count = 1;
		try {
			sc = new Scanner(new File(System.getProperty("user.dir") + File.separatorChar + "PeerInfo.cfg"));
			while (sc.hasNextLine()) {
				String arr[] = sc.nextLine().split(" ");
				PeerInfo peer = new PeerInfo();
				peer.setSequenceId(count++);
				peer.setPeerId(arr[0]);
				peer.setHostName(arr[1]);
				peer.setPort(Integer.parseInt(arr[2]));
				if (arr[3].equals("1")) {
					peer.setHasSharedFile(true);
				}
				else {
					peer.setHasSharedFile(false);
				}
				peers.put(arr[0], peer);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			sc.close();
		}
	}

	public static void setStateFromConfigFiles() {

		Properties properties = new Properties();
		try {
			FileInputStream in = new FileInputStream(System.getProperty("user.dir") + File.separatorChar + "Common.cfg");
			properties.load(in);
		}
		catch (IOException ex) {
			throw new RuntimeException("File not found : " + ex.getMessage());
		}

		fileName = properties.get("FileName").toString();
		fileSize = Long.parseLong(properties.get("FileSize").toString());
		numberOfPreferredNeighbors = Integer.parseInt(properties.get("NumberOfPreferredNeighbors").toString());
		optimisticUnchokingInterval = Integer.parseInt(properties.get("UnchokingInterval").toString());
		pieceSize = Integer.parseInt(properties.getProperty("PieceSize"));
		unchokingInterval = Integer.parseInt(properties.getProperty("OptimisticUnchokingInterval"));
        numberOfPieces = (int)Math.ceil((double)fileSize / pieceSize);
        System.out.println("Number of pieces: " + numberOfPieces);
		System.out.println(System.getProperty("user.dir") + File.separatorChar + ReadConfigFiles.fileName);
		setPeerMapFromProperties();

	}

	public static synchronized boolean hasAllPeersDownloadedFile() {
		for (PeerInfo peerState: peers.values()) {
			if (peerState.getBitField().nextClearBit(0) != numberOfPieces) {
				System.out.println(peerState.getPeerId() + " has incomplete file, so not exiting");
				return false;
			}
		}
		return true;
	}

}

