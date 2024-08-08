
package remote.wise.client.FleetSummaryService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.FleetSummaryService package. 
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

    private final static QName _GetFleetSummaryServiceResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetFleetSummaryServiceResponse");
    private final static QName _GetFleetSummaryService_QNAME = new QName("http://webservice.service.wise.remote/", "GetFleetSummaryService");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.FleetSummaryService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetFleetSummaryService }
     * 
     */
    public GetFleetSummaryService createGetFleetSummaryService() {
        return new GetFleetSummaryService();
    }

    /**
     * Create an instance of {@link GetFleetSummaryServiceResponse }
     * 
     */
    public GetFleetSummaryServiceResponse createGetFleetSummaryServiceResponse() {
        return new GetFleetSummaryServiceResponse();
    }

    /**
     * Create an instance of {@link FleetSummaryReqContract }
     * 
     */
    public FleetSummaryReqContract createFleetSummaryReqContract() {
        return new FleetSummaryReqContract();
    }

    /**
     * Create an instance of {@link FleetSummaryRespContract }
     * 
     */
    public FleetSummaryRespContract createFleetSummaryRespContract() {
        return new FleetSummaryRespContract();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetFleetSummaryServiceResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetFleetSummaryServiceResponse")
    public JAXBElement<GetFleetSummaryServiceResponse> createGetFleetSummaryServiceResponse(GetFleetSummaryServiceResponse value) {
        return new JAXBElement<GetFleetSummaryServiceResponse>(_GetFleetSummaryServiceResponse_QNAME, GetFleetSummaryServiceResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetFleetSummaryService }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetFleetSummaryService")
    public JAXBElement<GetFleetSummaryService> createGetFleetSummaryService(GetFleetSummaryService value) {
        return new JAXBElement<GetFleetSummaryService>(_GetFleetSummaryService_QNAME, GetFleetSummaryService.class, null, value);
    }

}
