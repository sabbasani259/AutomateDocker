
package remote.wise.client.PendingTenancyCreationService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.PendingTenancyCreationService package. 
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

    private final static QName _GetPendingTenancyCreationResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetPendingTenancyCreationResponse");
    private final static QName _GetPendingTenancyCreation_QNAME = new QName("http://webservice.service.wise.remote/", "GetPendingTenancyCreation");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.PendingTenancyCreationService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link PendingTenancyCreationRespContract }
     * 
     */
    public PendingTenancyCreationRespContract createPendingTenancyCreationRespContract() {
        return new PendingTenancyCreationRespContract();
    }

    /**
     * Create an instance of {@link GetPendingTenancyCreation }
     * 
     */
    public GetPendingTenancyCreation createGetPendingTenancyCreation() {
        return new GetPendingTenancyCreation();
    }

    /**
     * Create an instance of {@link PendingTenancyCreationReqContract }
     * 
     */
    public PendingTenancyCreationReqContract createPendingTenancyCreationReqContract() {
        return new PendingTenancyCreationReqContract();
    }

    /**
     * Create an instance of {@link GetPendingTenancyCreationResponse }
     * 
     */
    public GetPendingTenancyCreationResponse createGetPendingTenancyCreationResponse() {
        return new GetPendingTenancyCreationResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetPendingTenancyCreationResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetPendingTenancyCreationResponse")
    public JAXBElement<GetPendingTenancyCreationResponse> createGetPendingTenancyCreationResponse(GetPendingTenancyCreationResponse value) {
        return new JAXBElement<GetPendingTenancyCreationResponse>(_GetPendingTenancyCreationResponse_QNAME, GetPendingTenancyCreationResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetPendingTenancyCreation }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetPendingTenancyCreation")
    public JAXBElement<GetPendingTenancyCreation> createGetPendingTenancyCreation(GetPendingTenancyCreation value) {
        return new JAXBElement<GetPendingTenancyCreation>(_GetPendingTenancyCreation_QNAME, GetPendingTenancyCreation.class, null, value);
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
