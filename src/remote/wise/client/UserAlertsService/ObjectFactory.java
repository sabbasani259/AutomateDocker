
package remote.wise.client.UserAlertsService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.UserAlertsService package. 
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

    private final static QName _GetUserAlerts_QNAME = new QName("http://webservice.service.wise.remote/", "GetUserAlerts");
    private final static QName _SetUserAlerts_QNAME = new QName("http://webservice.service.wise.remote/", "setUserAlerts");
    private final static QName _GetUserAlertsResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetUserAlertsResponse");
    private final static QName _SetUserAlertsResponse_QNAME = new QName("http://webservice.service.wise.remote/", "setUserAlertsResponse");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.UserAlertsService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link UserAlertsCloserRespContract }
     * 
     */
    public UserAlertsCloserRespContract createUserAlertsCloserRespContract() {
        return new UserAlertsCloserRespContract();
    }

    /**
     * Create an instance of {@link UserAlertsReqContract }
     * 
     */
    public UserAlertsReqContract createUserAlertsReqContract() {
        return new UserAlertsReqContract();
    }

    /**
     * Create an instance of {@link SetUserAlerts }
     * 
     */
    public SetUserAlerts createSetUserAlerts() {
        return new SetUserAlerts();
    }

    /**
     * Create an instance of {@link SetUserAlertsResponse }
     * 
     */
    public SetUserAlertsResponse createSetUserAlertsResponse() {
        return new SetUserAlertsResponse();
    }

    /**
     * Create an instance of {@link GetUserAlerts }
     * 
     */
    public GetUserAlerts createGetUserAlerts() {
        return new GetUserAlerts();
    }

    /**
     * Create an instance of {@link UserAlertsRespContract }
     * 
     */
    public UserAlertsRespContract createUserAlertsRespContract() {
        return new UserAlertsRespContract();
    }

    /**
     * Create an instance of {@link GetUserAlertsResponse }
     * 
     */
    public GetUserAlertsResponse createGetUserAlertsResponse() {
        return new GetUserAlertsResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetUserAlerts }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetUserAlerts")
    public JAXBElement<GetUserAlerts> createGetUserAlerts(GetUserAlerts value) {
        return new JAXBElement<GetUserAlerts>(_GetUserAlerts_QNAME, GetUserAlerts.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetUserAlerts }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "setUserAlerts")
    public JAXBElement<SetUserAlerts> createSetUserAlerts(SetUserAlerts value) {
        return new JAXBElement<SetUserAlerts>(_SetUserAlerts_QNAME, SetUserAlerts.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetUserAlertsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetUserAlertsResponse")
    public JAXBElement<GetUserAlertsResponse> createGetUserAlertsResponse(GetUserAlertsResponse value) {
        return new JAXBElement<GetUserAlertsResponse>(_GetUserAlertsResponse_QNAME, GetUserAlertsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetUserAlertsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "setUserAlertsResponse")
    public JAXBElement<SetUserAlertsResponse> createSetUserAlertsResponse(SetUserAlertsResponse value) {
        return new JAXBElement<SetUserAlertsResponse>(_SetUserAlertsResponse_QNAME, SetUserAlertsResponse.class, null, value);
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
