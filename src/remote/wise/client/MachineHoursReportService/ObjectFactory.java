
package remote.wise.client.MachineHoursReportService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.MachineHoursReportService package. 
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

    private final static QName _GetMachineHoursReportServiceResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetMachineHoursReportServiceResponse");
    private final static QName _GetMachineHoursReportService_QNAME = new QName("http://webservice.service.wise.remote/", "GetMachineHoursReportService");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.MachineHoursReportService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link MachineHoursReportReqContract }
     * 
     */
    public MachineHoursReportReqContract createMachineHoursReportReqContract() {
        return new MachineHoursReportReqContract();
    }

    /**
     * Create an instance of {@link MachineHoursReportRespContract }
     * 
     */
    public MachineHoursReportRespContract createMachineHoursReportRespContract() {
        return new MachineHoursReportRespContract();
    }

    /**
     * Create an instance of {@link GetMachineHoursReportServiceResponse }
     * 
     */
    public GetMachineHoursReportServiceResponse createGetMachineHoursReportServiceResponse() {
        return new GetMachineHoursReportServiceResponse();
    }

    /**
     * Create an instance of {@link GetMachineHoursReportService }
     * 
     */
    public GetMachineHoursReportService createGetMachineHoursReportService() {
        return new GetMachineHoursReportService();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetMachineHoursReportServiceResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetMachineHoursReportServiceResponse")
    public JAXBElement<GetMachineHoursReportServiceResponse> createGetMachineHoursReportServiceResponse(GetMachineHoursReportServiceResponse value) {
        return new JAXBElement<GetMachineHoursReportServiceResponse>(_GetMachineHoursReportServiceResponse_QNAME, GetMachineHoursReportServiceResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetMachineHoursReportService }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetMachineHoursReportService")
    public JAXBElement<GetMachineHoursReportService> createGetMachineHoursReportService(GetMachineHoursReportService value) {
        return new JAXBElement<GetMachineHoursReportService>(_GetMachineHoursReportService_QNAME, GetMachineHoursReportService.class, null, value);
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
