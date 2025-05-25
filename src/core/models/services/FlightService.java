/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package core.models.services;

import core.models.flights.DirectFlight;
import core.models.flights.Flight;
import core.models.flights.FlightType;
import core.models.flights.ScaleFlight;
import core.models.repositories.FlightRepository;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 *
 * @author luchitojunior4
 */
public class FlightService {
    
    private FlightRepository flightRepository;
    private FlightType directFlight;
    private FlightType scaleFlight;
    
    public FlightService(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
        this.directFlight = new DirectFlight();
        this.scaleFlight = new ScaleFlight();
    }
    
    private FlightType getFlightType(Flight flight) {
        if (flight.getScaleLocation() == null) {
            return directFlight;
        } else {
            return scaleFlight;
        }
    }
    
    public LocalDateTime calculateArrivalDate(String flightId) {
        Flight flight = flightRepository.findById(flightId);
        if (flight == null) {
            return null;
        }
        FlightType flightType = getFlightType(flight);
        return flightType.calculateArrival(flight);
    }
    
    public Duration getTotalDuration(String flightId) {
        Flight flight = flightRepository.findById(flightId);
        if (flight == null) {
            return null;
        }
        FlightType flightType = getFlightType(flight);
        return flightType.getTotalDuration(flight);
    }
    
    public void delayFlight(String flightId, int delayHours, int delayMinutes) {
        Flight flight = flightRepository.findById(flightId);
        if (flight == null) {
            return;
        }
        LocalDateTime currDeparture = flight.getDepartureDate();
        LocalDateTime newDeparture = currDeparture.plusHours(delayHours)
                .plusMinutes(delayMinutes);
        flight.setDepartureDate(newDeparture);
        flightRepository.update(flight);
    }
    
}
