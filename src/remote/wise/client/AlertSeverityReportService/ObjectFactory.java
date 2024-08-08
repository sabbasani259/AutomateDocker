
package remote.wise.client.AlertSeverityReportService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.AlertSeverityReportService package. 
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

    private final static QName _AlertSeverityReportResponse_QNAME = new QName("http://webservice.service.wise.remote/", "AlertSeverityReportResponse");
    private final static QName _AlertSeverityReport_QNAME = new QName("http://webservice.service.wise.remote/", "AlertSeverityReport");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.AlertSeverityReportService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link AlertSeverityReportRespContract }
     * 
     */
    public AlertSeverityReportRespContract createAlertSeverityReportRespContract() {
        return new AlertSeverityReportRespContract();
    }

    /**
     * Create an instance of {@link AlertSeverityReportReqContract }
     * 
     */
    public AlertSeverityReportReqContract createAlertSeverityReportReqContract() {
        return new AlertSeverityReportReqContract();
    }

    /**
     * Create an instance of {@link AlertSeverityReportResponse }
     * 
     */
    public AlertSeverityReportResponse createAlertSeverityReportResponse() {
        return new AlertSeverityReportResponse();
    }

    /**
     * Create an instance of {@link AlertSeverityReport }
     * 
     */
    public AlertSeverityReport createAlertSeverityReport() {
        return new AlertSeverityReport();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AlertSeverityReportResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "AlertSeverityReportResponse")
    public JAXBElement<AlertSeverityReportResponse> createAlertSeverityReportResponse(AlertSeverityReportResponse value) {
        return new JAXBElement<AlertSeverityReportResponse>(_AlertSeverityReportResponse_QNAME, AlertSeverityReportResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AlertSeverityReport }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "AlertSeverityReport")
    public JAXBElement<AlertSeverityReport> createAlertSeverityReport(AlertSeverityReport value) {
        return new JAXBElement<AlertSeverityReport>(_AlertSeverityReport_QNAME, AlertSeverityReport.class, null, value);
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
