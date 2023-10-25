import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.*;

public class FileManager {

	public static ConcurrentHashMap<Integer, byte[]> splitFile() {
		File file = new File(System.getProperty("user.dir") + File.separatorChar + ReadConfigFiles.fileName);
		FileInputStream fileInputStream = null;
		DataInputStream dataInputStream = null;
		try {
			fileInputStream = new FileInputStream(file);
			dataInputStream = new DataInputStream(fileInputStream);

			int numberOfPieces = ReadConfigFiles.getNumberOfPieces();
			ConcurrentHashMap<Integer, byte[]> fileSplitMap = new ConcurrentHashMap<>();

			for (int i = 0; i < numberOfPieces; i++) {
				int pieceSize = i != numberOfPieces - 1 ? ReadConfigFiles.getPieceSize()
						: (int) (ReadConfigFiles.getFileSize() % ReadConfigFiles.getPieceSize());
				byte[] piece = new byte[pieceSize];
				dataInputStream.readFully(piece);
				fileSplitMap.put(i, piece);
			}
			return fileSplitMap;

		}
		catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fileInputStream.close();
				dataInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static void joinPiecesAndWriteFile(PeerInfo peerState) {
		String fileNameWithPath = System.getProperty("user.dir") + File.separatorChar + "btorrent/peer_" + peerState.getPeerId()
				+ File.separatorChar + ReadConfigFiles.fileName;
		System.out.println("Joining pieces and writing to file " + fileNameWithPath);
		FileOutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(fileNameWithPath);
			for (int i = 0; i < peerState.getFileSplitMap().size(); i++) {
				try {
					outputStream.write(peerState.getFileSplitMap().get(i));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		finally {
			try {
				outputStream.flush();
			}
			catch (Exception ex){
				ex.printStackTrace();
			}
		}
	}

	public static void makeFilesAndDirectories(String peerId){
		try {
			String fileNameWithPath = System.getProperty("user.dir") + File.separatorChar + "btorrent/peer_" + peerId
					+ File.separatorChar + ReadConfigFiles.fileName;
			File createdFile = new File(fileNameWithPath);
			createdFile.getParentFile().mkdirs();
			createdFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
