package algorithms.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TruckDroneDeliverySolutionOutput {
    private TruckRoute truckRoute;
    private List<DroneRoute> droneRoutes;
    private double totalCost;
    private double totalTruckCost;
    private double totalDroneCost;
    private double totalTruckWait;
    private double totalDroneWait;
//    private double maxTruckCost;
//    private double maxDroneCost;
//    private double maxTruckWait;
//    private double maxDroneWait;
    public TruckDroneDeliverySolutionOutput(TruckDroneDeliveryInput input, TruckRoute truckRoute, List<DroneRoute> droneRoutes) {
        this.truckRoute = truckRoute;
        this.droneRoutes = droneRoutes;
        this.totalCost = 0;
        this.totalTruckCost = 0;
        this.totalDroneCost = 0;
        this.totalTruckWait = 0;
        this.totalDroneWait = 0;
    }
//    public double calculateDroneCost() {
//
//    }
//    public double calculateTruckCost() {
//
//    }
//    public double calculateTruckWaitingCost() {
//
//    }
//    public double calculateDroneWaitingCost() {
//
//    }
    public double calculateTotalCost() {
        this.totalCost = this.totalTruckCost + this.totalDroneCost + this.totalDroneWait + this.totalTruckWait;
        return this.totalCost;
    }
    public List<Node> convertTruckRouteToNode(TruckRoute truckRoute) {
        List<Node> truckNode = new ArrayList<>();
        for (TruckRouteElement ele : truckRoute.getRouteElements()) {
            truckNode.add(ele.getNode());
        }
        return truckNode;
    }
    public List<Node> convertDroneRouteToNode(DroneRoute droneRoute) {
        List<Node> droneNode = new ArrayList<>();
        for (DroneRouteElement ele : droneRoute.getDroneRouteElements()) {
            droneNode.add(ele.getNode());
        }
        return droneNode;
    }
}
