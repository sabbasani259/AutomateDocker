
package remote.wise.client.MachineActivityReportService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.MachineActivityReportService package. 
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

    private final static QName _GetMachineActivityReportResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetMachineActivityReportResponse");
    private final static QName _GetMachineActivityReport_QNAME = new QName("http://webservice.service.wise.remote/", "GetMachineActivityReport");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.MachineActivityReportService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link MachineActivityReportRespContract }
     * 
     */
    public MachineActivityReportRespContract createMachineActivityReportRespContract() {
        return new MachineActivityReportRespContract();
    }

    /**
     * Create an instance of {@link GetMachineActivityReport }
     * 
     */
    public GetMachineActivityReport createGetMachineActivityReport() {
        return new GetMachineActivityReport();
    }

    /**
     * Create an instance of {@link GetMachineActivityReportResponse }
     * 
     */
    public GetMachineActivityReportResponse createGetMachineActivityReportResponse() {
        return new GetMachineActivityReportResponse();
    }

    /**
     * Create an instance of {@link MachineActivityReportReqContract }
     * 
     */
    public MachineActivityReportReqContract createMachineActivityReportReqContract() {
        return new MachineActivityReportReqContract();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetMachineActivityReportResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetMachineActivityReportResponse")
    public JAXBElement<GetMachineActivityReportResponse> createGetMachineActivityReportResponse(GetMachineActivityReportResponse value) {
        return new JAXBElement<GetMachineActivityReportResponse>(_GetMachineActivityReportResponse_QNAME, GetMachineActivityReportResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetMachineActivityReport }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetMachineActivityReport")
    public JAXBElement<GetMachineActivityReport> createGetMachineActivityReport(GetMachineActivityReport value) {
        return new JAXBElement<GetMachineActivityReport>(_GetMachineActivityReport_QNAME, GetMachineActivityReport.class, null, value);
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
