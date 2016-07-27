package edu.pdx.cs410J.pbt;

// import com.google.common.annotations.VisibleForTesting;
import edu.pdx.cs410J.web.HttpRequestHelper;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.net.URLConnection;

/**
 * A helper class for accessing the rest client
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
     * Returns all keys and values from the server
     */
    public Response getAllKeysAndValues() throws IOException
    {
        return get(this.url );
    }

    /**
     * Returns all values for the given key
     */
    public Response getValues( String key ) throws IOException
    {
        return get(this.url, "key", key);
    }

    public Response addAppointment( String newOwner, String newDescription, String newBeginTime, String newEndTime) throws IOException
    {



        String[] params = new String[8];
        params[0] = "owner";
        params[1] = newOwner;
        params[2] = "description";
        params[3] = newDescription;
        params[4] = "beginTime";
        params[5] = newBeginTime;
        params[6] = "endTime";
        params[7] = newEndTime;

        this.url += "?owner=" + URLEncoder.encode(newOwner, "UTF-8");
        return postToMyURL("key", "owner", "value", newOwner,
                "key", "description", "value", newDescription,
                "key", "beginTime", "value", newBeginTime,
                "key", "endTime", "value", newEndTime);
    }

    public Response addKeyValuePair( String key, String value ) throws IOException
    {
        return postToMyURL("key", key, "value", value);
    }

    // @VisibleForTesting
    Response postToMyURL(String... keysAndValues) throws IOException {
        return post(this.url, keysAndValues);
    }

    public Response removeAllMappings() throws IOException {
        return post(this.url);
        // return delete(this.url);
    }
}
