import java.util.*;

public class OptimisticUnchokingScheduler extends TimerTask {

    private PeerInfo currentPeerState;

    public OptimisticUnchokingScheduler(PeerInfo currentPeerState) {
        this.currentPeerState = currentPeerState;
    }

    @Override
    public void run() {
        System.out.println("OptimisticUnchokingTask: start");

        if (currentPeerState.getInterestedNeighbours().isEmpty()) {
            System.out.println("OptimisticUnchokingTask: No interested neighbors for " + this.currentPeerState.getPeerId());
            return;
        }

        List<String> chokedNeighbours = new ArrayList<>();

        for (String peerId: currentPeerState.getInterestedNeighbours().values()) {
            if (peerId.equals(currentPeerState.getPeerId())) {
                continue;
            }
            if (!currentPeerState.getPreferredNeighbours().containsKey(peerId)) {
                chokedNeighbours.add(peerId);
            }
        }
        if (chokedNeighbours.isEmpty()) {
            System.out.println("OptimisticUnchokingTask: No choked neighbors!");
            return;
        }
        Collections.shuffle(chokedNeighbours);
        String optimisticUnchokedPeerId = chokedNeighbours.get(0);
        currentPeerState.setOptimisticUnchokedPeerId(optimisticUnchokedPeerId);
        currentPeerState.getConnections().get(optimisticUnchokedPeerId).sendMessage(new UnchokeMessage());
        Logger.getLogger(currentPeerState.getPeerId()).logNewOptimisticallyUnchokedNeighbor(optimisticUnchokedPeerId);
    }
}
