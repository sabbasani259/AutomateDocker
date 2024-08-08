
package remote.wise.client.FotaAuthenticationService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.FotaAuthenticationService package. 
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

    private final static QName _FotaAuthenticationResponse_QNAME = new QName("http://webservice.service.wise.remote/", "FotaAuthenticationResponse");
    private final static QName _FotaAuthentication_QNAME = new QName("http://webservice.service.wise.remote/", "FotaAuthentication");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.FotaAuthenticationService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link FotaAuthentication }
     * 
     */
    public FotaAuthentication createFotaAuthentication() {
        return new FotaAuthentication();
    }

    /**
     * Create an instance of {@link FotaAuthenticationReqContract }
     * 
     */
    public FotaAuthenticationReqContract createFotaAuthenticationReqContract() {
        return new FotaAuthenticationReqContract();
    }

    /**
     * Create an instance of {@link FotaAuthenticationResponse }
     * 
     */
    public FotaAuthenticationResponse createFotaAuthenticationResponse() {
        return new FotaAuthenticationResponse();
    }

    /**
     * Create an instance of {@link FotaAuthenticationRespContract }
     * 
     */
    public FotaAuthenticationRespContract createFotaAuthenticationRespContract() {
        return new FotaAuthenticationRespContract();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FotaAuthenticationResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "FotaAuthenticationResponse")
    public JAXBElement<FotaAuthenticationResponse> createFotaAuthenticationResponse(FotaAuthenticationResponse value) {
        return new JAXBElement<FotaAuthenticationResponse>(_FotaAuthenticationResponse_QNAME, FotaAuthenticationResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FotaAuthentication }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "FotaAuthentication")
    public JAXBElement<FotaAuthentication> createFotaAuthentication(FotaAuthentication value) {
        return new JAXBElement<FotaAuthentication>(_FotaAuthentication_QNAME, FotaAuthentication.class, null, value);
    }

}
