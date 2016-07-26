package edu.pdx.cs410J.pbt;

import edu.pdx.cs410J.AbstractAppointment;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Appointment class allows for the creation of new appointments, comparison of appointments,
 * getting of formatted strings for beginTime and endTime, and the getting of the
 * beginTime, endTime, and description of an appointment object.
 */
public class Appointment extends AbstractAppointment implements Comparable<Appointment>{

    private Date beginTime;
    private Date endTime;
    private String description;

    /**
     * This constructor instantiates an appointment object.
     * @param newDescription - The description for the appointment as a string.
     * @param newBeginTime - The starting time for the appointment as a string
     *                     in the format of M(M)/d(d)/yyyy HH:mm
     * @param newEndTime - The ending time for the appointment as a string
     *                   in the format of M(M)/d(d)/yyyy HH:mm
     */
    public Appointment(String newDescription, String newBeginTime, String newEndTime) {


        SimpleDateFormat shortDateFormat = new SimpleDateFormat("M/d/yy h:mm a");
        shortDateFormat.setLenient(false);

        // Attempt to parse the begin date and time to ensure that they
        // are valid dates and times.
        try {
            this.beginTime = shortDateFormat.parse(newBeginTime);
        }
        catch (ParseException e) {
            System.err.println("Begin date and time format is incorrect.");
            System.exit(0);
        }

        // Attempt to parse the end date and time to ensure that they
        // are valid dates and times.
        try {
            this.endTime = shortDateFormat.parse(newEndTime);
        }
        catch (ParseException e) {
            System.err.println("End date and time format is incorrect.");
            System.exit(0);
        }

        this.description = newDescription;

    }

    /**
     * Compares whether the current Appointment is before, equal, or after
     * the passed in appointment.
     * @param appointment - The Appointment object to compare to the current
     *                      Appointment object.
     * @return Returns the equivalence between the two appointment objects
     *         -1 if this appointment occurs before the compared appointment,
     *         0 if this appointment is equal to the compared appointment,
     *         1 if this appointment occurs after the compared appointment.
     */
    public int compareTo(Appointment appointment) {

        final int BEFORE = -1;
        final int AFTER = 1;
        final int EQUAL = 0;

        // Check the relationship between beginTime for both
        // appointment objects.
        if(this.beginTime.before(appointment.beginTime)) {
            return BEFORE;
        }

        if(this.beginTime.after(appointment.beginTime)) {
            return AFTER;
        }

        // If beginTime is equal then we need to check
        // the relationship between the endTime for the
        // appointments.
        if(this.beginTime.equals(appointment.beginTime)) {

            if(this.endTime.before(appointment.endTime)) {
                return BEFORE;
            }

            if(this.endTime.after(appointment.endTime)) {
                return AFTER;
            }

            // If endTime is equal, ew now need to check
            // the relationship of the description between
            // the appointments.
            if(this.endTime.equals(appointment.endTime)) {

                if(this.description.compareTo(appointment.description) == -1) {
                    return BEFORE;
                }

                if(this.description.compareTo(appointment.description) == 1) {
                    return AFTER;
                }

                if(this.description.equals(appointment.description)) {
                    return EQUAL;
                }

            }
        }

        return EQUAL;
    }

    /**
     * Returns the current appointments beginTime Date object.
     * @return endTime - the current appointment's beginTime Date object.
     */
    @Override
    public Date getBeginTime() {
        return this.beginTime;
    }

    /**
     * Returns the current appointments endTime Date object.
     * @return endTime - the current appointment's endTime Date object.
     */
    @Override
    public Date getEndTime() {
        return this.endTime;
    }
    /**
     * Returns the a string representing the begin time of the appointment in the
     * form of M/d/yy h:mm a
     * @return - Returns a string representing the beginning time of an appointment.
     */
    @Override
    public String getBeginTimeString() {

        String twelveHourBeginTime = null;
        String twelveHourBeginDate = null;

        // Setup Date and time formatters.
        DateFormat shortDateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
        shortDateFormat.setLenient(false);

        DateFormat shortTimeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
        shortTimeFormat.setLenient(false);

        // Format the begin time to something a bit, prettier.
        twelveHourBeginDate = shortDateFormat.format(this.beginTime);
        twelveHourBeginTime = shortTimeFormat.format(this.beginTime);

        return twelveHourBeginDate + " " + twelveHourBeginTime;
    }

    /**
     * Returns the a string representing the end time of the appointment in the
     * form of M/d/yy h:mm a
     * @return - Returns a string representing the ending time of an appointment.
     */
    @Override
    public String getEndTimeString() {

        String twelveHourEndTime = null;
        String twelveHourEndDate = null;

        // Setup Date and time formatters.
        DateFormat shortDateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
        shortDateFormat.setLenient(false);

        DateFormat shortTimeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
        shortTimeFormat.setLenient(false);

        // Format the end time to something a bit, prettier.
        twelveHourEndDate = shortDateFormat.format(this.endTime);
        twelveHourEndTime = shortTimeFormat.format(this.endTime);

        return twelveHourEndDate + " " + twelveHourEndTime;
    }

    /**
     * Returns the description field of the appointment class object.
     * @return - Returns a string representing the description of an appointment.
     */
    @Override
    public String getDescription() {
        return this.description;
    }
}
