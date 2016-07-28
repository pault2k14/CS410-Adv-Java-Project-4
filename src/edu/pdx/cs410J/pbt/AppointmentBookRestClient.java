package edu.pdx.cs410J.pbt;

import edu.pdx.cs410J.web.HttpRequestHelper;
import java.io.IOException;
import java.net.URLEncoder;


/**
 * A helper class for accessing the rest client
 * Has the ability to search for appointments in a date range of
 * a specific owner, and  the ability to add a new appointment for
 * a specified owner.
 */
public class AppointmentBookRestClient extends HttpRequestHelper
{
    private static final String WEB_APP = "apptbook";
    private static final String SERVLET = "appointments";

    private String url;


    /**
     * Creates a client to the appointment book REST service running on the given host and port
     * @param hostName The name of the host
     * @param port The port
     */
    public AppointmentBookRestClient( String hostName, int port )
    {
        this.url = String.format( "http://%s:%d/%s/%s", hostName, port, WEB_APP, SERVLET );
    }

    /**
     * Adds a new appointment to the appointment book specified by the newOwner parameter.
     * @param newOwner - Owner of the appointment book to add the appointment to.
     * @param newDescription - Description of the new appointment.
     * @param newBeginTime - Beginning time of the new appointment.
     * @param newEndTime - Ending time of the new appointment.
     * @return - A server response object from the HTTP server that was contacted.
     * @throws IOException - Throws an exception when there was a problem contacting
     *                       the specified HTTP server.
     */
    public Response addAppointment( String newOwner, String newDescription, String newBeginTime, String newEndTime) throws IOException
    {
        // Build the parameter array.
        String[] params = new String[8];
        params[0] = "owner";
        params[1] = newOwner;
        params[2] = "description";
        params[3] = newDescription;
        params[4] = "beginTime";
        params[5] = newBeginTime;
        params[6] = "endTime";
        params[7] = newEndTime;

        // Encode the parameters of our POST URL.
        this.url += "?owner=" + URLEncoder.encode(newOwner, "UTF-8");

        return postToMyURL("key", "owner", "value", newOwner,
                "key", "description", "value", newDescription,
                "key", "beginTime", "value", newBeginTime,
                "key", "endTime", "value", newEndTime);
    }

    /**
     * Searchs for appointments belonging to the specified owner, between
     * the specified beginning and end dates.
     * @param newOwner - Owner of the appointment book to search.
     * @param newBeginTime - beginning time and date of the appointments to search.
     * @param newEndTime - ending time and date of the appointments to search.
     * @return - A response object from the HTTP server that was contacted.
     * @throws IOException - Throws an exception when there is a problem contacting
     *                       the HTTP server.
     */
    public Response searchAppointments( String newOwner, String newBeginTime, String newEndTime) throws IOException
    {

        // Build our array of parameters.
        String[] params = new String[6];
        params[0] = "owner";
        params[1] = newOwner;
        params[2] = "beginTime";
        params[3] = newBeginTime;
        params[4] = "endTime";
        params[5] = newEndTime;


        // Encode our URL as UTF-8.
        this.url += "?owner=" + URLEncoder.encode(newOwner, "UTF-8")
                 + "&beginTime=" + URLEncoder.encode(newBeginTime, "UTF-8")
                 + "&endTime=" + URLEncoder.encode(newEndTime, "UTF-8");

        return get(this.url);
    }

    /**
     * A convience method that sends a POST request along with it's
     * paramemters to a HTTP server.
     * @param keysAndValues - list of key value pairs that make up
     *                      a post requests payload paramemters.
     * @return - Returns a response object from the HTTP server.
     * @throws IOException - Throws an IOException if there is a problem
     *                       contact the server.
     */
    private Response postToMyURL(String... keysAndValues) throws IOException {
        return post(this.url, keysAndValues);
    }

}
