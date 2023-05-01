package algorithms;

import algorithms.drawings.DrawingTools;
import algorithms.entity.*;
import algorithms.io.InputIO;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;

import javax.swing.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Main extends JFrame{
    public static void main(String[] args) {
        try {
            // Read coordination data from file.
            File myObj = new File("TSP-core/src/main/java/algorithms/data/route3.txt");
            TruckDroneDeliveryInput input = InputIO.readInputFromFile(myObj);

            // Bootstraping with a TSP solution
//            NearestNeighborSolver solver = new NearestNeighborSolver(input.getLocations(), input.getDistances());
//            solver.calculateTSPTour(0);
//            List<Node> tspSolution = solver.getTSPSolution();
//            for (Node node : solution) {
//                log.info("Node info {} ({}, {})", node.getName(), node.getX(), node.getY());
//            }
//            DrawingTools drawingTools = new DrawingTools();
//            drawingTools.drawSolution(solution, 600, 600, true);

            // Solving TSP-LS heuristically
            TruckDroneDeliverySolver heuristicSolver = new TruckDroneDeliverySolver(input);
            heuristicSolver.solve();
            TruckDroneDeliverySolutionOutput finalSolution = heuristicSolver.getSolution();
//            System.out.println("TSP cost: " + finalSolution.getTotalTruckCost());
//            System.out.println("Total savings: " + finalSolution.calculateTotalCost());
            List<Node> truckRoute = finalSolution.convertTruckRouteToNode(finalSolution.getTruckRoute());
            List<List<Node>> droneRoutes = new ArrayList<>();
            for (DroneRoute ele: finalSolution.getDroneRoutes()) {
                List<Node> droneRoute = finalSolution.convertDroneRouteToNode(ele);
                droneRoutes.add(droneRoute);
            }
            for (Node node : truckRoute) {
                log.info("Truck node info {} ({}, {})", node.getName(), node.getX(), node.getY());
            }
            for (List<Node> drone : droneRoutes) {
                log.info("====================================");
                for (Node node : drone) {
                    log.info("Drone node info {} ({}, {})", node.getName(), node.getX(), node.getY());
                }
            }
            DrawingTools drawingTools = new DrawingTools();
            drawingTools.drawSolution(truckRoute, 600, 600, true, false);
            for (
                    List<Node> droneRoute : droneRoutes
            ) {
                drawingTools.drawSolution(droneRoute, 600, 600, true, true);
            }
        } catch (Exception e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}
