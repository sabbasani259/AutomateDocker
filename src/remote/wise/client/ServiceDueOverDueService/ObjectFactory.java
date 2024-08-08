
package remote.wise.client.ServiceDueOverDueService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.ServiceDueOverDueService package. 
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

    private final static QName _GetServiceDueOverDueDetails_QNAME = new QName("http://webservice.service.wise.remote/", "GetServiceDueOverDueDetails");
    private final static QName _GetServiceDueOverDueDetailsResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetServiceDueOverDueDetailsResponse");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.ServiceDueOverDueService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ServiceDueOverDueReqContract }
     * 
     */
    public ServiceDueOverDueReqContract createServiceDueOverDueReqContract() {
        return new ServiceDueOverDueReqContract();
    }

    /**
     * Create an instance of {@link GetServiceDueOverDueDetails }
     * 
     */
    public GetServiceDueOverDueDetails createGetServiceDueOverDueDetails() {
        return new GetServiceDueOverDueDetails();
    }

    /**
     * Create an instance of {@link ServiceDueOverDueRespContract }
     * 
     */
    public ServiceDueOverDueRespContract createServiceDueOverDueRespContract() {
        return new ServiceDueOverDueRespContract();
    }

    /**
     * Create an instance of {@link GetServiceDueOverDueDetailsResponse }
     * 
     */
    public GetServiceDueOverDueDetailsResponse createGetServiceDueOverDueDetailsResponse() {
        return new GetServiceDueOverDueDetailsResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetServiceDueOverDueDetails }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetServiceDueOverDueDetails")
    public JAXBElement<GetServiceDueOverDueDetails> createGetServiceDueOverDueDetails(GetServiceDueOverDueDetails value) {
        return new JAXBElement<GetServiceDueOverDueDetails>(_GetServiceDueOverDueDetails_QNAME, GetServiceDueOverDueDetails.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetServiceDueOverDueDetailsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetServiceDueOverDueDetailsResponse")
    public JAXBElement<GetServiceDueOverDueDetailsResponse> createGetServiceDueOverDueDetailsResponse(GetServiceDueOverDueDetailsResponse value) {
        return new JAXBElement<GetServiceDueOverDueDetailsResponse>(_GetServiceDueOverDueDetailsResponse_QNAME, GetServiceDueOverDueDetailsResponse.class, null, value);
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
