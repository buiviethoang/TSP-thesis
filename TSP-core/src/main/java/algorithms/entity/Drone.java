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
public class Drone {
    private String ID;
    private int capacity;
    private int durationCapacity; // max duration time
    private double speed;
    private double transportCostPerUnit = 1;
    private double waitingCost = 10;

    public List<Request> getEligibleServedByDroneCustomer(List<Request> customers) {
        List<Request> eligibleCustomers = new ArrayList<>();
        for (Request customer : customers) {
            if (isAbleToServeACustomer(customer)) {
                eligibleCustomers.add(customer);
            }
        }
        return eligibleCustomers;
    }
    public boolean isAbleToServeACustomer(Request customer) {
        return customer.getWeight() <= this.capacity;
    }
}
