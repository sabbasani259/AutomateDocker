
package remote.wise.client.FotaUpgradedHistoryDetailsService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.FotaUpgradedHistoryDetailsService package. 
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

    private final static QName _GetFotaUpgradedDetails_QNAME = new QName("http://webservice.service.wise.remote/", "getFotaUpgradedDetails");
    private final static QName _GetFotaUpgradedDetailsResponse_QNAME = new QName("http://webservice.service.wise.remote/", "getFotaUpgradedDetailsResponse");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.FotaUpgradedHistoryDetailsService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetFotaUpgradedDetailsResponse }
     * 
     */
    public GetFotaUpgradedDetailsResponse createGetFotaUpgradedDetailsResponse() {
        return new GetFotaUpgradedDetailsResponse();
    }

    /**
     * Create an instance of {@link FotaUpgradedHistoryDetailsReqContract }
     * 
     */
    public FotaUpgradedHistoryDetailsReqContract createFotaUpgradedHistoryDetailsReqContract() {
        return new FotaUpgradedHistoryDetailsReqContract();
    }

    /**
     * Create an instance of {@link FotaUpgradedHistoryDetailsRespContract }
     * 
     */
    public FotaUpgradedHistoryDetailsRespContract createFotaUpgradedHistoryDetailsRespContract() {
        return new FotaUpgradedHistoryDetailsRespContract();
    }

    /**
     * Create an instance of {@link GetFotaUpgradedDetails }
     * 
     */
    public GetFotaUpgradedDetails createGetFotaUpgradedDetails() {
        return new GetFotaUpgradedDetails();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetFotaUpgradedDetails }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "getFotaUpgradedDetails")
    public JAXBElement<GetFotaUpgradedDetails> createGetFotaUpgradedDetails(GetFotaUpgradedDetails value) {
        return new JAXBElement<GetFotaUpgradedDetails>(_GetFotaUpgradedDetails_QNAME, GetFotaUpgradedDetails.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetFotaUpgradedDetailsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "getFotaUpgradedDetailsResponse")
    public JAXBElement<GetFotaUpgradedDetailsResponse> createGetFotaUpgradedDetailsResponse(GetFotaUpgradedDetailsResponse value) {
        return new JAXBElement<GetFotaUpgradedDetailsResponse>(_GetFotaUpgradedDetailsResponse_QNAME, GetFotaUpgradedDetailsResponse.class, null, value);
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
