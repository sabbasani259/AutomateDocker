
package remote.wise.client.MachineHealthDetailsService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.MachineHealthDetailsService package. 
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

    private final static QName _GetMachineHealthDetails_QNAME = new QName("http://webservice.service.wise.remote/", "GetMachineHealthDetails");
    private final static QName _GetMachineHealthDetailsResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetMachineHealthDetailsResponse");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.MachineHealthDetailsService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link MachineHealthDetailsReqContract }
     * 
     */
    public MachineHealthDetailsReqContract createMachineHealthDetailsReqContract() {
        return new MachineHealthDetailsReqContract();
    }

    /**
     * Create an instance of {@link GetMachineHealthDetailsResponse }
     * 
     */
    public GetMachineHealthDetailsResponse createGetMachineHealthDetailsResponse() {
        return new GetMachineHealthDetailsResponse();
    }

    /**
     * Create an instance of {@link MachineHealthDetailsRespContract }
     * 
     */
    public MachineHealthDetailsRespContract createMachineHealthDetailsRespContract() {
        return new MachineHealthDetailsRespContract();
    }

    /**
     * Create an instance of {@link GetMachineHealthDetails }
     * 
     */
    public GetMachineHealthDetails createGetMachineHealthDetails() {
        return new GetMachineHealthDetails();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetMachineHealthDetails }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetMachineHealthDetails")
    public JAXBElement<GetMachineHealthDetails> createGetMachineHealthDetails(GetMachineHealthDetails value) {
        return new JAXBElement<GetMachineHealthDetails>(_GetMachineHealthDetails_QNAME, GetMachineHealthDetails.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetMachineHealthDetailsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetMachineHealthDetailsResponse")
    public JAXBElement<GetMachineHealthDetailsResponse> createGetMachineHealthDetailsResponse(GetMachineHealthDetailsResponse value) {
        return new JAXBElement<GetMachineHealthDetailsResponse>(_GetMachineHealthDetailsResponse_QNAME, GetMachineHealthDetailsResponse.class, null, value);
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
