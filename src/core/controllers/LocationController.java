/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package core.controllers;

import core.controllers.utils.Response;
import core.controllers.utils.Status;
import core.models.Location;
import core.models.storage.Storage;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 *
 * @author Kevin
 */
public class LocationController {

    public static Response createLocation(
            String locationId,
            String name,
            String city,
            String country,
            String latitude,
            String longitude
    ) {
        try {
            double latitudeDouble, longitudeDouble;

            // Válidar locationId
            if (locationId == null || locationId.trim().isEmpty()) {
                return new Response("Airport id must be not empty.", Status.BAD_REQUEST);
            }
            if (!locationId.trim().matches("^[A-Z]{3}$")) {
                return new Response("Airport id must be a valid format: XXX (e.g. AAA).", Status.BAD_REQUEST);
            }
            if (Storage.getInstance().getLocation(locationId) != null) {
                return new Response("Airport with that id already exist.", Status.BAD_REQUEST);
            }

            // válidar name
            if (name == null || name.trim().isEmpty()) {
                return new Response("Airport name must be not empty.", Status.BAD_REQUEST);
            }

            // válidar city
            if (city == null || city.trim().isEmpty()) {
                return new Response("Airport city must be not empty.", Status.BAD_REQUEST);
            }

            // Válidar country
            if (country == null || country.trim().isEmpty()) {
                return new Response("Airport country must be not empty.", Status.BAD_REQUEST);
            }

            // Válidar latitude
            if (latitude == null || latitude.trim().isEmpty()) {
                return new Response("Latitude must be not empty.", Status.BAD_REQUEST);
            }
            try {
                BigDecimal bdLatitude = new BigDecimal(latitude.trim());
                if (bdLatitude.scale() > 4) {
                    return new Response("Latitude must have a maximun of 4 digits.", Status.BAD_REQUEST);
                }
                latitudeDouble = bdLatitude.doubleValue();
                if (latitudeDouble < -90.0 || latitudeDouble > 90.0) {
                    return new Response("Latitude must be in range [-90.0, 90.0]", Status.BAD_REQUEST);
                }
            } catch (NumberFormatException ex) {
                return new Response("Latitude must be a number.", Status.BAD_REQUEST);
            } catch (ArithmeticException ex) {
                return new Response("Unexpected error.", Status.INTERNAL_SERVER_ERROR);
            }

            // Válidar longitud
            if (longitude == null || longitude.trim().isEmpty()) {
                return new Response("Longitud must be not empty.", Status.BAD_REQUEST);
            }
            try {
                BigDecimal bdLongitude = new BigDecimal(longitude.trim());
                if (bdLongitude.scale() > 4) {
                    return new Response("Longitude must have a maximun of 4 digits.", Status.BAD_REQUEST);
                }
                longitudeDouble = bdLongitude.doubleValue();
                if (longitudeDouble < -180.0 || longitudeDouble > 180.0) {
                    return new Response("Longitude must be in range [-180.0, 180.0]", Status.BAD_REQUEST);
                }
            } catch (NumberFormatException ex) {
                return new Response("Longitude must be a number.", Status.BAD_REQUEST);
            } catch (ArithmeticException ex) {
                return new Response("Unexpected error.", Status.INTERNAL_SERVER_ERROR);
            }

            if (!Storage.getInstance().addLocation(new Location(
                    locationId, name, city,
                    country, latitudeDouble,
                    longitudeDouble))) {
                return new Response("Airport with that id already exist.", Status.BAD_REQUEST);
            }
            
            return new Response("Airport created succesfully.", Status.CREATED);
            
        } catch (Exception ex) {
            return new Response("Unexpected error.", Status.INTERNAL_SERVER_ERROR);
        }
    }

    public static Response getSortedLocations() {
        try {
            ArrayList<Location> locations = Storage.getInstance().getSortedLocations();
            ArrayList<Location> locationsCopy = new ArrayList<>();
            if (locations != null) {
                for (Location location : locations) {
                    if (location != null) {
                        locationsCopy.add(location.clone());
                    } else {
                        locationsCopy.add(null);
                    }
                }
            }
            return new Response("Locations loaded succesfully.", Status.OK, locationsCopy);
        } catch (Exception ex) {
            return new Response("Unexpected error.", Status.INTERNAL_SERVER_ERROR);
        }
    }
}
