package edu.pdx.cs410J.pbt;

import edu.pdx.cs410J.AbstractAppointmentBook;
import edu.pdx.cs410J.web.HttpRequestHelper;
import edu.pdx.cs410J.pbt.AppointmentBookRestClient;

import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * The main class that parses the command line and communicates with the
 * Appointment Book server using REST.
 */
public class Project4 {

    public static void main(String... args) {

        Class c = AbstractAppointmentBook.class;  // Refer to one of Dave's classes so that we can be sure it is on the classpath

        String appointmentBookFileName = null;
        boolean searchPresent = false;
        String hostName = null;
        int port = 0;
        String key = null;
        String value = null;
        Date beginDate = null;
        Date endDate = null;
        int expectedArgs = 8;
        int firstAppointmentArg = 0;
        boolean printAppointment = false;
        String newOwner = null;
        String newDescription = null;
        String stringBeginDate = null;
        String stringEndDate = null;
        String stringOnlyBeginDate = null;
        String stringOnlyEndDate = null;
        String stringStartTime = null;
        String stringEndTime = null;
        String startAmPmModifier = null;
        String endAmPmModifier = null;

        // Hold the command line options that we expect to exist.
        ArrayList<String> expectedOptions = new ArrayList<>();
        expectedOptions.add("-README");
        expectedOptions.add("-print");
        expectedOptions.add("-host");
        expectedOptions.add("-port");
        expectedOptions.add("-search");

        // Look through the command line arugments to see if
        // -README, -print, -pretty, or -textFile options were provided by the user.
        for(int i = 0; i < args.length; ++i) {

            // If an unknown option is passed exit gracefully.
            if (args[i].startsWith("-") && !expectedOptions.contains(args[i])) {
                System.err.println("Bad command line option: " + args[i].toString());
                System.exit(0);
            }

            if(args[i].equals("-README")) {
                String readme = "Paul Thompson - CS 410J Project 1\n" +
                        "This project allows the user to create an appointment book and an appointment\n" +
                        "that will be placed into the appointment book. The user can optionally print out the newly created\n" +
                        "appoint. The program supports the following optional command line arguments\n" +
                        "and should be specified first if used:\n" +
                        "-host [hostname] The appointment book host to contact.\n" +
                        "-port [port]     The port on the appointment book host to contact\n" +
                        "-search [begin date] [begin time] [end date] [end time] " +
                        "               Searches for appoints between begin date and time" +
                        "               and end date and time.\n" +
                        "-print         Prints a description of the new appointment.\n" +
                        "-README        Prints a this README and exits.\n" +
                        "Then command line appointment arguments should be placed in this order\n" +
                        "owner          The person that owns the appointment book, should be opened and closed with double quotes.\n" +
                        "description    A description of the appointment, should be opened and closed with double quotes.\n" +
                        "begin date     When the appointment begins in the format of m(m)/d(d)/yyyy\n" +
                        "begin time     The time the appointment begins in the format of HH:mm\n" +
                        "end date       When the appointment ends in the format of m)m)/d(d)/yyyy\n" +
                        "end time       The time the appoineent ends in the format of HH:mm\n";

                System.out.println(readme);
                System.exit(1);
            }


            if(args[i].equals("-host")) {
                expectedArgs += 2;
                firstAppointmentArg += 2;

                if(i + 1 < args.length) {
                    hostName = args[i + 1];
                }
                else {
                    System.err.println("Host parameter used, but hostname not specified!");
                }

                if(!Arrays.asList(args).contains("-port")) {
                    System.err.println("host specified but port was not specified.");
                    System.exit(0);
                }
            }


            if(args[i].equals("-port")) {
                expectedArgs += 2;
                firstAppointmentArg += 2;

                if(i + 1 < args.length) {

                    try {
                        port = Integer.parseInt(args[i + 1]);

                    } catch (NumberFormatException ex) {
                        System.err.println("Port must be an integer!");
                    }
                }

                if(!Arrays.asList(args).contains("-host")) {
                    System.err.println("port specified but host was not specified.");
                    System.exit(0);
                }
            }

            if(args[i].equals("-search")) {

                // Take away description, but adding search.
                expectedArgs += 0;
                firstAppointmentArg += 1;
                searchPresent = true;

                if(!Arrays.asList(args).contains("-host")) {
                    System.err.println("search specified but host was not specified.");
                    System.exit(0);
                }

                if(!Arrays.asList(args).contains("-port")) {
                    System.err.println("search specified but port was not specified.");
                    System.exit(0);
                }
            }

            if(args[i].equals("-print")) {

                expectedArgs += 1;
                firstAppointmentArg += 1;
                printAppointment = true;
            }
        }

        // Check to make sure there aren't too few command
        // line arguments.
        if(args.length < expectedArgs) {

            System.err.println("Missing command line arguments");
            System.exit(0);
        }

        // Check to make sure there aren't too many command line arguments.
        if (args.length > expectedArgs) {

            System.err.println("Extraneous command line arguments");
            System.exit(0);
        }

        if(searchPresent) {
            newOwner = args[firstAppointmentArg + 0];
            newDescription = args[firstAppointmentArg + 1];
            stringBeginDate = args[firstAppointmentArg + 2] + " " + args[firstAppointmentArg + 3] + " " + args[firstAppointmentArg + 4];
            stringEndDate = args[firstAppointmentArg + 5] + " " + args[firstAppointmentArg + 6] + " " + args[firstAppointmentArg + 7];
            stringOnlyBeginDate = args[firstAppointmentArg + 2];
            stringOnlyEndDate = args[firstAppointmentArg + 5];
            stringStartTime = args[firstAppointmentArg + 3];
            stringEndTime = args[firstAppointmentArg + 6];
            startAmPmModifier = args[firstAppointmentArg + 4];
            endAmPmModifier = args[firstAppointmentArg + 7];
        }
        else {
            newOwner = args[firstAppointmentArg + 0];
            stringBeginDate = args[firstAppointmentArg + 1] + " " + args[firstAppointmentArg + 2] + " " + args[firstAppointmentArg + 3];
            stringEndDate = args[firstAppointmentArg + 4] + " " + args[firstAppointmentArg + 5] + " " + args[firstAppointmentArg + 6];
            stringOnlyBeginDate = args[firstAppointmentArg + 1];
            stringOnlyEndDate = args[firstAppointmentArg + 4];
            stringStartTime = args[firstAppointmentArg + 2];
            stringEndTime = args[firstAppointmentArg + 5];
            startAmPmModifier = args[firstAppointmentArg + 3];
            endAmPmModifier = args[firstAppointmentArg + 6];
        }

        // Check to make sure the owner field is not blank.
        if(newOwner.length() == 0) {
            System.err.println("Owner cannot be blank.");
            System.exit(0);
        }

        // Check to make sure the description field is not blank.
        if(!searchPresent && newDescription.length() == 0) {
            System.err.println("Description cannot be blank.");
            System.exit(0);
        }

        // Check to make sure the begin date format is correct.
        if(!stringOnlyBeginDate.matches("\\d\\d?/\\d\\d?/\\d\\d\\d\\d")) {
            System.err.println("Begin date is incorrectly formatted!");
            System.exit(0);
        }

        // Check to make sure the begin time format is correct.
        if(!stringStartTime.matches("\\d\\d?:\\d\\d")) {
            System.err.println("Begin time is incorrectly formatted!");
            System.exit(0);
        }

        // Check to make sure the begin time am/pm is correct.
        if(!startAmPmModifier.matches("(am|pm)")) {
            System.err.println("Begin time is incorrectly formatted!");
            System.exit(0);
        }

        // Check to make sure the end date format is correct.
        if(!stringOnlyEndDate.matches("\\d\\d?/\\d\\d?/\\d\\d\\d\\d")) {
            System.err.println("End date is incorrectly formatted!");
            System.exit(0);
        }

        // Check to make sure the end time format is correct.
        if(!stringEndTime.matches("\\d\\d?:\\d\\d")) {
            System.err.println("End time is incorrectly formatted!");
            System.exit(0);
        }

        // Check to make sure the end time am/pm is correct.
        if(!endAmPmModifier.matches("(am|pm)")) {
            System.err.println("End time is incorrectly formatted!");
            System.exit(0);
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
        dateFormat.setLenient(false);

        // Attempt to parse the begin date and time to ensure that they
        // are valid dates and times.
        try {
            beginDate = dateFormat.parse(stringBeginDate);
        }
        catch (ParseException e) {
            System.err.println("In project2 Begin date and time format is incorrect.");
            System.exit(0);
        }

        // Attempt to parse the begin date and time to ensure that they
        // are valid dates and times.
        try {
            endDate = dateFormat.parse(stringEndDate);
        }
        catch (ParseException e) {
            System.err.println("End date and time format is incorrect.");
            System.exit(0);
        }



        AppointmentBookRestClient client = new AppointmentBookRestClient(hostName, port);

        HttpRequestHelper.Response response = null;
        try {

            response = client.addAppointment(newOwner, newDescription, stringBeginDate, stringEndDate);
            /*
            if (key == null) {
                // Print all key/value pairs
                response = client.getAllKeysAndValues();

            } else if (value == null) {
                // Print all values of key
                response = client.getValues(key);

            } else {
                // Post the key/value pair
                response = client.addKeyValuePair(key, value);
            }
            */

            checkResponseCode( HttpURLConnection.HTTP_OK, response);

        } catch ( IOException ex ) {
            error("While contacting server: " + ex);
            return;
        }

        if(printAppointment) {
            // System.out.println(appointment.toString());
        }

        System.out.println(response.getContent());

        System.exit(0);
    }

    /**
     * Makes sure that the give response has the expected HTTP status code
     * @param code The expected status code
     * @param response The response from the server
     */
    private static void checkResponseCode( int code, HttpRequestHelper.Response response )
    {
        if (response.getCode() != code) {
            error(String.format("Expected HTTP code %d, got code %d.\n\n%s", code,
                                response.getCode(), response.getContent()));
        }
    }

    private static void error( String message )
    {
        PrintStream err = System.err;
        err.println("** " + message);

        System.exit(1);
    }

    /**
     * Prints usage information for this program and exits
     * @param message An error message to print
     */
    private static void usage( String message )
    {
        PrintStream err = System.err;
        err.println("** " + message);
        err.println();
        err.println("usage: java Project4 host port [key] [value]");
        err.println("  host    Host of web server");
        err.println("  port    Port of web server");
        err.println("  key     Key to query");
        err.println("  value   Value to add to server");
        err.println();
        err.println("This simple program posts key/value pairs to the server");
        err.println("If no value is specified, then all values are printed");
        err.println("If no key is specified, all key/value pairs are printed");
        err.println();

        System.exit(1);
    }
}