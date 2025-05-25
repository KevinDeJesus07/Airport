/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package core.models.planes;

/**
 *
 * @author edangulo
 */
public abstract class Plane {
    
    protected final String id;
    protected String brand;
    protected String model;
    protected String airline;

    public Plane(String id, String brand, String model, String airline) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.airline = airline;
    }
    
    protected Plane(Plane plane) {
        this.id = plane.id;
        this.brand = plane.brand;
        this.model = plane.model;
        this.airline = plane.airline;
    }
    
    public abstract Plane clone();
    
    public String getId() {
        return id;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public String getAirline() {
        return airline;
    }
    
    public abstract int getCapacity();
    
}
