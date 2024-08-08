
package remote.wise.client.AssetOwnershipManagementService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.AssetOwnershipManagementService package. 
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

    private final static QName _SetAssetOwnership_QNAME = new QName("http://webservice.service.wise.remote/", "SetAssetOwnership");
    private final static QName _SetAssetOwnershipResponse_QNAME = new QName("http://webservice.service.wise.remote/", "SetAssetOwnershipResponse");
    private final static QName _GetAssetOwnership_QNAME = new QName("http://webservice.service.wise.remote/", "GetAssetOwnership");
    private final static QName _GetAssetOwnershipResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetAssetOwnershipResponse");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.AssetOwnershipManagementService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link AssetOwnershipRespContract }
     * 
     */
    public AssetOwnershipRespContract createAssetOwnershipRespContract() {
        return new AssetOwnershipRespContract();
    }

    /**
     * Create an instance of {@link GetAssetOwnership }
     * 
     */
    public GetAssetOwnership createGetAssetOwnership() {
        return new GetAssetOwnership();
    }

    /**
     * Create an instance of {@link AssetOwnershipReqContract }
     * 
     */
    public AssetOwnershipReqContract createAssetOwnershipReqContract() {
        return new AssetOwnershipReqContract();
    }

    /**
     * Create an instance of {@link SetAssetOwnershipResponse }
     * 
     */
    public SetAssetOwnershipResponse createSetAssetOwnershipResponse() {
        return new SetAssetOwnershipResponse();
    }

    /**
     * Create an instance of {@link GetAssetOwnershipResponse }
     * 
     */
    public GetAssetOwnershipResponse createGetAssetOwnershipResponse() {
        return new GetAssetOwnershipResponse();
    }

    /**
     * Create an instance of {@link SetAssetOwnership }
     * 
     */
    public SetAssetOwnership createSetAssetOwnership() {
        return new SetAssetOwnership();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetAssetOwnership }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "SetAssetOwnership")
    public JAXBElement<SetAssetOwnership> createSetAssetOwnership(SetAssetOwnership value) {
        return new JAXBElement<SetAssetOwnership>(_SetAssetOwnership_QNAME, SetAssetOwnership.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetAssetOwnershipResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "SetAssetOwnershipResponse")
    public JAXBElement<SetAssetOwnershipResponse> createSetAssetOwnershipResponse(SetAssetOwnershipResponse value) {
        return new JAXBElement<SetAssetOwnershipResponse>(_SetAssetOwnershipResponse_QNAME, SetAssetOwnershipResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAssetOwnership }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetAssetOwnership")
    public JAXBElement<GetAssetOwnership> createGetAssetOwnership(GetAssetOwnership value) {
        return new JAXBElement<GetAssetOwnership>(_GetAssetOwnership_QNAME, GetAssetOwnership.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAssetOwnershipResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetAssetOwnershipResponse")
    public JAXBElement<GetAssetOwnershipResponse> createGetAssetOwnershipResponse(GetAssetOwnershipResponse value) {
        return new JAXBElement<GetAssetOwnershipResponse>(_GetAssetOwnershipResponse_QNAME, GetAssetOwnershipResponse.class, null, value);
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
