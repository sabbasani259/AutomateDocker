
package remote.wise.client.CustomersUnderDealerService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.CustomersUnderDealerService package. 
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

    private final static QName _GetCustomersForDealerResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetCustomersForDealerResponse");
    private final static QName _GetCustomersForDealer_QNAME = new QName("http://webservice.service.wise.remote/", "GetCustomersForDealer");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.CustomersUnderDealerService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link CustomerForDealerRespContract.CustomerMap }
     * 
     */
    public CustomerForDealerRespContract.CustomerMap createCustomerForDealerRespContractCustomerMap() {
        return new CustomerForDealerRespContract.CustomerMap();
    }

    /**
     * Create an instance of {@link CustomerForDealerRespContract.CustomerMap.Entry }
     * 
     */
    public CustomerForDealerRespContract.CustomerMap.Entry createCustomerForDealerRespContractCustomerMapEntry() {
        return new CustomerForDealerRespContract.CustomerMap.Entry();
    }

    /**
     * Create an instance of {@link CustomerForDealerReqContract }
     * 
     */
    public CustomerForDealerReqContract createCustomerForDealerReqContract() {
        return new CustomerForDealerReqContract();
    }

    /**
     * Create an instance of {@link GetCustomersForDealerResponse }
     * 
     */
    public GetCustomersForDealerResponse createGetCustomersForDealerResponse() {
        return new GetCustomersForDealerResponse();
    }

    /**
     * Create an instance of {@link GetCustomersForDealer }
     * 
     */
    public GetCustomersForDealer createGetCustomersForDealer() {
        return new GetCustomersForDealer();
    }

    /**
     * Create an instance of {@link HashMap }
     * 
     */
    public HashMap createHashMap() {
        return new HashMap();
    }

    /**
     * Create an instance of {@link CustomerForDealerRespContract }
     * 
     */
    public CustomerForDealerRespContract createCustomerForDealerRespContract() {
        return new CustomerForDealerRespContract();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetCustomersForDealerResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetCustomersForDealerResponse")
    public JAXBElement<GetCustomersForDealerResponse> createGetCustomersForDealerResponse(GetCustomersForDealerResponse value) {
        return new JAXBElement<GetCustomersForDealerResponse>(_GetCustomersForDealerResponse_QNAME, GetCustomersForDealerResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetCustomersForDealer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetCustomersForDealer")
    public JAXBElement<GetCustomersForDealer> createGetCustomersForDealer(GetCustomersForDealer value) {
        return new JAXBElement<GetCustomersForDealer>(_GetCustomersForDealer_QNAME, GetCustomersForDealer.class, null, value);
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
