package edu.pdx.cs410J.pbt;

/**
 * Class for formatting messages on the server side.  This is mainly to enable
 * test methods that validate that the server returned expected strings.
 */
public class Messages
{

    /**
     * Sends a message for missing parameters.
     * @param parameterName - The specified parameter that is missing.
     * @return - A string stating the missing parameter.
     */
    public static String missingRequiredParameter( String parameterName )
    {
        return String.format("The required parameter \"%s\" is missing", parameterName);
    }

    /**
     * Creates a failure message for a bad POST URL.
     * @return - Returns a string with a failure message for a bad POST URL.
     */
    public static String badPostUrl()
    {
        return String.format( "The POST request used an invalid URL query pattern.");
    }


}
