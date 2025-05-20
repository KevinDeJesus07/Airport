/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package core.controllers;

import core.controllers.utils.Response;
import core.controllers.utils.Status;
import core.models.Flight;
import core.models.storage.Storage;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 *
 * @author Kevin
 */
public class FlightController {

    public static Response createFlight(String id, String planeId,
            String departureLocationId, String arrivalLocationId,
            String scaleLocationId, String departureYear, String departureMonth,
            String departureDay, String departureHour, String departureMinutes,
            String arrivalHour, String arrivalMinutes, String scaleHour,
            String scaleMinutes) {
        try {
            int departureYearInt;
            int departureMonthInt;
            int departureDayInt;
            int departureHourInt;
            int departureMinutesInt;

            int arrivalHourInt;
            int arrivalMinutesInt;

            int scaleHourInt = 0;
            int scaleMinutesInt = 0;

            LocalDateTime departureDate;

            // Válidar id
            if (id == null || id.trim().isEmpty()) {
                return new Response("Id must be not empty.", Status.BAD_REQUEST);
            }
            if (!id.matches("^[A-Z]{3}\\d{3}$")) {
                return new Response("Id must be a valid format: XXXYYY (e.g: ABC123).", Status.BAD_REQUEST);
            }

            if (Storage.getInstance().getFlight(id) != null) {
                return new Response("Id must be unique.", Status.BAD_REQUEST);
            }

            // Válidar planeId
            if (planeId == null || planeId.trim().isEmpty()) {
                return new Response("Plane id must be not empty.", Status.BAD_REQUEST);
            }
            if (!planeId.matches("^[A-Z]{2}\\d{5}$")) {
                return new Response("Plane ide must be a valid format: XXYYYYY (e.g; AB12345).", Status.BAD_REQUEST);
            }

            // válidar departureLocationId
            if (departureLocationId == null || departureLocationId.trim().isEmpty()) {
                return new Response("Departure location must be not empty.", Status.BAD_REQUEST);
            }

            // Válidar arrivalLocationId
            if (arrivalLocationId == null || arrivalLocationId.trim().isEmpty()) {
                return new Response("Arrival location must be not empty.", Status.BAD_REQUEST);
            }

            // Válidar departureYear
            if (departureYear == null || departureYear.trim().isEmpty()) {
                return new Response("Departure year must be not empty.", Status.BAD_REQUEST);
            }

            try {
                departureYearInt = Integer.parseInt(departureYear.trim());
                if (departureYearInt < 0) {
                    return new Response("Departure year must be positive.", Status.BAD_REQUEST);
                }
            } catch (NumberFormatException ex) {
                return new Response("Departure year must be a number.", Status.BAD_REQUEST);
            }

            // Válidar departureMonth
            if (departureMonth == null || departureMonth.trim().isEmpty()) {
                return new Response("Departure month must be not empty.", Status.BAD_REQUEST);
            }

            try {
                departureMonthInt = Integer.parseInt(departureMonth.trim());
            } catch (NumberFormatException ex) {
                return new Response("Departure month must be a number.", Status.BAD_REQUEST);
            }

            // Válidar departureDay
            if (departureDay == null || departureDay.trim().isEmpty()) {
                return new Response("Departure day must be not empty.", Status.BAD_REQUEST);
            }

            try {
                departureDayInt = Integer.parseInt(departureDay.trim());
            } catch (NumberFormatException ex) {
                return new Response("Departure day must bea a number.", Status.BAD_REQUEST);
            }

            // Válidar departureHour
            if (departureHour == null || departureHour.trim().isEmpty()) {
                return new Response("Departure hour must be not empty.", Status.BAD_REQUEST);
            }

            try {
                departureHourInt = Integer.parseInt(departureHour.trim());
            } catch (NumberFormatException ex) {
                return new Response("Departure hour must be a number.", Status.BAD_REQUEST);
            }

            // Válidar departureMinutes
            if (departureMinutes == null || departureMinutes.trim().isEmpty()) {
                return new Response("Departure minutes must be not empty.", Status.BAD_REQUEST);
            }

            try {
                departureMinutesInt = Integer.parseInt(departureMinutes.trim());
            } catch (NumberFormatException ex) {
                return new Response("Departure minutes must be a number.", Status.BAD_REQUEST);
            }

            // Válidar arrivalHour
            if (arrivalHour == null || arrivalHour.trim().isEmpty()) {
                return new Response("Arrival hour must be not empty.", Status.BAD_REQUEST);
            }

            try {
                arrivalHourInt = Integer.parseInt(arrivalHour.trim());
            } catch (NumberFormatException ex) {
                return new Response("Arrival hour must be a number.", Status.BAD_REQUEST);
            }

            // Válidar arrivalMinutes
            if (arrivalMinutes == null || arrivalMinutes.trim().isEmpty()) {
                return new Response("Arrival minutes must be not empty.", Status.BAD_REQUEST);
            }

            try {
                arrivalMinutesInt = Integer.parseInt(arrivalMinutes.trim());
            } catch (NumberFormatException ex) {
                return new Response("Arrival minutes must be a number.", Status.BAD_REQUEST);
            }

            departureDate = LocalDateTime.of(departureYearInt, departureMonthInt, departureDayInt, departureHourInt, departureMinutesInt);

            // Válidar scaleLocationId
            boolean hasScale = !(scaleLocationId == null || scaleLocationId.trim().isEmpty());
            if (hasScale) {
                // Válidar scaleHour
                if (scaleHour == null || scaleHour.trim().isEmpty()) {
                    return new Response("Scale hour must be not empty.", Status.BAD_REQUEST);
                }

                try {
                    scaleHourInt = Integer.parseInt(scaleHour.trim());
                } catch (NumberFormatException ex) {
                    return new Response("Scale hour must be a number.", Status.BAD_REQUEST);
                }

                // Válidar scaleMinutes
                if (scaleMinutes == null || scaleMinutes.trim().isEmpty()) {
                    return new Response("Scale minutes must be not empty.", Status.BAD_REQUEST);
                }

                try {
                    scaleMinutesInt = Integer.parseInt(scaleMinutes.trim());
                } catch (NumberFormatException ex) {
                    return new Response("Scale minutes must be a number.", Status.BAD_REQUEST);
                }
                
                if (arrivalHourInt <= 0 && arrivalMinutesInt <= 0 && scaleHourInt <= 0 && scaleMinutesInt <= 0) {
                    return new Response("Flight time must be largest that 0.", Status.BAD_REQUEST);
                }

                Flight flight = new Flight(
                        id,
                        Storage.getInstance().getPlane(planeId),
                        Storage.getInstance().getLocation(departureLocationId),
                        Storage.getInstance().getLocation(scaleLocationId),
                        Storage.getInstance().getLocation(arrivalLocationId),
                        departureDate,
                        arrivalHourInt,
                        arrivalMinutesInt,
                        scaleHourInt,
                        scaleMinutesInt
                );
                if (!Storage.getInstance().addFlight(flight)) {
                    return new Response("A flight with that id already exist.", Status.BAD_REQUEST);
                }
                return new Response("Flight created succesfully.", Status.CREATED);
            }
            
            if (arrivalHourInt <= 0 && arrivalMinutesInt <= 0) {
                return new Response("Flight time must be largest that 0.", Status.BAD_REQUEST);
            }
            
            Flight flight = new Flight(
                    id,
                    Storage.getInstance().getPlane(planeId),
                    Storage.getInstance().getLocation(departureLocationId),
                    Storage.getInstance().getLocation(arrivalLocationId),
                    departureDate,
                    arrivalHourInt,
                    arrivalMinutesInt
            );
            if (!Storage.getInstance().addFlight(flight)) {
                return new Response("A flight with that id already existe.", Status.BAD_REQUEST);
            }
            return new Response("Flight created succesfully.", Status.CREATED);

        } catch (Exception ex) {
            return new Response("Unexpected error.", Status.INTERNAL_SERVER_ERROR);
        }
    }

    public static Response addFlight(String passengerId, String flightId) {
        try {
            long passengerIdLong;
            int flightIdInt;
            
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
            
            // Válidar flightId
            if (flightId == null || flightId.trim().isEmpty()) {
                return new Response("Flight id must be not empty.", Status.BAD_REQUEST);
            }
            try {
                flightIdInt = Integer.parseInt(flightId.trim());
            } catch (NumberFormatException ex) {
                return new Response("Flight id must be a number.", Status.BAD_REQUEST);
            }
            
            Storage.getInstance().getPassenger(passengerId).addFlight(Storage.getInstance().getFlight(flightId));
            Storage.getInstance().getFlight(flightId).addPassenger(Storage.getInstance().getPassenger(passengerId));
            
            return new Response("Passenger added succesfully to the flight.", Status.OK);
            
        } catch (Exception ex) {
            return new Response("Unexpected error.", Status.INTERNAL_SERVER_ERROR);
        }
    }
    
    public static Response delayFlight(String flightId, String hours, String minutes) {
        try {
            int flightIdInt;
            int hoursInt;
            int minutesInt;
            
            // Válidar flightId
            if (flightId == null || flightId.trim().isEmpty()) {
                return new Response("Flight id must be not empty.", Status.BAD_REQUEST);
            }
            try {
                flightIdInt = Integer.parseInt(flightId.trim());
            } catch (NumberFormatException ex) {
                return new Response("Flight id must be a number.", Status.BAD_REQUEST);
            }
            
            // Válidar hours
            if (hours == null || hours.trim().isEmpty()) {
                return new Response("Hours must be not empty.", Status.BAD_REQUEST);
            }
            try {
                hoursInt = Integer.parseInt(hours.trim());
            } catch (Exception ex) {
                return new Response("Hours must be a number.", Status.BAD_REQUEST);
            }
            
            // Válidar minutes
            if (minutes == null || minutes.trim().isEmpty()) {
                return new Response("Minutes must be not empty.", Status.BAD_REQUEST);
            }
            try {
                minutesInt = Integer.parseInt(minutes.trim());
            } catch (Exception ex) {
                return new Response("Minutes must be a number.", Status.BAD_REQUEST);
            }
            
            if (hoursInt <= 0 && minutesInt <= 0) {
                return new Response("Delay time must be a longest that 00:00.", Status.BAD_REQUEST);
            }
            
            Storage.getInstance().getFlight(flightId).delay(hoursInt, minutesInt);
            return new Response("Delay apply succesfully.", Status.OK);
        } catch (Exception ex) {
            return new Response("Unexpected error.", Status.INTERNAL_SERVER_ERROR);
        }
    }
    
    public static Response getSortedFlights() {
        try {
            ArrayList<Flight> flights = Storage.getInstance().getSortedFlights();
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
}
