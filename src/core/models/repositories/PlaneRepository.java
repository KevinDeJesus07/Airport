/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package core.models.repositories;

import core.models.planes.Plane;
import core.models.storage.Storage;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 *
 * @author luchitojunior4
 */
public class PlaneRepository {
    
    private Storage storage;
    
    public PlaneRepository(Storage storage) {
        this.storage = storage;
    }
    
    public Plane findById(String planeId) {
        return storage.getPlane(planeId);
    }
    
    public ArrayList<Plane> findAll() {
        return new ArrayList<>(storage.getPlanes());
    }
    
    public ArrayList<Plane> findByAirline(String airline) {
        return (ArrayList<Plane>) storage.getPlanes().stream()
                .filter(p -> p != null && airline.equals(p.getAirline()))
                .collect(Collectors.toList());
    }
    
    public boolean save(Plane plane) {
        return storage.addPlane(plane);
    }
    
    public void update(Plane plane) {
        
    }
    
    public void delete(String planeId) {
        
    }
    
}
