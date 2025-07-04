/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package core.controllers;

import core.controllers.utils.Response;
import core.controllers.utils.Status;
import core.models.planes.PassengerPlane;
import core.models.planes.Plane;
import core.models.repositories.PlaneRepository;
import core.models.storage.Storage;
import java.util.ArrayList;

/**
 *
 * @author Kevin
 */
public class PlaneController {
    
    private static PlaneRepository planeRepository = new PlaneRepository(Storage.getInstance());

    public static Response createPlane(
            String planeId,
            String brand,
            String model,
            String maxCapacity,
            String airline
    ) {
        try {
            int maxCapacityInt;
            // Válidar planeId
            if (planeId == null || planeId.trim().isEmpty()) {
                return new Response("Plane id must be not empty.", Status.BAD_REQUEST);
            }
            if (!planeId.trim().matches("^[A-Z]{2}\\d{5}$")) {
                return new Response("Plane id must be a valid format: XXYYYYY (e.g. AB12345)", Status.BAD_REQUEST);
            }
            if (Storage.getInstance().getPlane(planeId) != null) {
                return new Response("Plane id must be unique.", Status.BAD_REQUEST);
            }
            
            // válidar brand
            if (brand == null || brand.trim().isEmpty()) {
                return new Response("Brand must be not empty.", Status.BAD_REQUEST);
            }
            
            // válidar model
            if (model == null || model.trim().isEmpty()) {
                return new Response("Model must be not empty.", Status.BAD_REQUEST);
            }
            
            // válidar maxCapacity
            if (maxCapacity == null || maxCapacity.trim().isEmpty()) {
                return new Response("Max capacity must be not empty.", Status.BAD_REQUEST);
            }
            try {
                maxCapacityInt = Integer.parseInt(maxCapacity.trim());
                if (maxCapacityInt < 0) {
                    return new Response("Max capcity must be positive.", Status.BAD_REQUEST);
                }
            } catch (NumberFormatException ex) {
                return new Response("Max capacity must be a number.", Status.BAD_REQUEST);
            }
            
            // válidar airline
            if (airline == null || airline.trim().isEmpty()) {
                return new Response("Airline must be not empty.", Status.BAD_REQUEST);
            }
            Plane newPlane = new PassengerPlane(planeId, brand, model, airline, maxCapacityInt);
            if (!planeRepository.save(newPlane)) {
                return new Response("Plane with that id already exist.", Status.BAD_REQUEST);
            }
            
            return new Response("Plane created succesfully.", Status.CREATED);
            
        } catch (Exception ex) {
            return new Response("Unexpected error.", Status.INTERNAL_SERVER_ERROR);
        }
    }
    
    public static Response getSortedPlanes() {
        try {
            ArrayList<Plane> planes = Storage.getInstance().getSortedPlanes();
            ArrayList<Plane> planesCopy = new ArrayList<>();
            if (planes != null) {
                for (Plane plane : planes) {
                    if (plane != null) {
                        planesCopy.add(plane.clone());
                    } else {
                        planesCopy.add(null);
                    }
                }
            }
            return new Response("Planes loaded succesfully.", Status.OK, planesCopy);
        } catch (Exception ex) {
            return new Response("Unexpected error.", Status.INTERNAL_SERVER_ERROR);
        }
    }

}
