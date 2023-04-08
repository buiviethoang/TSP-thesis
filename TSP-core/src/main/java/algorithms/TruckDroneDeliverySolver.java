package algorithms;

import algorithms.entity.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class TruckDroneDeliverySolver {
    private TruckDroneDeliveryInput constructedInput;

    public TruckDroneDeliverySolver(TruckDroneDeliveryInput input) {
        this.constructedInput = input;
    }
    private TruckDroneDeliverySolutionOutput solve(){
        NearestNeighborSolver solver = new NearestNeighborSolver(constructedInput.getLocations(), constructedInput.getDistances());
        solver.calculateTSPTour(0);
        List<Request> customers = constructedInput.getRequests();
        List<Node> truckRouteNode = solver.getTSPSolution();
        List<List<Node>> truckSubRoutes = new ArrayList<>();
        truckSubRoutes.add(truckRouteNode);
        TruckDroneDeliverySolutionOutput solution = new TruckDroneDeliverySolutionOutput();
        List<DroneRoute> droneRoutes = new ArrayList<>();
        TruckRoute truckRoutes = new TruckRoute();
        for (Node node : constructedInput.getLocations()) {

        }
        solution.setDroneRoutes(droneRoutes);
        solution.setTruckRoute(truckRoutes);

        int bestLaunchIndex = -1;
        int bestVisitIndex = -1;
        int bestRendezvousIndex = -1;
        double maxSavings = 0;
        boolean isDroneNode;
        boolean isStop = false;


        while (!isStop) {
            for (int i = 0; i < customers.size(); i++) {
                // Algorithm 6: Calculate savings;
                double savings = calcSaving(i, truckRouteNode);
                for (List<Node> subroute : truckSubRoutes) {
                    if (isFeasibleAsDroneDelivery(subroute, solution)) {
//                        isDroneNode, maxSavings, bestLaunchIndex, bestVisitIndex, bestRendezvousIndex =
//                                relocateAsTruck(customer, subroute, savings);
                    }
                }
            }
        }

        if (maxSavings > 0) {
            applyChanges();
            maxSavings = 0;
        }
        // TODO: assign value for droneRoutes and truckRoutes

        return solution;
    }
    public double calcSaving(int customerIndex, List<Node> truckRoute) {
        Node currentCustomer = constructedInput.getLocations().get(customerIndex);
        Node prevCustomer = getPreviousRouteSequenceNode(customerIndex, truckRoute);
        Node nextCustomer = getNextRouteSequenceNode(customerIndex, truckRoute);
        double dij = constructedInput.getDistanceBtwTwoNodesByNode(prevCustomer, currentCustomer);
        double djk = constructedInput.getDistanceBtwTwoNodesByNode(currentCustomer, nextCustomer);
        double dik = constructedInput.getDistanceBtwTwoNodesByNode(prevCustomer, nextCustomer);
        double savings = (dij + djk - dik) * constructedInput.getTruck().getTransportCostPerUnit();
        // TODO: Add case served by drone

        System.out.println("Savings : " + savings);
        return savings;
    }

    private Node getPreviousRouteSequenceNode(int index, List<Node> truckRoute) {
        if (index == 0) return null;
        return truckRoute.get(index - 1);
    }

    private Node getNextRouteSequenceNode(int index, List<Node> truckRoute) {
        if (truckRoute.size() == index)
            return null;
        return truckRoute.get(index + 1);
    }
    private boolean isFeasibleAsDroneDelivery(List<Node> subroute, TruckDroneDeliverySolutionOutput solution) {
        return true;
    }

    /**
     * Algorithm 7: Calculates the cost of relocating the customer j
     * into a different position in the truck's route.
     */
    private void relocateAsTruck() {

    }

    /**
     * Algorithm 8: Calculates the cost of relocating customer j
     * as a drone node
     */
    private void relocateAsDrone() {

    }
    // Algorithm 9: Update solution when there is a better one
    private void applyChanges() {

    }

}
