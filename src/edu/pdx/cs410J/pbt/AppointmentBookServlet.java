package edu.pdx.cs410J.pbt;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * This servlet ultimately provides a REST API for working with an
 * <code>AppointmentBook</code>. It has been implemented to support
 * GET requests and POST requests only. Specifically GET requests to
 * the search appointments and view all appointments URLs, and POST
 * requests to the add appointment URL.
 */
public class AppointmentBookServlet extends HttpServlet
{
    private final Map<String, String> data = new HashMap<>();
    private ArrayList<AppointmentBook> appointmentBooks = new ArrayList<>();


    /**
     * Handles an HTTP GET request from a client by either allowing the user
     * to view all appointments owned by a particular owner, or by searching
     * for appointments between a begin date and end date, and then returning
     * only matching appointments.
     * @param request - The request object from the client.
     * @param response - The response object returned to the client.
     * @throws ServletException - Thrown when the server runs into a problem.
     * @throws IOException - Thrown when there is a problem on disk or other IO.
     */
    @Override
    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
    {
        response.setContentType( "text/plain" );

        AppointmentBook currentAppointmentBook = null;
        String owner = request.getParameter("owner");
        String beginTime = null;
        String endTime = null;
        Date beginDate = null;
        Date endDate = null;
        StringBuffer requestURI = request.getRequestURL();
        String queryString = request.getQueryString();
        String fullUrl = requestURI + "?" + queryString;
        String decodedUrl = URLDecoder.decode(fullUrl);
        String decodedQueryString = decodedUrl.substring(decodedUrl.lastIndexOf("?") + 1);
        PrettyPrinter pretty = new PrettyPrinter("apptbook.txt");
        PrintWriter pw = response.getWriter();

        // Setup Regexes so we can parse the GET request.
        String dateFilter = "\\d\\d?/\\d\\d?/\\d\\d\\d\\d";
        String timeFilter = "\\d\\d?:\\d\\d";
        String amPmFilter = "(am|pm)";
        String entireDateFilter = dateFilter + " " + timeFilter + " " + amPmFilter;
        boolean viewAllAppointmentsUrlMatch = decodedQueryString.matches("^owner=(\\w|[+])+$");
        boolean searchAppointmentsUrlMatch = decodedQueryString.matches("^owner=(\\w|[+])+&beginTime=" + entireDateFilter + "&endTime=" + entireDateFilter);

        // Make sure owner is specified in the parameters.
        if(owner == null) {
            pw.println("The owner parameter must be specified.");
            pw.flush();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Let's try to find an appointment book that
        for(AppointmentBook apptBook : appointmentBooks) {

            if(apptBook.getOwnerName().equals(owner)) {
                currentAppointmentBook = apptBook;
            }
        }

        // No appointment book was found, nothing we can do
        // in a GET request.
        if(currentAppointmentBook == null) {
            pw.println("No appointment book found for " + owner);
            pw.flush();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // The user requested to view all appointments for an owner
        // fulfill the request.
        if(viewAllAppointmentsUrlMatch) {

            // Dump the appointment book we found to a file,
            // then read it back and send it back to the user
            // as text.
            pretty.dump(currentAppointmentBook);
            byte[] appointments = Files.readAllBytes(Paths.get("apptbook.txt"));
            String apptBook = new String(appointments);

            if (currentAppointmentBook.getAppointments().size() > 0){
                pw.println(apptBook);
            }
            else {
                pw.println("No appointments found.");
            }

            pw.flush();
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        // If the user came to the search appointments URL
        // fulfill their request.
        if(searchAppointmentsUrlMatch) {

            // Before we search for appoinments we need
            // to convert our date strings to dates.
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
            dateFormat.setLenient(false);

            beginTime = request.getParameter("beginTime");
            endTime = request.getParameter("endTime");

            // Attempt to parse the begin date and time to ensure that they
            // are valid dates and times.
            try {
                beginDate = dateFormat.parse(beginTime);
            }
            catch (ParseException e) {
                pw.println("Begin date and time format is incorrect.");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            // Attempt to parse the begin date and time to ensure that they
            // are valid dates and times.
            try {
                endDate = dateFormat.parse(endTime);
            }
            catch (ParseException e) {
                pw.println("End date and time format is incorrect.");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            // Make a temporary appointment book so we can
            // dump it using pretty printer.
            Collection<Appointment> appointments = currentAppointmentBook.getAppointments();
            AppointmentBook tempBook = new AppointmentBook(owner);

            for(Appointment appt: appointments) {

                if(appt.getBeginTime().compareTo(beginDate) == 1 || appt.getBeginTime().compareTo(beginDate) == 0 ) {

                    if(appt.getEndTime().compareTo(endDate) == -1 || appt.getEndTime().compareTo(endDate) == 0) {
                        tempBook.addAppointment(appt);
                    }
                }
            }

            pretty.dump(tempBook);
            byte[] appts = Files.readAllBytes(Paths.get("apptbook.txt"));
            String apptBook = new String(appts);

            if (tempBook.getAppointments().size() > 0){
                pw.println("The following appointments were found: ");
                pw.println(apptBook);
            }
            else {
                pw.println("No appointments found.");
            }

            pw.flush();
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        // Somehow we made it all of the way here, so
        // our resource was not found, let the user know.
        pw.println("Unable to find resource.");
        pw.flush();
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

    }

    /**
     * Handles an HTTP POST request by storing the appointment to the correct
     * appointment book, if there is no matching appointment book one is
     * created.
     * @param request - The request object from the client.
     * @param response - The response object returned to the client.
     * @throws ServletException - Thrown when the server runs into a problem.
     * @throws IOException - Thrown when there is a problem on disk or other IO.
     */
    @Override
    protected void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
    {
        response.setContentType( "text/plain" );

        AppointmentBook currentAppointmentBook = null;
        PrintWriter pw = response.getWriter();
        String queryString = request.getQueryString();
        boolean addAppointmentUrlMatch = queryString.matches("^owner=(\\w|[+])+$");

        // The user made it to the add appointment URL
        // fulfill the request and add the appointment.
        if(addAppointmentUrlMatch) {
            String[] keyParamValues = request.getParameterValues("key");
            String[] valueParamValues = request.getParameterValues("value");
            HashMap<String, String> paramMap = new HashMap<>();
            Appointment appointment = null;
            String owner = null;
            String description = null;
            String beginTime = null;
            String endTime = null;

            // Get all of our keys.
            if(keyParamValues == null) {
                missingRequiredParameter(response, "No key parameters found!");
                return;
            }

            // Get all of our key values.
            if(valueParamValues == null) {
                missingRequiredParameter(response, "No values for key parameters found!");
                return;
            }

            // Pair all of the keys and key values.
            if (keyParamValues.length == valueParamValues.length) {

                for (int i = 0; i < keyParamValues.length; ++i) {
                    paramMap.put(keyParamValues[i], valueParamValues[i]);
                }
            } else {
                missingRequiredParameter(response, "Query parameter mismatch!");
                return;
            }

            // Setup our appointment parameters.
            owner = paramMap.get("owner");
            description = paramMap.get("description");
            beginTime = paramMap.get("beginTime");
            endTime = paramMap.get("endTime");

            // Make sure owner isn't null, and that it isn't blank.
            if (owner == null || owner.length() == 0) {
                missingRequiredParameter(response, "owner");
                return;
            }

            // Make sure description isn't null, and that it isn't blank.
            if (description == null || description.length() == 0) {
                missingRequiredParameter(response, "description");
                return;
            }

            if (beginTime == null) {
                missingRequiredParameter(response, "beginTime");
                return;
            }

            if (endTime == null) {
                missingRequiredParameter(response, "endTime");
                return;
            }

            // Let's see if there is currently an appointment book
            // for the user.
            for(AppointmentBook apptBook : appointmentBooks) {

                if(apptBook.getOwnerName().equals(owner)) {
                    currentAppointmentBook = apptBook;
                }
            }

            // If there is no appointment book, create one.
            if (currentAppointmentBook == null) {
                currentAppointmentBook = new AppointmentBook(owner);
                appointmentBooks.add(currentAppointmentBook);
            }

            appointment = new Appointment(description, beginTime, endTime);

            currentAppointmentBook.addAppointment(appointment);

            pw.println(Messages.addedAppointment(owner));
            pw.flush();

            response.setStatus(HttpServletResponse.SC_OK);

        }

        // If the user's URL didn't match the add appointment
        // URL it wasn't a good request.
        else {
            pw.println(Messages.badPostUrl());
            pw.flush();

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

    }

    /**
     * Writes an error message about a missing parameter to the HTTP response.
     *
     * The text of the error message is created by {@link Messages#missingRequiredParameter(String)}
     */
    private void missingRequiredParameter( HttpServletResponse response, String parameterName )
        throws IOException
    {
        String message = Messages.missingRequiredParameter(parameterName);
        response.sendError(HttpServletResponse.SC_PRECONDITION_FAILED, message);
    }

}
