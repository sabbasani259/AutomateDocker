
package remote.wise.client.AssetEventLogService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.AssetEventLogService package. 
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

    private final static QName _GetAssetEventLog_QNAME = new QName("http://webservice.service.wise.remote/", "GetAssetEventLog");
    private final static QName _GetAssetEventLogResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetAssetEventLogResponse");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.AssetEventLogService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link AssetEventLogReqContract }
     * 
     */
    public AssetEventLogReqContract createAssetEventLogReqContract() {
        return new AssetEventLogReqContract();
    }

    /**
     * Create an instance of {@link GetAssetEventLog }
     * 
     */
    public GetAssetEventLog createGetAssetEventLog() {
        return new GetAssetEventLog();
    }

    /**
     * Create an instance of {@link AssetEventRespContract }
     * 
     */
    public AssetEventRespContract createAssetEventRespContract() {
        return new AssetEventRespContract();
    }

    /**
     * Create an instance of {@link GetAssetEventLogResponse }
     * 
     */
    public GetAssetEventLogResponse createGetAssetEventLogResponse() {
        return new GetAssetEventLogResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAssetEventLog }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetAssetEventLog")
    public JAXBElement<GetAssetEventLog> createGetAssetEventLog(GetAssetEventLog value) {
        return new JAXBElement<GetAssetEventLog>(_GetAssetEventLog_QNAME, GetAssetEventLog.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAssetEventLogResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetAssetEventLogResponse")
    public JAXBElement<GetAssetEventLogResponse> createGetAssetEventLogResponse(GetAssetEventLogResponse value) {
        return new JAXBElement<GetAssetEventLogResponse>(_GetAssetEventLogResponse_QNAME, GetAssetEventLogResponse.class, null, value);
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
