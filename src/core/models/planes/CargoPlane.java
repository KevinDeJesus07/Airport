/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package core.models.planes;

import core.models.planes.Plane;

/**
 *
 * @author luchitojunior4
 */
public class CargoPlane extends Plane {
    
    private double maxWeight;

    public CargoPlane(String id, String brand, String model, String airline,
            double maxWeight
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
    public int getMaxCapacity() {
        return 0;
    }
      
}
