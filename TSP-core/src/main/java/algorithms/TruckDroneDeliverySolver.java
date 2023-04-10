package algorithms;

import algorithms.entity.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.ClientInfoStatus;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class TruckDroneDeliverySolver {
    private TruckDroneDeliveryInput constructedInput;
    private List<Node> tspBootstrappingSolution;
    private List<List<Node>> globalTruckSubRoutes;
    private List<Node> globalTruckRoute;
    private List<Request> globalCustomers;
    private int bestLaunchIndex;
    private int bestVisitIndex;
    private int bestRendezvousIndex;
    private int bestSubrouteIndex;
    private double maxSavings;
    private boolean isDroneNode;
    private boolean isStop;
    private TruckDroneDeliverySolutionOutput solution;
    public TruckDroneDeliverySolver(TruckDroneDeliveryInput input) {
        this.constructedInput = input;
        NearestNeighborSolver solver = new NearestNeighborSolver(constructedInput.getLocations(), constructedInput.getDistances());
        solver.calculateTSPTour(0);
        this.globalCustomers = constructedInput.getRequests();
        this.tspBootstrappingSolution = solver.getTSPSolution();
        this.globalTruckRoute = this.tspBootstrappingSolution;
        List<List<Node>> truckSubRoutes = new ArrayList<>();
        truckSubRoutes.add(this.globalTruckRoute);
        this.globalTruckSubRoutes = truckSubRoutes;
        List<DroneRoute> droneRoutes = new ArrayList<>();
        TruckRoute truckRoute = new TruckRoute(this.globalTruckRoute);
        this.solution = new TruckDroneDeliverySolutionOutput();
        this.solution.setDroneRoutes(droneRoutes);
        this.solution.setTruckRoute(truckRoute);
        this.isStop = false;
        this.bestLaunchIndex = -1;
        this.bestVisitIndex = -1;
        this.bestRendezvousIndex = -1;
        this.bestSubrouteIndex = -1;
        this.maxSavings = 0.0;
    }
    public TruckDroneDeliverySolutionOutput solve(){
        while (!isStop) {
            for (int j = 1; j <= globalCustomers.size(); j++) {
                // Algorithm 6: Calculate savings;
                double savings = calcSaving(j);
                for (int i = 0; i < globalTruckSubRoutes.size(); i++) {
                    if (isSubRouteAssociateWithDroneDelivery(globalTruckSubRoutes.get(i), solution)) {
                        relocateAsTruck(j, i, globalTruckSubRoutes.get(i), savings);
                    }
                    else {
                        relocateAsDrone(j, i, globalTruckSubRoutes.get(i), savings);
                    }
                }
            }
            if (this.maxSavings > 0) {
                applyChanges();
                this.maxSavings = 0;
            }
            else {
                this.isStop = true;
            }
        }

        // TODO: assign value for droneRoutes and truckRoutes

        return solution;
    }
    public double calcSaving(int customerIndex) {
        Node currentCustomer = this.tspBootstrappingSolution.get(customerIndex);
        Node prevCustomer = getPreviousRouteSequenceNode(customerIndex, this.tspBootstrappingSolution);
        Node nextCustomer = getNextRouteSequenceNode(customerIndex, this.tspBootstrappingSolution);
        double dij = constructedInput.getDistanceBtwTwoNodesByNode(prevCustomer, currentCustomer);
        double djk = constructedInput.getDistanceBtwTwoNodesByNode(currentCustomer, nextCustomer);
        double dik = constructedInput.getDistanceBtwTwoNodesByNode(prevCustomer, nextCustomer);
        double savings = (dij + djk - dik) * constructedInput.getTruck().getTransportCostPerUnit();
        // TODO: Add case associated with a drone for a specific candidated customer.
        /**
         * For any i, k belongs to truckRoute, <i, j, k > isPossibleDroneDelivery => True.
         * Read more of why this is brought into consideration in this paper: The Flying sidekick TSP.
         */
        List<Node> subRouteWithDD = getSubRouteAssociateWithDroneDelivery(customerIndex);
        if ( subRouteWithDD != null) {
            Node first = subRouteWithDD.get(0);
            Node last = subRouteWithDD.get(subRouteWithDD.size() - 1);
            Node customerNode = tspBootstrappingSolution.get(customerIndex);
            List<Node> droneRoute = new ArrayList<>();
            droneRoute.add(first);
            droneRoute.add(customerNode);
            droneRoute.add(last);
            double timeCostIJ = constructedInput.getTravellingTimeBtwTwoNodes(first, customerNode);
            double timeCostJK = constructedInput.getTravellingTimeBtwTwoNodes(customerNode, last);
            double timeCostIK = constructedInput.getTravellingTimeBtwTwoNodes(first, last);
            double timeCostTruckTourIK = constructedInput.calTruckTimeTravellingInTour(subRouteWithDD);
            double timeCostDrone = constructedInput.calDroneTimeFlyingInTour(droneRoute);
            double totalTimeCost = timeCostTruckTourIK - timeCostIJ - timeCostJK + timeCostIK - timeCostDrone;
            double truckWaitingCost = constructedInput.getTruck().getWaitingCost() *
                    Math.max(0, totalTimeCost);
            double droneWaitingCost = constructedInput.getDrone().getWaitingCost() *
                    Math.max(0, -totalTimeCost);
            savings = savings + truckWaitingCost + droneWaitingCost;
        }
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
    private boolean isSubRouteAssociateWithDroneDelivery(List<Node> truckSubRoute, TruckDroneDeliverySolutionOutput sol) {
        List<DroneRoute> droneRoutes = sol.getDroneRoutes();
        for (DroneRoute droneRoute: droneRoutes) {
            if (isTruckRoutePairWithADrone(truckSubRoute, droneRoute)) return true;
        }
        return false;
    }
    private List<Node> getSubRouteAssociateWithDroneDelivery(int candidate) {
        /**
         * To be associated, a candidate must be contained in a truck subRoute
         * that has first node and last node the same as lauch node and rendezvous node of a drone route.
         */
        List<DroneRoute> droneRoutes = solution.getDroneRoutes();
        List<Node> candidateBelongedSubRoute = getCandidateSubRoute(candidate);
        if (candidateBelongedSubRoute == null) return null;
        for (DroneRoute droneRoute: droneRoutes) {
            if (isTruckRoutePairWithADrone(candidateBelongedSubRoute, droneRoute)) return candidateBelongedSubRoute;
        }
        return null;
    }
    private List<Node> getCandidateSubRoute(int candidate) {
        Node candiateNode = this.tspBootstrappingSolution.get(candidate);
        for (List<Node> subRoute: globalTruckSubRoutes) {
            for (Node node : subRoute) {
                if (node.getName().equals(candiateNode.getName())) {
                    return subRoute;
                }
            }
        }
        return null;
    }
    private boolean isTruckRoutePairWithADrone(List<Node> truckRoute, DroneRoute droneRoute) {
        Node firstTruckEle = truckRoute.get(0);
        Node lastTruckEle = truckRoute.get(truckRoute.size() - 1);
        DroneRouteElement launchEle = droneRoute.getDroneRouteElements().get(0);
        DroneRouteElement rendezvousEle = droneRoute.getDroneRouteElements().get(2);
        return firstTruckEle.getName().equals(launchEle.getLocationID()) &&
                lastTruckEle.getName().equals(rendezvousEle.getLocationID());
    }
    private boolean isPossibleDroneDelivery(List<Node> droneRoute) {
        if (droneRoute.size() > 3) {
            return false; // drone delivery should contain no more than 3 nodes
        }
        Node first = droneRoute.get(0);
        Node middle = droneRoute.get(1);
        Node last = droneRoute.get(2);

        if (first.getName().equals(middle.getName()) || first.getName().equals(last.getName()) || middle.getName().equals(last.getName())) {
            return false;
        }
        double enduranceTime = constructedInput.getDrone().getDurationCapacity();
        double visitTime = constructedInput.getDistanceBtwTwoNodesByNode(first, middle);
        double rendezvousTime = constructedInput.getDistanceBtwTwoNodesByNode(middle, last);

        return visitTime + rendezvousTime <= enduranceTime;
    }
    /**
     * Algorithm 7: Calculates the cost of relocating the customer j
     * into a different position in the truck's route.
     */
    private void relocateAsTruck(int customerIndex, int subRouteIndex, List<Node> subRoute, double currSavings) {
        Node first = subRoute.get(0);
        Node last = subRoute.get(subRoute.size() - 1);
        Node customer = tspBootstrappingSolution.get(customerIndex);
        for (int i = 0 ; i < subRoute.size() - 1; i++) {
            // TODO: Enhancing this distance calculating by using distanceMatrix.
            double distanceIJ = constructedInput.getDistanceBtwTwoNodesByNode(subRoute.get(i), customer);
            double distanceJK = constructedInput.getDistanceBtwTwoNodesByNode(customer, subRoute.get(i+1));
            double distanceIK = constructedInput.getDistanceBtwTwoNodesByNode(subRoute.get(i), subRoute.get(i+1));
            double delta = (distanceIJ + distanceJK - distanceIK) * constructedInput.getTruck().getTransportCostPerUnit();
            if (delta < currSavings) {
                if (constructedInput.getDrone().isAbleToFly(0.0)) {
                    if (currSavings - delta > maxSavings) {
                        isDroneNode = false;
                        bestVisitIndex = getIndexOfNodeInTSPSol(customer);
                        bestLaunchIndex = getIndexOfNodeInTSPSol(first);
                        bestRendezvousIndex = getIndexOfNodeInTSPSol(last);
                        maxSavings = currSavings - delta;
                        bestSubrouteIndex = subRouteIndex;
                        //TODO: Should state the subroute this best solution belongs to.
                    }
                }
            }
        }
    }

    /**
     * Algorithm 8: Calculates the cost of relocating customer j
     * as a drone node
     */
    private void relocateAsDrone(int customerIndex, int subRouteIndex, List<Node> subRoute, double currSavings) {
        for (int i = 0; i < subRoute.size() - 1; i++) {
            for (int k = i + 1; k < subRoute.size(); k++) {
                List<Node> checkingDroneRoute = new ArrayList<>();
                Node first = subRoute.get(i);
                Node last = subRoute.get(k);
                Node customer = tspBootstrappingSolution.get(customerIndex);
                checkingDroneRoute.add(first);
                checkingDroneRoute.add(customer);
                checkingDroneRoute.add(last);
                if (isPossibleDroneDelivery(checkingDroneRoute)) {
                    double truckTime = constructedInput.calTruckTimeTravellingInTour(subRoute.subList(i, k+1));
                    double droneTime = constructedInput.calDroneTimeFlyingInTour(checkingDroneRoute);
                    double waitingTime = Math.abs(truckTime - droneTime);
                    double waitingCost = truckTime > droneTime ? // drone have to wait ? if true -> cost on drone.
                            constructedInput.getDrone().getWaitingCost() * waitingTime : constructedInput.getTruck().getWaitingCost() * waitingTime;
                    double distanceIJ = constructedInput.getDistanceBtwTwoNodesByNode(first, customer);
                    double distanceJK = constructedInput.getDistanceBtwTwoNodesByNode(customer, last);
                    double delta = (distanceIJ + distanceJK) * constructedInput.getDrone().getTransportCostPerUnit()
                            + waitingCost;
                    if (currSavings - delta > maxSavings) {
                        isDroneNode = true;
                        bestVisitIndex = getIndexOfNodeInTSPSol(customer);
                        bestLaunchIndex = getIndexOfNodeInTSPSol(first);
                        bestRendezvousIndex = getIndexOfNodeInTSPSol(last);
                        maxSavings = currSavings - delta;
                        bestSubrouteIndex = subRouteIndex;
                        //TODO: Should state the subroute this best solution belongs to.
                    }
                }
            }
        }
    }
    // Algorithm 9: Update solution when there is a better one
    private void applyChanges() {
        if (isDroneNode) {
            // Assign new drone route
            List<Node> droneRouteNode = new ArrayList<>();
            droneRouteNode.add(this.tspBootstrappingSolution.get(bestLaunchIndex));
            droneRouteNode.add(this.tspBootstrappingSolution.get(bestRendezvousIndex));
            droneRouteNode.add(this.tspBootstrappingSolution.get(bestVisitIndex));
            DroneRoute droneRoute = new DroneRoute(droneRouteNode);
            solution.getDroneRoutes().add(droneRoute);
            Node nodeToProceed = tspBootstrappingSolution.get(bestVisitIndex);
            // Removing j*
            int subRouteForRemoving = findSubRouteContainNodeFromSubRoutes(nodeToProceed, globalTruckSubRoutes);
            List<Node> subRouteBeforeRemoving = globalTruckSubRoutes.get(subRouteForRemoving);
            RemoveNodeReturnValue subRouteAfterRemoving = removeNodeFromRoute(nodeToProceed, subRouteBeforeRemoving);
            globalTruckSubRoutes.set(subRouteForRemoving, subRouteAfterRemoving.route);
            removeNodeFromRoute(nodeToProceed, globalTruckRoute);
            // Append a new truck subRoute
            List<Node> leftSubRoute = subRouteBeforeRemoving.subList(0, subRouteAfterRemoving.removingIndex);
            List<Node> rightSubRoute = subRouteBeforeRemoving.subList(subRouteAfterRemoving.removingIndex + 1, subRouteBeforeRemoving.size());
            globalTruckSubRoutes.set(subRouteForRemoving, leftSubRoute);
            globalTruckSubRoutes.add(rightSubRoute);
            // Removing i*, j*, k* from customers
            globalCustomers.remove(bestLaunchIndex);
            globalCustomers.remove(bestRendezvousIndex);
            globalCustomers.remove(bestVisitIndex);
        }
        else {
            Node nodeToProceed = tspBootstrappingSolution.get(bestVisitIndex);
            // Removing from current subRoute
            int subRouteForRemoving = findSubRouteContainNodeFromSubRoutes(nodeToProceed, globalTruckSubRoutes);
            List<Node> subRouteBeforeRemoving = globalTruckSubRoutes.get(subRouteForRemoving);
            RemoveNodeReturnValue subRouteAfterRemoving = removeNodeFromRoute(nodeToProceed, subRouteBeforeRemoving);
            globalTruckSubRoutes.set(subRouteForRemoving, subRouteAfterRemoving.route);
            // Inserting to another subRoute
            List<Node> truckSubRouteToInsert = globalTruckSubRoutes.get(bestSubrouteIndex);
            List<Node> truckSubRouteAfterInserting = insertNodeToRoute(nodeToProceed, bestLaunchIndex, truckSubRouteToInsert);
            globalTruckSubRoutes.set(bestSubrouteIndex, truckSubRouteAfterInserting);
        }
    }
    static class RemoveNodeReturnValue {
        int removingIndex;
        List<Node> route;

    }
    private RemoveNodeReturnValue removeNodeFromRoute(Node removingNode, List<Node> subRoute) {
        RemoveNodeReturnValue results = new RemoveNodeReturnValue();
        for (int i = 0; i < subRoute.size(); i++) {
            if (subRoute.get(i).getName().equals(removingNode.getName())) {
                subRoute.remove(i);
                results.removingIndex = i;
            }
        }
        results.route = subRoute;
        return results;
    }
    private List<Node> insertNodeToRoute(Node insertingNode, int insertFromRange, List<Node> subRoute) {
        subRoute.add(insertFromRange, insertingNode);
        return subRoute;
    }
    private int findSubRouteContainNodeFromSubRoutes(Node nodeToFind, List<List<Node>> subRoutes) {
        for (int i = 0; i < subRoutes.size(); i++) {
            List<Node> subRoute = subRoutes.get(i);
            for (Node node: subRoute) {
                if (node.getName().equals(nodeToFind.getName())) {
                    return i;
                }
            }
        }
        return -1;
    }
    public TruckDroneDeliverySolutionOutput getSolution() {
        return this.solution;
    }

    public int getIndexOfNodeInTSPSol(Node node) {
        if (node.getName().equals("depot")) return 0;
        for (int i = 0; i < tspBootstrappingSolution.size(); i++) {
            if (node.getName().equals(tspBootstrappingSolution.get(i).getName()))
                return i;
        }
        return -1;
    }
}
