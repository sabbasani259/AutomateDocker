
package remote.wise.client.GetDealersForZoneService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.GetDealersForZoneService package. 
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

    private final static QName _GetDealersForZone_QNAME = new QName("http://webservice.service.wise.remote/", "GetDealersForZone");
    private final static QName _GetDealersForZoneResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetDealersForZoneResponse");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.GetDealersForZoneService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link DealersUnderZoneReqContract }
     * 
     */
    public DealersUnderZoneReqContract createDealersUnderZoneReqContract() {
        return new DealersUnderZoneReqContract();
    }

    /**
     * Create an instance of {@link DealersUnderZoneRespContract }
     * 
     */
    public DealersUnderZoneRespContract createDealersUnderZoneRespContract() {
        return new DealersUnderZoneRespContract();
    }

    /**
     * Create an instance of {@link GetDealersForZoneResponse }
     * 
     */
    public GetDealersForZoneResponse createGetDealersForZoneResponse() {
        return new GetDealersForZoneResponse();
    }

    /**
     * Create an instance of {@link CustomerEntity }
     * 
     */
    public CustomerEntity createCustomerEntity() {
        return new CustomerEntity();
    }

    /**
     * Create an instance of {@link DealersUnderZoneRespContract.DealerMap.Entry }
     * 
     */
    public DealersUnderZoneRespContract.DealerMap.Entry createDealersUnderZoneRespContractDealerMapEntry() {
        return new DealersUnderZoneRespContract.DealerMap.Entry();
    }

    /**
     * Create an instance of {@link DealerEntity }
     * 
     */
    public DealerEntity createDealerEntity() {
        return new DealerEntity();
    }

    /**
     * Create an instance of {@link GetDealersForZone }
     * 
     */
    public GetDealersForZone createGetDealersForZone() {
        return new GetDealersForZone();
    }

    /**
     * Create an instance of {@link DealersUnderZoneRespContract.DealerMap }
     * 
     */
    public DealersUnderZoneRespContract.DealerMap createDealersUnderZoneRespContractDealerMap() {
        return new DealersUnderZoneRespContract.DealerMap();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetDealersForZone }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetDealersForZone")
    public JAXBElement<GetDealersForZone> createGetDealersForZone(GetDealersForZone value) {
        return new JAXBElement<GetDealersForZone>(_GetDealersForZone_QNAME, GetDealersForZone.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetDealersForZoneResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetDealersForZoneResponse")
    public JAXBElement<GetDealersForZoneResponse> createGetDealersForZoneResponse(GetDealersForZoneResponse value) {
        return new JAXBElement<GetDealersForZoneResponse>(_GetDealersForZoneResponse_QNAME, GetDealersForZoneResponse.class, null, value);
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
