/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package core.models.repositories;

import core.models.Location;
import core.models.storage.Storage;
import java.util.ArrayList;

/**
 *
 * @author luchitojunior4
 */
public class LocationRepository {
    
    private Storage storage;
    
    public LocationRepository(Storage storage) {
        this.storage = storage;
    }
    
    public Location findById(String airportId) {
        return storage.getLocation(airportId);
    }
    
    public ArrayList<Location> findAll() {
        return new ArrayList<>(storage.getLocations());
    }
    
    public boolean save(Location location) {
        return storage.addLocation(location);
    }
    
    public void update(Location location) {
        //return storage.updateLocation(location);
    }
    
    public void delete(String airportId) {
        //return storage.delLocation(airportId);
    }
    
}
