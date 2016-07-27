package edu.pdx.cs410J.pbt;

// import com.google.common.annotations.VisibleForTesting;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * This servlet ultimately provides a REST API for working with an
 * <code>AppointmentBook</code>.  However, in its current state, it is an example
 * of how to use HTTP and Java servlets to store simple key/value pairs.
 */
public class AppointmentBookServlet extends HttpServlet
{
    private final Map<String, String> data = new HashMap<>();
    private AppointmentBook appointmentBook = null;

    /**
     * Handles an HTTP GET request from a client by writing the value of the key
     * specified in the "key" HTTP parameter to the HTTP response.  If the "key"
     * parameter is not specified, all of the key/value pairs are written to the
     * HTTP response.
     */
    @Override
    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
    {
        response.setContentType( "text/plain" );

        PrintWriter pw = response.getWriter();
        String queryString = request.getQueryString();
        boolean viewAllAppointmentsUrlMatch = queryString.matches("^owner=(\\w|[+])+$");
        boolean searchAppointmentsUrlMatch = queryString.matches("^owner=(\\w|[+])+&beginTime=(\\w|[+])&endTime=(\\w|[+])");

        if(viewAllAppointmentsUrlMatch) {
            pw.println("Made it to View All Appointments URL!");
            pw.flush();

            response.setStatus(HttpServletResponse.SC_OK);
        }
        else if(searchAppointmentsUrlMatch) {
            pw.println("Made it to Search All Appointments URL!");
            pw.flush();

            response.setStatus(HttpServletResponse.SC_OK);
        }
        else {
            pw.println("Bad GET URL!");
            pw.flush();

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }





    }

    /**
     * Handles an HTTP POST request by storing the key/value pair specified by the
     * "key" and "value" request parameters.  It writes the key/value pair to the
     * HTTP response.
     */
    @Override
    protected void doPost( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
    {
        response.setContentType( "text/plain" );

        PrintWriter pw = response.getWriter();
        String queryString = request.getQueryString();
        boolean addAppointmentUrlMatch = queryString.matches("^owner=(\\w|[+])+$");

        if(addAppointmentUrlMatch) {
            String[] keyParamValues = request.getParameterValues("key");
            String[] valueParamValues = request.getParameterValues("value");
            HashMap<String, String> paramMap = new HashMap<>();
            Appointment appointment = null;
            String owner = null;
            String description = null;
            String beginTime = null;
            String endTime = null;

            if(keyParamValues == null) {
                missingRequiredParameter(response, "No key parameters found!");
                return;
            }

            if(valueParamValues == null) {
                missingRequiredParameter(response, "No values for key parameters found!");
                return;
            }

            if (keyParamValues.length == valueParamValues.length) {

                for (int i = 0; i < keyParamValues.length; ++i) {
                    paramMap.put(keyParamValues[i], valueParamValues[i]);
                }
            } else {
                missingRequiredParameter(response, "Query parameter mismatch!");
                return;
            }

            owner = paramMap.get("owner");
            description = paramMap.get("description");
            beginTime = paramMap.get("beginTime");
            endTime = paramMap.get("endTime");

            if (owner == null || owner.length() == 0) {
                missingRequiredParameter(response, "owner");
                return;
            }

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

            if (appointmentBook == null) {
                appointmentBook = new AppointmentBook(owner);
            }

            appointment = new Appointment(description, beginTime, endTime);

            appointmentBook.addAppointment(appointment);

            pw.println(Messages.addedAppointment(owner));
            pw.flush();

            response.setStatus(HttpServletResponse.SC_OK);

        }

        else {
            pw.println(Messages.badPostUrl());
            pw.flush();

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

    }

    /**
     * Handles an HTTP DELETE request by removing all key/value pairs.  This
     * behavior is exposed for testing purposes only.  It's probably not
     * something that you'd want a real application to expose.
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");

        this.data.clear();

        PrintWriter pw = response.getWriter();
        pw.println(Messages.allMappingsDeleted());
        pw.flush();

        response.setStatus(HttpServletResponse.SC_OK);

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

    /**
     * Writes the value of the given key to the HTTP response.
     *
     * The text of the message is formatted with {@link Messages#getMappingCount(int)}
     * and {@link Messages#formatKeyValuePair(String, String)}
     */
    private void writeValue( String key, HttpServletResponse response ) throws IOException
    {
        String value = this.data.get(key);

        PrintWriter pw = response.getWriter();
        pw.println(Messages.getMappingCount( value != null ? 1 : 0 ));
        pw.println(Messages.formatKeyValuePair(key, value));

        pw.flush();

        response.setStatus( HttpServletResponse.SC_OK );
    }

    /**
     * Writes all of the key/value pairs to the HTTP response.
     *
     * The text of the message is formatted with
     * {@link Messages#formatKeyValuePair(String, String)}
     */
    private void writeAllMappings( HttpServletResponse response ) throws IOException
    {
        PrintWriter pw = response.getWriter();
        pw.println(Messages.getMappingCount(data.size()));

        for (Map.Entry<String, String> entry : this.data.entrySet()) {
            pw.println(Messages.formatKeyValuePair(entry.getKey(), entry.getValue()));
        }

        pw.flush();

        response.setStatus( HttpServletResponse.SC_OK );
    }

    /**
     * Returns the value of the HTTP request parameter with the given name.
     *
     * @return <code>null</code> if the value of the parameter is
     *         <code>null</code> or is the empty string
     */
    private String getParameter(String name, HttpServletRequest request) {
      String value = request.getParameter(name);
      if (value == null || "".equals(value)) {
        return null;

      } else {
        return value;
      }
    }

    // @VisibleForTesting
    void setValueForKey(String key, String value) {
        this.data.put(key, value);
    }

    // @VisibleForTesting
    String getValueForKey(String key) {
        return this.data.get(key);
    }
}
