
package remote.wise.client.DetailedUsercommunicationReportService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.DetailedUsercommunicationReportService package. 
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

    private final static QName _GetDetailedUsercommunicationReportResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetDetailedUsercommunicationReportResponse");
    private final static QName _GetDetailedUsercommunicationReport_QNAME = new QName("http://webservice.service.wise.remote/", "GetDetailedUsercommunicationReport");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.DetailedUsercommunicationReportService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetDetailedUsercommunicationReportResponse }
     * 
     */
    public GetDetailedUsercommunicationReportResponse createGetDetailedUsercommunicationReportResponse() {
        return new GetDetailedUsercommunicationReportResponse();
    }

    /**
     * Create an instance of {@link DetailedUsercommunicationReportInputContract }
     * 
     */
    public DetailedUsercommunicationReportInputContract createDetailedUsercommunicationReportInputContract() {
        return new DetailedUsercommunicationReportInputContract();
    }

    /**
     * Create an instance of {@link DetailedUsercommunicationReportOutputContract }
     * 
     */
    public DetailedUsercommunicationReportOutputContract createDetailedUsercommunicationReportOutputContract() {
        return new DetailedUsercommunicationReportOutputContract();
    }

    /**
     * Create an instance of {@link GetDetailedUsercommunicationReport }
     * 
     */
    public GetDetailedUsercommunicationReport createGetDetailedUsercommunicationReport() {
        return new GetDetailedUsercommunicationReport();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetDetailedUsercommunicationReportResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetDetailedUsercommunicationReportResponse")
    public JAXBElement<GetDetailedUsercommunicationReportResponse> createGetDetailedUsercommunicationReportResponse(GetDetailedUsercommunicationReportResponse value) {
        return new JAXBElement<GetDetailedUsercommunicationReportResponse>(_GetDetailedUsercommunicationReportResponse_QNAME, GetDetailedUsercommunicationReportResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetDetailedUsercommunicationReport }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetDetailedUsercommunicationReport")
    public JAXBElement<GetDetailedUsercommunicationReport> createGetDetailedUsercommunicationReport(GetDetailedUsercommunicationReport value) {
        return new JAXBElement<GetDetailedUsercommunicationReport>(_GetDetailedUsercommunicationReport_QNAME, GetDetailedUsercommunicationReport.class, null, value);
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
