package algorithms;

import algorithms.drawings.DrawingTools;
import algorithms.entity.Node;
import algorithms.entity.TruckDroneDeliveryInput;
import algorithms.io.InputIO;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;

import javax.swing.*;

import java.io.File;
import java.util.List;

@Slf4j
public class Main extends JFrame{
    public static void main(String[] args) {
        try {
            // Read coordination data from file.
            File myObj = new File("/home/hoangbui/Desktop/TSP-thesis/TSP-core/src/main/java/algorithms/data/route.txt");
            TruckDroneDeliveryInput input = InputIO.readInputFromFile(myObj);

            // Bootstraping with a TSP solution
            NearestNeighborSolver solver = new NearestNeighborSolver(input.getLocations(), input.getDistances());
            solver.calculateTSPTour(0);
            List<Node> solution = solver.getTSPSolution();
//            for (Node node : solution) {
//                log.info("Node info {} ({}, {})", node.getName(), node.getX(), node.getY());
//            }
//            DrawingTools drawingTools = new DrawingTools();
//            drawingTools.drawSolution(solution, 600, 600, true);

            // Solving TSP-LS heuristically
            TruckDroneDeliverySolver heuristicSolver = new TruckDroneDeliverySolver(input);
            heuristicSolver.solve();
        } catch (Exception e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}
