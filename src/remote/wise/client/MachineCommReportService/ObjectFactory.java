
package remote.wise.client.MachineCommReportService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.MachineCommReportService package. 
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

    private final static QName _GetMachineCommReportResponse_QNAME = new QName("http://webservice.service.wise.remote/", "getMachineCommReportResponse");
    private final static QName _GetMachineCommReport_QNAME = new QName("http://webservice.service.wise.remote/", "getMachineCommReport");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.MachineCommReportService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetMachineCommReport }
     * 
     */
    public GetMachineCommReport createGetMachineCommReport() {
        return new GetMachineCommReport();
    }

    /**
     * Create an instance of {@link MachineCommReportRespContract }
     * 
     */
    public MachineCommReportRespContract createMachineCommReportRespContract() {
        return new MachineCommReportRespContract();
    }

    /**
     * Create an instance of {@link GetMachineCommReportResponse }
     * 
     */
    public GetMachineCommReportResponse createGetMachineCommReportResponse() {
        return new GetMachineCommReportResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetMachineCommReportResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "getMachineCommReportResponse")
    public JAXBElement<GetMachineCommReportResponse> createGetMachineCommReportResponse(GetMachineCommReportResponse value) {
        return new JAXBElement<GetMachineCommReportResponse>(_GetMachineCommReportResponse_QNAME, GetMachineCommReportResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetMachineCommReport }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "getMachineCommReport")
    public JAXBElement<GetMachineCommReport> createGetMachineCommReport(GetMachineCommReport value) {
        return new JAXBElement<GetMachineCommReport>(_GetMachineCommReport_QNAME, GetMachineCommReport.class, null, value);
    }

}
