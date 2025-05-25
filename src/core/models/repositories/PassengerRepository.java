/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package core.models.repositories;

import core.models.Passenger;
import core.models.storage.Storage;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 *
 * @author luchitojunior4
 */
public class PassengerRepository {
    
    private Storage storage;
    
    public PassengerRepository(Storage storage) {
        this.storage = storage;
    }
    
    public Passenger findById(long passengerId) {
        return storage.getPassenger("" + passengerId);
    }
    
    public ArrayList<Passenger> findAll() {
        return new ArrayList<>(storage.getPassengers());
    }
    
    public ArrayList<Passenger> findByFlightId(String flightId) {
        ArrayList<Long> passengerIds = (ArrayList<Long>) storage.getPassengerIdsForFlight(flightId);
        if (passengerIds == null || passengerIds.isEmpty()) {
            return new ArrayList<>();
        }
        return (ArrayList<Passenger>) passengerIds.stream()
                .map(this::findById)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    
    public boolean save(Passenger passenger) {
        boolean saved = storage.addPassenger(passenger);
        return saved;
    }
    
    public boolean update(Passenger passenger) {
        boolean updated = storage.updatePassenger(passenger);
        return updated;
    }
    
    public void delete(long passengerId) {
        return;
    }

    public int countByFlightId(String flightId) {
        ArrayList<Long> ids = (ArrayList<Long>) storage.getPassengerIdsForFlight(flightId);
        return ids == null ? 0 : ids.size();
    }

    public boolean linkPassengerToFlight(long passengerId, String flightId) {
        Passenger passenger = findById(passengerId);
        if (passenger == null || storage.getFlight(flightId) == null) {
            return false;
        }
        storage.addBookingLink(flightId, passengerId);
        return true;
    }
    
}
