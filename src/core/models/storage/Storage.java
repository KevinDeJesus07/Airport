/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package core.models.storage;

import core.models.Flight;
import core.models.Location;
import core.models.Passenger;
import core.models.Plane;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 *
 * @author Kevin
 */
public class Storage {

    private static Storage instance;
    private ArrayList<Flight> flights;
    private ArrayList<Location> locations;
    private ArrayList<Plane> planes;
    private ArrayList<Passenger> passengers;

    private Storage() {
        this.flights = new ArrayList<>();
        this.locations = new ArrayList<>();
        this.planes = new ArrayList<>();
    }

    public static Storage getInstance() {
        if (instance == null) {
            instance = new Storage();
        }
        return instance;
    }

    public Flight getFlight(String id) {
        for (Flight flight : this.flights) {
            if (flight.getId() == id) {
                return flight;
            }
        }
        return null;
    }

    public boolean addFlight(Flight flight) {
        for (Flight f : this.flights) {
            if (f.getId() == flight.getId()) {
                return false;
            }
        }
        this.flights.add(flight);
        return true;
    }

    public boolean delFlight(String id) {
        for (Flight flight : this.flights) {
            if (flight.getId() == id) {
                this.flights.remove(flight);
                return true;
            }
        }
        return false;
    }

    public Location getLocation(String id) {
        for (Location location : this.locations) {
            if (location.getAirportId() == id) {
                return location;
            }
        }
        return null;
    }

    public Plane getPlane(String id) {
        for (Plane plane : this.planes) {
            if (plane.getId() == id) {
                return plane;
            }
        }
        return null;
    }

    public Passenger getPassenger(String id) throws NumberFormatException {
        long idLong = Long.parseLong(id);
        for (Passenger passenger : this.passengers) {
            if (passenger.getId() == idLong) {
                return passenger;
            }
        }
        return null;
    }

    public boolean addPassenger(Passenger passenger) {
        for (Passenger p : this.passengers) {
            if (p.getId() == passenger.getId()) {
                return false;
            }
        }
        passengers.add(passenger);
        return true;
    }

    public boolean updatePassenger(Passenger passenger) {
        Passenger updatePassenger = null;
        for (Passenger p : passengers) {
            if (p.getId() == passenger.getId()) {
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
        return true;
    }

    public ArrayList<Flight> getPassengerFlights(Passenger passenger) {
        for (Passenger p : passengers) {
            if (p.getId() == passenger.getId()) {
                return passenger.getFlights();
            }
        }
        return new ArrayList<>();
    }

    public boolean addPlane(Plane plane) {
        for (Plane p : planes) {
            if (p.getId() == plane.getId()) {
                return false;
            }
        }
        planes.add(plane);
        return true;
    }

    public boolean addLocation(Location location) {
        for (Location l : locations) {
            if (l.getId() == location.getId()) {
                return false;
            }
        }
        locations.add(location);
        return true;
    }

    public ArrayList<Location> getLocations() {
        return locations;
    }

    public ArrayList<Location> getSortedLocations() {
        ArrayList<Location> sortedLocations = new ArrayList<>(locations);
        sortedLocations.sort(Comparator.comparing(loc -> Integer.valueOf(loc.getId())));
        return sortedLocations;
    }

    public ArrayList<Passenger> getSortedPassengers() {
        ArrayList<Passenger> sortedPassengers = new ArrayList<>(passengers);
        sortedPassengers.sort(Comparator.comparingLong(Passenger::getId));
        return sortedPassengers;
    }

    public ArrayList<Flight> getSortedFlights() {
        ArrayList<Flight> sortedFlights = new ArrayList<>(flights);
        sortedFlights.sort(Comparator.comparing(Flight::getDepartureDate));
        return sortedFlights;
    }

    public ArrayList<Plane> getSortedPlanes() {
        ArrayList<Plane> sortedPlanes = new ArrayList<>(planes);
        sortedPlanes.sort(Comparator.comparing(loc -> Integer.valueOf(loc.getId())));
        return sortedPlanes;
    }
}
