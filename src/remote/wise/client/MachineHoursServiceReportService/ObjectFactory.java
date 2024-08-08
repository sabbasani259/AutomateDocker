
package remote.wise.client.MachineHoursServiceReportService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.MachineHoursServiceReportService package. 
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

    private final static QName _GetMachineHoursServiceReportService_QNAME = new QName("http://webservice.service.wise.remote/", "GetMachineHoursServiceReportService");
    private final static QName _GetMachineHoursServiceReportServiceResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetMachineHoursServiceReportServiceResponse");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.MachineHoursServiceReportService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link MachineHoursServiceReportRespContract }
     * 
     */
    public MachineHoursServiceReportRespContract createMachineHoursServiceReportRespContract() {
        return new MachineHoursServiceReportRespContract();
    }

    /**
     * Create an instance of {@link GetMachineHoursServiceReportServiceResponse }
     * 
     */
    public GetMachineHoursServiceReportServiceResponse createGetMachineHoursServiceReportServiceResponse() {
        return new GetMachineHoursServiceReportServiceResponse();
    }

    /**
     * Create an instance of {@link GetMachineHoursServiceReportService }
     * 
     */
    public GetMachineHoursServiceReportService createGetMachineHoursServiceReportService() {
        return new GetMachineHoursServiceReportService();
    }

    /**
     * Create an instance of {@link MachineHoursServiceReportReqContract }
     * 
     */
    public MachineHoursServiceReportReqContract createMachineHoursServiceReportReqContract() {
        return new MachineHoursServiceReportReqContract();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetMachineHoursServiceReportService }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetMachineHoursServiceReportService")
    public JAXBElement<GetMachineHoursServiceReportService> createGetMachineHoursServiceReportService(GetMachineHoursServiceReportService value) {
        return new JAXBElement<GetMachineHoursServiceReportService>(_GetMachineHoursServiceReportService_QNAME, GetMachineHoursServiceReportService.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetMachineHoursServiceReportServiceResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetMachineHoursServiceReportServiceResponse")
    public JAXBElement<GetMachineHoursServiceReportServiceResponse> createGetMachineHoursServiceReportServiceResponse(GetMachineHoursServiceReportServiceResponse value) {
        return new JAXBElement<GetMachineHoursServiceReportServiceResponse>(_GetMachineHoursServiceReportServiceResponse_QNAME, GetMachineHoursServiceReportServiceResponse.class, null, value);
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
