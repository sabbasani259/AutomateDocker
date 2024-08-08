
package remote.wise.client.MachineBillingReportService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.MachineBillingReportService package. 
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

    private final static QName _GetMachineBillingReportResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetMachineBillingReportResponse");
    private final static QName _GetMachineBillingReport_QNAME = new QName("http://webservice.service.wise.remote/", "GetMachineBillingReport");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.MachineBillingReportService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link MachineBillingReportOutputContract }
     * 
     */
    public MachineBillingReportOutputContract createMachineBillingReportOutputContract() {
        return new MachineBillingReportOutputContract();
    }

    /**
     * Create an instance of {@link GetMachineBillingReport }
     * 
     */
    public GetMachineBillingReport createGetMachineBillingReport() {
        return new GetMachineBillingReport();
    }

    /**
     * Create an instance of {@link MachineBillingReportInputContract }
     * 
     */
    public MachineBillingReportInputContract createMachineBillingReportInputContract() {
        return new MachineBillingReportInputContract();
    }

    /**
     * Create an instance of {@link GetMachineBillingReportResponse }
     * 
     */
    public GetMachineBillingReportResponse createGetMachineBillingReportResponse() {
        return new GetMachineBillingReportResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetMachineBillingReportResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetMachineBillingReportResponse")
    public JAXBElement<GetMachineBillingReportResponse> createGetMachineBillingReportResponse(GetMachineBillingReportResponse value) {
        return new JAXBElement<GetMachineBillingReportResponse>(_GetMachineBillingReportResponse_QNAME, GetMachineBillingReportResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetMachineBillingReport }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetMachineBillingReport")
    public JAXBElement<GetMachineBillingReport> createGetMachineBillingReport(GetMachineBillingReport value) {
        return new JAXBElement<GetMachineBillingReport>(_GetMachineBillingReport_QNAME, GetMachineBillingReport.class, null, value);
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
