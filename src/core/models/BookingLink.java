/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package core.models;

/**
 *
 * @author luchitojunior4
 */
public class BookingLink {
    
    String flightId;
    long passengerId;
    
    public BookingLink(String flightId, long passengerId) {
        this.flightId = flightId;
        this.passengerId = passengerId;
    }
    
    public String getFlightId() {
        return flightId;
    }
    
    public long getPassengerId() {
        return passengerId;
    }
    
}
