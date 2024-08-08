
package remote.wise.client.UserDetailsService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.UserDetailsService package. 
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

    private final static QName _SetUserDetails_QNAME = new QName("http://webservice.service.wise.remote/", "SetUserDetails");
    private final static QName _SetUserDetailsResponse_QNAME = new QName("http://webservice.service.wise.remote/", "SetUserDetailsResponse");
    private final static QName _IOException_QNAME = new QName("http://webservice.service.wise.remote/", "IOException");
    private final static QName _GetUserDetailsResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetUserDetailsResponse");
    private final static QName _GetUserDetails_QNAME = new QName("http://webservice.service.wise.remote/", "GetUserDetails");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.UserDetailsService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetUserDetails }
     * 
     */
    public GetUserDetails createGetUserDetails() {
        return new GetUserDetails();
    }

    /**
     * Create an instance of {@link UserDetailsRespContract }
     * 
     */
    public UserDetailsRespContract createUserDetailsRespContract() {
        return new UserDetailsRespContract();
    }

    /**
     * Create an instance of {@link SetUserDetailsResponse }
     * 
     */
    public SetUserDetailsResponse createSetUserDetailsResponse() {
        return new SetUserDetailsResponse();
    }

    /**
     * Create an instance of {@link IOException }
     * 
     */
    public IOException createIOException() {
        return new IOException();
    }

    /**
     * Create an instance of {@link SetUserDetails }
     * 
     */
    public SetUserDetails createSetUserDetails() {
        return new SetUserDetails();
    }

    /**
     * Create an instance of {@link GetUserDetailsResponse }
     * 
     */
    public GetUserDetailsResponse createGetUserDetailsResponse() {
        return new GetUserDetailsResponse();
    }

    /**
     * Create an instance of {@link UserDetailsReqContract }
     * 
     */
    public UserDetailsReqContract createUserDetailsReqContract() {
        return new UserDetailsReqContract();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetUserDetails }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "SetUserDetails")
    public JAXBElement<SetUserDetails> createSetUserDetails(SetUserDetails value) {
        return new JAXBElement<SetUserDetails>(_SetUserDetails_QNAME, SetUserDetails.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetUserDetailsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "SetUserDetailsResponse")
    public JAXBElement<SetUserDetailsResponse> createSetUserDetailsResponse(SetUserDetailsResponse value) {
        return new JAXBElement<SetUserDetailsResponse>(_SetUserDetailsResponse_QNAME, SetUserDetailsResponse.class, null, value);
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
     * Create an instance of {@link JAXBElement }{@code <}{@link GetUserDetailsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetUserDetailsResponse")
    public JAXBElement<GetUserDetailsResponse> createGetUserDetailsResponse(GetUserDetailsResponse value) {
        return new JAXBElement<GetUserDetailsResponse>(_GetUserDetailsResponse_QNAME, GetUserDetailsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetUserDetails }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetUserDetails")
    public JAXBElement<GetUserDetails> createGetUserDetails(GetUserDetails value) {
        return new JAXBElement<GetUserDetails>(_GetUserDetails_QNAME, GetUserDetails.class, null, value);
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
