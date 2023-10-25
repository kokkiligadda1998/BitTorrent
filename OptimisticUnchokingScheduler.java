import java.util.*;

public class OptimisticUnchokingScheduler extends TimerTask {

    private PeerInfo peerCondition;// Reference to the peer's information.

    // Constructor to initialize the scheduler with peer information.

    public OptimisticUnchokingScheduler(PeerInfo peerCondition) {
        this.peerCondition = peerCondition;
    }

    @Override
    public void run() {
        System.out.println("checking the status for Task: start");

        // Check if there are no interested peers.
        if (peerCondition.getInterestedNeighbours().isEmpty()) {
            System.out.println("checking the status for Task: No interested peers for " + this.peerCondition.getPeerId());
            return;
        }

        List<String> chokedNeighbours = new ArrayList<>();

         // Iterate through interested peers to find choked neighbors.

        for (String peerId: peerCondition.getInterestedNeighbours().values()) {
            // Skip the current peer.
            if (peerId.equals(peerCondition.getPeerId())) {
                continue;
            }
            // Check if the peer is not a preferred neighbor (choked).
            if (!peerCondition.getPreferredNeighbours().containsKey(peerId)) {
                chokedNeighbours.add(peerId);
            }
        }
        // If there are no choked neighbors, exit.
        if (chokedNeighbours.isEmpty()) {
            System.out.println("checking the status for Task: found no choked neighbors!!!");
            return;
        }
        // Randomly select one choked neighbor as the optimistic unchoked neighbor.
       
        Collections.shuffle(chokedNeighbours);
        String optimisticUnchokedPeerId = chokedNeighbours.get(0);
         // Set the optimistic unchoked neighbor and send an unchoke message.      
        peerCondition.setOptimisticUnchokedPeerId(optimisticUnchokedPeerId);
        peerCondition.getConnections().get(optimisticUnchokedPeerId).sendMessage(new UnchokeMessage());
         // Log the selection of the new optimistically unchoked neighbor.
        Logger.getLogger(peerCondition.getPeerId()).logNewOptimisticallyUnchokedNeighbor(optimisticUnchokedPeerId);
    }
}