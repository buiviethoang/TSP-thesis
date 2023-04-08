package algorithms;

import algorithms.entity.TruckDroneDeliveryInput;
import algorithms.entity.TruckDroneDeliverySolutionOutput;

public interface ISolver {
    public TruckDroneDeliverySolutionOutput solve(TruckDroneDeliveryInput input);
}
