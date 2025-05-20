/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package core.models;

import java.util.ArrayList;

/**
 *
 * @author edangulo
 */
public class Plane {
    
    private final String id;
    private String brand;
    private String model;
    private final int maxCapacity;
    private String airline;
    private ArrayList<Flight> flights;

    public Plane(String id, String brand, String model, int maxCapacity, String airline) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.maxCapacity = maxCapacity;
        this.airline = airline;
        this.flights = new ArrayList<>();
    }
    
    public Plane(Plane plane) {
        this.id = plane.id;
        this.brand = plane.brand;
        this.model = plane.model;
        this.maxCapacity = plane.maxCapacity;
        this.airline = plane.airline;
        this.flights = new ArrayList<>();
        if (plane.flights != null) {
            for (Flight flight : plane.flights) {
                if (flight != null) {
                    this.flights.add(flight.clone());
                } else {
                    this.flights.add(null);
                }
            }
        }
    }
    
    public Plane clone() {
        return new Plane(this);
    }

    public void addFlight(Flight flight) {
        this.flights.add(flight);
    }
    
    public String getId() {
        return id;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public String getAirline() {
        return airline;
    }

    public ArrayList<Flight> getFlights() {
        return flights;
    }
    
    public int getNumFlights() {
        return flights.size();
    }
    
}
