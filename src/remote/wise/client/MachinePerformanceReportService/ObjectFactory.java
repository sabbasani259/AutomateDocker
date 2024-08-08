
package remote.wise.client.MachinePerformanceReportService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.MachinePerformanceReportService package. 
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

    private final static QName _MachinePerformanceReportService_QNAME = new QName("http://webservice.service.wise.remote/", "MachinePerformanceReportService");
    private final static QName _MachinePerformanceReportServiceResponse_QNAME = new QName("http://webservice.service.wise.remote/", "MachinePerformanceReportServiceResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.MachinePerformanceReportService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link MachinePerformanceReportServiceResponse }
     * 
     */
    public MachinePerformanceReportServiceResponse createMachinePerformanceReportServiceResponse() {
        return new MachinePerformanceReportServiceResponse();
    }

    /**
     * Create an instance of {@link MachinePerformanceReportService_Type }
     * 
     */
    public MachinePerformanceReportService_Type createMachinePerformanceReportService_Type() {
        return new MachinePerformanceReportService_Type();
    }

    /**
     * Create an instance of {@link MachinePerformanceReportRespContract }
     * 
     */
    public MachinePerformanceReportRespContract createMachinePerformanceReportRespContract() {
        return new MachinePerformanceReportRespContract();
    }

    /**
     * Create an instance of {@link MachinePerformanceReportReqContract }
     * 
     */
    public MachinePerformanceReportReqContract createMachinePerformanceReportReqContract() {
        return new MachinePerformanceReportReqContract();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MachinePerformanceReportService_Type }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "MachinePerformanceReportService")
    public JAXBElement<MachinePerformanceReportService_Type> createMachinePerformanceReportService(MachinePerformanceReportService_Type value) {
        return new JAXBElement<MachinePerformanceReportService_Type>(_MachinePerformanceReportService_QNAME, MachinePerformanceReportService_Type.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MachinePerformanceReportServiceResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "MachinePerformanceReportServiceResponse")
    public JAXBElement<MachinePerformanceReportServiceResponse> createMachinePerformanceReportServiceResponse(MachinePerformanceReportServiceResponse value) {
        return new JAXBElement<MachinePerformanceReportServiceResponse>(_MachinePerformanceReportServiceResponse_QNAME, MachinePerformanceReportServiceResponse.class, null, value);
    }

}
