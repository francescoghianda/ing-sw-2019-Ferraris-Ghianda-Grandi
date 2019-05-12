package it.polimi.se2019.utils.xml;

import it.polimi.se2019.utils.logging.Logger;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;

public class XMLValidator
{

    private static String error = "";

    private XMLValidator()
    {

    }

    public static boolean isValid(String xmlFilePath, String xdsFilePath)
    {
        error = "";
        try
        {
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = schemaFactory.newSchema(XMLValidator.class.getResource(xdsFilePath));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(XMLValidator.class.getResourceAsStream(xmlFilePath)));
        }
        catch (SAXException | IOException e)
        {
            error = e.getMessage();
            Logger.exception(e);
            return false;
        }
        return true;
    }

    public static String getError()
    {
        return error;
    }
}
