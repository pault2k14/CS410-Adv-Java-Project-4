package edu.pdx.cs410J.pbt;

import edu.pdx.cs410J.AbstractAppointmentBook;
import edu.pdx.cs410J.ParserException;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The main class for the CS410J appointment book Project
 * Performs parsing and error checking of the user inputted command line
 * options and arguments, upon successful parsing, it will optionally read in
 * an appoinment book from a file, if the -textFile option is specified and the file exists,
 * if it does not exist, the file will be created. If the file existed and was a valid
 * appointment book the program will add the appointment specified on the command line to the
 * appointment book, and save it back to the appointment book file. If the appointment book
 * file was newly created, the program will instead create a new appointment book and enter the
 * user's appointment specified from the command line into it, and save the appointment book to
 * the new file. It will optionally print the appointment for the user if they use the -print
 * command line option or display a read me to the user if they use the -README option. If the user
 * uses the -pretty file option, the appointment book will be formatted in a pretty way and either
 * printed to standard out or to a file.
 *
 */
public class Project3 {

    /**
     * Parses command line arguments and options, as well as specifying
     * when a appointment book should be read from or written to, specifies when a
     * pretty printed appointment book should be displayed or saved to file, and
     * builds appointment book and appointment objects.
     *
     * @param args - array of strings containing the arguments for
     *             creating an appointment, and program options.
     */
  public static void main(String[] args) {

      Class c = AbstractAppointmentBook.class;  // Refer to one of Dave's classes so that we can be sure it is on the classpath

      AppointmentBook appointmentBook = null;
      Appointment appointment = null;
      TextDumper dumper = null;
      PrettyPrinter printer = null;
      TextParser parser = null;
      String appointmentBookFileName = null;
      String prettyFileName = null;
      Date beginDate = null;
      Date endDate = null;
      int expectedArgs = 8;
      int firstAppointmentArg = 0;
      boolean printAppointment = false;
      boolean prettyPrintAppointmentBook = false;

      // Hold the command line options that we expect to exist.
      ArrayList<String> expectedOptions = new ArrayList<>();
      expectedOptions.add("-README");
      expectedOptions.add("-print");
      expectedOptions.add("-textFile");
      expectedOptions.add("-pretty");
      expectedOptions.add("-");

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
              "-pretty file   Where to write the appointment book to in a pretty printed manner, - can be specified\n" +
              "               as the file name to pretty print to the screen only.\n" +
              "-textFile file Where to read/write the appointment book.\n" +
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

          // If a -pretty print file is desired by the
          // user adjust the expected arguments and then
          // locate the file name for the pretty print file.
          if(args[i].equals("-pretty")) {
              expectedArgs += 2;
              firstAppointmentArg += 2;
              prettyPrintAppointmentBook = true;

              if(i + 1 < args.length) {
                  prettyFileName = args[i + 1];
              }

          }

          // Check if a file was specified for the appointment book
          // if it was save the file name.
          if(args[i].equals("-textFile")) {
              expectedArgs += 2;
              firstAppointmentArg += 2;

              if(i + 1 < args.length) {
                  appointmentBookFileName = args[i + 1];
              }
          }

          if(args[i].equals("-print")) {

              expectedArgs += 1;
              printAppointment = true;
              firstAppointmentArg += 1;
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

      // If a text file was specified, attempt
      // to parse the appointments in that file.
      if(appointmentBookFileName != null) {

          parser = new TextParser(appointmentBookFileName);

          try {
              appointmentBook = (AppointmentBook) parser.parse();
          }
          catch(ParserException e) {
              System.err.println("Unable to read appointment book text file!");
              System.exit(0);
          }
      }

      String newOwner = args[firstAppointmentArg + 0];
      String newDescription = args[firstAppointmentArg + 1];
      String stringBeginDate = args[firstAppointmentArg + 2] + " " + args[firstAppointmentArg + 3] + " " + args[firstAppointmentArg + 4];
      String stringEndDate = args[firstAppointmentArg + 5] + " " + args[firstAppointmentArg + 6] + " " + args[firstAppointmentArg + 7];

      // Check to make sure the owner field is not blank.
      if(newOwner.length() == 0) {
          System.err.println("Owner cannot be blank.");
          System.exit(0);
      }

      // Check to make sure the description field is not blank.
      if(newDescription.length() == 0) {
          System.err.println("Description cannot be blank.");
          System.exit(0);
      }

      // Check to make sure the begin date format is correct.
      if(!args[firstAppointmentArg + 2].matches("\\d\\d?/\\d\\d?/\\d\\d\\d\\d")) {
          System.err.println("Begin date is incorrectly formatted!");
          System.exit(0);
      }

      // Check to make sure the begin time format is correct.
      if(!args[firstAppointmentArg + 3].matches("\\d\\d?:\\d\\d")) {
          System.err.println("Begin time is incorrectly formatted!");
          System.exit(0);
      }

      // Check to make sure the begin time am/pm is correct.
      if(!args[firstAppointmentArg + 4].matches("(am|pm)")) {
          System.err.println("Begin time is incorrectly formatted!");
          System.exit(0);
      }

      // Check to make sure the end date format is correct.
      if(!args[firstAppointmentArg + 5].matches("\\d\\d?/\\d\\d?/\\d\\d\\d\\d")) {
          System.err.println("End date is incorrectly formatted!");
          System.exit(0);
      }

      // Check to make sure the end time format is correct.
      if(!args[firstAppointmentArg + 6].matches("\\d\\d?:\\d\\d")) {
          System.err.println("End time is incorrectly formatted!");
          System.exit(0);
      }

      // Check to make sure the end time am/pm is correct.
      if(!args[firstAppointmentArg + 7].matches("(am|pm)")) {
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

      // Magic value telling us that this is a newly created appointment book.
      if(appointmentBook != null && appointmentBook.getOwnerName().equals("newbook")) {
          appointmentBook = new AppointmentBook(newOwner);
      }

      // Check if the owner of the specified appointment book
      // file is the same as the owner of the appointment specified
      // on the command line.
      if(appointmentBook != null && !appointmentBook.getOwnerName().equals(newOwner)) {
          System.err.println("Specified owner name and appointment book owner name do not match!");
          System.exit(0);
      }

      // Create a new appointment book and appointment, and add
      // that appointment to the appointment book.
      if(appointmentBook == null) {
          appointmentBook = new AppointmentBook(newOwner);
      }

      appointment = new Appointment(newDescription, stringBeginDate, stringEndDate);
      appointmentBook.addAppointment(appointment);

      if(printAppointment) {
          System.out.println(appointment.toString());
      }

      // If a pretty print file is specified on the command line
      // create a pretty print to standard out or file.
      if(prettyPrintAppointmentBook) {

          printer = new PrettyPrinter(prettyFileName);

          try {
              printer.dump(appointmentBook);
          }
          catch(IOException e) {
              System.err.println("Unable to write to appointment book pretty print file!");
              System.exit(0);
          }
      }

      // if a text file was specified, save the appointment book
      // to that file.
      if(appointmentBookFileName != null) {

          dumper = new TextDumper(appointmentBookFileName);

          try {
              dumper.dump(appointmentBook);
          }
          catch(IOException e) {
              System.err.println("Unable to write to appointment book text file!");
              System.exit(0);
          }
      }

      System.exit(1);
  }

}