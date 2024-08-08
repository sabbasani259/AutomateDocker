
package remote.wise.client.MachineProfileService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.MachineProfileService package. 
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

    private final static QName _GetMachineProfileServiceResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetMachineProfileServiceResponse");
    private final static QName _SetMachineProfileService_QNAME = new QName("http://webservice.service.wise.remote/", "SetMachineProfileService");
    private final static QName _SetMachineProfileServiceResponse_QNAME = new QName("http://webservice.service.wise.remote/", "SetMachineProfileServiceResponse");
    private final static QName _GetMachineProfileService_QNAME = new QName("http://webservice.service.wise.remote/", "GetMachineProfileService");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.MachineProfileService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link MachineProfileRespContract }
     * 
     */
    public MachineProfileRespContract createMachineProfileRespContract() {
        return new MachineProfileRespContract();
    }

    /**
     * Create an instance of {@link GetMachineProfileServiceResponse }
     * 
     */
    public GetMachineProfileServiceResponse createGetMachineProfileServiceResponse() {
        return new GetMachineProfileServiceResponse();
    }

    /**
     * Create an instance of {@link MachineProfileReqContract }
     * 
     */
    public MachineProfileReqContract createMachineProfileReqContract() {
        return new MachineProfileReqContract();
    }

    /**
     * Create an instance of {@link GetMachineProfileService }
     * 
     */
    public GetMachineProfileService createGetMachineProfileService() {
        return new GetMachineProfileService();
    }

    /**
     * Create an instance of {@link SetMachineProfileServiceResponse }
     * 
     */
    public SetMachineProfileServiceResponse createSetMachineProfileServiceResponse() {
        return new SetMachineProfileServiceResponse();
    }

    /**
     * Create an instance of {@link SetMachineProfileService }
     * 
     */
    public SetMachineProfileService createSetMachineProfileService() {
        return new SetMachineProfileService();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetMachineProfileServiceResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetMachineProfileServiceResponse")
    public JAXBElement<GetMachineProfileServiceResponse> createGetMachineProfileServiceResponse(GetMachineProfileServiceResponse value) {
        return new JAXBElement<GetMachineProfileServiceResponse>(_GetMachineProfileServiceResponse_QNAME, GetMachineProfileServiceResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetMachineProfileService }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "SetMachineProfileService")
    public JAXBElement<SetMachineProfileService> createSetMachineProfileService(SetMachineProfileService value) {
        return new JAXBElement<SetMachineProfileService>(_SetMachineProfileService_QNAME, SetMachineProfileService.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetMachineProfileServiceResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "SetMachineProfileServiceResponse")
    public JAXBElement<SetMachineProfileServiceResponse> createSetMachineProfileServiceResponse(SetMachineProfileServiceResponse value) {
        return new JAXBElement<SetMachineProfileServiceResponse>(_SetMachineProfileServiceResponse_QNAME, SetMachineProfileServiceResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetMachineProfileService }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetMachineProfileService")
    public JAXBElement<GetMachineProfileService> createGetMachineProfileService(GetMachineProfileService value) {
        return new JAXBElement<GetMachineProfileService>(_GetMachineProfileService_QNAME, GetMachineProfileService.class, null, value);
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
