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
public class PassengerPlane extends Plane {
    
    private int maxCapacity;
    
    public PassengerPlane(String id, String brand, String model,
            String airline, int maxCapacity
    ) {
        super(id, brand, model, airline);
        this.maxCapacity = maxCapacity;
    }
    
    public PassengerPlane(PassengerPlane plane) {
        super(plane);
        this.maxCapacity = plane.maxCapacity;
    }

    @Override
    public Plane clone() {
        return new PassengerPlane(this);
    }

    @Override
    public int getMaxCapacity() {
        return this.maxCapacity;
    }
    
}
