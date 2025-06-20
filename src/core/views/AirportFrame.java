/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package core.views;

import core.models.flights.Flight;
import core.models.Location;
import core.models.Passenger;
import core.models.planes.Plane;
import com.formdev.flatlaf.FlatDarkLaf;
import core.controllers.FlightController;
import core.controllers.LocationController;
import core.controllers.PassengerController;
import core.controllers.PlaneController;
import core.controllers.utils.Response;
import core.controllers.utils.Status;
import core.models.repositories.FlightRepository;
import core.models.repositories.PassengerRepository;
import core.models.services.PassengerService;
import core.models.storage.Storage;
import java.awt.Color;
import java.time.LocalDateTime;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import core.utils.events.EventListeners;
import core.utils.events.DataType;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.SwingUtilities;

/**
 *
 * @author edangulo
 */
public class AirportFrame extends javax.swing.JFrame implements EventListeners {

    /**
     * Creates new form AirportFrame
     */
    private int x, y;
    private ArrayList<Passenger> passengers;
    private ArrayList<Plane> planes;
    private ArrayList<Location> locations;
    private ArrayList<Flight> flights;
    private PassengerRepository passengerRepository = new PassengerRepository(Storage.getInstance());
    private FlightRepository flightRepository = new FlightRepository(Storage.getInstance());
    private PassengerService passengerService = new PassengerService();

    public AirportFrame() {
        initComponents();

        Storage storage = Storage.getInstance();
        storage.subscribe(DataType.PASSENGER, this);
        storage.subscribe(DataType.LOCATION, this);
        storage.subscribe(DataType.PLANE, this);
        storage.subscribe(DataType.FLIGHT, this);

        this.passengers = new ArrayList<>();
        this.planes = new ArrayList<>();
        this.locations = new ArrayList<>();
        this.flights = new ArrayList<>();

        this.setBackground(new Color(0, 0, 0, 0));
        this.setLocationRelativeTo(null);

        if (userSelect != null) {
            userSelect.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent evt) {
                    if (evt.getStateChange() == ItemEvent.SELECTED) {
                        refreshMyFlightsTable();
                    }
                }
            });
        }

        this.generateMonths();
        this.generateDays();
        this.generateHours();
        this.generateMinutes();
        this.blockPanels();

        refreshPassengerUIComponents();
        refreshLocationUIComponents();
        refreshPlaneUIComponents();
        refreshFlightUIComponents();

    }

    @Override
    public void update(DataType type) {
        SwingUtilities.invokeLater(() -> {
            if (type == DataType.PASSENGER || type == DataType.ALL) {
                refreshPassengerUIComponents();
            }
            if (type == DataType.LOCATION || type == DataType.ALL) {
                refreshLocationUIComponents();
            }
            if (type == DataType.PLANE || type == DataType.PLANE) {
                refreshPlaneUIComponents();
            }
            if (type == DataType.FLIGHT || type == DataType.FLIGHT) {
                refreshFlightUIComponents();
            }
        });
    }

    private void refreshFlightUIComponents() {
        Response response = FlightController.getSortedFlights();

        List<Flight> flights = null;
        boolean success = false;
        if (response.getStatus() < 400 && response.getObject() != null) {
            try {
                flights = (List<Flight>) response.getObject();
                success = true;
            } catch (ClassCastException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Error interno: Datos de vuelos con formato inesperado.",
                        "Error de Datos", JOptionPane.ERROR_MESSAGE);
                flights = new ArrayList<>();
            }
        } else if (response.getStatus() >= 400) {
            JOptionPane.showMessageDialog(this,
                    response.getMessage(), "Error " + response.getStatus(),
                    JOptionPane.WARNING_MESSAGE);
            flights = new ArrayList<>();
        } else {
            flights = new ArrayList<>();
            success = true;
        }

        if (flights == null) {
            flights = new ArrayList<>();
        }

        populateFlightComboBox(flights);
        populateFlightAddComboBox(flights);
        refreshAllFlightsTable(flights);
        refreshMyFlightsTable();
    }

    private void populateFlightComboBox(List<Flight> flightList) {
        if (idDelayComboBox == null) {
            return;
        }

        Object select = idDelayComboBox.getSelectedItem();
        String st = null;
        if (select != null) {
            st = select.toString();
        }

        idDelayComboBox.removeAllItems();
        idDelayComboBox.addItem("ID");

        if (idDelayComboBox != null) {
            for (Flight f : flightList) {
                if (f != null) {
                    idDelayComboBox.addItem("" + f.getId());
                }
            }
        }

        if (st != null) {
            idDelayComboBox.setSelectedItem(st);
            if (idDelayComboBox.getSelectedIndex() <= 0 && !st.equals("ID") && idDelayComboBox.getItemCount() > 1) {
                idDelayComboBox.setSelectedIndex(0);
            } else if (idDelayComboBox.getSelectedIndex() == -1 && idDelayComboBox.getItemCount() > 0) {
                idDelayComboBox.setSelectedIndex(0);
            }
        } else {
            if (idDelayComboBox.getItemCount() > 0) {
                idDelayComboBox.setSelectedIndex(0);
            }
        }
    }

    private void populateFlightAddComboBox(List<Flight> flightList) {
        if (flightAddComboBox == null) {
            return;
        }

        Object select = flightAddComboBox.getSelectedItem();
        String st = null;
        if (select != null) {
            st = select.toString();
        }

        flightAddComboBox.removeAllItems();
        flightAddComboBox.addItem("ID");

        for (Flight f : flightList) {
            if (f != null) {
                flightAddComboBox.addItem(f.getId());
            }
        }

        if (st != null) {
            flightAddComboBox.setSelectedItem(st);
            if (flightAddComboBox.getSelectedIndex() <= 0 && !st.equals("ID") && flightAddComboBox.getItemCount() > 1) {
                flightAddComboBox.setSelectedIndex(0);
            } else if (flightAddComboBox.getSelectedIndex() == -1 && flightAddComboBox.getItemCount() > 0) {
                flightAddComboBox.setSelectedIndex(0);
            }
        } else {
            if (flightAddComboBox.getItemCount() > 0) {
                flightAddComboBox.setSelectedIndex(0);
            }
        }
    }

    private void refreshMyFlightsTable() {
        if (myFlightsTable == null || userSelect == null) {
            if (myFlightsTable != null) {
                ((DefaultTableModel) myFlightsTable.getModel()).setRowCount(0);
            }
            return;
        }
        DefaultTableModel model = (DefaultTableModel) myFlightsTable.getModel();
        model.setRowCount(0);

        String id = null;
        if (userSelect.getSelectedIndex() > 0) {
            Object select = userSelect.getSelectedItem();
            if (select != null) {
                id = select.toString();
            }
        }
        if (id != null && !id.trim().isEmpty()) {
            Response response = PassengerController.showMyFlights(id);
            if (response.getStatus() < 400 && response.getObject() != null) {
                try {
                    List<Flight> flights = (List<Flight>) response.getObject();
                    if (flights != null && !flights.isEmpty()) {
                        for (Flight f : flights) {
                            if (f != null) {
                                model.addRow(new Object[]{
                                    f.getId(),
                                    (f.getDepartureDate() != null) ? f.getDepartureDate() : "",
                                    (f.calculateArrivalDate() != null) ? f.calculateArrivalDate() : ""
                                });
                            }
                        }
                    }
                } catch (ClassCastException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this,
                            "Error interno: Datos de 'Mis Vuelos' con formato inesperado.",
                            "Error de Datos", JOptionPane.ERROR_MESSAGE);
                }
            } else if (response.getStatus() >= 400) {
                JOptionPane.showMessageDialog(this,
                        response.getMessage(),
                        "Error al Cargar 'Mis Vuelos' (Status: " + response.getStatus() + ")",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void refreshAllFlightsTable(List<Flight> flighList) {
        if (allFlightsTable == null) {
            return;
        }
        DefaultTableModel model = (DefaultTableModel) allFlightsTable.getModel();
        model.setRowCount(0);
        if (flighList != null) {
            for (Flight f : flighList) {
                if (f != null) {
                    model.addRow(new Object[]{
                        f.getId(),
                        (f.getDepartureLocation() != null && f.getDepartureLocation().getId() != null) ? f.getDepartureLocation().getId() : "",
                        (f.getArrivalLocation() != null && f.getArrivalLocation().getId() != null) ? f.getArrivalLocation().getId() : "",
                        (f.getScaleLocation() != null && f.getScaleLocation().getId() != null) ? f.getScaleLocation().getId() : "",
                        (f.getDepartureDate() != null) ? f.getDepartureDate() : "",
                        (f.calculateArrivalDate() != null) ? f.calculateArrivalDate() : "",
                        (f.getPlane() != null && f.getPlane().getId() != null) ? f.getPlane().getId() : "",
                        (this.passengerRepository != null) ? this.passengerRepository.countByFlightId(f.getId()) : 0
                    });
                }
            }
        }
    }

    private void refreshPassengerUIComponents() {
        Response response = PassengerController.getSortedPassengers();

        List<Passenger> passengers = null;
        boolean success = false;
        if (response.getStatus() < 400 && response.getObject() != null) {
            try {
                passengers = (List<Passenger>) response.getObject();
                success = true;
            } catch (ClassCastException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Error interno: Datos de pasajeros con formato inesperado.",
                        "Error de Datos", JOptionPane.ERROR_MESSAGE);
                passengers = new ArrayList<>();
            }
        } else if (response.getStatus() >= 400) {
            JOptionPane.showMessageDialog(this,
                    response.getMessage(), "Error " + response.getStatus(),
                    JOptionPane.WARNING_MESSAGE);
            passengers = new ArrayList<>();
        } else {
            passengers = new ArrayList<>();
            success = true;
        }

        if (passengers == null) {
            passengers = new ArrayList<>();
        }

        populateUserSelectComboBox(passengers);
        refreshPassengerTable(passengers);
    }

    private void refreshLocationUIComponents() {
        Response response = LocationController.getSortedLocations();

        List<Location> locations = null;
        boolean success = false;
        if (response.getStatus() < 400 && response.getObject() != null) {
            try {
                locations = (List<Location>) response.getObject();
                success = true;
            } catch (ClassCastException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Error interno: Datos de localizaciones con formato inesperado.",
                        "Error de Datos", JOptionPane.ERROR_MESSAGE);
                locations = new ArrayList<>();
            }
        } else if (response.getStatus() >= 400) {
            JOptionPane.showMessageDialog(this,
                    response.getMessage(), "Error " + response.getStatus(),
                    JOptionPane.WARNING_MESSAGE);
            locations = new ArrayList<>();
        } else {
            locations = new ArrayList<>();
            success = true;
        }

        if (locations == null) {
            locations = new ArrayList<>();
        }

        populateLocationComboBox(departureLocationFlightComboBox, locations, "Location");
        populateLocationComboBox(arrivalLocationFlightComboBox, locations, "Location");
        populateLocationComboBox(scaleLocationFlightComboBox, locations, "Location");
        refreshLocationTable(locations);
    }

    private void refreshPlaneTable(List<Plane> planeList) {
        if (allPlanesTable == null) {
            return;
        }
        DefaultTableModel model = (DefaultTableModel) allPlanesTable.getModel();
        model.setRowCount(0);

        if (planeList != null) {
            for (Plane p : planeList) {
                if (p != null) {
                    model.addRow(new Object[]{
                        p.getId(),
                        p.getBrand(),
                        p.getModel(),
                        p.getCapacity(),
                        p.getAirline(),
                        (this.flightRepository != null) ? this.flightRepository.findByPlaneId(p.getId()).size() : 0
                    });
                }
            }
        }
    }

    private void populateLocationComboBox(
            JComboBox<String> comboBox,
            List<Location> locationList,
            String placeholder
    ) {
        if (comboBox == null) {
            return;
        }
        Object select = comboBox.getSelectedItem();
        String st = null;
        if (select != null) {
            st = select.toString();
        }

        comboBox.removeAllItems();
        comboBox.addItem(placeholder);

        if (locationList != null) {
            for (Location l : locationList) {
                if (l != null) {
                    comboBox.addItem("" + l.getId());
                }
            }
        }

        if (st != null) {
            comboBox.setSelectedItem(st);
            if (comboBox.getSelectedIndex() <= 0 && !st.equals(placeholder) && comboBox.getItemCount() > 1) {
                comboBox.setSelectedIndex(0);
            } else if (comboBox.getSelectedIndex() == -1 && comboBox.getItemCount() > 0) {
                comboBox.setSelectedIndex(0);
            }
        } else {
            if (comboBox.getItemCount() > 0) {
                comboBox.setSelectedIndex(0);
            }
        }
    }

    private void refreshLocationTable(List<Location> locationList) {
        if (allLocationsTable == null) {
            return;
        }
        DefaultTableModel model = (DefaultTableModel) allLocationsTable.getModel();
        model.setRowCount(0);

        if (locationList != null) {
            for (Location l : locationList) {
                if (l != null) {
                    model.addRow(new Object[]{
                        l.getAirportId(),
                        l.getAirportName(),
                        l.getAirportCity(),
                        l.getAirportCountry()
                    });
                }
            }
        }
    }

    private void refreshPassengerTable(List<Passenger> passengerList) {
        if (allPassengersTable == null) {
            return;
        }
        DefaultTableModel model = (DefaultTableModel) allPassengersTable.getModel();
        model.setRowCount(0);

        if (passengerList != null) {
            for (Passenger p : passengerList) {
                if (p != null) {
                    model.addRow(new Object[]{
                        p.getId(),
                        (this.passengerService != null) ? this.passengerService.getFullname(p.getFirstname(), p.getLastname()) : "",
                        (p.getBirthDate() != null) ? p.getBirthDate() : "",
                        (this.passengerService != null && p.getBirthDate() != null) ? this.passengerService.calculateAge(p.getBirthDate()) : 0,
                        (this.passengerService != null) ? this.passengerService.generateFullPhone(p.getCountryPhoneCode(), p.getPhone()) : "",
                        p.getCountry(),
                        (this.flightRepository != null) ? this.flightRepository.findByPassengerId(p.getId()).size() : 0
                    });
                }
            }
        }
    }

    private void populateUserSelectComboBox(List<Passenger> passengerList) {
        if (userSelect == null) {
            return;
        }
        Object select = userSelect.getSelectedItem();
        String st = null;
        if (select != null) {
            st = select.toString();
        }

        userSelect.removeAllItems();
        userSelect.addItem("Select User");

        if (passengerList != null) {
            for (Passenger p : passengerList) {
                if (p != null) {
                    userSelect.addItem("" + p.getId());
                }
            }
        }

        if (st != null) {
            userSelect.setSelectedItem(st);
            if (userSelect.getSelectedIndex() <= 0 && !st.equals("Select User") && userSelect.getItemCount() > 1) {
                userSelect.setSelectedIndex(0);
            } else if (userSelect.getSelectedIndex() == -1 && userSelect.getItemCount() > 0) {
                userSelect.setSelectedIndex(0);
            }
        } else {
            if (userSelect.getItemCount() > 0) {
                userSelect.setSelectedIndex(0);
            }
        }
    }

    private void populateAllPlaneComboBoxes(List<Plane> planeList) {
        if (planeFlightComboBox == null) {
            return;
        }
        Object select = planeFlightComboBox.getSelectedItem();
        String st = null;
        if (select != null) {
            st = select.toString();
        }

        planeFlightComboBox.removeAllItems();
        planeFlightComboBox.addItem("Plane");

        if (planeList != null) {
            for (Plane p : planeList) {
                if (p != null) {
                    planeFlightComboBox.addItem("" + p.getId());
                }
            }
        }

        if (st != null) {
            planeFlightComboBox.setSelectedItem(st);
            if (planeFlightComboBox.getSelectedIndex() <= 0 && !st.equals("Plane") && planeFlightComboBox.getItemCount() > 1) {
                planeFlightComboBox.setSelectedIndex(0);
            } else if (planeFlightComboBox.getSelectedIndex() == -1 && planeFlightComboBox.getItemCount() > 0) {
                planeFlightComboBox.setSelectedIndex(0);
            }
        } else {
            if (planeFlightComboBox.getItemCount() > 0) {
                planeFlightComboBox.setSelectedIndex(0);
            }
        }
    }

    private void refreshPlaneUIComponents() {
        Response response = PlaneController.getSortedPlanes();

        List<Plane> planes = null;
        boolean success = false;
        if (response.getStatus() < 400 && response.getObject() != null) {
            try {
                planes = (List<Plane>) response.getObject();
                success = true;
            } catch (ClassCastException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Error interno: Datos de aviones con formato inesperado.",
                        "Error de Datos", JOptionPane.ERROR_MESSAGE);
                planes = new ArrayList<>();
            }
        } else if (response.getStatus() >= 400) {
            JOptionPane.showMessageDialog(this,
                    response.getMessage(), "Error " + response.getStatus(),
                    JOptionPane.WARNING_MESSAGE);
            planes = new ArrayList<>();
        } else {
            planes = new ArrayList<>();
            success = true;
        }

        if (planes == null) {
            planes = new ArrayList<>();
        }

        populateAllPlaneComboBoxes(planes);
        refreshPlaneTable(planes);
    }

    private void blockPanels() {
        //9, 11
        for (int i = 1; i < TabbedPane.getTabCount(); i++) {
            if (i != 9 && i != 11) {
                TabbedPane.setEnabledAt(i, false);
            }
        }
    }

    private void generateMonths() {
        for (int i = 1; i < 13; i++) {
            monthPassengerComboxBox.addItem("" + i);
            departureMonthFlightComboBox.addItem("" + i);
            monthUpdateComboBox.addItem("" + i);
        }
    }

    private void generateDays() {
        for (int i = 1; i < 32; i++) {
            dayPassengerComboBox.addItem("" + i);
            departureDayFlightComboBox.addItem("" + i);
            dayUpdateComboBox.addItem("" + i);
        }
    }

    private void generateHours() {
        for (int i = 0; i < 24; i++) {
            departureHourFlightComboBox.addItem("" + i);
            arrivalHourFlightComboBOx.addItem("" + i);
            scaleHourFlightComboBox.addItem("" + i);
            hourDelayComboBox.addItem("" + i);
        }
    }

    private void generateMinutes() {
        for (int i = 0; i < 60; i++) {
            departureMinutesFlightComboBox.addItem("" + i);
            arrivalMinutesFlightComboBox.addItem("" + i);
            scaleMinutesFlightComboBox.addItem("" + i);
            minutesDelayComboBox.addItem("" + i);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelRound1 = new core.views.PanelRound();
        panelRound2 = new core.views.PanelRound();
        jButton13 = new javax.swing.JButton();
        TabbedPane = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        user = new javax.swing.JRadioButton();
        administrator = new javax.swing.JRadioButton();
        userSelect = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        phoneCodePassengerTextField = new javax.swing.JTextField();
        idPassengerTextField = new javax.swing.JTextField();
        yearPassengerTextField = new javax.swing.JTextField();
        countryPassengerTextField = new javax.swing.JTextField();
        phonePassengerTextField = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        lastnamePassengerTextField = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        monthPassengerComboxBox = new javax.swing.JComboBox<>();
        firstnamePassengerTextField = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        dayPassengerComboBox = new javax.swing.JComboBox<>();
        createPassengerButton = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        idPlaneTextField = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        brandPlaneTextField = new javax.swing.JTextField();
        modelPlaneTextField = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        maxCapacityPlaneTextField = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        airlinePlaneTextField = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        createPlaneButton = new javax.swing.JButton();
        jPanel13 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        idLocationTextField = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        nameLocationTextField = new javax.swing.JTextField();
        cityLocationTextField = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        countryLocationTextField = new javax.swing.JTextField();
        latitudeLocationTextField = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        longitudeLocationTextField = new javax.swing.JTextField();
        createLocationButton = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        idFlightTextField = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        planeFlightComboBox = new javax.swing.JComboBox<>();
        departureLocationFlightComboBox = new javax.swing.JComboBox<>();
        jLabel24 = new javax.swing.JLabel();
        arrivalLocationFlightComboBox = new javax.swing.JComboBox<>();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        scaleLocationFlightComboBox = new javax.swing.JComboBox<>();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        departureYearFlightTextField = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        departureMonthFlightComboBox = new javax.swing.JComboBox<>();
        jLabel31 = new javax.swing.JLabel();
        departureDayFlightComboBox = new javax.swing.JComboBox<>();
        jLabel32 = new javax.swing.JLabel();
        departureHourFlightComboBox = new javax.swing.JComboBox<>();
        jLabel33 = new javax.swing.JLabel();
        departureMinutesFlightComboBox = new javax.swing.JComboBox<>();
        arrivalHourFlightComboBOx = new javax.swing.JComboBox<>();
        jLabel34 = new javax.swing.JLabel();
        arrivalMinutesFlightComboBox = new javax.swing.JComboBox<>();
        jLabel35 = new javax.swing.JLabel();
        scaleHourFlightComboBox = new javax.swing.JComboBox<>();
        scaleMinutesFlightComboBox = new javax.swing.JComboBox<>();
        createFlightButton = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jLabel36 = new javax.swing.JLabel();
        idUpdateTextField = new javax.swing.JTextField();
        jLabel37 = new javax.swing.JLabel();
        firstnameUpdateTextField = new javax.swing.JTextField();
        jLabel38 = new javax.swing.JLabel();
        lastnameUpdateTextField = new javax.swing.JTextField();
        jLabel39 = new javax.swing.JLabel();
        yearUpdateTextField = new javax.swing.JTextField();
        monthUpdateComboBox = new javax.swing.JComboBox<>();
        dayUpdateComboBox = new javax.swing.JComboBox<>();
        phoneUpdateTextField = new javax.swing.JTextField();
        jLabel40 = new javax.swing.JLabel();
        phoneCodeUpdateTextField = new javax.swing.JTextField();
        jLabel41 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        countryUpdateTextField = new javax.swing.JTextField();
        updatePassengerButton = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        idPassengerAddTextField = new javax.swing.JTextField();
        jLabel44 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        flightAddComboBox = new javax.swing.JComboBox<>();
        addToFlightButton = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        myFlightsTable = new javax.swing.JTable();
        refreshMyFlightsButton = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        allPassengersTable = new javax.swing.JTable();
        refreshAllPassengersButton = new javax.swing.JButton();
        jPanel9 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        allFlightsTable = new javax.swing.JTable();
        refreshAllFlightsButton = new javax.swing.JButton();
        jPanel10 = new javax.swing.JPanel();
        refreshAllPlanesButton = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        allPlanesTable = new javax.swing.JTable();
        jPanel11 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        allLocationsTable = new javax.swing.JTable();
        refreshAllLocationsButton = new javax.swing.JButton();
        jPanel12 = new javax.swing.JPanel();
        hourDelayComboBox = new javax.swing.JComboBox<>();
        jLabel46 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        idDelayComboBox = new javax.swing.JComboBox<>();
        jLabel48 = new javax.swing.JLabel();
        minutesDelayComboBox = new javax.swing.JComboBox<>();
        delayButton = new javax.swing.JButton();
        panelRound3 = new core.views.PanelRound();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);

        panelRound1.setRadius(40);
        panelRound1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panelRound2.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                panelRound2MouseDragged(evt);
            }
        });
        panelRound2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                panelRound2MousePressed(evt);
            }
        });

        jButton13.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jButton13.setText("X");
        jButton13.setBorderPainted(false);
        jButton13.setContentAreaFilled(false);
        jButton13.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelRound2Layout = new javax.swing.GroupLayout(panelRound2);
        panelRound2.setLayout(panelRound2Layout);
        panelRound2Layout.setHorizontalGroup(
            panelRound2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelRound2Layout.createSequentialGroup()
                .addContainerGap(1083, Short.MAX_VALUE)
                .addComponent(jButton13, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17))
        );
        panelRound2Layout.setVerticalGroup(
            panelRound2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound2Layout.createSequentialGroup()
                .addComponent(jButton13)
                .addGap(0, 12, Short.MAX_VALUE))
        );

        panelRound1.add(panelRound2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1150, -1));

        TabbedPane.setFont(new java.awt.Font("Yu Gothic UI", 0, 14)); // NOI18N

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        user.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        user.setText("User");
        user.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                userActionPerformed(evt);
            }
        });
        jPanel1.add(user, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 230, -1, -1));

        administrator.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        administrator.setText("Administrator");
        administrator.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                administratorActionPerformed(evt);
            }
        });
        jPanel1.add(administrator, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 164, -1, -1));

        userSelect.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        userSelect.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select User" }));
        userSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                userSelectActionPerformed(evt);
            }
        });
        jPanel1.add(userSelect, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 300, 130, -1));

        TabbedPane.addTab("Administration", jPanel1);

        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel1.setText("Country:");
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 400, -1, -1));

        jLabel2.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel2.setText("ID:");
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 90, -1, -1));

        jLabel3.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel3.setText("First Name:");
        jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 160, -1, -1));

        jLabel4.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel4.setText("Last Name:");
        jPanel2.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 220, -1, -1));

        jLabel5.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel5.setText("Birthdate:");
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 280, -1, -1));

        jLabel6.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel6.setText("+");
        jPanel2.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 340, 20, -1));

        phoneCodePassengerTextField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jPanel2.add(phoneCodePassengerTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 340, 50, -1));

        idPassengerTextField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jPanel2.add(idPassengerTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 90, 130, -1));

        yearPassengerTextField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jPanel2.add(yearPassengerTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 280, 90, -1));

        countryPassengerTextField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        countryPassengerTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                countryPassengerTextFieldActionPerformed(evt);
            }
        });
        jPanel2.add(countryPassengerTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 400, 130, -1));

        phonePassengerTextField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jPanel2.add(phonePassengerTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 340, 130, -1));

        jLabel7.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel7.setText("Phone:");
        jPanel2.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 340, -1, -1));

        jLabel8.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel8.setText("-");
        jPanel2.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 280, 30, -1));

        lastnamePassengerTextField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jPanel2.add(lastnamePassengerTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 220, 130, -1));

        jLabel9.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel9.setText("-");
        jPanel2.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 340, 30, -1));

        monthPassengerComboxBox.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        monthPassengerComboxBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Month" }));
        jPanel2.add(monthPassengerComboxBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 280, -1, -1));

        firstnamePassengerTextField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jPanel2.add(firstnamePassengerTextField, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 160, 130, -1));

        jLabel10.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel10.setText("-");
        jPanel2.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 280, 30, -1));

        dayPassengerComboBox.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        dayPassengerComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Day" }));
        jPanel2.add(dayPassengerComboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 280, -1, -1));

        createPassengerButton.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        createPassengerButton.setText("Register");
        createPassengerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createPassengerButtonActionPerformed(evt);
            }
        });
        jPanel2.add(createPassengerButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 480, -1, -1));

        TabbedPane.addTab("Passenger registration", jPanel2);

        jPanel3.setLayout(null);

        jLabel11.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel11.setText("ID:");
        jPanel3.add(jLabel11);
        jLabel11.setBounds(53, 96, 22, 25);

        idPlaneTextField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jPanel3.add(idPlaneTextField);
        idPlaneTextField.setBounds(180, 93, 130, 31);

        jLabel12.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel12.setText("Brand:");
        jPanel3.add(jLabel12);
        jLabel12.setBounds(53, 157, 50, 25);

        brandPlaneTextField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jPanel3.add(brandPlaneTextField);
        brandPlaneTextField.setBounds(180, 154, 130, 31);

        modelPlaneTextField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jPanel3.add(modelPlaneTextField);
        modelPlaneTextField.setBounds(180, 213, 130, 31);

        jLabel13.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel13.setText("Model:");
        jPanel3.add(jLabel13);
        jLabel13.setBounds(53, 216, 55, 25);

        maxCapacityPlaneTextField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jPanel3.add(maxCapacityPlaneTextField);
        maxCapacityPlaneTextField.setBounds(180, 273, 130, 31);

        jLabel14.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel14.setText("Max Capacity:");
        jPanel3.add(jLabel14);
        jLabel14.setBounds(53, 276, 109, 25);

        airlinePlaneTextField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jPanel3.add(airlinePlaneTextField);
        airlinePlaneTextField.setBounds(180, 333, 130, 31);

        jLabel15.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel15.setText("Airline:");
        jPanel3.add(jLabel15);
        jLabel15.setBounds(53, 336, 70, 25);

        createPlaneButton.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        createPlaneButton.setText("Create");
        createPlaneButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createPlaneButtonActionPerformed(evt);
            }
        });
        jPanel3.add(createPlaneButton);
        createPlaneButton.setBounds(490, 480, 120, 40);

        TabbedPane.addTab("Airplane registration", jPanel3);

        jLabel16.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel16.setText("Airport ID:");

        idLocationTextField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        idLocationTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                idLocationTextFieldActionPerformed(evt);
            }
        });

        jLabel17.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel17.setText("Airport name:");

        nameLocationTextField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        cityLocationTextField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        jLabel18.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel18.setText("Airport city:");

        jLabel19.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel19.setText("Airport country:");

        countryLocationTextField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        latitudeLocationTextField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        jLabel20.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel20.setText("Airport latitude:");

        jLabel21.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel21.setText("Airport longitude:");

        longitudeLocationTextField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        createLocationButton.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        createLocationButton.setText("Create");
        createLocationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createLocationButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addGap(52, 52, 52)
                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel16)
                            .addComponent(jLabel17)
                            .addComponent(jLabel18)
                            .addComponent(jLabel19)
                            .addComponent(jLabel20)
                            .addComponent(jLabel21))
                        .addGap(80, 80, 80)
                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(longitudeLocationTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(idLocationTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(nameLocationTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cityLocationTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(countryLocationTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(latitudeLocationTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addGap(515, 515, 515)
                        .addComponent(createLocationButton, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(515, 515, 515))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGap(71, 71, 71)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addComponent(jLabel16)
                        .addGap(36, 36, 36)
                        .addComponent(jLabel17)
                        .addGap(34, 34, 34)
                        .addComponent(jLabel18)
                        .addGap(35, 35, 35)
                        .addComponent(jLabel19)
                        .addGap(35, 35, 35)
                        .addComponent(jLabel20))
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addComponent(idLocationTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30)
                        .addComponent(nameLocationTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28)
                        .addComponent(cityLocationTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(29, 29, 29)
                        .addComponent(countryLocationTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(29, 29, 29)
                        .addComponent(latitudeLocationTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(44, 44, 44)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(longitudeLocationTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 51, Short.MAX_VALUE)
                .addComponent(createLocationButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(47, 47, 47))
        );

        TabbedPane.addTab("Location registration", jPanel13);

        jLabel22.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel22.setText("ID:");

        idFlightTextField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        jLabel23.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel23.setText("Plane:");

        planeFlightComboBox.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        planeFlightComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Plane" }));
        planeFlightComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                planeFlightComboBoxActionPerformed(evt);
            }
        });

        departureLocationFlightComboBox.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        departureLocationFlightComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Location" }));
        departureLocationFlightComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                departureLocationFlightComboBoxActionPerformed(evt);
            }
        });

        jLabel24.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel24.setText("Departure location:");

        arrivalLocationFlightComboBox.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        arrivalLocationFlightComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Location" }));

        jLabel25.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel25.setText("Arrival location:");

        jLabel26.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel26.setText("Scale location:");

        scaleLocationFlightComboBox.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        scaleLocationFlightComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Location" }));

        jLabel27.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel27.setText("Duration:");

        jLabel28.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel28.setText("Duration:");

        jLabel29.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel29.setText("Departure date:");

        departureYearFlightTextField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        departureYearFlightTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                departureYearFlightTextFieldActionPerformed(evt);
            }
        });

        jLabel30.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel30.setText("-");

        departureMonthFlightComboBox.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        departureMonthFlightComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Month" }));
        departureMonthFlightComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                departureMonthFlightComboBoxActionPerformed(evt);
            }
        });

        jLabel31.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel31.setText("-");

        departureDayFlightComboBox.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        departureDayFlightComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Day" }));

        jLabel32.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel32.setText("-");

        departureHourFlightComboBox.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        departureHourFlightComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Hour" }));

        jLabel33.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel33.setText("-");

        departureMinutesFlightComboBox.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        departureMinutesFlightComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Minute" }));

        arrivalHourFlightComboBOx.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        arrivalHourFlightComboBOx.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Hour" }));

        jLabel34.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel34.setText("-");

        arrivalMinutesFlightComboBox.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        arrivalMinutesFlightComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Minute" }));

        jLabel35.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel35.setText("-");

        scaleHourFlightComboBox.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        scaleHourFlightComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Hour" }));

        scaleMinutesFlightComboBox.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        scaleMinutesFlightComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Minute" }));

        createFlightButton.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        createFlightButton.setText("Create");
        createFlightButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createFlightButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(73, 73, 73)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel26)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(scaleLocationFlightComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel25)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(arrivalLocationFlightComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel24)
                        .addGap(46, 46, 46)
                        .addComponent(departureLocationFlightComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel22)
                            .addComponent(jLabel23))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(idFlightTextField)
                            .addComponent(planeFlightComboBox, 0, 130, Short.MAX_VALUE))))
                .addGap(45, 45, 45)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel27)
                    .addComponent(jLabel28)
                    .addComponent(jLabel29))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(departureYearFlightTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(departureMonthFlightComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(14, 14, 14)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(departureDayFlightComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(departureHourFlightComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(14, 14, 14)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(departureMinutesFlightComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(30, 30, 30))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(arrivalHourFlightComboBOx, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(14, 14, 14)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addGap(20, 20, 20)
                                        .addComponent(arrivalMinutesFlightComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(scaleHourFlightComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(14, 14, 14)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel4Layout.createSequentialGroup()
                                        .addGap(20, 20, 20)
                                        .addComponent(scaleMinutesFlightComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(createFlightButton, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(530, 530, 530))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel22))
                    .addComponent(idFlightTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23)
                    .addComponent(planeFlightComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(departureHourFlightComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel32)
                    .addComponent(jLabel33)
                    .addComponent(departureMinutesFlightComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel24)
                                .addComponent(departureLocationFlightComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel29))
                            .addComponent(departureYearFlightTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(departureMonthFlightComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel30)
                            .addComponent(jLabel31)
                            .addComponent(departureDayFlightComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(38, 38, 38)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel25)
                                .addComponent(arrivalLocationFlightComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel28))
                            .addComponent(arrivalHourFlightComboBOx, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel34)
                            .addComponent(arrivalMinutesFlightComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(34, 34, 34)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(scaleHourFlightComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel35)
                            .addComponent(scaleMinutesFlightComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel26)
                                .addComponent(scaleLocationFlightComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel27)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 134, Short.MAX_VALUE)
                .addComponent(createFlightButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(50, 50, 50))
        );

        TabbedPane.addTab("Flight registration", jPanel4);

        jLabel36.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel36.setText("ID:");

        idUpdateTextField.setEditable(false);
        idUpdateTextField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        idUpdateTextField.setEnabled(false);
        idUpdateTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                idUpdateTextFieldActionPerformed(evt);
            }
        });

        jLabel37.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel37.setText("First Name:");

        firstnameUpdateTextField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        jLabel38.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel38.setText("Last Name:");

        lastnameUpdateTextField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        jLabel39.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel39.setText("Birthdate:");

        yearUpdateTextField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        monthUpdateComboBox.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        monthUpdateComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Month" }));

        dayUpdateComboBox.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        dayUpdateComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Day" }));

        phoneUpdateTextField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        jLabel40.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel40.setText("-");

        phoneCodeUpdateTextField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        jLabel41.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel41.setText("+");

        jLabel42.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel42.setText("Phone:");

        jLabel43.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel43.setText("Country:");

        countryUpdateTextField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N

        updatePassengerButton.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        updatePassengerButton.setText("Update");
        updatePassengerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updatePassengerButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(72, 72, 72)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel36)
                                .addGap(108, 108, 108)
                                .addComponent(idUpdateTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel37)
                                .addGap(41, 41, 41)
                                .addComponent(firstnameUpdateTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel38)
                                .addGap(43, 43, 43)
                                .addComponent(lastnameUpdateTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel39)
                                .addGap(55, 55, 55)
                                .addComponent(yearUpdateTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(30, 30, 30)
                                .addComponent(monthUpdateComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(34, 34, 34)
                                .addComponent(dayUpdateComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel42)
                                .addGap(56, 56, 56)
                                .addComponent(jLabel41, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addComponent(phoneCodeUpdateTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(20, 20, 20)
                                .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addComponent(phoneUpdateTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel43)
                                .addGap(63, 63, 63)
                                .addComponent(countryUpdateTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(507, 507, 507)
                        .addComponent(updatePassengerButton)))
                .addContainerGap(586, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(59, 59, 59)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel36)
                    .addComponent(idUpdateTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(39, 39, 39)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel37)
                    .addComponent(firstnameUpdateTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel38)
                    .addComponent(lastnameUpdateTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel39)
                    .addComponent(yearUpdateTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(monthUpdateComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dayUpdateComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel42)
                    .addComponent(jLabel41)
                    .addComponent(phoneCodeUpdateTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel40)
                    .addComponent(phoneUpdateTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel43)
                    .addComponent(countryUpdateTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                .addComponent(updatePassengerButton)
                .addGap(113, 113, 113))
        );

        TabbedPane.addTab("Update info", jPanel5);

        idPassengerAddTextField.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        idPassengerAddTextField.setEnabled(false);

        jLabel44.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel44.setText("ID:");

        jLabel45.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel45.setText("Flight:");

        flightAddComboBox.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        flightAddComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Flight" }));

        addToFlightButton.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        addToFlightButton.setText("Add");
        addToFlightButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addToFlightButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(64, 64, 64)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel44)
                    .addComponent(jLabel45))
                .addGap(79, 79, 79)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(flightAddComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(idPassengerAddTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(860, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(addToFlightButton, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(509, 509, 509))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jLabel44))
                    .addComponent(idPassengerAddTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(35, 35, 35)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel45)
                    .addComponent(flightAddComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 288, Short.MAX_VALUE)
                .addComponent(addToFlightButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(85, 85, 85))
        );

        TabbedPane.addTab("Add to flight", jPanel6);

        myFlightsTable.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        myFlightsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "ID", "Departure Date", "Arrival Date"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(myFlightsTable);

        refreshMyFlightsButton.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        refreshMyFlightsButton.setText("Refresh");
        refreshMyFlightsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshMyFlightsButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(269, 269, 269)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 590, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(322, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(refreshMyFlightsButton)
                .addGap(527, 527, 527))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(61, 61, 61)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                .addComponent(refreshMyFlightsButton)
                .addContainerGap())
        );

        TabbedPane.addTab("Show my flights", jPanel7);

        allPassengersTable.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        allPassengersTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Name", "Birthdate", "Age", "Phone", "Country", "Num Flight"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Long.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(allPassengersTable);

        refreshAllPassengersButton.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        refreshAllPassengersButton.setText("Refresh");
        refreshAllPassengersButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshAllPassengersButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(489, 489, 489)
                        .addComponent(refreshAllPassengersButton))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(47, 47, 47)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 1078, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(56, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap(72, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(refreshAllPassengersButton)
                .addContainerGap())
        );

        TabbedPane.addTab("Show all passengers", jPanel8);

        allFlightsTable.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        allFlightsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Departure Airport ID", "Arrival Airport ID", "Scale Airport ID", "Departure Date", "Arrival Date", "Plane ID", "Number Passengers"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(allFlightsTable);

        refreshAllFlightsButton.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        refreshAllFlightsButton.setText("Refresh");
        refreshAllFlightsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshAllFlightsButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 1100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(521, 521, 521)
                        .addComponent(refreshAllFlightsButton)))
                .addContainerGap(52, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(60, 60, 60)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(refreshAllFlightsButton)
                .addContainerGap(18, Short.MAX_VALUE))
        );

        TabbedPane.addTab("Show all flights", jPanel9);

        refreshAllPlanesButton.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        refreshAllPlanesButton.setText("Refresh");
        refreshAllPlanesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshAllPlanesButtonActionPerformed(evt);
            }
        });

        allPlanesTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Brand", "Model", "Max Capacity", "Airline", "Number Flights"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane4.setViewportView(allPlanesTable);

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(508, 508, 508)
                        .addComponent(refreshAllPlanesButton))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(145, 145, 145)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 816, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(220, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addContainerGap(45, Short.MAX_VALUE)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(34, 34, 34)
                .addComponent(refreshAllPlanesButton)
                .addGap(17, 17, 17))
        );

        TabbedPane.addTab("Show all planes", jPanel10);

        allLocationsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Airport ID", "Airport Name", "City", "Country"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane5.setViewportView(allLocationsTable);

        refreshAllLocationsButton.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        refreshAllLocationsButton.setText("Refresh");
        refreshAllLocationsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshAllLocationsButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGap(508, 508, 508)
                        .addComponent(refreshAllLocationsButton))
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGap(226, 226, 226)
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 652, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(303, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                .addContainerGap(48, Short.MAX_VALUE)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31)
                .addComponent(refreshAllLocationsButton)
                .addGap(17, 17, 17))
        );

        TabbedPane.addTab("Show all locations", jPanel11);

        hourDelayComboBox.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        hourDelayComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Hour" }));

        jLabel46.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel46.setText("Hours:");

        jLabel47.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel47.setText("ID:");

        idDelayComboBox.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        idDelayComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ID" }));

        jLabel48.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        jLabel48.setText("Minutes:");

        minutesDelayComboBox.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        minutesDelayComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Minute" }));

        delayButton.setFont(new java.awt.Font("Yu Gothic UI", 0, 18)); // NOI18N
        delayButton.setText("Delay");
        delayButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delayButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(94, 94, 94)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(jLabel48)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(minutesDelayComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel47)
                            .addComponent(jLabel46))
                        .addGap(79, 79, 79)
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(hourDelayComboBox, 0, 136, Short.MAX_VALUE)
                            .addComponent(idDelayComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(820, 820, 820))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(delayButton)
                .addGap(531, 531, 531))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel47)
                    .addComponent(idDelayComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel46)
                    .addComponent(hourDelayComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel48)
                    .addComponent(minutesDelayComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 307, Short.MAX_VALUE)
                .addComponent(delayButton)
                .addGap(33, 33, 33))
        );

        TabbedPane.addTab("Delay flight", jPanel12);

        panelRound1.add(TabbedPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 41, 1150, 620));

        javax.swing.GroupLayout panelRound3Layout = new javax.swing.GroupLayout(panelRound3);
        panelRound3.setLayout(panelRound3Layout);
        panelRound3Layout.setHorizontalGroup(
            panelRound3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1150, Short.MAX_VALUE)
        );
        panelRound3Layout.setVerticalGroup(
            panelRound3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 36, Short.MAX_VALUE)
        );

        panelRound1.add(panelRound3, new org.netbeans.lib.awtextra.AbsoluteConstraints(-2, 660, 1150, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelRound1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelRound1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    private void panelRound2MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelRound2MousePressed
        x = evt.getX();
        y = evt.getY();
    }//GEN-LAST:event_panelRound2MousePressed

    private void panelRound2MouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelRound2MouseDragged
        this.setLocation(this.getLocation().x + evt.getX() - x, this.getLocation().y + evt.getY() - y);
    }//GEN-LAST:event_panelRound2MouseDragged

    private void administratorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_administratorActionPerformed
        if (user.isSelected()) {
            user.setSelected(false);
            userSelect.setSelectedIndex(0);

        }
        for (int i = 1; i < TabbedPane.getTabCount(); i++) {
            TabbedPane.setEnabledAt(i, true);
        }
        TabbedPane.setEnabledAt(5, false);
        TabbedPane.setEnabledAt(6, false);
    }//GEN-LAST:event_administratorActionPerformed

    private void userActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_userActionPerformed
        if (administrator.isSelected()) {
            administrator.setSelected(false);
        }
        for (int i = 1; i < TabbedPane.getTabCount(); i++) {

            TabbedPane.setEnabledAt(i, false);

        }
        TabbedPane.setEnabledAt(9, true);
        TabbedPane.setEnabledAt(5, true);
        TabbedPane.setEnabledAt(6, true);
        TabbedPane.setEnabledAt(7, true);
        TabbedPane.setEnabledAt(11, true);
    }//GEN-LAST:event_userActionPerformed

    private void createPassengerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createPassengerButtonActionPerformed

        String id = idPassengerTextField.getText();
        String firsname = firstnamePassengerTextField.getText();
        String lastname = lastnamePassengerTextField.getText();
        String year = yearPassengerTextField.getText();
        String month = monthPassengerComboxBox.getItemAt(monthPassengerComboxBox.getSelectedIndex());
        String day = dayPassengerComboBox.getItemAt(dayPassengerComboBox.getSelectedIndex());
        String phoneCode = phoneCodePassengerTextField.getText();
        String phone = phonePassengerTextField.getText();
        String country = countryPassengerTextField.getText();

        Response response = PassengerController.createPassenger(
                id, firsname, lastname, year, month, day, phoneCode,
                phone, country);

        if (response.getStatus() >= 500) {
            JOptionPane.showMessageDialog(null, response.getMessage(), "Error " + response.getStatus(), JOptionPane.ERROR_MESSAGE);
        } else if (response.getStatus() >= 400) {
            JOptionPane.showMessageDialog(null, response.getMessage(), "Error " + response.getStatus(), JOptionPane.WARNING_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, response.getMessage(), "Response Message", JOptionPane.INFORMATION_MESSAGE);

            idPassengerTextField.setText("");
            firstnamePassengerTextField.setText("");
            lastnamePassengerTextField.setText("");
            yearPassengerTextField.setText("");
            if (monthPassengerComboxBox.getItemCount() > 0) {
                monthPassengerComboxBox.setSelectedIndex(0);
            }
            if (dayPassengerComboBox.getItemCount() > 0) {
                dayPassengerComboBox.setSelectedIndex(0);
            }
            phoneCodePassengerTextField.setText("");
            phonePassengerTextField.setText("");
            countryPassengerTextField.setText("");

            this.userSelect.addItem(id);
        }

    }//GEN-LAST:event_createPassengerButtonActionPerformed

    private void createPlaneButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createPlaneButtonActionPerformed

        String id = idPlaneTextField.getText();
        String brand = brandPlaneTextField.getText();
        String model = modelPlaneTextField.getText();
        String maxCapacity = maxCapacityPlaneTextField.getText();
        String airline = airlinePlaneTextField.getText();

        Response response = PlaneController.createPlane(
                id, brand, model, maxCapacity, airline
        );

        if (response.getStatus() >= 500) {
            JOptionPane.showMessageDialog(null, response.getMessage(), "Error " + response.getStatus(), JOptionPane.ERROR_MESSAGE);
        } else if (response.getStatus() >= 400) {
            JOptionPane.showMessageDialog(null, response.getMessage(), "Error " + response.getStatus(), JOptionPane.WARNING_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, response.getMessage(), "Response Message", JOptionPane.INFORMATION_MESSAGE);

            idPlaneTextField.setText("");
            brandPlaneTextField.setText("");
            modelPlaneTextField.setText("");
            maxCapacityPlaneTextField.setText("");
            airlinePlaneTextField.setText("");

            this.planeFlightComboBox.addItem(id);
        }

    }//GEN-LAST:event_createPlaneButtonActionPerformed

    private void createLocationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createLocationButtonActionPerformed

        String id = idLocationTextField.getText();
        String name = nameLocationTextField.getText();
        String city = cityLocationTextField.getText();
        String country = countryLocationTextField.getText();
        String latitude = latitudeLocationTextField.getText();
        String longitude = longitudeLocationTextField.getText();

        Response response = LocationController.createLocation(
                id, name, city, country, latitude, longitude
        );

        if (response.getStatus() >= 500) {
            JOptionPane.showMessageDialog(null, response.getMessage(), "Error " + response.getStatus(), JOptionPane.ERROR_MESSAGE);
        } else if (response.getStatus() >= 400) {
            JOptionPane.showMessageDialog(null, response.getMessage(), "Error " + response.getStatus(), JOptionPane.WARNING_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, response.getMessage(), "Response Message", JOptionPane.INFORMATION_MESSAGE);

            idLocationTextField.setText("");
            nameLocationTextField.setText("");
            cityLocationTextField.setText("");
            countryLocationTextField.setText("");
            latitudeLocationTextField.setText("");
            longitudeLocationTextField.setText("");
            /*
            this.departureLocationFlightComboBox.addItem(id);
            this.arrivalLocationFlightComboBox.addItem(id);
            this.scaleLocationFlightComboBox.addItem(id);
             */
            refreshLocationUIComponents();
        }

    }//GEN-LAST:event_createLocationButtonActionPerformed

    private void createFlightButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createFlightButtonActionPerformed

        String id = idFlightTextField.getText();
        String planeId = planeFlightComboBox.getItemAt(planeFlightComboBox.getSelectedIndex());
        String departureLocationId = departureLocationFlightComboBox.getItemAt(departureLocationFlightComboBox.getSelectedIndex());
        String arrivalLocationId = arrivalLocationFlightComboBox.getItemAt(arrivalLocationFlightComboBox.getSelectedIndex());
        String scaleLocationId = scaleLocationFlightComboBox.getItemAt(scaleLocationFlightComboBox.getSelectedIndex());
        String year = departureYearFlightTextField.getText();
        String month = departureMonthFlightComboBox.getItemAt(departureMonthFlightComboBox.getSelectedIndex());
        String day = departureDayFlightComboBox.getItemAt(departureDayFlightComboBox.getSelectedIndex());
        String hour = departureHourFlightComboBox.getItemAt(departureHourFlightComboBox.getSelectedIndex());
        String minutes = departureMinutesFlightComboBox.getItemAt(departureMinutesFlightComboBox.getSelectedIndex());
        String hoursDurationsArrival = arrivalHourFlightComboBOx.getItemAt(arrivalHourFlightComboBOx.getSelectedIndex());
        String minutesDurationsArrival = arrivalMinutesFlightComboBox.getItemAt(arrivalMinutesFlightComboBox.getSelectedIndex());
        String hoursDurationsScale = scaleHourFlightComboBox.getItemAt(scaleHourFlightComboBox.getSelectedIndex());
        String minutesDurationsScale = scaleMinutesFlightComboBox.getItemAt(scaleMinutesFlightComboBox.getSelectedIndex());

        Response response = FlightController.createFlight(
                id, planeId, departureLocationId, arrivalLocationId, scaleLocationId, year,
                month, day, hour, minutes, hoursDurationsArrival, minutesDurationsArrival,
                hoursDurationsScale, minutesDurationsScale
        );

        if (response.getStatus() >= 500) {
            JOptionPane.showMessageDialog(null, response.getMessage(), "Error " + response.getStatus(), JOptionPane.ERROR_MESSAGE);
        } else if (response.getStatus() >= 400) {
            JOptionPane.showMessageDialog(null, response.getMessage(), "Error " + response.getStatus(), JOptionPane.WARNING_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, response.getMessage(), "Response Message", JOptionPane.INFORMATION_MESSAGE);

            idFlightTextField.setText("");
            if (planeFlightComboBox.getItemCount() > 0) {
                planeFlightComboBox.setSelectedIndex(0);
            }
            if (departureLocationFlightComboBox.getItemCount() > 0) {
                departureLocationFlightComboBox.setSelectedIndex(0);
            }
            if (arrivalLocationFlightComboBox.getItemCount() > 0) {
                arrivalLocationFlightComboBox.setSelectedIndex(0);
            }
            if (scaleLocationFlightComboBox.getItemCount() > 0) {
                scaleLocationFlightComboBox.setSelectedIndex(0);
            }
            departureYearFlightTextField.setText("");
            if (departureMonthFlightComboBox.getItemCount() > 0) {
                departureMonthFlightComboBox.setSelectedIndex(0);
            }
            if (departureDayFlightComboBox.getItemCount() > 0) {
                departureDayFlightComboBox.setSelectedIndex(0);
            }
            if (departureHourFlightComboBox.getItemCount() > 0) {
                departureHourFlightComboBox.setSelectedIndex(0);
            }
            if (departureMinutesFlightComboBox.getItemCount() > 0) {
                departureMinutesFlightComboBox.setSelectedIndex(0);
            }
            if (arrivalHourFlightComboBOx.getItemCount() > 0) {
                arrivalHourFlightComboBOx.setSelectedIndex(0);
            }
            if (arrivalMinutesFlightComboBox.getItemCount() > 0) {
                arrivalMinutesFlightComboBox.setSelectedIndex(0);
            }
            if (scaleHourFlightComboBox.getItemCount() > 0) {
                scaleHourFlightComboBox.setSelectedIndex(0);
            }
            if (scaleMinutesFlightComboBox.getItemCount() > 0) {
                scaleMinutesFlightComboBox.setSelectedIndex(0);
            }

            this.flightAddComboBox.addItem(id);
        }

    }//GEN-LAST:event_createFlightButtonActionPerformed

    private void updatePassengerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updatePassengerButtonActionPerformed

        String id = idUpdateTextField.getText();
        String idUser = userSelect.getItemAt(userSelect.getSelectedIndex());
        String firstname = firstnameUpdateTextField.getText();
        String lastname = lastnameUpdateTextField.getText();
        String year = yearUpdateTextField.getText();
        String month = monthUpdateComboBox.getItemAt(monthUpdateComboBox.getSelectedIndex());
        String day = dayUpdateComboBox.getItemAt(dayUpdateComboBox.getSelectedIndex());
        String phoneCode = phoneCodeUpdateTextField.getText();
        String phone = phoneUpdateTextField.getText();
        String country = countryUpdateTextField.getText();

        Response response = PassengerController.updatePassenger(
                idUser, firstname, lastname, year, month, day, phoneCode, phone, country
        );

        if (response.getStatus() >= 500) {
            JOptionPane.showMessageDialog(null, response.getMessage(), "Error " + response.getStatus(), JOptionPane.ERROR_MESSAGE);
        } else if (response.getStatus() >= 400) {
            JOptionPane.showMessageDialog(null, response.getMessage(), "Error " + response.getStatus(), JOptionPane.WARNING_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, response.getMessage(), "Response Message", JOptionPane.INFORMATION_MESSAGE);

            idUpdateTextField.setText("");
            firstnameUpdateTextField.setText("");
            lastnameUpdateTextField.setText("");
            yearUpdateTextField.setText("");

            if (monthUpdateComboBox.getItemCount() > 0) {
                monthUpdateComboBox.setSelectedIndex(0);
            }
            if (dayUpdateComboBox.getItemCount() > 0) {
                dayUpdateComboBox.setSelectedIndex(0);
            }

            phoneCodeUpdateTextField.setText("");
            phoneUpdateTextField.setText("");
            countryUpdateTextField.setText("");
        }
    }//GEN-LAST:event_updatePassengerButtonActionPerformed

    private void addToFlightButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addToFlightButtonActionPerformed

        String passengerId = idPassengerAddTextField.getText();
        String flightId = flightAddComboBox.getItemAt(flightAddComboBox.getSelectedIndex());

        Response response = FlightController.addFlight(passengerId, flightId);

        if (response.getStatus() >= 500) {
            JOptionPane.showMessageDialog(null, response.getMessage(), "Error " + response.getStatus(), JOptionPane.ERROR_MESSAGE);
        } else if (response.getStatus() >= 400) {
            JOptionPane.showMessageDialog(null, response.getMessage(), "Error " + response.getStatus(), JOptionPane.WARNING_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, response.getMessage(), "Response Message", JOptionPane.INFORMATION_MESSAGE);

            idPassengerAddTextField.setText("");
            if (flightAddComboBox.getItemCount() > 0) {
                flightAddComboBox.setSelectedIndex(0);
            }
        }
    }//GEN-LAST:event_addToFlightButtonActionPerformed

    private void delayButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delayButtonActionPerformed

        String flightId = idDelayComboBox.getItemAt(idDelayComboBox.getSelectedIndex());
        String hours = hourDelayComboBox.getItemAt(hourDelayComboBox.getSelectedIndex());
        String minutes = minutesDelayComboBox.getItemAt(minutesDelayComboBox.getSelectedIndex());

        Response response = FlightController.delayFlight(flightId, hours, minutes);

        if (response.getStatus() >= 500) {
            JOptionPane.showMessageDialog(null, response.getMessage(), "Error " + response.getStatus(), JOptionPane.ERROR_MESSAGE);
        } else if (response.getStatus() >= 400) {
            JOptionPane.showMessageDialog(null, response.getMessage(), "Error " + response.getStatus(), JOptionPane.WARNING_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, response.getMessage(), "Response Message", JOptionPane.INFORMATION_MESSAGE);

            if (idDelayComboBox.getItemCount() > 0) {
                idDelayComboBox.setSelectedIndex(0);
            }
            if (hourDelayComboBox.getItemCount() > 0) {
                hourDelayComboBox.setSelectedIndex(0);
            }
            if (minutesDelayComboBox.getItemCount() > 0) {
                minutesDelayComboBox.setSelectedIndex(0);
            }
        }
    }//GEN-LAST:event_delayButtonActionPerformed

    private void refreshMyFlightsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshMyFlightsButtonActionPerformed

        String passengerId = userSelect.getItemAt(userSelect.getSelectedIndex());

        Response response = PassengerController.showMyFlights(passengerId);

        if (response.getStatus() >= 500) {
            JOptionPane.showMessageDialog(null, response.getMessage(), "Error " + response.getStatus(), JOptionPane.ERROR_MESSAGE);
        } else if (response.getStatus() >= 400) {
            JOptionPane.showMessageDialog(null, response.getMessage(), "Error " + response.getStatus(), JOptionPane.WARNING_MESSAGE);
        } else {

            ArrayList<Flight> flights = (ArrayList<Flight>) response.getObject();

            DefaultTableModel model = (DefaultTableModel) myFlightsTable.getModel();
            model.setRowCount(0);
            for (Flight flight : flights) {
                model.addRow(new Object[]{
                    flight.getId(),
                    flight.getDepartureDate(),
                    flight.calculateArrivalDate()
                });
            }
        }
    }//GEN-LAST:event_refreshMyFlightsButtonActionPerformed

    private void refreshAllPassengersButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshAllPassengersButtonActionPerformed

        Response response = PassengerController.getSortedPassengers();

        if (response.getStatus() >= 500) {
            JOptionPane.showMessageDialog(null, response.getMessage(), "Error " + response.getStatus(), JOptionPane.ERROR_MESSAGE);
        } else if (response.getStatus() >= 400) {
            JOptionPane.showMessageDialog(null, response.getMessage(), "Error " + response.getStatus(), JOptionPane.WARNING_MESSAGE);
        } else {
            ArrayList<Passenger> passengers = (ArrayList<Passenger>) response.getObject();

            DefaultTableModel model = (DefaultTableModel) allPassengersTable.getModel();
            model.setRowCount(0);
            for (Passenger p : passengers) {
                if (p != null) {
                    model.addRow(new Object[]{
                        p.getId(),
                        (this.passengerService != null) ? this.passengerService.getFullname(p.getFirstname(), p.getLastname()) : "",
                        (p.getBirthDate() != null) ? p.getBirthDate() : "",
                        (this.passengerService != null && p.getBirthDate() != null) ? this.passengerService.calculateAge(p.getBirthDate()) : 0,
                        (this.passengerService != null) ? this.passengerService.generateFullPhone(p.getCountryPhoneCode(), p.getPhone()) : "",
                        p.getCountry(),
                        (this.flightRepository != null) ? this.flightRepository.findByPassengerId(p.getId()).size() : 0
                    });
                }
            }
        }
    }//GEN-LAST:event_refreshAllPassengersButtonActionPerformed

    private void refreshAllFlightsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshAllFlightsButtonActionPerformed

        Response response = FlightController.getSortedFlights();

        if (response.getStatus() >= 500) {
            JOptionPane.showMessageDialog(null, response.getMessage(), "Error " + response.getStatus(), JOptionPane.ERROR_MESSAGE);
        } else if (response.getStatus() >= 400) {
            JOptionPane.showMessageDialog(null, response.getMessage(), "Error " + response.getStatus(), JOptionPane.WARNING_MESSAGE);
        } else {
            ArrayList<Flight> flights = (ArrayList<Flight>) response.getObject();

            refreshAllFlightsTable(flights);
        }
    }//GEN-LAST:event_refreshAllFlightsButtonActionPerformed

    private void refreshAllPlanesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshAllPlanesButtonActionPerformed

        Response response = PlaneController.getSortedPlanes();

        if (response.getStatus() >= 500) {
            JOptionPane.showMessageDialog(null, response.getMessage(), "Error " + response.getStatus(), JOptionPane.ERROR_MESSAGE);
        } else if (response.getStatus() >= 400) {
            JOptionPane.showMessageDialog(null, response.getMessage(), "Error " + response.getStatus(), JOptionPane.WARNING_MESSAGE);
        } else {
            ArrayList<Plane> planes = (ArrayList<Plane>) response.getObject();

            refreshPlaneTable(planes);
        }
    }//GEN-LAST:event_refreshAllPlanesButtonActionPerformed

    private void refreshAllLocationsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshAllLocationsButtonActionPerformed

        Response response = LocationController.getSortedLocations();

        if (response.getStatus() >= 500) {
            JOptionPane.showMessageDialog(null, response.getMessage(), "Error " + response.getStatus(), JOptionPane.ERROR_MESSAGE);
        } else if (response.getStatus() >= 400) {
            JOptionPane.showMessageDialog(null, response.getMessage(), "Error " + response.getStatus(), JOptionPane.WARNING_MESSAGE);
        } else {
            ArrayList<Location> locations = (ArrayList<Location>) response.getObject();
            refreshLocationTable(locations);
        }
    }//GEN-LAST:event_refreshAllLocationsButtonActionPerformed

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        System.exit(0);
    }//GEN-LAST:event_jButton13ActionPerformed

    private void userSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_userSelectActionPerformed
        try {
            String id = userSelect.getSelectedItem().toString();
            if (!id.equals(userSelect.getItemAt(0))) {
                idUpdateTextField.setText(id);
                idPassengerAddTextField.setText(id);
            } else {
                idUpdateTextField.setText("");
                idPassengerAddTextField.setText("");
            }
        } catch (Exception e) {
        }
    }//GEN-LAST:event_userSelectActionPerformed

    private void departureYearFlightTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_departureYearFlightTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_departureYearFlightTextFieldActionPerformed

    private void departureMonthFlightComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_departureMonthFlightComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_departureMonthFlightComboBoxActionPerformed

    private void idLocationTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_idLocationTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_idLocationTextFieldActionPerformed

    private void countryPassengerTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_countryPassengerTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_countryPassengerTextFieldActionPerformed

    private void planeFlightComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_planeFlightComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_planeFlightComboBoxActionPerformed

    private void idUpdateTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_idUpdateTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_idUpdateTextFieldActionPerformed

    private void departureLocationFlightComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_departureLocationFlightComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_departureLocationFlightComboBoxActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane TabbedPane;
    private javax.swing.JButton addToFlightButton;
    private javax.swing.JRadioButton administrator;
    private javax.swing.JTextField airlinePlaneTextField;
    private javax.swing.JTable allFlightsTable;
    private javax.swing.JTable allLocationsTable;
    private javax.swing.JTable allPassengersTable;
    private javax.swing.JTable allPlanesTable;
    private javax.swing.JComboBox<String> arrivalHourFlightComboBOx;
    private javax.swing.JComboBox<String> arrivalLocationFlightComboBox;
    private javax.swing.JComboBox<String> arrivalMinutesFlightComboBox;
    private javax.swing.JTextField brandPlaneTextField;
    private javax.swing.JTextField cityLocationTextField;
    private javax.swing.JTextField countryLocationTextField;
    private javax.swing.JTextField countryPassengerTextField;
    private javax.swing.JTextField countryUpdateTextField;
    private javax.swing.JButton createFlightButton;
    private javax.swing.JButton createLocationButton;
    private javax.swing.JButton createPassengerButton;
    private javax.swing.JButton createPlaneButton;
    private javax.swing.JComboBox<String> dayPassengerComboBox;
    private javax.swing.JComboBox<String> dayUpdateComboBox;
    private javax.swing.JButton delayButton;
    private javax.swing.JComboBox<String> departureDayFlightComboBox;
    private javax.swing.JComboBox<String> departureHourFlightComboBox;
    private javax.swing.JComboBox<String> departureLocationFlightComboBox;
    private javax.swing.JComboBox<String> departureMinutesFlightComboBox;
    private javax.swing.JComboBox<String> departureMonthFlightComboBox;
    private javax.swing.JTextField departureYearFlightTextField;
    private javax.swing.JTextField firstnamePassengerTextField;
    private javax.swing.JTextField firstnameUpdateTextField;
    private javax.swing.JComboBox<String> flightAddComboBox;
    private javax.swing.JComboBox<String> hourDelayComboBox;
    private javax.swing.JComboBox<String> idDelayComboBox;
    private javax.swing.JTextField idFlightTextField;
    private javax.swing.JTextField idLocationTextField;
    private javax.swing.JTextField idPassengerAddTextField;
    private javax.swing.JTextField idPassengerTextField;
    private javax.swing.JTextField idPlaneTextField;
    private javax.swing.JTextField idUpdateTextField;
    private javax.swing.JButton jButton13;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTextField lastnamePassengerTextField;
    private javax.swing.JTextField lastnameUpdateTextField;
    private javax.swing.JTextField latitudeLocationTextField;
    private javax.swing.JTextField longitudeLocationTextField;
    private javax.swing.JTextField maxCapacityPlaneTextField;
    private javax.swing.JComboBox<String> minutesDelayComboBox;
    private javax.swing.JTextField modelPlaneTextField;
    private javax.swing.JComboBox<String> monthPassengerComboxBox;
    private javax.swing.JComboBox<String> monthUpdateComboBox;
    private javax.swing.JTable myFlightsTable;
    private javax.swing.JTextField nameLocationTextField;
    private core.views.PanelRound panelRound1;
    private core.views.PanelRound panelRound2;
    private core.views.PanelRound panelRound3;
    private javax.swing.JTextField phoneCodePassengerTextField;
    private javax.swing.JTextField phoneCodeUpdateTextField;
    private javax.swing.JTextField phonePassengerTextField;
    private javax.swing.JTextField phoneUpdateTextField;
    private javax.swing.JComboBox<String> planeFlightComboBox;
    private javax.swing.JButton refreshAllFlightsButton;
    private javax.swing.JButton refreshAllLocationsButton;
    private javax.swing.JButton refreshAllPassengersButton;
    private javax.swing.JButton refreshAllPlanesButton;
    private javax.swing.JButton refreshMyFlightsButton;
    private javax.swing.JComboBox<String> scaleHourFlightComboBox;
    private javax.swing.JComboBox<String> scaleLocationFlightComboBox;
    private javax.swing.JComboBox<String> scaleMinutesFlightComboBox;
    private javax.swing.JButton updatePassengerButton;
    private javax.swing.JRadioButton user;
    private javax.swing.JComboBox<String> userSelect;
    private javax.swing.JTextField yearPassengerTextField;
    private javax.swing.JTextField yearUpdateTextField;
    // End of variables declaration//GEN-END:variables
}
