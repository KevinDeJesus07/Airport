/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package core.models.flights;

import core.models.flights.FlightType;
import core.models.flights.Flight;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 *
 * @author luchitojunior4
 */
public class DirectFlight implements FlightType {

    @Override
    public LocalDateTime calculateArrival(Flight flight) {
        return flight.getDepartureDate()
                .plusHours(flight.getHoursDurationArrival())
                .plusMinutes(flight.getMinutesDurationArrival());
    }

    @Override
    public Duration getTotalDuration(Flight flight) {
        return Duration.ofHours(flight.getHoursDurationArrival())
                .plusMinutes(flight.getMinutesDurationArrival());
    }
    
    
    
}
