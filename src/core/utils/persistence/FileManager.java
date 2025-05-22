/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package core.utils.persistence;

import core.models.Flight;
import core.models.Location;
import core.models.Passenger;
import core.models.Plane;
import core.models.storage.Storage;
import core.utils.events.DataType;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Kevin
 */
public class FileManager {

    private static Map<String, Location> loadedLocationsMap = new HashMap<>();
    private static Map<String, Plane> loadedPlanesMap = new HashMap<>();
    private static Map<Long, Passenger> loadedPassengersMap = new HashMap<>();

    private FileManager() {
        
    }
    
    private static String readFileAsString(String filePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
    }

    public static void loadAllDataFromFiles(
            String locationsFilePath,
            String planesFilePath,
            String passengersFilePath,
            String flightsFilePath
    ) {
        loadedLocationsMap.clear();
        loadedPlanesMap.clear();
        loadedPassengersMap.clear();
        
        loadLocations(locationsFilePath);
        loadPlanes(planesFilePath);
        loadPassengers(passengersFilePath);
        loadFlights(flightsFilePath);
        
        linkFlights();
    }

    private static void loadLocations(String filePath) {
        try {
            String jsonString = readFileAsString(filePath);

            JSONArray locationsArray = new JSONArray(jsonString);

            for (int i = 0; i < locationsArray.length(); i++) {
                JSONObject locJson = locationsArray.getJSONObject(i);

                String airportId = locJson.getString("airportId");
                String name = locJson.getString("airportName");
                String city = locJson.getString("airportCity");
                String country = locJson.getString("airportCountry");
                double latitude = locJson.getDouble("airportLatitude");
                double longitude = locJson.getDouble("airportLongitude");

                Location location = new Location(
                        airportId, name, city,
                        country, latitude,
                        longitude);

                loadedLocationsMap.put(location.getId(), location);
                Storage.getInstance().addLocation(location);
            }

        } catch (IOException | JSONException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void loadPlanes(String filePath) {
        try {
            String jsonString = readFileAsString(filePath);

            JSONArray planesArray = new JSONArray(jsonString);

            for (int i = 0; i < planesArray.length(); i++) {
                JSONObject planeJson = planesArray.getJSONObject(i);

                String id = planeJson.getString("id");
                String brand = planeJson.getString("brand");
                String model = planeJson.getString("model");
                int maxCapacity = planeJson.getInt("maxCapacity");
                String airline = planeJson.getString("airline");

                Plane plane = new Plane(
                        id, brand, model,
                        maxCapacity, airline
                );

                loadedPlanesMap.put(plane.getId(), plane);
                Storage.getInstance().addPlane(plane);
            }

        } catch (IOException | JSONException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void loadPassengers(String filePath) {
        try {
            String jsonString = readFileAsString(filePath);

            JSONArray passengersArray = new JSONArray(jsonString);

            for (int i = 0; i < passengersArray.length(); i++) {
                JSONObject passengerJson = passengersArray.getJSONObject(i);

                long id = passengerJson.getLong("id");
                String firstname = passengerJson.getString("firstname");
                String lastname = passengerJson.getString("lastname");
                LocalDate birthDate = LocalDate.parse(passengerJson.getString("birthDate"));
                int countryPhoneCode = passengerJson.getInt("countryPhoneCode");
                long phone = passengerJson.getLong("phone");
                String country = passengerJson.getString("country");

                Passenger passenger = new Passenger(
                        id, firstname, lastname, birthDate, countryPhoneCode,
                        phone, country
                );

                loadedPassengersMap.put(passenger.getId(), passenger);
                Storage.getInstance().addPassenger(passenger);
            }

        } catch (IOException | JSONException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private static void linkFlights() {
        List<Passenger> passengers = Storage.getInstance().getPassengers();
        List<Flight> flights = Storage.getInstance().getFlights();
        
        if (passengers.isEmpty() || flights.isEmpty()) {
            return;
        }
        
        int links = 0;
        for (int i = 0; i < Math.min(passengers.size(), flights.size()); i++) {
            Passenger p = passengers.get(i);
            Flight f = flights.get(i);
            
            if (p != null && f != null) {
                p.addFlight(f);
                f.addPassenger(p);
                links++;
            }
        }
        if (links > 0) {
            Storage.getInstance().notifyListeners(DataType.PASSENGER);
            Storage.getInstance().notifyListeners(DataType.FLIGHT);
        }
    }

    private static void loadFlights(String filePath) {
        try {
            String jsonString = readFileAsString(filePath);
            JSONArray flightsArray = new JSONArray(jsonString);
            int flightsLoadedCount = 0;

            for (int i = 0; i < flightsArray.length(); i++) {
                JSONObject flightJson = flightsArray.getJSONObject(i);

                String flightId = flightJson.getString("id");
                String planeId = flightJson.getString("plane");
                String depLocId = flightJson.getString("departureLocation");
                String arrLocId = flightJson.getString("arrivalLocation");

                String scaleLocId = null;
                if (flightJson.has("scaleLocation") && !flightJson.isNull("scaleLocation")) {
                    scaleLocId = flightJson.getString("scaleLocation");
                }

                Plane plane = loadedPlanesMap.get(planeId);
                Location departureLocation = loadedLocationsMap.get(depLocId);
                Location arrivalLocation = loadedLocationsMap.get(arrLocId);
                Location scaleLocation = (scaleLocId != null) ? loadedLocationsMap.get(scaleLocId) : null;

                if (plane == null) {
                    System.err.println("  ERROR: Avión ID '" + planeId + "' no encontrado para vuelo '" + flightId + "'. Vuelo omitido.");
                    continue;
                }
                if (departureLocation == null) {
                    System.err.println("  ERROR: Loc Salida ID '" + depLocId + "' no encontrada para vuelo '" + flightId + "'. Vuelo omitido.");
                    continue;
                }
                if (arrivalLocation == null) {
                    System.err.println("  ERROR: Loc Llegada ID '" + arrLocId + "' no encontrada para vuelo '" + flightId + "'. Vuelo omitido.");
                    continue;
                }
                if (scaleLocId != null && scaleLocation == null) {
                    System.err.println("  ERROR: Loc Escala ID '" + scaleLocId + "' especificada pero no encontrada para vuelo '" + flightId + "'. Vuelo omitido.");
                    continue;
                }

                LocalDateTime departureDateTime = LocalDateTime.parse(flightJson.getString("departureDate"));

                int durArrH = flightJson.getInt("hoursDurationArrival");
                int durArrM = flightJson.getInt("minutesDurationArrival");
                int durScaH = flightJson.optInt("hoursDurationScale", 0);
                int durScaM = flightJson.optInt("minutesDurationScale", 0);

                Flight flight;
                if (scaleLocation != null) {
                    flight = new Flight(flightId, plane, departureLocation, scaleLocation, arrivalLocation,
                            departureDateTime, durArrH, durArrM, durScaH, durScaM);
                } else {
                    flight = new Flight(flightId, plane, departureLocation, arrivalLocation,
                            departureDateTime, durArrH, durArrM);
                }

                if (flightJson.has("passengerIdsOnFlight")) {
                    JSONArray passIdsArray = flightJson.getJSONArray("passengerIdsOnFlight");
                    for (int j = 0; j < passIdsArray.length(); j++) {
                        long passengerId = passIdsArray.getLong(j);
                        Passenger passenger = loadedPassengersMap.get(passengerId);
                        if (passenger != null) {
                            flight.addPassenger(passenger);
                            passenger.addFlight(flight);
                        } else {
                            System.err.println("  Advertencia: Pasajero con ID " + passengerId + " no encontrado para el vuelo '" + flightId + "'.");
                        }
                    }
                }

                Storage.getInstance().addFlight(flight);
                flightsLoadedCount++;
            }
            System.out.println("  " + flightsLoadedCount + " vuelos cargados y añadidos a Storage.");
        } catch (IOException | JSONException | DateTimeException e) {
            System.err.println("ERROR cargando/parseando Vuelos desde '" + filePath + "': " + e.getMessage());
        } catch (Exception e) {
            System.err.println("ERROR INESPERADO procesando Vuelos '" + filePath + "': " + e.getMessage());
            e.printStackTrace();
        }
    }

}
