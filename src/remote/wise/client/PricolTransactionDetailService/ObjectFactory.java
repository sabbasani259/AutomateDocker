
package remote.wise.client.PricolTransactionDetailService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.PricolTransactionDetailService package. 
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

    private final static QName _GetTransactionDetailsResponse_QNAME = new QName("http://webservice.service.wise.remote/", "getTransactionDetailsResponse");
    private final static QName _GetTransactionDetails_QNAME = new QName("http://webservice.service.wise.remote/", "getTransactionDetails");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.PricolTransactionDetailService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetTransactionDetailsResponse }
     * 
     */
    public GetTransactionDetailsResponse createGetTransactionDetailsResponse() {
        return new GetTransactionDetailsResponse();
    }

    /**
     * Create an instance of {@link PricolTransactionDetailRespContract }
     * 
     */
    public PricolTransactionDetailRespContract createPricolTransactionDetailRespContract() {
        return new PricolTransactionDetailRespContract();
    }

    /**
     * Create an instance of {@link PricolTransactionDetailRespContract.TransactionData }
     * 
     */
    public PricolTransactionDetailRespContract.TransactionData createPricolTransactionDetailRespContractTransactionData() {
        return new PricolTransactionDetailRespContract.TransactionData();
    }

    /**
     * Create an instance of {@link HashMap }
     * 
     */
    public HashMap createHashMap() {
        return new HashMap();
    }

    /**
     * Create an instance of {@link GetTransactionDetails }
     * 
     */
    public GetTransactionDetails createGetTransactionDetails() {
        return new GetTransactionDetails();
    }

    /**
     * Create an instance of {@link PricolTransactionDetailRespContract.TransactionData.Entry }
     * 
     */
    public PricolTransactionDetailRespContract.TransactionData.Entry createPricolTransactionDetailRespContractTransactionDataEntry() {
        return new PricolTransactionDetailRespContract.TransactionData.Entry();
    }

    /**
     * Create an instance of {@link PricolTransactionDetailReqContract }
     * 
     */
    public PricolTransactionDetailReqContract createPricolTransactionDetailReqContract() {
        return new PricolTransactionDetailReqContract();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetTransactionDetailsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "getTransactionDetailsResponse")
    public JAXBElement<GetTransactionDetailsResponse> createGetTransactionDetailsResponse(GetTransactionDetailsResponse value) {
        return new JAXBElement<GetTransactionDetailsResponse>(_GetTransactionDetailsResponse_QNAME, GetTransactionDetailsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetTransactionDetails }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "getTransactionDetails")
    public JAXBElement<GetTransactionDetails> createGetTransactionDetails(GetTransactionDetails value) {
        return new JAXBElement<GetTransactionDetails>(_GetTransactionDetails_QNAME, GetTransactionDetails.class, null, value);
    }

}
