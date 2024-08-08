
package remote.wise.client.DeviceDiagnosticService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.DeviceDiagnosticService package. 
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

    private final static QName _SetDeviceData_QNAME = new QName("http://webservice.service.wise.remote/", "setDeviceData");
    private final static QName _SetEventData_QNAME = new QName("http://webservice.service.wise.remote/", "setEventData");
    private final static QName _SetDeviceDataResponse_QNAME = new QName("http://webservice.service.wise.remote/", "setDeviceDataResponse");
    private final static QName _SetEventDataResponse_QNAME = new QName("http://webservice.service.wise.remote/", "setEventDataResponse");
    private final static QName _FileNotFoundException_QNAME = new QName("http://webservice.service.wise.remote/", "FileNotFoundException");
    private final static QName _ParseException_QNAME = new QName("http://webservice.service.wise.remote/", "ParseException");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.DeviceDiagnosticService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link FileNotFoundException }
     * 
     */
    public FileNotFoundException createFileNotFoundException() {
        return new FileNotFoundException();
    }

    /**
     * Create an instance of {@link SetEventDataResponse }
     * 
     */
    public SetEventDataResponse createSetEventDataResponse() {
        return new SetEventDataResponse();
    }

    /**
     * Create an instance of {@link SetDeviceDataResponse }
     * 
     */
    public SetDeviceDataResponse createSetDeviceDataResponse() {
        return new SetDeviceDataResponse();
    }

    /**
     * Create an instance of {@link ParseException }
     * 
     */
    public ParseException createParseException() {
        return new ParseException();
    }

    /**
     * Create an instance of {@link SetEventData }
     * 
     */
    public SetEventData createSetEventData() {
        return new SetEventData();
    }

    /**
     * Create an instance of {@link SetDeviceData }
     * 
     */
    public SetDeviceData createSetDeviceData() {
        return new SetDeviceData();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetDeviceData }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "setDeviceData")
    public JAXBElement<SetDeviceData> createSetDeviceData(SetDeviceData value) {
        return new JAXBElement<SetDeviceData>(_SetDeviceData_QNAME, SetDeviceData.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetEventData }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "setEventData")
    public JAXBElement<SetEventData> createSetEventData(SetEventData value) {
        return new JAXBElement<SetEventData>(_SetEventData_QNAME, SetEventData.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetDeviceDataResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "setDeviceDataResponse")
    public JAXBElement<SetDeviceDataResponse> createSetDeviceDataResponse(SetDeviceDataResponse value) {
        return new JAXBElement<SetDeviceDataResponse>(_SetDeviceDataResponse_QNAME, SetDeviceDataResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetEventDataResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "setEventDataResponse")
    public JAXBElement<SetEventDataResponse> createSetEventDataResponse(SetEventDataResponse value) {
        return new JAXBElement<SetEventDataResponse>(_SetEventDataResponse_QNAME, SetEventDataResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FileNotFoundException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "FileNotFoundException")
    public JAXBElement<FileNotFoundException> createFileNotFoundException(FileNotFoundException value) {
        return new JAXBElement<FileNotFoundException>(_FileNotFoundException_QNAME, FileNotFoundException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ParseException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "ParseException")
    public JAXBElement<ParseException> createParseException(ParseException value) {
        return new JAXBElement<ParseException>(_ParseException_QNAME, ParseException.class, null, value);
    }

}
