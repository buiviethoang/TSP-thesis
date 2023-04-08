package algorithms;

import algorithms.entity.TruckDroneDeliveryInput;
import algorithms.entity.TruckDroneDeliverySolutionOutput;

import java.util.Random;

public class RandomSolver implements ISolver {
    private Random ran;
    private int repeat;
    public RandomSolver(long seed) {
        this(seed, 1);
    }
    public RandomSolver(long seed, int repeat) {
        this.ran = new Random(seed);
        this.repeat = repeat;
    }
    public RandomSolver(Random ran) {
        this(ran, 1);
    }
    public RandomSolver(Random ran, int repeat) {
        this.ran = ran;
        this.repeat = repeat;
    }

    @Override
    public TruckDroneDeliverySolutionOutput solve(TruckDroneDeliveryInput input) {
        TruckDroneDeliverySolutionOutput bestSolution = null;
        for (int i = 0; i < this.repeat; i++) {
            TruckDroneDeliverySolutionOutput currentSolution = randomSolution(input);
            if (bestSolution == null || bestSolution.getTotalCost() > currentSolution.getTotalCost()) {
                bestSolution = currentSolution;
            }
        }
        return bestSolution;
    }
    public TruckDroneDeliverySolutionOutput randomSolution(TruckDroneDeliveryInput input) {
        return new TruckDroneDeliverySolutionOutput();
    }
}
