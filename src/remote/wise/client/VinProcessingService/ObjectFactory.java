
package remote.wise.client.VinProcessingService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.VinProcessingService package. 
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

    private final static QName _VinSearchResponse_QNAME = new QName("http://webservice.service.wise.remote/", "VinSearchResponse");
    private final static QName _VinSearch_QNAME = new QName("http://webservice.service.wise.remote/", "VinSearch");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.VinProcessingService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link VinSearchResponse }
     * 
     */
    public VinSearchResponse createVinSearchResponse() {
        return new VinSearchResponse();
    }

    /**
     * Create an instance of {@link VinSearchReqContract }
     * 
     */
    public VinSearchReqContract createVinSearchReqContract() {
        return new VinSearchReqContract();
    }

    /**
     * Create an instance of {@link VinSearchRespContract }
     * 
     */
    public VinSearchRespContract createVinSearchRespContract() {
        return new VinSearchRespContract();
    }

    /**
     * Create an instance of {@link VinSearch }
     * 
     */
    public VinSearch createVinSearch() {
        return new VinSearch();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link VinSearchResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "VinSearchResponse")
    public JAXBElement<VinSearchResponse> createVinSearchResponse(VinSearchResponse value) {
        return new JAXBElement<VinSearchResponse>(_VinSearchResponse_QNAME, VinSearchResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link VinSearch }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "VinSearch")
    public JAXBElement<VinSearch> createVinSearch(VinSearch value) {
        return new JAXBElement<VinSearch>(_VinSearch_QNAME, VinSearch.class, null, value);
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
