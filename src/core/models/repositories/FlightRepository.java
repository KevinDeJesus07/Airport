/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package core.models.repositories;

import core.models.flights.Flight;
import core.models.storage.Storage;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 *
 * @author luchitojunior4
 */
public class FlightRepository {

    private Storage storage;

    public FlightRepository(Storage storage) {
        this.storage = storage;
    }

    public Flight findById(String flightId) {
        return storage.getFlight(flightId);
    }

    public ArrayList<Flight> findAll() {
        return new ArrayList<>(storage.getFlights());
    }

    public ArrayList<Flight> findByPlaneId(String planeId) {
        return (ArrayList<Flight>) storage.getFlights().stream()
                .filter(f -> f.getPlane() != null && planeId.equals(f.getPlane().getId()))
                .collect(Collectors.toList());
    }

    public ArrayList<Flight> findByDepartureLocation(String locationId) {
        return (ArrayList<Flight>) storage.getFlights().stream()
                .filter(f -> f.getDepartureLocation() != null && locationId.equals(f.getDepartureLocation().getId()))
                .collect(Collectors.toList());
    }

    public ArrayList<Flight> findByPassengerId(long passengerId) {
        ArrayList<String> flightIds = (ArrayList<String>) storage.getFlightIdsForPassenger(passengerId);
        if (flightIds == null || flightIds.isEmpty()) {
            return new ArrayList<>();
        }
        return (ArrayList<Flight>) flightIds.stream()
                .map(this::findById)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

    }

    public boolean save(Flight flight) {
        boolean saved = storage.addFlight(flight);
        return saved;
    }

    public void update(Flight flight) {
        storage.updateFlight(flight);
    }

    public void delete(String flightId) {
        boolean deleted = storage.delFlight(flightId);
        if (deleted) {
            storage.removeBookingLinksForFlight(flightId);
        }
    }

}
