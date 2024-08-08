
package remote.wise.client.AssetServiceScheduleService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.AssetServiceScheduleService package. 
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

    private final static QName _SetAssetServiceScheduleServiceResponse_QNAME = new QName("http://webservice.service.wise.remote/", "SetAssetServiceScheduleServiceResponse");
    private final static QName _GetAssetServiceScheduleService_QNAME = new QName("http://webservice.service.wise.remote/", "GetAssetServiceScheduleService");
    private final static QName _SetAssetServiceScheduleService_QNAME = new QName("http://webservice.service.wise.remote/", "SetAssetServiceScheduleService");
    private final static QName _GetAssetServiceScheduleServiceResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetAssetServiceScheduleServiceResponse");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.AssetServiceScheduleService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link AssetServiceScheduleGetRespContract }
     * 
     */
    public AssetServiceScheduleGetRespContract createAssetServiceScheduleGetRespContract() {
        return new AssetServiceScheduleGetRespContract();
    }

    /**
     * Create an instance of {@link GetAssetServiceScheduleService }
     * 
     */
    public GetAssetServiceScheduleService createGetAssetServiceScheduleService() {
        return new GetAssetServiceScheduleService();
    }

    /**
     * Create an instance of {@link AssetServiceScheduleRespContract }
     * 
     */
    public AssetServiceScheduleRespContract createAssetServiceScheduleRespContract() {
        return new AssetServiceScheduleRespContract();
    }

    /**
     * Create an instance of {@link SetAssetServiceScheduleService }
     * 
     */
    public SetAssetServiceScheduleService createSetAssetServiceScheduleService() {
        return new SetAssetServiceScheduleService();
    }

    /**
     * Create an instance of {@link AssetServiceScheduleGetReqContract }
     * 
     */
    public AssetServiceScheduleGetReqContract createAssetServiceScheduleGetReqContract() {
        return new AssetServiceScheduleGetReqContract();
    }

    /**
     * Create an instance of {@link SetAssetServiceScheduleServiceResponse }
     * 
     */
    public SetAssetServiceScheduleServiceResponse createSetAssetServiceScheduleServiceResponse() {
        return new SetAssetServiceScheduleServiceResponse();
    }

    /**
     * Create an instance of {@link GetAssetServiceScheduleServiceResponse }
     * 
     */
    public GetAssetServiceScheduleServiceResponse createGetAssetServiceScheduleServiceResponse() {
        return new GetAssetServiceScheduleServiceResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetAssetServiceScheduleServiceResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "SetAssetServiceScheduleServiceResponse")
    public JAXBElement<SetAssetServiceScheduleServiceResponse> createSetAssetServiceScheduleServiceResponse(SetAssetServiceScheduleServiceResponse value) {
        return new JAXBElement<SetAssetServiceScheduleServiceResponse>(_SetAssetServiceScheduleServiceResponse_QNAME, SetAssetServiceScheduleServiceResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAssetServiceScheduleService }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetAssetServiceScheduleService")
    public JAXBElement<GetAssetServiceScheduleService> createGetAssetServiceScheduleService(GetAssetServiceScheduleService value) {
        return new JAXBElement<GetAssetServiceScheduleService>(_GetAssetServiceScheduleService_QNAME, GetAssetServiceScheduleService.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetAssetServiceScheduleService }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "SetAssetServiceScheduleService")
    public JAXBElement<SetAssetServiceScheduleService> createSetAssetServiceScheduleService(SetAssetServiceScheduleService value) {
        return new JAXBElement<SetAssetServiceScheduleService>(_SetAssetServiceScheduleService_QNAME, SetAssetServiceScheduleService.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAssetServiceScheduleServiceResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetAssetServiceScheduleServiceResponse")
    public JAXBElement<GetAssetServiceScheduleServiceResponse> createGetAssetServiceScheduleServiceResponse(GetAssetServiceScheduleServiceResponse value) {
        return new JAXBElement<GetAssetServiceScheduleServiceResponse>(_GetAssetServiceScheduleServiceResponse_QNAME, GetAssetServiceScheduleServiceResponse.class, null, value);
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
