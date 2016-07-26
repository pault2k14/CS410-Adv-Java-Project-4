package edu.pdx.cs410J.pbt;

import edu.pdx.cs410J.AbstractAppointmentBook;
import edu.pdx.cs410J.AppointmentBookDumper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * PrettyPrinter class takes an AbstractAppointmentBook and then performs a formatted printing
 * of it to either standard out or a specified file.
 */
public class PrettyPrinter implements AppointmentBookDumper {

    private String fileName = null;

    /**
     * Constructor for the PrettyPrint class,
     * @param newFileName - String that represents the file that will have the pretty printed AbstractAppointmentBook
     *                      to it.
     */
    public PrettyPrinter(String newFileName) {
        this.fileName = newFileName;
    }


    /**
     * Prints the passed in AbstractAppointmentBook to a text file or to the standard output.
     * @param var1 - The AbstractAppointmentBook to be printed.
     * @throws IOException - Thrown when the specified filename written to.
     */
    public void dump(AbstractAppointmentBook var1) throws IOException {

        long msDuration;
        long minsDuration;
        File dir = new File(".");
        File dumpFile = new File(dir.getCanonicalPath() + File.separator + this.fileName);
        AppointmentBook appointmentBook = (AppointmentBook) var1;
        Collection<Appointment> appointments = appointmentBook.getAppointments();

        // If the filename is - send our output to the standard output instead.
        if(!this.fileName.equals("-")) {
            PrintStream fileOut = new PrintStream(new FileOutputStream(this.fileName));
            System.setOut(fileOut);
        }

        System.out.println( appointmentBook.getOwnerName() + "'s" + " appointment book!");
        System.out.println();

        // print each appointment.
        for(Appointment appt: appointments) {

            msDuration = appt.getEndTime().getTime() - appt.getBeginTime().getTime();
            minsDuration = TimeUnit.MINUTES.convert(msDuration, TimeUnit.MILLISECONDS);

            System.out.println("Description: " + appt.getDescription());
            System.out.println("Duration: " + minsDuration + " minutes.");
            System.out.println("Begin Time: " + appt.getBeginTimeString());
            System.out.println("End Time: " + appt.getEndTimeString());
            System.out.println();
        }
    }
}
