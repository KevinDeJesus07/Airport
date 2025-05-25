/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package core.models.storage;

import core.models.BookingLink;
import core.models.flights.Flight;
import core.models.Location;
import core.models.Passenger;
import core.models.planes.Plane;
import core.utils.events.DataType;
import core.utils.events.EventListeners;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.EnumMap;
import java.util.stream.Collectors;

/**
 *
 * @author Kevin
 */
public class Storage {

    private final Map<DataType, List<EventListeners>> listeners;
    private static Storage instance;
    private ArrayList<Flight> flights;
    private ArrayList<Location> locations;
    private ArrayList<Plane> planes;
    private ArrayList<Passenger> passengers;
    private ArrayList<BookingLink> bookingLinks;

    private Storage() {
        this.flights = new ArrayList<>();
        this.locations = new ArrayList<>();
        this.planes = new ArrayList<>();
        this.passengers = new ArrayList<>();
        this.listeners = new EnumMap<>(DataType.class);
        this.bookingLinks = new ArrayList<>();
        for (DataType type : DataType.values()) {
            listeners.put(type, new ArrayList<>());
        }
    }

    public void subscribe(DataType eventType, EventListeners listener) {
        listeners.get(eventType).add(listener);
    }

    public void unsubscribe(DataType eventType, EventListeners listener) {
        if (listeners.containsKey(eventType)) {
            listeners.get(eventType).remove(listener);
        }
    }

    public void notifyListeners(DataType eventType) {
        List<EventListeners> l = listeners.get(eventType);
        if (l != null) {
            for (EventListeners listener : l) {
                if (listener != null) {
                    listener.update(eventType);
                }
            }
        }
    }

    public static Storage getInstance() {
        if (instance == null) {
            instance = new Storage();
        }
        return instance;
    }

    public ArrayList<Flight> getFlights() {
        return this.flights;
    }

    public ArrayList<Passenger> getPassengers() {
        return this.passengers;
    }

    public Flight getFlight(String id) {
        for (Flight flight : this.flights) {
            if (flight != null && flight.getId() != null && flight.getId().equals(id)) {
                return flight;
            }
        }
        return null;
    }

    public boolean addFlight(Flight flight) {
        if (flight == null || flight.getId() == null) {
            return false;
        }
        for (Flight f : this.flights) {
            if (f != null && f.getId() != null && f.getId().equals(flight.getId())) {
                return false;
            }
        }
        boolean added = this.flights.add(flight);
        if (added) {
            notifyListeners(DataType.FLIGHT);
        }
        return added;
    }

    public boolean updateFlight(Flight flight) {
        for (int i = 0; i < this.flights.size(); i++) {
            Flight currFlight = this.flights.get(i);
            if (currFlight.getId().equals(flight.getId())) {
                this.flights.set(i, flight);
                notifyListeners(DataType.FLIGHT);
                return true;
            }
        }
        return false;
    }

    public boolean delFlight(String id) {
        if (id == null) {
            return false;
        }
        boolean removed = false;
        for (Flight flight : this.flights) {
            if (flight != null && flight.getId() != null && flight.getId().equals(id)) {
                removed = this.flights.remove(flight);
                return true;
            }
        }
        if (removed) {
            notifyListeners(DataType.FLIGHT);
        }
        return removed;
    }

    public Location getLocation(String id) {
        for (Location location : this.locations) {
            if (location != null && location.getAirportId() != null && location.getAirportId().equals(id)) {
                return location;
            }
        }
        return null;
    }

    public Plane getPlane(String id) {
        for (Plane plane : this.planes) {
            if (plane != null && plane.getId() != null && plane.getId().equals(id)) {
                return plane;
            }
        }
        return null;
    }

    public Passenger getPassenger(String id) throws NumberFormatException {
        long idLong = Long.parseLong(id);
        for (Passenger passenger : this.passengers) {
            if (passenger != null && passenger.getId() == idLong) {
                return passenger;
            }
        }
        return null;
    }

    public boolean addPassenger(Passenger passenger) {
        if (passenger == null) {
            return false;
        }
        for (Passenger p : this.passengers) {
            if (p != null && p.getId() == passenger.getId()) {
                return false;
            }
        }
        boolean added = passengers.add(passenger);
        if (added) {
            notifyListeners(DataType.PASSENGER);
        }
        return added;
    }

    public boolean updatePassenger(Passenger passenger) {
        if (passenger == null) {
            return false;
        }
        Passenger updatePassenger = null;
        for (Passenger p : passengers) {
            if (p != null && p.getId() == passenger.getId()) {
                updatePassenger = p;
                break;
            }
        }
        if (updatePassenger == null) {
            return false;
        }
        updatePassenger.setFirstname(passenger.getFirstname());
        updatePassenger.setLastname(passenger.getLastname());
        updatePassenger.setBirthDate(passenger.getBirthDate());
        updatePassenger.setCountryPhoneCode(passenger.getCountryPhoneCode());
        updatePassenger.setPhone(passenger.getPhone());
        updatePassenger.setCountry(passenger.getCountry());

        notifyListeners(DataType.PASSENGER);

        return true;
    }

    public boolean addPlane(Plane plane) {
        if (plane == null || plane.getId() == null) {
            return false;
        }
        for (Plane p : planes) {
            if (p != null && p.getId() != null && p.getId().equals(plane.getId())) {
                return false;
            }
        }
        boolean added = planes.add(plane);
        if (added) {
            notifyListeners(DataType.PLANE);
        }
        return added;
    }

    public boolean addLocation(Location location) {
        if (location == null || location.getId() == null) {
            return false;
        }
        for (Location l : locations) {
            if (l != null && l.getId() != null && l.getId().equals(location.getId())) {
                return false;
            }
        }
        boolean added = locations.add(location);
        if (added) {
            notifyListeners(DataType.LOCATION);
        }
        return added;
    }

    public ArrayList<Location> getLocations() {
        return locations;
    }

    public ArrayList<Location> getSortedLocations() {
        if (this.locations == null) {
            return new ArrayList<>();
        }
        ArrayList<Location> sortedLocations = new ArrayList<>(locations);

        sortedLocations.removeIf(l -> l == null || l.getId() == null);

        sortedLocations.sort(Comparator.comparing(Location::getId));

        return sortedLocations;
    }

    public ArrayList<Passenger> getSortedPassengers() {
        if (this.passengers == null) {
            return new ArrayList<>();
        }
        ArrayList<Passenger> sortedPassengers = new ArrayList<>(passengers);
        sortedPassengers.removeIf(p -> p == null);
        sortedPassengers.sort(Comparator.comparingLong(Passenger::getId));
        return sortedPassengers;
    }

    public ArrayList<Flight> getSortedFlights() {
        if (this.flights == null) {
            return new ArrayList<>();
        }
        ArrayList<Flight> sortedFlights = new ArrayList<>(flights);
        sortedFlights.removeIf(f -> f == null || f.getId() == null);
        sortedFlights.sort(Comparator.comparing(Flight::getDepartureDate));
        return sortedFlights;
    }

    public ArrayList<Plane> getSortedPlanes() {
        if (this.planes == null) {
            return new ArrayList<>();
        }
        ArrayList<Plane> sortedPlanes = new ArrayList<>(planes);
        sortedPlanes.removeIf(p -> p == null || p.getId() == null);
        sortedPlanes.sort(Comparator.comparing(Plane::getId));
        return sortedPlanes;
    }

    public void addBookinfLink(String flightId, long passengerId) {
        for (BookingLink link : bookingLinks) {
            if (link.getFlightId().equals(flightId) && link.getPassengerId() == passengerId) {
                return;
            }
        }
        this.bookingLinks.add(new BookingLink(flightId, passengerId));
        notifyListeners(DataType.ALL);
    }

    public List<String> getFlightIdsForPassenger(long passengerId) {
        return this.bookingLinks.stream()
                .filter(link -> link.getPassengerId() == passengerId)
                .map(BookingLink::getFlightId)
                .collect(Collectors.toList());
    }

    public List<Long> getPassengerIdsForFlight(String flightId) {
        return this.bookingLinks.stream()
                .filter(link -> flightId.equals(link.getFlightId()))
                .map(BookingLink::getPassengerId)
                .collect(Collectors.toList());
    }

    public void removeBookingLinksForFlight(String flightId) {
        this.bookingLinks.removeIf(link -> flightId.equals(link.getFlightId()));
        notifyListeners(DataType.ALL);
    }

    public void removeBookingLinksForPassenger(long passengerId) {
        this.bookingLinks.removeIf(link -> link.getPassengerId() == passengerId);
        notifyListeners(DataType.ALL);
    }

    public void removeSpecificBookingLink(String flightId, long passengerId) {
        this.bookingLinks.removeIf(link -> flightId.equals(link.getFlightId()) && link.getPassengerId() == passengerId);
        notifyListeners(DataType.ALL);
    }

}
