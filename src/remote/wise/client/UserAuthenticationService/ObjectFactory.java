
package remote.wise.client.UserAuthenticationService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.UserAuthenticationService package. 
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
    private final static QName _UserAuthentication_QNAME = new QName("http://webservice.service.wise.remote/", "UserAuthentication");
    private final static QName _UserAuthenticationResponse_QNAME = new QName("http://webservice.service.wise.remote/", "UserAuthenticationResponse");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.UserAuthenticationService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link UserAuthentication }
     * 
     */
    public UserAuthentication createUserAuthentication() {
        return new UserAuthentication();
    }

    /**
     * Create an instance of {@link UserAuthenticationReqContract }
     * 
     */
    public UserAuthenticationReqContract createUserAuthenticationReqContract() {
        return new UserAuthenticationReqContract();
    }

    /**
     * Create an instance of {@link IOException }
     * 
     */
    public IOException createIOException() {
        return new IOException();
    }

    /**
     * Create an instance of {@link UserAuthenticationRespContract }
     * 
     */
    public UserAuthenticationRespContract createUserAuthenticationRespContract() {
        return new UserAuthenticationRespContract();
    }

    /**
     * Create an instance of {@link UserAuthenticationResponse }
     * 
     */
    public UserAuthenticationResponse createUserAuthenticationResponse() {
        return new UserAuthenticationResponse();
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
     * Create an instance of {@link JAXBElement }{@code <}{@link UserAuthentication }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "UserAuthentication")
    public JAXBElement<UserAuthentication> createUserAuthentication(UserAuthentication value) {
        return new JAXBElement<UserAuthentication>(_UserAuthentication_QNAME, UserAuthentication.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UserAuthenticationResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "UserAuthenticationResponse")
    public JAXBElement<UserAuthenticationResponse> createUserAuthenticationResponse(UserAuthenticationResponse value) {
        return new JAXBElement<UserAuthenticationResponse>(_UserAuthenticationResponse_QNAME, UserAuthenticationResponse.class, null, value);
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
