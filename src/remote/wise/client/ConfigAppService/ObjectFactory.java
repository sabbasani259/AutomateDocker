
package remote.wise.client.ConfigAppService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.ConfigAppService package. 
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

    private final static QName _GetConfigAppServiceResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetConfigAppServiceResponse");
    private final static QName _SetConfigAppService_QNAME = new QName("http://webservice.service.wise.remote/", "SetConfigAppService");
    private final static QName _GetConfigAppService_QNAME = new QName("http://webservice.service.wise.remote/", "GetConfigAppService");
    private final static QName _SetConfigAppServiceResponse_QNAME = new QName("http://webservice.service.wise.remote/", "SetConfigAppServiceResponse");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.ConfigAppService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ConfigAppReqContract }
     * 
     */
    public ConfigAppReqContract createConfigAppReqContract() {
        return new ConfigAppReqContract();
    }

    /**
     * Create an instance of {@link SetConfigAppService }
     * 
     */
    public SetConfigAppService createSetConfigAppService() {
        return new SetConfigAppService();
    }

    /**
     * Create an instance of {@link ConfigAppRespContract }
     * 
     */
    public ConfigAppRespContract createConfigAppRespContract() {
        return new ConfigAppRespContract();
    }

    /**
     * Create an instance of {@link SetConfigAppServiceResponse }
     * 
     */
    public SetConfigAppServiceResponse createSetConfigAppServiceResponse() {
        return new SetConfigAppServiceResponse();
    }

    /**
     * Create an instance of {@link GetConfigAppService }
     * 
     */
    public GetConfigAppService createGetConfigAppService() {
        return new GetConfigAppService();
    }

    /**
     * Create an instance of {@link GetConfigAppServiceResponse }
     * 
     */
    public GetConfigAppServiceResponse createGetConfigAppServiceResponse() {
        return new GetConfigAppServiceResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetConfigAppServiceResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetConfigAppServiceResponse")
    public JAXBElement<GetConfigAppServiceResponse> createGetConfigAppServiceResponse(GetConfigAppServiceResponse value) {
        return new JAXBElement<GetConfigAppServiceResponse>(_GetConfigAppServiceResponse_QNAME, GetConfigAppServiceResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetConfigAppService }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "SetConfigAppService")
    public JAXBElement<SetConfigAppService> createSetConfigAppService(SetConfigAppService value) {
        return new JAXBElement<SetConfigAppService>(_SetConfigAppService_QNAME, SetConfigAppService.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetConfigAppService }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetConfigAppService")
    public JAXBElement<GetConfigAppService> createGetConfigAppService(GetConfigAppService value) {
        return new JAXBElement<GetConfigAppService>(_GetConfigAppService_QNAME, GetConfigAppService.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetConfigAppServiceResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "SetConfigAppServiceResponse")
    public JAXBElement<SetConfigAppServiceResponse> createSetConfigAppServiceResponse(SetConfigAppServiceResponse value) {
        return new JAXBElement<SetConfigAppServiceResponse>(_SetConfigAppServiceResponse_QNAME, SetConfigAppServiceResponse.class, null, value);
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
