package edu.pdx.cs410J.pbt;

import edu.pdx.cs410J.AbstractAppointment;
import edu.pdx.cs410J.AbstractAppointmentBook;
import jdk.nashorn.internal.ir.debug.JSONWriter;
import jdk.nashorn.internal.runtime.JSONFunctions;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Writes the contents of an AbstractAppointmentBook to a text file
 * in XML format. It will overwrite the specified file. If the file
 * is unable to be written to an IOException will be thrown
 * by the dump function.
 */
public class TextDumper implements edu.pdx.cs410J.AppointmentBookDumper {

    private String fileName = null;

    /**
     * Constructor that accepts the name of a file that should have
     * an appointment book saved to it in XML format.
     * @param newFileName - file name to have an appointment book written
     *                    to it in XML format.
     */
    public TextDumper(String newFileName) {
        this.fileName = newFileName;
    }


    /**
     * Writes AbstractAppointmentBook object to the specified text file
     * in XML format. Throws IOException if unable to write the XML contents
     * to the specified file.
     * @param var1 - An AbstractAppointmentBook to be written to the specified file.
     * @throws IOException - Thrown when there is a problem writing appointment book XML
     *                     to the specified file.
     */
    public void dump(AbstractAppointmentBook var1) throws IOException {

        DocumentBuilder documentBuilder = null;
        Transformer transformer = null;
        File dir = new File(".");
        File dumpFile = new File(dir.getCanonicalPath() + File.separator + this.fileName);

        // Setup framework for writing XML to the specified file.
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

        try {
            documentBuilder =   documentBuilderFactory.newDocumentBuilder();
        }
        catch (ParserConfigurationException e) {
            System.err.println("Unable to read XML from appointment book file!");
        }

        // Begin building a new XML document.
        Document document = documentBuilder.newDocument();
        Element appointmentBookRoot = document.createElement("appointmentBook");
        document.appendChild(appointmentBookRoot);

        // create the owner element.
        Element owner = document.createElement("owner");
        owner.appendChild(document.createTextNode(var1.getOwnerName()));
        appointmentBookRoot.appendChild(owner);

        // Create the appointments element
        Element appointmentsElement = document.createElement("appointments");

        Collection<AbstractAppointment> appointments = var1.getAppointments();

        // Create each appointment.
        for(AbstractAppointment appointment1: appointments) {
            Element appointmentElement = document.createElement("appointment");

            Element description = document.createElement("description");
            description.appendChild(document.createTextNode(appointment1.getDescription()));
            appointmentElement.appendChild(description);

            Element beginTime = document.createElement("beginTime");
            beginTime.appendChild(document.createTextNode(appointment1.getBeginTimeString()));
            appointmentElement.appendChild(beginTime);

            Element endTime = document.createElement("endTime");
            endTime.appendChild(document.createTextNode(appointment1.getEndTimeString()));
            appointmentElement.appendChild(endTime);

            appointmentsElement.appendChild(appointmentElement);
        }

        appointmentBookRoot.appendChild(appointmentsElement);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();

        try {
            transformer = transformerFactory.newTransformer();
        }
        catch (TransformerConfigurationException e) {
            System.err.println("Problem with application: Transformer");
        }

        // Set the newly created document as our source.
        DOMSource domSource = new DOMSource(document);

        // Set the specified file as a target.
        StreamResult streamResult = new StreamResult(dumpFile);

        // Write document to file.
        try{
            transformer.transform(domSource, streamResult);
        }

        catch(TransformerException e) {
            System.err.println("Unable to write XML to text file!");
        }
    }

}
