/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package core.models.planes;

/**
 *
 * @author luchitojunior4
 */
public class PassengerPlane extends Plane implements PassengerTransportable {
    
    private int maxCapacity;
    private boolean entertaimentOn;
    
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
    public int getCapacity() {
        return this.maxCapacity;
    }

    @Override
    public void turnEntertaimentOn() {
        this.entertaimentOn = true;
    }

    @Override
    public void turnEntertaimentOff() {
        this.entertaimentOn = false;
    }
    
    public boolean isEntertaimentOn() {
        return this.entertaimentOn;
    }
    
}
