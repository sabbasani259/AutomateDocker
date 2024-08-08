
package remote.wise.client.ServiceScheduleService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.ServiceScheduleService package. 
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

    private final static QName _GetServiceScheduleService_QNAME = new QName("http://webservice.service.wise.remote/", "GetServiceScheduleService");
    private final static QName _GetServiceScheduleServiceResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetServiceScheduleServiceResponse");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.ServiceScheduleService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetServiceScheduleService }
     * 
     */
    public GetServiceScheduleService createGetServiceScheduleService() {
        return new GetServiceScheduleService();
    }

    /**
     * Create an instance of {@link ServiceScheduleRespContract }
     * 
     */
    public ServiceScheduleRespContract createServiceScheduleRespContract() {
        return new ServiceScheduleRespContract();
    }

    /**
     * Create an instance of {@link GetServiceScheduleServiceResponse }
     * 
     */
    public GetServiceScheduleServiceResponse createGetServiceScheduleServiceResponse() {
        return new GetServiceScheduleServiceResponse();
    }

    /**
     * Create an instance of {@link ServiceScheduleReqContract }
     * 
     */
    public ServiceScheduleReqContract createServiceScheduleReqContract() {
        return new ServiceScheduleReqContract();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetServiceScheduleService }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetServiceScheduleService")
    public JAXBElement<GetServiceScheduleService> createGetServiceScheduleService(GetServiceScheduleService value) {
        return new JAXBElement<GetServiceScheduleService>(_GetServiceScheduleService_QNAME, GetServiceScheduleService.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetServiceScheduleServiceResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetServiceScheduleServiceResponse")
    public JAXBElement<GetServiceScheduleServiceResponse> createGetServiceScheduleServiceResponse(GetServiceScheduleServiceResponse value) {
        return new JAXBElement<GetServiceScheduleServiceResponse>(_GetServiceScheduleServiceResponse_QNAME, GetServiceScheduleServiceResponse.class, null, value);
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
