
package remote.wise.client.FleetSummaryReportService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.FleetSummaryReportService package. 
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

    private final static QName _GetFleetSummaryReportServiceResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetFleetSummaryReportServiceResponse");
    private final static QName _GetFleetSummaryReportService_QNAME = new QName("http://webservice.service.wise.remote/", "GetFleetSummaryReportService");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.FleetSummaryReportService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link FleetSummaryReportReqContract }
     * 
     */
    public FleetSummaryReportReqContract createFleetSummaryReportReqContract() {
        return new FleetSummaryReportReqContract();
    }

    /**
     * Create an instance of {@link GetFleetSummaryReportService }
     * 
     */
    public GetFleetSummaryReportService createGetFleetSummaryReportService() {
        return new GetFleetSummaryReportService();
    }

    /**
     * Create an instance of {@link FleetSummaryReportRespContract }
     * 
     */
    public FleetSummaryReportRespContract createFleetSummaryReportRespContract() {
        return new FleetSummaryReportRespContract();
    }

    /**
     * Create an instance of {@link GetFleetSummaryReportServiceResponse }
     * 
     */
    public GetFleetSummaryReportServiceResponse createGetFleetSummaryReportServiceResponse() {
        return new GetFleetSummaryReportServiceResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetFleetSummaryReportServiceResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetFleetSummaryReportServiceResponse")
    public JAXBElement<GetFleetSummaryReportServiceResponse> createGetFleetSummaryReportServiceResponse(GetFleetSummaryReportServiceResponse value) {
        return new JAXBElement<GetFleetSummaryReportServiceResponse>(_GetFleetSummaryReportServiceResponse_QNAME, GetFleetSummaryReportServiceResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetFleetSummaryReportService }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetFleetSummaryReportService")
    public JAXBElement<GetFleetSummaryReportService> createGetFleetSummaryReportService(GetFleetSummaryReportService value) {
        return new JAXBElement<GetFleetSummaryReportService>(_GetFleetSummaryReportService_QNAME, GetFleetSummaryReportService.class, null, value);
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
