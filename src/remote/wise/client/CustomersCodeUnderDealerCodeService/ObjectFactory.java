
package remote.wise.client.CustomersCodeUnderDealerCodeService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.CustomersCodeUnderDealerCodeService package. 
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

    private final static QName _GetCustomersCodeForDealerResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetCustomersCodeForDealerResponse");
    private final static QName _GetCustomersCodeForDealer_QNAME = new QName("http://webservice.service.wise.remote/", "GetCustomersCodeForDealer");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.CustomersCodeUnderDealerCodeService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link LinkedHashMap }
     * 
     */
    public LinkedHashMap createLinkedHashMap() {
        return new LinkedHashMap();
    }

    /**
     * Create an instance of {@link GetCustomersCodeForDealerResponse }
     * 
     */
    public GetCustomersCodeForDealerResponse createGetCustomersCodeForDealerResponse() {
        return new GetCustomersCodeForDealerResponse();
    }

    /**
     * Create an instance of {@link CustomerCodeForDealerCodeRespContract }
     * 
     */
    public CustomerCodeForDealerCodeRespContract createCustomerCodeForDealerCodeRespContract() {
        return new CustomerCodeForDealerCodeRespContract();
    }

    /**
     * Create an instance of {@link HashMap }
     * 
     */
    public HashMap createHashMap() {
        return new HashMap();
    }

    /**
     * Create an instance of {@link GetCustomersCodeForDealer }
     * 
     */
    public GetCustomersCodeForDealer createGetCustomersCodeForDealer() {
        return new GetCustomersCodeForDealer();
    }

    /**
     * Create an instance of {@link CustomerCodeForDealerCodeRespContract.CustomerMap.Entry }
     * 
     */
    public CustomerCodeForDealerCodeRespContract.CustomerMap.Entry createCustomerCodeForDealerCodeRespContractCustomerMapEntry() {
        return new CustomerCodeForDealerCodeRespContract.CustomerMap.Entry();
    }

    /**
     * Create an instance of {@link CustomerCodeForDealerCodeRespContract.CustomerMap }
     * 
     */
    public CustomerCodeForDealerCodeRespContract.CustomerMap createCustomerCodeForDealerCodeRespContractCustomerMap() {
        return new CustomerCodeForDealerCodeRespContract.CustomerMap();
    }

    /**
     * Create an instance of {@link CustomerCodeForDealerCodeReqContract }
     * 
     */
    public CustomerCodeForDealerCodeReqContract createCustomerCodeForDealerCodeReqContract() {
        return new CustomerCodeForDealerCodeReqContract();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetCustomersCodeForDealerResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetCustomersCodeForDealerResponse")
    public JAXBElement<GetCustomersCodeForDealerResponse> createGetCustomersCodeForDealerResponse(GetCustomersCodeForDealerResponse value) {
        return new JAXBElement<GetCustomersCodeForDealerResponse>(_GetCustomersCodeForDealerResponse_QNAME, GetCustomersCodeForDealerResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetCustomersCodeForDealer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetCustomersCodeForDealer")
    public JAXBElement<GetCustomersCodeForDealer> createGetCustomersCodeForDealer(GetCustomersCodeForDealer value) {
        return new JAXBElement<GetCustomersCodeForDealer>(_GetCustomersCodeForDealer_QNAME, GetCustomersCodeForDealer.class, null, value);
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
