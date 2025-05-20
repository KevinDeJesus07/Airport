/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package core.controllers;

import core.controllers.utils.Response;
import core.controllers.utils.Status;
import core.models.Flight;
import core.models.Passenger;
import core.models.storage.Storage;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;

/**
 *
 * @author Kevin
 */
public class PassengerController {

    public static Response createPassenger(
            String passengerId,
            String firsname,
            String lastname,
            String year,
            String month,
            String day,
            String phoneCode,
            String phone,
            String country
    ) {
        try {
            long passengerIdLong;
            int firsnameInt;
            int lastnameInt;
            int yearInt, monthInt, dayInt;
            int phoneCodeInt;
            long phoneLong;
            int countryInt;

            // Válidar passengerId
            if (passengerId == null || passengerId.trim().isEmpty()) {
                return new Response("Passenger id must be not empty.", Status.BAD_REQUEST);
            }
            try {
                passengerIdLong = Long.parseLong(passengerId.trim());
                if (passengerIdLong <= 0) {
                    return new Response("Passenger id must be positive.", Status.BAD_REQUEST);
                }
            } catch (NumberFormatException ex) {
                return new Response("Passenger id must be a number.", Status.BAD_REQUEST);
            }
            if (passengerId.trim().length() > 15) {
                return new Response("Passenger id must have a maximum of 15 digits.", Status.BAD_REQUEST);
            }
            if (Storage.getInstance().getPassenger(passengerId) != null) {
                return new Response("Passenger id must be unique.", Status.BAD_REQUEST);
            }

            // Válidar firstane
            if (firsname == null || firsname.trim().isEmpty()) {
                return new Response("Firstname must be not empty.", Status.BAD_REQUEST);
            }
            try {
                firsnameInt = Integer.parseInt(firsname.trim());
                return new Response("Firsname must be not a number.", Status.BAD_REQUEST);
            } catch (NumberFormatException ex) {

            }

            // Válidar lastname
            if (lastname == null || lastname.trim().isEmpty()) {
                return new Response("Lastname must be not empty.", Status.BAD_REQUEST);
            }
            try {
                lastnameInt = Integer.parseInt(lastname.trim());
                return new Response("Lastname must be not a number.", Status.BAD_REQUEST);
            } catch (NumberFormatException ex) {

            }

            // Válidar year
            if (year == null || year.trim().isEmpty()) {
                return new Response("Year must be not empty.", Status.BAD_REQUEST);
            }
            try {
                yearInt = Integer.parseInt(year.trim());
                if (yearInt <= 0) {
                    return new Response("Year must be positive.", Status.BAD_REQUEST);
                }
            } catch (NumberFormatException ex) {
                return new Response("Year must be a number.", Status.BAD_REQUEST);
            }

            // Válidar month
            if (month == null || month.trim().isEmpty()) {
                return new Response("Month must be not empty.", Status.BAD_REQUEST);
            }
            try {
                monthInt = Integer.parseInt(month.trim());
                if (monthInt <= 0) {
                    return new Response("Month must be positive.", Status.BAD_REQUEST);
                }
            } catch (NumberFormatException ex) {
                return new Response("Month must be a number.", Status.BAD_REQUEST);
            }

            // Válidar day
            if (day == null || day.trim().isEmpty()) {
                return new Response("Day must be not empty.", Status.BAD_REQUEST);
            }
            try {
                dayInt = Integer.parseInt(day.trim());
                if (dayInt <= 0) {
                    return new Response("Day must be positive.", Status.BAD_REQUEST);
                }
            } catch (NumberFormatException ex) {
                return new Response("Day must be a number.", Status.BAD_REQUEST);
            }

            // Válidar phoneCode
            if (phoneCode == null || phoneCode.trim().isEmpty()) {
                return new Response("Phone code must be not empty.", Status.BAD_REQUEST);
            }
            if (phoneCode.length() > 3) {
                return new Response("Phone code must have a maiximum of 3 digits.", Status.BAD_REQUEST);
            }
            try {
                phoneCodeInt = Integer.parseInt(phoneCode.trim());
                if (phoneCodeInt < 0) {
                    return new Response("Phone code must be positive.", Status.BAD_REQUEST);
                }
            } catch (Exception ex) {
                return new Response("Phone code must be a number.", Status.BAD_REQUEST);
            }

            // Válidar phone
            if (phone == null || phone.trim().isEmpty()) {
                return new Response("Phone must be not empty.", Status.BAD_REQUEST);
            }
            if (phone.length() > 11) {
                return new Response("Phone must have a maiximum of 11 digits.", Status.BAD_REQUEST);
            }
            try {
                phoneLong = Integer.parseInt(phone.trim());
                if (phoneLong < 0) {
                    return new Response("Phone must be positive.", Status.BAD_REQUEST);
                }
            } catch (Exception ex) {
                return new Response("Phone must be a number.", Status.BAD_REQUEST);
            }

            // Válidar country
            if (country == null || country.trim().isEmpty()) {
                return new Response("Country must be not empty.", Status.BAD_REQUEST);
            }
            try {
                countryInt = Integer.parseInt(country.trim());
                return new Response("Country must be not a number.", Status.BAD_REQUEST);
            } catch (NumberFormatException ex) {

            }

            if (!Storage.getInstance().addPassenger(Storage.getInstance().getPassenger(passengerId))) {
                return new Response("Passenger with that id alredy exist.", Status.BAD_REQUEST);
            }

            return new Response("Passenger added succesfully.", Status.CREATED);

        } catch (Exception ex) {
            return new Response("Unexpected error.", Status.INTERNAL_SERVER_ERROR);
        }
    }

    public static Response updatePassenger(
            String passengerId,
            String firsname,
            String lastname,
            String year,
            String month,
            String day,
            String phoneCode,
            String phone,
            String country
    ) {
        try {
            long passengerIdLong;
            int firsnameInt;
            int lastnameInt;
            int yearInt, monthInt, dayInt;
            int phoneCodeInt;
            long phoneLong;
            int countryInt;

            // Válidar passengerId
            if (passengerId == null || passengerId.trim().isEmpty()) {
                return new Response("Passenger id must be not empty.", Status.BAD_REQUEST);
            }
            try {
                passengerIdLong = Long.parseLong(passengerId.trim());
                if (passengerIdLong <= 0) {
                    return new Response("Passenger id must be positive.", Status.BAD_REQUEST);
                }
            } catch (NumberFormatException ex) {
                return new Response("Passenger id must be a number.", Status.BAD_REQUEST);
            }
            if (passengerId.trim().length() > 15) {
                return new Response("Passenger id must have a maximum of 15 digits.", Status.BAD_REQUEST);
            }
            if (Storage.getInstance().getPassenger(passengerId) == null) {
                return new Response("Passenger with that id not exits.", Status.BAD_REQUEST);
            }

            // Válidar firstane
            if (firsname == null || firsname.trim().isEmpty()) {
                return new Response("Firstname must be not empty.", Status.BAD_REQUEST);
            }
            try {
                firsnameInt = Integer.parseInt(firsname.trim());
                return new Response("Firsname must be not a number.", Status.BAD_REQUEST);
            } catch (NumberFormatException ex) {

            }

            // Válidar lastname
            if (lastname == null || lastname.trim().isEmpty()) {
                return new Response("Lastname must be not empty.", Status.BAD_REQUEST);
            }
            try {
                lastnameInt = Integer.parseInt(lastname.trim());
                return new Response("Lastname must be not a number.", Status.BAD_REQUEST);
            } catch (NumberFormatException ex) {

            }

            // Válidar year
            if (year == null || year.trim().isEmpty()) {
                return new Response("Year must be not empty.", Status.BAD_REQUEST);
            }
            try {
                yearInt = Integer.parseInt(year.trim());
                if (yearInt <= 0) {
                    return new Response("Year must be positive.", Status.BAD_REQUEST);
                }
            } catch (NumberFormatException ex) {
                return new Response("Year must be a number.", Status.BAD_REQUEST);
            }

            // Válidar month
            if (month == null || month.trim().isEmpty()) {
                return new Response("Month must be not empty.", Status.BAD_REQUEST);
            }
            try {
                monthInt = Integer.parseInt(month.trim());
                if (monthInt <= 0) {
                    return new Response("Month must be positive.", Status.BAD_REQUEST);
                }
            } catch (NumberFormatException ex) {
                return new Response("Month must be a number.", Status.BAD_REQUEST);
            }

            // Válidar day
            if (day == null || day.trim().isEmpty()) {
                return new Response("Day must be not empty.", Status.BAD_REQUEST);
            }
            try {
                dayInt = Integer.parseInt(day.trim());
                if (dayInt <= 0) {
                    return new Response("Day must be positive.", Status.BAD_REQUEST);
                }
            } catch (NumberFormatException ex) {
                return new Response("Day must be a number.", Status.BAD_REQUEST);
            }

            // Válidar phoneCode
            if (phoneCode == null || phoneCode.trim().isEmpty()) {
                return new Response("Phone code must be not empty.", Status.BAD_REQUEST);
            }
            if (phoneCode.length() > 3) {
                return new Response("Phone code must have a maiximum of 3 digits.", Status.BAD_REQUEST);
            }
            try {
                phoneCodeInt = Integer.parseInt(phoneCode.trim());
                if (phoneCodeInt < 0) {
                    return new Response("Phone code must be positive.", Status.BAD_REQUEST);
                }
            } catch (NumberFormatException ex) {
                return new Response("Phone code must be a number.", Status.BAD_REQUEST);
            }

            // Válidar phone
            if (phone == null || phone.trim().isEmpty()) {
                return new Response("Phone must be not empty.", Status.BAD_REQUEST);
            }
            if (phone.length() > 11) {
                return new Response("Phone must have a maiximum of 11 digits.", Status.BAD_REQUEST);
            }
            try {
                phoneLong = Integer.parseInt(phone.trim());
                if (phoneLong < 0) {
                    return new Response("Phone must be positive.", Status.BAD_REQUEST);
                }
            } catch (NumberFormatException ex) {
                return new Response("Phone must be a number.", Status.BAD_REQUEST);
            }

            // Válidar country
            if (country == null || country.trim().isEmpty()) {
                return new Response("Country must be not empty.", Status.BAD_REQUEST);
            }
            try {
                countryInt = Integer.parseInt(country.trim());
                return new Response("Country must be not a number.", Status.BAD_REQUEST);
            } catch (NumberFormatException ex) {

            }

            if (!Storage.getInstance().updatePassenger(new Passenger(
                    passengerIdLong,
                    firsname,
                    lastname,
                    LocalDate.of(yearInt, monthInt, dayInt),
                    phoneCodeInt,
                    phoneLong,
                    country))) {
                return new Response("Passenger id not found.", Status.NOT_FOUND);
            }

            return new Response("Passenger updated succesfully.", Status.OK);

        } catch (Exception ex) {
            return new Response("Unexpected error.", Status.INTERNAL_SERVER_ERROR);
        }
    }
    
    public static Response showMyFlights(String passengerId) {
        try {
            long passengerIdLong;
            if (passengerId == null || passengerId.trim().isEmpty()) {
                return new Response("Passenger id must be not empty.", Status.BAD_REQUEST);
            }
            try {
                passengerIdLong = Long.parseLong(passengerId.trim());
                if (passengerIdLong <= 0) {
                    return new Response("Passenger id must be positive.", Status.BAD_REQUEST);
                }
            } catch (NumberFormatException ex) {
                return new Response("Passenger id must be a number.", Status.BAD_REQUEST);
            }
            if (passengerId.trim().length() > 15) {
                return new Response("Passenger id must have a maximum of 15 digits.", Status.BAD_REQUEST);
            }
            if (Storage.getInstance().getPassenger(passengerId) == null) {
                return new Response("Passenger id not exist.", Status.BAD_REQUEST);
            }
            
            ArrayList<Flight> flights = Storage.getInstance().getPassengerFlights(Storage.getInstance().getPassenger(passengerId));
            flights.sort(Comparator.comparing(Flight::getDepartureDate));
            
            ArrayList<Flight> flightsCopy = new ArrayList<>();
            if (flights != null) {
                for (Flight flight : flights) {
                    if (flight != null) {
                        flightsCopy.add(flight.clone());
                    } else {
                        flightsCopy.add(null);
                    }
                }
            }
            
            return new Response("Flights loaded succesfully.", Status.OK, flightsCopy);
            
        } catch (Exception ex) {
            return new Response("Unexpected error.", Status.INTERNAL_SERVER_ERROR);
        }
    }
    public static Response getSortedPassengers() {
        try {
            ArrayList<Passenger> passengers = Storage.getInstance().getSortedPassengers();
            ArrayList<Passenger> passengersCopy = new ArrayList<>();
            if (passengers != null) {
                for (Passenger passenger : passengers) {
                    if (passenger != null) {
                        passengersCopy.add(passenger.clone());
                    } else {
                        passengersCopy.add(null);
                    }
                }
            }
            return new Response("Passengers loaded succesfully.", Status.OK, passengersCopy);
        } catch (Exception ex) {
            return new Response("Unexpected error.", Status.INTERNAL_SERVER_ERROR);
        }
    }
}
