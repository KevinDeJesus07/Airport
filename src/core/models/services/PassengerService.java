/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package core.models.services;

import java.time.LocalDate;
import java.time.Period;

/**
 *
 * @author luchitojunior4
 */
public class PassengerService {
    
    public int calculateAge(LocalDate birthDate) {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
    
    public String generateFullPhone(int countryCode, long phone) {
        return "+" + countryCode + " " + phone;
    }
    
    public String getFullname(String firstname, String lastname) {
        return firstname + " " + lastname;
    }
    
}
