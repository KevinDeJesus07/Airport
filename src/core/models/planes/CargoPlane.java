/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package core.models.planes;

/**
 *
 * @author luchitojunior4
 */
public class CargoPlane extends Plane implements CargoTransportable {
    
    private int maxWeight;
    private boolean rampDeployed;

    public CargoPlane(String id, String brand, String model, String airline,
            int maxWeight
    ) {
        super(id, brand, model, airline);
        this.maxWeight = maxWeight;
    }
    
    public CargoPlane(CargoPlane plane) {
        super(plane);
        this.maxWeight = plane.maxWeight;
    }
    
    @Override
    public Plane clone() {
        return new CargoPlane(this);
    }

    @Override
    public int getCapacity() {
        return maxWeight;
    }

    @Override
    public void deployRamp() {
        this.rampDeployed = true;
    }

    @Override
    public void retractRamp() {
        this.rampDeployed = false;
    }
    
    public boolean isRampDeployed() {
        return rampDeployed;
    }
      
}
