
package remote.wise.client.ZonalAccountCodeService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.ZonalAccountCodeService package. 
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

    private final static QName _GetZoneDealerAccountsResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetZoneDealerAccountsResponse");
    private final static QName _GetZoneDealerAccounts_QNAME = new QName("http://webservice.service.wise.remote/", "GetZoneDealerAccounts");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.ZonalAccountCodeService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ZonalAccountRespContract }
     * 
     */
    public ZonalAccountRespContract createZonalAccountRespContract() {
        return new ZonalAccountRespContract();
    }

    /**
     * Create an instance of {@link ZonalAccountRespContract.DealerMap.Entry }
     * 
     */
    public ZonalAccountRespContract.DealerMap.Entry createZonalAccountRespContractDealerMapEntry() {
        return new ZonalAccountRespContract.DealerMap.Entry();
    }

    /**
     * Create an instance of {@link GetZoneDealerAccountsResponse }
     * 
     */
    public GetZoneDealerAccountsResponse createGetZoneDealerAccountsResponse() {
        return new GetZoneDealerAccountsResponse();
    }

    /**
     * Create an instance of {@link ZonalAccountRespContract.DealerMap }
     * 
     */
    public ZonalAccountRespContract.DealerMap createZonalAccountRespContractDealerMap() {
        return new ZonalAccountRespContract.DealerMap();
    }

    /**
     * Create an instance of {@link GetZoneDealerAccounts }
     * 
     */
    public GetZoneDealerAccounts createGetZoneDealerAccounts() {
        return new GetZoneDealerAccounts();
    }

    /**
     * Create an instance of {@link DealerCodeEntity }
     * 
     */
    public DealerCodeEntity createDealerCodeEntity() {
        return new DealerCodeEntity();
    }

    /**
     * Create an instance of {@link ZonalAccountReqContract }
     * 
     */
    public ZonalAccountReqContract createZonalAccountReqContract() {
        return new ZonalAccountReqContract();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetZoneDealerAccountsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetZoneDealerAccountsResponse")
    public JAXBElement<GetZoneDealerAccountsResponse> createGetZoneDealerAccountsResponse(GetZoneDealerAccountsResponse value) {
        return new JAXBElement<GetZoneDealerAccountsResponse>(_GetZoneDealerAccountsResponse_QNAME, GetZoneDealerAccountsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetZoneDealerAccounts }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetZoneDealerAccounts")
    public JAXBElement<GetZoneDealerAccounts> createGetZoneDealerAccounts(GetZoneDealerAccounts value) {
        return new JAXBElement<GetZoneDealerAccounts>(_GetZoneDealerAccounts_QNAME, GetZoneDealerAccounts.class, null, value);
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
