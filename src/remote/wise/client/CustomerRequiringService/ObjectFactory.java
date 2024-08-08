
package remote.wise.client.CustomerRequiringService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.CustomerRequiringService package. 
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

    private final static QName _GetCustomerRequiringResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetCustomerRequiringResponse");
    private final static QName _GetCustomerRequiring_QNAME = new QName("http://webservice.service.wise.remote/", "GetCustomerRequiring");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.CustomerRequiringService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link CustomerRequiringRespContract }
     * 
     */
    public CustomerRequiringRespContract createCustomerRequiringRespContract() {
        return new CustomerRequiringRespContract();
    }

    /**
     * Create an instance of {@link GetCustomerRequiring }
     * 
     */
    public GetCustomerRequiring createGetCustomerRequiring() {
        return new GetCustomerRequiring();
    }

    /**
     * Create an instance of {@link CustomerRequiringReqContract }
     * 
     */
    public CustomerRequiringReqContract createCustomerRequiringReqContract() {
        return new CustomerRequiringReqContract();
    }

    /**
     * Create an instance of {@link GetCustomerRequiringResponse }
     * 
     */
    public GetCustomerRequiringResponse createGetCustomerRequiringResponse() {
        return new GetCustomerRequiringResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetCustomerRequiringResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetCustomerRequiringResponse")
    public JAXBElement<GetCustomerRequiringResponse> createGetCustomerRequiringResponse(GetCustomerRequiringResponse value) {
        return new JAXBElement<GetCustomerRequiringResponse>(_GetCustomerRequiringResponse_QNAME, GetCustomerRequiringResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetCustomerRequiring }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetCustomerRequiring")
    public JAXBElement<GetCustomerRequiring> createGetCustomerRequiring(GetCustomerRequiring value) {
        return new JAXBElement<GetCustomerRequiring>(_GetCustomerRequiring_QNAME, GetCustomerRequiring.class, null, value);
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
