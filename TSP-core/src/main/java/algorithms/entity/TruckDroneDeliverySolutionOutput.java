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

}
