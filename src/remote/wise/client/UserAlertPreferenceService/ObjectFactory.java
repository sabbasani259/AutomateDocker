
package remote.wise.client.UserAlertPreferenceService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.UserAlertPreferenceService package. 
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

    private final static QName _SetUserAlertPreferenceResponse_QNAME = new QName("http://webservice.service.wise.remote/", "SetUserAlertPreferenceResponse");
    private final static QName _SetUserAlertPreference_QNAME = new QName("http://webservice.service.wise.remote/", "SetUserAlertPreference");
    private final static QName _SetAdminPreference_QNAME = new QName("http://webservice.service.wise.remote/", "SetAdminPreference");
    private final static QName _GetUserAlertPreference_QNAME = new QName("http://webservice.service.wise.remote/", "GetUserAlertPreference");
    private final static QName _GetUserAlertPreferenceResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetUserAlertPreferenceResponse");
    private final static QName _SetAdminPreferenceResponse_QNAME = new QName("http://webservice.service.wise.remote/", "SetAdminPreferenceResponse");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.UserAlertPreferenceService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SetAdminPreferenceResponse }
     * 
     */
    public SetAdminPreferenceResponse createSetAdminPreferenceResponse() {
        return new SetAdminPreferenceResponse();
    }

    /**
     * Create an instance of {@link SetAdminPreference }
     * 
     */
    public SetAdminPreference createSetAdminPreference() {
        return new SetAdminPreference();
    }

    /**
     * Create an instance of {@link UserAlertPreferenceRespContract }
     * 
     */
    public UserAlertPreferenceRespContract createUserAlertPreferenceRespContract() {
        return new UserAlertPreferenceRespContract();
    }

    /**
     * Create an instance of {@link GetUserAlertPreference }
     * 
     */
    public GetUserAlertPreference createGetUserAlertPreference() {
        return new GetUserAlertPreference();
    }

    /**
     * Create an instance of {@link UserAlertPreferenceReqContract }
     * 
     */
    public UserAlertPreferenceReqContract createUserAlertPreferenceReqContract() {
        return new UserAlertPreferenceReqContract();
    }

    /**
     * Create an instance of {@link GetUserAlertPreferenceResponse }
     * 
     */
    public GetUserAlertPreferenceResponse createGetUserAlertPreferenceResponse() {
        return new GetUserAlertPreferenceResponse();
    }

    /**
     * Create an instance of {@link SetUserAlertPreference }
     * 
     */
    public SetUserAlertPreference createSetUserAlertPreference() {
        return new SetUserAlertPreference();
    }

    /**
     * Create an instance of {@link SetUserAlertPreferenceResponse }
     * 
     */
    public SetUserAlertPreferenceResponse createSetUserAlertPreferenceResponse() {
        return new SetUserAlertPreferenceResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetUserAlertPreferenceResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "SetUserAlertPreferenceResponse")
    public JAXBElement<SetUserAlertPreferenceResponse> createSetUserAlertPreferenceResponse(SetUserAlertPreferenceResponse value) {
        return new JAXBElement<SetUserAlertPreferenceResponse>(_SetUserAlertPreferenceResponse_QNAME, SetUserAlertPreferenceResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetUserAlertPreference }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "SetUserAlertPreference")
    public JAXBElement<SetUserAlertPreference> createSetUserAlertPreference(SetUserAlertPreference value) {
        return new JAXBElement<SetUserAlertPreference>(_SetUserAlertPreference_QNAME, SetUserAlertPreference.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetAdminPreference }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "SetAdminPreference")
    public JAXBElement<SetAdminPreference> createSetAdminPreference(SetAdminPreference value) {
        return new JAXBElement<SetAdminPreference>(_SetAdminPreference_QNAME, SetAdminPreference.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetUserAlertPreference }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetUserAlertPreference")
    public JAXBElement<GetUserAlertPreference> createGetUserAlertPreference(GetUserAlertPreference value) {
        return new JAXBElement<GetUserAlertPreference>(_GetUserAlertPreference_QNAME, GetUserAlertPreference.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetUserAlertPreferenceResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetUserAlertPreferenceResponse")
    public JAXBElement<GetUserAlertPreferenceResponse> createGetUserAlertPreferenceResponse(GetUserAlertPreferenceResponse value) {
        return new JAXBElement<GetUserAlertPreferenceResponse>(_GetUserAlertPreferenceResponse_QNAME, GetUserAlertPreferenceResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetAdminPreferenceResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "SetAdminPreferenceResponse")
    public JAXBElement<SetAdminPreferenceResponse> createSetAdminPreferenceResponse(SetAdminPreferenceResponse value) {
        return new JAXBElement<SetAdminPreferenceResponse>(_SetAdminPreferenceResponse_QNAME, SetAdminPreferenceResponse.class, null, value);
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
