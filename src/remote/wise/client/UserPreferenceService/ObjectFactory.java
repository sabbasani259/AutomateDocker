
package remote.wise.client.UserPreferenceService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.UserPreferenceService package. 
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

    private final static QName _GetUserPreferenceResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetUserPreferenceResponse");
    private final static QName _GetUserPreference_QNAME = new QName("http://webservice.service.wise.remote/", "GetUserPreference");
    private final static QName _SetUserPreference_QNAME = new QName("http://webservice.service.wise.remote/", "SetUserPreference");
    private final static QName _SetUserPreferenceResponse_QNAME = new QName("http://webservice.service.wise.remote/", "SetUserPreferenceResponse");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.UserPreferenceService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link UserPreferenceReqContract }
     * 
     */
    public UserPreferenceReqContract createUserPreferenceReqContract() {
        return new UserPreferenceReqContract();
    }

    /**
     * Create an instance of {@link SetUserPreference }
     * 
     */
    public SetUserPreference createSetUserPreference() {
        return new SetUserPreference();
    }

    /**
     * Create an instance of {@link GetUserPreferenceResponse }
     * 
     */
    public GetUserPreferenceResponse createGetUserPreferenceResponse() {
        return new GetUserPreferenceResponse();
    }

    /**
     * Create an instance of {@link UserPreferenceRespContract }
     * 
     */
    public UserPreferenceRespContract createUserPreferenceRespContract() {
        return new UserPreferenceRespContract();
    }

    /**
     * Create an instance of {@link SetUserPreferenceResponse }
     * 
     */
    public SetUserPreferenceResponse createSetUserPreferenceResponse() {
        return new SetUserPreferenceResponse();
    }

    /**
     * Create an instance of {@link GetUserPreference }
     * 
     */
    public GetUserPreference createGetUserPreference() {
        return new GetUserPreference();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetUserPreferenceResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetUserPreferenceResponse")
    public JAXBElement<GetUserPreferenceResponse> createGetUserPreferenceResponse(GetUserPreferenceResponse value) {
        return new JAXBElement<GetUserPreferenceResponse>(_GetUserPreferenceResponse_QNAME, GetUserPreferenceResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetUserPreference }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetUserPreference")
    public JAXBElement<GetUserPreference> createGetUserPreference(GetUserPreference value) {
        return new JAXBElement<GetUserPreference>(_GetUserPreference_QNAME, GetUserPreference.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetUserPreference }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "SetUserPreference")
    public JAXBElement<SetUserPreference> createSetUserPreference(SetUserPreference value) {
        return new JAXBElement<SetUserPreference>(_SetUserPreference_QNAME, SetUserPreference.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetUserPreferenceResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "SetUserPreferenceResponse")
    public JAXBElement<SetUserPreferenceResponse> createSetUserPreferenceResponse(SetUserPreferenceResponse value) {
        return new JAXBElement<SetUserPreferenceResponse>(_SetUserPreferenceResponse_QNAME, SetUserPreferenceResponse.class, null, value);
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
