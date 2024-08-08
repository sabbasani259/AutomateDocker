
package remote.wise.client.MachineServiceDueOverDueReportService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.MachineServiceDueOverDueReportService package. 
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

    private final static QName _GetMachineServiceDueOverDueCountResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetMachineServiceDueOverDueCountResponse");
    private final static QName _GetMachineServiceDueOverDueCount_QNAME = new QName("http://webservice.service.wise.remote/", "GetMachineServiceDueOverDueCount");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.MachineServiceDueOverDueReportService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetMachineServiceDueOverDueCount }
     * 
     */
    public GetMachineServiceDueOverDueCount createGetMachineServiceDueOverDueCount() {
        return new GetMachineServiceDueOverDueCount();
    }

    /**
     * Create an instance of {@link MachineServiceDueOverDueRespContract }
     * 
     */
    public MachineServiceDueOverDueRespContract createMachineServiceDueOverDueRespContract() {
        return new MachineServiceDueOverDueRespContract();
    }

    /**
     * Create an instance of {@link GetMachineServiceDueOverDueCountResponse }
     * 
     */
    public GetMachineServiceDueOverDueCountResponse createGetMachineServiceDueOverDueCountResponse() {
        return new GetMachineServiceDueOverDueCountResponse();
    }

    /**
     * Create an instance of {@link MachineServiceDueOverDueReqContract }
     * 
     */
    public MachineServiceDueOverDueReqContract createMachineServiceDueOverDueReqContract() {
        return new MachineServiceDueOverDueReqContract();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetMachineServiceDueOverDueCountResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetMachineServiceDueOverDueCountResponse")
    public JAXBElement<GetMachineServiceDueOverDueCountResponse> createGetMachineServiceDueOverDueCountResponse(GetMachineServiceDueOverDueCountResponse value) {
        return new JAXBElement<GetMachineServiceDueOverDueCountResponse>(_GetMachineServiceDueOverDueCountResponse_QNAME, GetMachineServiceDueOverDueCountResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetMachineServiceDueOverDueCount }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetMachineServiceDueOverDueCount")
    public JAXBElement<GetMachineServiceDueOverDueCount> createGetMachineServiceDueOverDueCount(GetMachineServiceDueOverDueCount value) {
        return new JAXBElement<GetMachineServiceDueOverDueCount>(_GetMachineServiceDueOverDueCount_QNAME, GetMachineServiceDueOverDueCount.class, null, value);
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
