package edu.pdx.cs410J.pbt;

import edu.pdx.cs410J.AbstractAppointment;
import edu.pdx.cs410J.AbstractAppointmentBook;

import java.util.*;

/**
 * Te AppointmentBook class is an object that represents a real world appointment book
 * and as such holds appointments. It provides functionality to perform the following tasks:
 * Create a new appointment book, get the name of the owner of the appointment book,
 * get a listing of appointments in the appointment book, and add an appointment to the
 * appointment book.
 */
public class AppointmentBook extends AbstractAppointmentBook {

    private String owner;
    private ArrayList<Appointment> appointments;

    /**
     * Instantiates a new AppointmentBook object.
     * @param newOwner - The name of the owner of the newly created appointment book.
     */
    public AppointmentBook(String newOwner) {
        this.owner = newOwner;
        this.appointments = new ArrayList<>();
    }

    /**
     * Returns the name of owner of the appointment book.
     * @return - The owner object, the name of the owner of the appointment book as a String.
     */
    public String getOwnerName() {
        return this.owner;
    }

    /**
     * Returns a list of appointments in the appointment book.
     * @return - The appointments object, a list of appointments in the appointment
     * book as a collection.
     */
    public Collection getAppointments() {
        return this.appointments;
    }

    /**
     * Adds an appointment to the appointment book.
     * @param var1 - Any object extending AbstractAPpointment, this will be
     *             added to the appointment book.
     */
    public void addAppointment(AbstractAppointment var1) {
        this.appointments.add((Appointment) var1);
        Collections.sort(this.appointments);
    }
}
