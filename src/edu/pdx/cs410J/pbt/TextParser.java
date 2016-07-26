package edu.pdx.cs410J.pbt;

import edu.pdx.cs410J.AbstractAppointmentBook;
import edu.pdx.cs410J.ParserException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Parses text from a specified from to read from. Expects file to be in
 * the XML format. After reading the appointment book and its appointments
 * it will return a populated appointment book object. If the file does not exist
 * a blank file will be created and a blank appointment book will be returned. If the file
 * is malformed and does not follow the expected format a ParserException will be
 * thrown.
 */
public class TextParser implements edu.pdx.cs410J.AppointmentBookParser {

    private String fileName;

    /**
     * Constructor that takes a newFileName of a text file
     * that should contain an appoinment book in XML format.
     * @param newFileName - a string that denotes a file name.
     */
    public TextParser(String newFileName) {
        this.fileName = newFileName;
    }

    /**
     * The Parse function reads from an XML formatted file, and builds an
     * appointment book object and associated appointment objects that it
     * adds to the appointment book.
     * @return - An AbstractAppointment book as an AppointmentBook object.
     * @throws ParserException - Throws a ParserException if the text file is malformed.
     */
    public AbstractAppointmentBook parse() throws ParserException {

        DocumentBuilder documentBuilder = null;
        Transformer transformer = null;
        File dir = new File(".");
        File parseFile = null;
        Document document = null;

        try {
            parseFile = new File(dir.getCanonicalPath() + File.separator + this.fileName);

            // Handle case where appointment book does not exist at all.
            if(!parseFile.exists()) {
                return new AppointmentBook("newbook");
            }

            // If the appointment book is a directory
            // then throw an exception.
            if(parseFile.isDirectory()) {
                throw new ParserException("Directory specified as parse file!");
            }

        }
        // In case there is a problem with the file system, such as
        // bad permissions.
        catch(IOException e) {
            throw new ParserException("Unable to use parse file!");
        }

        // Start building up the framework to read XML
        // from our specified file.
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

        try {
            documentBuilder =   documentBuilderFactory.newDocumentBuilder();
        }
        catch (ParserConfigurationException e) {
            throw new ParserException("Unable to use parse file!");
        }

        // Parse text from the specified file.
        try {
            document = documentBuilder.parse(parseFile);
        }
        catch(SAXException|IOException e) {
            throw new ParserException("Unable to read parse file!");
        }

        // Build the node list for the root of the appointment book.
        NodeList nodes = document.getElementsByTagName("appointmentBook");

        Element appointmentBookElement = (Element) nodes.item(0);
        String owner = appointmentBookElement.getElementsByTagName("owner").item(0).getTextContent();

        // Now tat we know our owner create an appointment book object.
        AppointmentBook appointmentBook = new AppointmentBook(owner);

        NodeList appointmentsNodeList = appointmentBookElement.getElementsByTagName("appointments");

        // Begin going through the list of nodes and looking for
        // the appointments element so we can save all of them to
        // individual appoinment objects.
        for(int i = 0; i < appointmentsNodeList.getLength(); ++i) {

            Node appointmentsNode = appointmentsNodeList.item(i);

            if(appointmentsNode.getNodeType() == Node.ELEMENT_NODE) {
                Element appointmentsElement = (Element) appointmentsNode;
                NodeList appointmentNodes = appointmentsElement.getElementsByTagName("appointment");

                // Find an individual appointment and build an
                // object out of it.
                for(int j = 0; j < appointmentNodes.getLength(); ++j) {

                    Node appointmentNode = appointmentNodes.item(j);

                    if(appointmentNode.getNodeType() == Node.ELEMENT_NODE) {

                        Element appointmentElement = (Element) appointmentNode;

                        SimpleDateFormat twentyFourHourDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
                        twentyFourHourDateFormat.setLenient(false);

                        SimpleDateFormat twelveHourDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
                        twelveHourDateFormat.setLenient(false);

                        String description = appointmentElement.getElementsByTagName("description").item(0).getTextContent();
                        String beginTime = appointmentElement.getElementsByTagName("beginTime").item(0).getTextContent();
                        String endTime = appointmentElement.getElementsByTagName("endTime").item(0).getTextContent();

                        // Build the appointment object and add it to the appointment book.
                        Appointment appointment = new Appointment(description, beginTime, endTime);
                        appointmentBook.addAppointment(appointment);
                    }
                }
            }
        }

        return appointmentBook;
    }

}
