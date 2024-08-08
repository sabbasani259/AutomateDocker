
package remote.wise.client.UtilizationSummaryReportService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.UtilizationSummaryReportService package. 
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

    private final static QName _GetUtilizationSummaryReport_QNAME = new QName("http://webservice.service.wise.remote/", "GetUtilizationSummaryReport");
    private final static QName _GetUtilizationSummaryReportResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetUtilizationSummaryReportResponse");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.UtilizationSummaryReportService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link UtilizationSummaryReportReqContract }
     * 
     */
    public UtilizationSummaryReportReqContract createUtilizationSummaryReportReqContract() {
        return new UtilizationSummaryReportReqContract();
    }

    /**
     * Create an instance of {@link GetUtilizationSummaryReportResponse }
     * 
     */
    public GetUtilizationSummaryReportResponse createGetUtilizationSummaryReportResponse() {
        return new GetUtilizationSummaryReportResponse();
    }

    /**
     * Create an instance of {@link GetUtilizationSummaryReport }
     * 
     */
    public GetUtilizationSummaryReport createGetUtilizationSummaryReport() {
        return new GetUtilizationSummaryReport();
    }

    /**
     * Create an instance of {@link UtilizationSummaryReportRespContract }
     * 
     */
    public UtilizationSummaryReportRespContract createUtilizationSummaryReportRespContract() {
        return new UtilizationSummaryReportRespContract();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetUtilizationSummaryReport }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetUtilizationSummaryReport")
    public JAXBElement<GetUtilizationSummaryReport> createGetUtilizationSummaryReport(GetUtilizationSummaryReport value) {
        return new JAXBElement<GetUtilizationSummaryReport>(_GetUtilizationSummaryReport_QNAME, GetUtilizationSummaryReport.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetUtilizationSummaryReportResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetUtilizationSummaryReportResponse")
    public JAXBElement<GetUtilizationSummaryReportResponse> createGetUtilizationSummaryReportResponse(GetUtilizationSummaryReportResponse value) {
        return new JAXBElement<GetUtilizationSummaryReportResponse>(_GetUtilizationSummaryReportResponse_QNAME, GetUtilizationSummaryReportResponse.class, null, value);
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
