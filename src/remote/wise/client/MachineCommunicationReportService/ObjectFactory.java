
package remote.wise.client.MachineCommunicationReportService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.MachineCommunicationReportService package. 
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

    private final static QName _GetMachineCommunicationReport_QNAME = new QName("http://webservice.service.wise.remote/", "GetMachineCommunicationReport");
    private final static QName _GetMachineCommunicationReportResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetMachineCommunicationReportResponse");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.MachineCommunicationReportService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetMachineCommunicationReportResponse }
     * 
     */
    public GetMachineCommunicationReportResponse createGetMachineCommunicationReportResponse() {
        return new GetMachineCommunicationReportResponse();
    }

    /**
     * Create an instance of {@link MachineCommunicationInputContract }
     * 
     */
    public MachineCommunicationInputContract createMachineCommunicationInputContract() {
        return new MachineCommunicationInputContract();
    }

    /**
     * Create an instance of {@link GetMachineCommunicationReport }
     * 
     */
    public GetMachineCommunicationReport createGetMachineCommunicationReport() {
        return new GetMachineCommunicationReport();
    }

    /**
     * Create an instance of {@link MachineCommunicationOutputContract }
     * 
     */
    public MachineCommunicationOutputContract createMachineCommunicationOutputContract() {
        return new MachineCommunicationOutputContract();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetMachineCommunicationReport }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetMachineCommunicationReport")
    public JAXBElement<GetMachineCommunicationReport> createGetMachineCommunicationReport(GetMachineCommunicationReport value) {
        return new JAXBElement<GetMachineCommunicationReport>(_GetMachineCommunicationReport_QNAME, GetMachineCommunicationReport.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetMachineCommunicationReportResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetMachineCommunicationReportResponse")
    public JAXBElement<GetMachineCommunicationReportResponse> createGetMachineCommunicationReportResponse(GetMachineCommunicationReportResponse value) {
        return new JAXBElement<GetMachineCommunicationReportResponse>(_GetMachineCommunicationReportResponse_QNAME, GetMachineCommunicationReportResponse.class, null, value);
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
