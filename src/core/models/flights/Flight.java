/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package core.models.flights;

import core.models.Location;
import core.models.planes.Plane;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 *
 * @author Kevin
 */
public class Flight {

    private final String id;
    private Plane plane;
    private Location departureLocation;
    private Location scaleLocation;
    private Location arrivalLocation;
    private LocalDateTime departureDate;
    private int hoursDurationArrival;
    private int minutesDurationArrival;
    private int hoursDurationScale;
    private int minutesDurationScale;
    private FlightType flightType;

    public Flight(String id, Plane plane, Location departureLocation,
            Location arrivalLocation, LocalDateTime departureDate,
            int hoursDurationArrival, int minutesDurationArrival) {
        this.id = id;
        this.plane = plane;
        this.departureLocation = departureLocation;
        this.arrivalLocation = arrivalLocation;
        this.departureDate = departureDate;
        this.hoursDurationArrival = hoursDurationArrival;
        this.minutesDurationArrival = minutesDurationArrival;
        this.scaleLocation = null;
        this.hoursDurationScale = 0;
        this.minutesDurationScale = 0;
        this.flightType = new DirectFlight();
    }

    public Flight(String id, Plane plane, Location departureLocation,
            Location scaleLocation, Location arrivalLocation,
            LocalDateTime departureDate, int hoursDurationArrival,
            int minutesDurationArrival, int hoursDurationScale,
            int minutesDurationScale) {
        this.id = id;
        this.plane = plane;
        this.departureLocation = departureLocation;
        this.scaleLocation = scaleLocation;
        this.arrivalLocation = arrivalLocation;
        this.departureDate = departureDate;
        this.hoursDurationArrival = hoursDurationArrival;
        this.minutesDurationArrival = minutesDurationArrival;
        this.hoursDurationScale = hoursDurationScale;
        this.minutesDurationScale = minutesDurationScale;
        this.flightType = new ScaleFlight();
    }

    public Flight(Flight flight) {
        this.id = flight.id;
        this.plane = (flight.plane != null) ? flight.plane.clone() : null;
        this.departureLocation = (flight.departureLocation != null) ? flight.departureLocation.clone() : null;
        this.arrivalLocation = (flight.arrivalLocation != null) ? flight.arrivalLocation.clone() : null;
        this.scaleLocation = (flight.scaleLocation != null) ? flight.scaleLocation.clone() : null;
        this.departureDate = flight.departureDate;
        this.hoursDurationArrival = flight.hoursDurationArrival;
        this.minutesDurationArrival = flight.minutesDurationArrival;
        this.hoursDurationScale = flight.hoursDurationScale;
        this.minutesDurationScale = flight.minutesDurationScale;
        
        if (this.scaleLocation == null) {
            this.flightType = new DirectFlight();
        } else {
            this.flightType = new ScaleFlight();
        }
    }

    public Flight clone() {
        return new Flight(this);
    }

    public String getId() {
        return id;
    }

    public Location getDepartureLocation() {
        return departureLocation;
    }

    public Location getScaleLocation() {
        return scaleLocation;
    }

    public Location getArrivalLocation() {
        return arrivalLocation;
    }

    public LocalDateTime getDepartureDate() {
        return departureDate;
    }

    public int getHoursDurationArrival() {
        return hoursDurationArrival;
    }

    public int getMinutesDurationArrival() {
        return minutesDurationArrival;
    }

    public int getHoursDurationScale() {
        return hoursDurationScale;
    }

    public int getMinutesDurationScale() {
        return minutesDurationScale;
    }

    public Plane getPlane() {
        return plane;
    }
    
    public void setPlane(Plane plane) {
        this.plane = plane;
    }

    public void setDepartureDate(LocalDateTime departureDate) {
        this.departureDate = departureDate;
    }

    public LocalDateTime calculateArrivalDate() {
        return this.flightType.calculateArrival(this);
    }
    
    public Duration getTotatalDuration() {
        return this.flightType.getTotalDuration(this);
    }

}
