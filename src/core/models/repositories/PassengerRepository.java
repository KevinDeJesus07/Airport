/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package core.models.repositories;

import core.models.Passenger;
import core.models.storage.Storage;
import java.util.ArrayList;

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
        return null;
    }
    
    public ArrayList<Passenger> findAll() {
        return null;
    }
    
    public ArrayList<Passenger> findByFlightId(String flightId) {
        return null;
    }
    
    public void save(Passenger passenger) {
        
    }
    
    public void update(Passenger passenger) {
        
    }
    
    public void delete(long passengerId) {
        
    }
    
}
