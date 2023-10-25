import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.*;

public class FileManager {

    // Split a file into pieces and store them in a ConcurrentHashMap
    public static ConcurrentHashMap<Integer, byte[]> splitFile() {
        File file = new File(System.getProperty("user.dir") + File.separatorChar + ReadConfigFiles.fName);
        FileInputStream fileIS = null;
        DataInputStream dataIS = null;
        try {
            fileIS = new FileInputStream(file);
            dataIS = new DataInputStream(fileIS);

            int noOfPieces = ReadConfigFiles.getNumberOfPieces();
            ConcurrentHashMap<Integer, byte[]> fileSM = new ConcurrentHashMap<>();

            for (int i = 0; i < noOfPieces; i++) {
                int pieceSize = i != noOfPieces - 1 ? ReadConfigFiles.getPieceSize()
                        : (int) (ReadConfigFiles.getFileSize() % ReadConfigFiles.getPieceSize());
                byte[] piece = new byte[pieceSize];
                dataIS.readFully(piece);
                fileSM.put(i, piece);
            }
            return fileSM;

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fileIS.close();
                dataIS.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    // Join pieces together and write to a file
    public static void joinPiecesAndWriteFile(PeerInfo peerInfo) {
        String filePath = System.getProperty("user.dir") + File.separatorChar + "btorrent/peer_" + peerInfo.getPeerId()
                + File.separatorChar + ReadConfigFiles.fName;
        System.out.println("Joining pieces and writing to file " + filePath);
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(filePath);
            for (int i = 0; i < peerInfo.getFileSplitMap().size(); i++) {
                try {
                    os.write(peerInfo.getFileSplitMap().get(i));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                os.flush();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    // Create necessary directories and files for a peer
    public static void makeFilesAndDirectories(String peerId) {
        try {
            String filePath = System.getProperty("user.dir") + File.separatorChar + "btorrent/peer_" + peerId
                    + File.separatorChar + ReadConfigFiles.fName;
            File createdFile = new File(filePath);
            createdFile.getParentFile().mkdirs();
            createdFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}