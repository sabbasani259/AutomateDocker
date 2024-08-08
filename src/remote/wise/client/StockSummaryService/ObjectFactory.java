
package remote.wise.client.StockSummaryService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.StockSummaryService package. 
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

    private final static QName _GetStockSummaryResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetStockSummaryResponse");
    private final static QName _GetStockSummary_QNAME = new QName("http://webservice.service.wise.remote/", "GetStockSummary");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.StockSummaryService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link StockSummaryReqContract }
     * 
     */
    public StockSummaryReqContract createStockSummaryReqContract() {
        return new StockSummaryReqContract();
    }

    /**
     * Create an instance of {@link StockSummaryRespContract }
     * 
     */
    public StockSummaryRespContract createStockSummaryRespContract() {
        return new StockSummaryRespContract();
    }

    /**
     * Create an instance of {@link GetStockSummaryResponse }
     * 
     */
    public GetStockSummaryResponse createGetStockSummaryResponse() {
        return new GetStockSummaryResponse();
    }

    /**
     * Create an instance of {@link GetStockSummary }
     * 
     */
    public GetStockSummary createGetStockSummary() {
        return new GetStockSummary();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetStockSummaryResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetStockSummaryResponse")
    public JAXBElement<GetStockSummaryResponse> createGetStockSummaryResponse(GetStockSummaryResponse value) {
        return new JAXBElement<GetStockSummaryResponse>(_GetStockSummaryResponse_QNAME, GetStockSummaryResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetStockSummary }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetStockSummary")
    public JAXBElement<GetStockSummary> createGetStockSummary(GetStockSummary value) {
        return new JAXBElement<GetStockSummary>(_GetStockSummary_QNAME, GetStockSummary.class, null, value);
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
