/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

import com.formdev.flatlaf.FlatDarkLaf;
import core.utils.persistence.FileManager;
import core.views.AirportFrame;
import javax.swing.UIManager;

/**
 *
 * @author Kevin
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        System.setProperty("flatlaf.useNativeLibrary", "false");

        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }

        String locationsFilePath = "json/locations.json"; 
        String planesFilePath = "json/planes.json";       
        String passengersFilePath = "json/passengers.json"; 
        String flightsFilePath = "json/flights.json"; 
        
        FileManager.loadAllDataFromFiles(
                locationsFilePath,  
                planesFilePath,     
                passengersFilePath, 
                flightsFilePath     
        );

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AirportFrame().setVisible(true);
            }
        });
    }
}
