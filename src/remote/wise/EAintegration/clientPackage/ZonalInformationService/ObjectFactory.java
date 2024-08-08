
package remote.wise.EAintegration.clientPackage.ZonalInformationService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.EAintegration.clientPackage.ZonalInformationService package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _IOException_QNAME = new QName("http://webservice.service.wise.remote/", "IOException");
    private final static QName _SetZonalDetailsResponse_QNAME = new QName("http://webservice.service.wise.remote/", "setZonalDetailsResponse");
    private final static QName _SetZonalDetails_QNAME = new QName("http://webservice.service.wise.remote/", "setZonalDetails");
    private final static QName _ParseException_QNAME = new QName("http://webservice.service.wise.remote/", "ParseException");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.EAintegration.clientPackage.ZonalInformationService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link IOException }
     * 
     */
    public IOException createIOException() {
        return new IOException();
    }

    /**
     * Create an instance of {@link SetZonalDetailsResponse }
     * 
     */
    public SetZonalDetailsResponse createSetZonalDetailsResponse() {
        return new SetZonalDetailsResponse();
    }

    /**
     * Create an instance of {@link SetZonalDetails }
     * 
     */
    public SetZonalDetails createSetZonalDetails() {
        return new SetZonalDetails();
    }

    /**
     * Create an instance of {@link ParseException }
     * 
     */
    public ParseException createParseException() {
        return new ParseException();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IOException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "IOException")
    public JAXBElement<IOException> createIOException(IOException value) {
        return new JAXBElement<IOException>(_IOException_QNAME, IOException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetZonalDetailsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "setZonalDetailsResponse")
    public JAXBElement<SetZonalDetailsResponse> createSetZonalDetailsResponse(SetZonalDetailsResponse value) {
        return new JAXBElement<SetZonalDetailsResponse>(_SetZonalDetailsResponse_QNAME, SetZonalDetailsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetZonalDetails }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "setZonalDetails")
    public JAXBElement<SetZonalDetails> createSetZonalDetails(SetZonalDetails value) {
        return new JAXBElement<SetZonalDetails>(_SetZonalDetails_QNAME, SetZonalDetails.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ParseException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "ParseException")
    public JAXBElement<ParseException> createParseException(ParseException value) {
        return new JAXBElement<ParseException>(_ParseException_QNAME, ParseException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "CustomFault")
    public JAXBElement<String> createCustomFault(String value) {
        return new JAXBElement<String>(_CustomFault_QNAME, String.class, null, value);
    }

}
