package algorithms.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TruckDroneDeliveryInput {
    private List<Request> requests;
    private Truck truck;
    private Drone drone;
    private Depot depot;
    private List<List<DistanceElement>> distances;
    private List<Node> locations;
    public Node getSpecificLocationById(String nodeId) {
        for (Node node : locations) {
            if (node.getName().equals(nodeId)) {
                return node;
            }
        }
        return null;
    }
    public double getDistanceBetweenTwoNodesByIndex(int node1Index, int node2Index) {
        if (node1Index > distances.size() || node2Index > distances.size()) return -1.0;
        return distances.get(node1Index).get(node2Index).getDistance();
    }
    public double getDistanceBtwTwoNodesByNode(Node node1, Node node2) {
        for (int i = 0; i < locations.size(); i++) {
            for (int j = 0; j < locations.size(); j++) {
                String fromNode = distances.get(i).get(j).getFromLocationId();
                String toNode = distances.get(i).get(j).getToLocationId();
                if (node1.getName().equals(fromNode) && node2.getName().equals(toNode)) {
                    return distances.get(i).get(j).getDistance();
                }
            }
        }
        return -1.0;
    }
}
