
package remote.wise.client.AdminAlertPrefService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.AdminAlertPrefService package. 
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

    private final static QName _SetAlertModeResponse_QNAME = new QName("http://webservice.service.wise.remote/", "SetAlertModeResponse");
    private final static QName _GetAlertModeResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetAlertModeResponse");
    private final static QName _SetAlertMode_QNAME = new QName("http://webservice.service.wise.remote/", "SetAlertMode");
    private final static QName _GetAlertMode_QNAME = new QName("http://webservice.service.wise.remote/", "GetAlertMode");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.AdminAlertPrefService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SetAlertMode }
     * 
     */
    public SetAlertMode createSetAlertMode() {
        return new SetAlertMode();
    }

    /**
     * Create an instance of {@link SetAlertModeResponse }
     * 
     */
    public SetAlertModeResponse createSetAlertModeResponse() {
        return new SetAlertModeResponse();
    }

    /**
     * Create an instance of {@link GetAlertMode }
     * 
     */
    public GetAlertMode createGetAlertMode() {
        return new GetAlertMode();
    }

    /**
     * Create an instance of {@link GetAlertModeResponse }
     * 
     */
    public GetAlertModeResponse createGetAlertModeResponse() {
        return new GetAlertModeResponse();
    }

    /**
     * Create an instance of {@link AdminAlertPrefReqContract }
     * 
     */
    public AdminAlertPrefReqContract createAdminAlertPrefReqContract() {
        return new AdminAlertPrefReqContract();
    }

    /**
     * Create an instance of {@link AdminAlertPrefRespContract }
     * 
     */
    public AdminAlertPrefRespContract createAdminAlertPrefRespContract() {
        return new AdminAlertPrefRespContract();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetAlertModeResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "SetAlertModeResponse")
    public JAXBElement<SetAlertModeResponse> createSetAlertModeResponse(SetAlertModeResponse value) {
        return new JAXBElement<SetAlertModeResponse>(_SetAlertModeResponse_QNAME, SetAlertModeResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAlertModeResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetAlertModeResponse")
    public JAXBElement<GetAlertModeResponse> createGetAlertModeResponse(GetAlertModeResponse value) {
        return new JAXBElement<GetAlertModeResponse>(_GetAlertModeResponse_QNAME, GetAlertModeResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetAlertMode }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "SetAlertMode")
    public JAXBElement<SetAlertMode> createSetAlertMode(SetAlertMode value) {
        return new JAXBElement<SetAlertMode>(_SetAlertMode_QNAME, SetAlertMode.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAlertMode }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetAlertMode")
    public JAXBElement<GetAlertMode> createGetAlertMode(GetAlertMode value) {
        return new JAXBElement<GetAlertMode>(_GetAlertMode_QNAME, GetAlertMode.class, null, value);
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
