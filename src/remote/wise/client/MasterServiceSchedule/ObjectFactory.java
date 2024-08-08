
package remote.wise.client.MasterServiceSchedule;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.MasterServiceSchedule package. 
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

    private final static QName _GetMasterServiceScheduleResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetMasterServiceScheduleResponse");
    private final static QName _GetMasterServiceSchedule_QNAME = new QName("http://webservice.service.wise.remote/", "GetMasterServiceSchedule");
    private final static QName _SetMasterServiceScheduleServiceResponse_QNAME = new QName("http://webservice.service.wise.remote/", "SetMasterServiceScheduleServiceResponse");
    private final static QName _SetMasterServiceScheduleService_QNAME = new QName("http://webservice.service.wise.remote/", "SetMasterServiceScheduleService");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.MasterServiceSchedule
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetMasterServiceSchedule }
     * 
     */
    public GetMasterServiceSchedule createGetMasterServiceSchedule() {
        return new GetMasterServiceSchedule();
    }

    /**
     * Create an instance of {@link MasterServiceScheduleResponseContract }
     * 
     */
    public MasterServiceScheduleResponseContract createMasterServiceScheduleResponseContract() {
        return new MasterServiceScheduleResponseContract();
    }

    /**
     * Create an instance of {@link SetMasterServiceScheduleServiceResponse }
     * 
     */
    public SetMasterServiceScheduleServiceResponse createSetMasterServiceScheduleServiceResponse() {
        return new SetMasterServiceScheduleServiceResponse();
    }

    /**
     * Create an instance of {@link GetMasterServiceScheduleResponse }
     * 
     */
    public GetMasterServiceScheduleResponse createGetMasterServiceScheduleResponse() {
        return new GetMasterServiceScheduleResponse();
    }

    /**
     * Create an instance of {@link MasterServiceScheduleRequestContract }
     * 
     */
    public MasterServiceScheduleRequestContract createMasterServiceScheduleRequestContract() {
        return new MasterServiceScheduleRequestContract();
    }

    /**
     * Create an instance of {@link SetMasterServiceScheduleService }
     * 
     */
    public SetMasterServiceScheduleService createSetMasterServiceScheduleService() {
        return new SetMasterServiceScheduleService();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetMasterServiceScheduleResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetMasterServiceScheduleResponse")
    public JAXBElement<GetMasterServiceScheduleResponse> createGetMasterServiceScheduleResponse(GetMasterServiceScheduleResponse value) {
        return new JAXBElement<GetMasterServiceScheduleResponse>(_GetMasterServiceScheduleResponse_QNAME, GetMasterServiceScheduleResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetMasterServiceSchedule }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetMasterServiceSchedule")
    public JAXBElement<GetMasterServiceSchedule> createGetMasterServiceSchedule(GetMasterServiceSchedule value) {
        return new JAXBElement<GetMasterServiceSchedule>(_GetMasterServiceSchedule_QNAME, GetMasterServiceSchedule.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetMasterServiceScheduleServiceResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "SetMasterServiceScheduleServiceResponse")
    public JAXBElement<SetMasterServiceScheduleServiceResponse> createSetMasterServiceScheduleServiceResponse(SetMasterServiceScheduleServiceResponse value) {
        return new JAXBElement<SetMasterServiceScheduleServiceResponse>(_SetMasterServiceScheduleServiceResponse_QNAME, SetMasterServiceScheduleServiceResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetMasterServiceScheduleService }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "SetMasterServiceScheduleService")
    public JAXBElement<SetMasterServiceScheduleService> createSetMasterServiceScheduleService(SetMasterServiceScheduleService value) {
        return new JAXBElement<SetMasterServiceScheduleService>(_SetMasterServiceScheduleService_QNAME, SetMasterServiceScheduleService.class, null, value);
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
