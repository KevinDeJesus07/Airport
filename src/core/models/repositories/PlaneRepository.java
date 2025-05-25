/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package core.models.repositories;

import core.models.planes.Plane;
import core.models.storage.Storage;
import java.util.ArrayList;

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
        return null;
    }
    
    public ArrayList<Plane> findAll() {
        return null;
    }
    
    public ArrayList<Plane> findByAirline(String airline) {
        return null;
    }
    
    public void save(Plane plane) {
        
    }
    
    public void update(Plane plane) {
        
    }
    
    public void delete(String planeId) {
        
    }
    
}
