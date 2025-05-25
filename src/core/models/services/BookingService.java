/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package core.models.services;

import core.models.flights.Flight;
import core.models.Passenger;
import core.models.planes.Plane;
import core.models.repositories.FlightRepository;
import core.models.repositories.PassengerRepository;
import core.models.repositories.PlaneRepository;
import java.util.ArrayList;

/**
 *
 * @author luchitojunior4
 */
public class BookingService {
    
    private FlightRepository flightRepository;
    private PassengerRepository passengerRepository;
    private PlaneRepository planeRepository;
    
    public BookingService(FlightRepository fr, PassengerRepository pr, PlaneRepository plr) {
        this.flightRepository = fr;
        this.passengerRepository = pr;
        this.planeRepository = plr;
    }
    
    public boolean addPassengerToFlight(long passengerId, String flightId) {
        Passenger passenger = passengerRepository.findById(passengerId);
        Flight flight = flightRepository.findById(flightId);
        
        if (passenger == null || flight == null) {
            return false;
        }
        
        Plane plane = flight.getPlane();
        if (plane == null) {
            return false;
        }
        
        int currPassengers = passengerRepository.countByFlightId(flightId);
        int capacity = plane.getMaxCapacity();
        
        if (currPassengers >= capacity) {
            return false;
        }
        
        boolean success = passengerRepository.linkPassengerToFlight(passengerId, flightId);
        
        if (success) {
            // Observer
        }
        
        return success;
    }
    
    public ArrayList<Passenger> getPassengersForFlight(String flightId) {
        return passengerRepository.findByFlightId(flightId);
    }
    
    public ArrayList<Flight> getFlightsForPassenger(long passengerId) {
        return flightRepository.findByPassengerId(passengerId);
    }
    
    public boolean assignPlaneToFlight(String planeId, String flightId) {
        Flight flight = flightRepository.findById(flightId);
        Plane plane = planeRepository.findById(planeId);
        
        if (flight == null || plane == null) {
            return false;
        }
        
        flight.setPlane(plane);
        flightRepository.update(flight);
        
        return true;
    }
    
}
