/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package core.models.flights;

import core.models.flights.Flight;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 *
 * @author luchitojunior4
 */
public interface FlightType {
    
    LocalDateTime calculateArrival(Flight flight);
    
    Duration getTotalDuration(Flight flight);
    
}
