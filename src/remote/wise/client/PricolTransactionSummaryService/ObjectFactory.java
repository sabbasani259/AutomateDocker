
package remote.wise.client.PricolTransactionSummaryService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.PricolTransactionSummaryService package. 
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

    private final static QName _GetVinList_QNAME = new QName("http://webservice.service.wise.remote/", "getVinList");
    private final static QName _GetVinListResponse_QNAME = new QName("http://webservice.service.wise.remote/", "getVinListResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.PricolTransactionSummaryService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetVinListResponse }
     * 
     */
    public GetVinListResponse createGetVinListResponse() {
        return new GetVinListResponse();
    }

    /**
     * Create an instance of {@link GetVinList }
     * 
     */
    public GetVinList createGetVinList() {
        return new GetVinList();
    }

    /**
     * Create an instance of {@link PricolTransactionSummaryReqContract }
     * 
     */
    public PricolTransactionSummaryReqContract createPricolTransactionSummaryReqContract() {
        return new PricolTransactionSummaryReqContract();
    }

    /**
     * Create an instance of {@link PricolTransactionSummaryRespContract }
     * 
     */
    public PricolTransactionSummaryRespContract createPricolTransactionSummaryRespContract() {
        return new PricolTransactionSummaryRespContract();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetVinList }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "getVinList")
    public JAXBElement<GetVinList> createGetVinList(GetVinList value) {
        return new JAXBElement<GetVinList>(_GetVinList_QNAME, GetVinList.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetVinListResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "getVinListResponse")
    public JAXBElement<GetVinListResponse> createGetVinListResponse(GetVinListResponse value) {
        return new JAXBElement<GetVinListResponse>(_GetVinListResponse_QNAME, GetVinListResponse.class, null, value);
    }

}
