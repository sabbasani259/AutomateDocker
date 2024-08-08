
package remote.wise.client.AssetGroupTypeService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.AssetGroupTypeService package. 
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

    private final static QName _DeleteAssetGroupType_QNAME = new QName("http://webservice.service.wise.remote/", "DeleteAssetGroupType");
    private final static QName _GetAssetGroupType_QNAME = new QName("http://webservice.service.wise.remote/", "GetAssetGroupType");
    private final static QName _DeleteAssetGroupTypeResponse_QNAME = new QName("http://webservice.service.wise.remote/", "DeleteAssetGroupTypeResponse");
    private final static QName _SetAssetGroupTypeResponse_QNAME = new QName("http://webservice.service.wise.remote/", "SetAssetGroupTypeResponse");
    private final static QName _GetAssetGroupTypeResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetAssetGroupTypeResponse");
    private final static QName _SetAssetGroupType_QNAME = new QName("http://webservice.service.wise.remote/", "SetAssetGroupType");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.AssetGroupTypeService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetAssetGroupTypeResponse }
     * 
     */
    public GetAssetGroupTypeResponse createGetAssetGroupTypeResponse() {
        return new GetAssetGroupTypeResponse();
    }

    /**
     * Create an instance of {@link SetAssetGroupType }
     * 
     */
    public SetAssetGroupType createSetAssetGroupType() {
        return new SetAssetGroupType();
    }

    /**
     * Create an instance of {@link DeleteAssetGroupTypeResponse }
     * 
     */
    public DeleteAssetGroupTypeResponse createDeleteAssetGroupTypeResponse() {
        return new DeleteAssetGroupTypeResponse();
    }

    /**
     * Create an instance of {@link SetAssetGroupTypeResponse }
     * 
     */
    public SetAssetGroupTypeResponse createSetAssetGroupTypeResponse() {
        return new SetAssetGroupTypeResponse();
    }

    /**
     * Create an instance of {@link GetAssetGroupType }
     * 
     */
    public GetAssetGroupType createGetAssetGroupType() {
        return new GetAssetGroupType();
    }

    /**
     * Create an instance of {@link DeleteAssetGroupType }
     * 
     */
    public DeleteAssetGroupType createDeleteAssetGroupType() {
        return new DeleteAssetGroupType();
    }

    /**
     * Create an instance of {@link AssetGroupTypeRespContract }
     * 
     */
    public AssetGroupTypeRespContract createAssetGroupTypeRespContract() {
        return new AssetGroupTypeRespContract();
    }

    /**
     * Create an instance of {@link AssetGroupTypeReqContract }
     * 
     */
    public AssetGroupTypeReqContract createAssetGroupTypeReqContract() {
        return new AssetGroupTypeReqContract();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteAssetGroupType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "DeleteAssetGroupType")
    public JAXBElement<DeleteAssetGroupType> createDeleteAssetGroupType(DeleteAssetGroupType value) {
        return new JAXBElement<DeleteAssetGroupType>(_DeleteAssetGroupType_QNAME, DeleteAssetGroupType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAssetGroupType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetAssetGroupType")
    public JAXBElement<GetAssetGroupType> createGetAssetGroupType(GetAssetGroupType value) {
        return new JAXBElement<GetAssetGroupType>(_GetAssetGroupType_QNAME, GetAssetGroupType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteAssetGroupTypeResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "DeleteAssetGroupTypeResponse")
    public JAXBElement<DeleteAssetGroupTypeResponse> createDeleteAssetGroupTypeResponse(DeleteAssetGroupTypeResponse value) {
        return new JAXBElement<DeleteAssetGroupTypeResponse>(_DeleteAssetGroupTypeResponse_QNAME, DeleteAssetGroupTypeResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetAssetGroupTypeResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "SetAssetGroupTypeResponse")
    public JAXBElement<SetAssetGroupTypeResponse> createSetAssetGroupTypeResponse(SetAssetGroupTypeResponse value) {
        return new JAXBElement<SetAssetGroupTypeResponse>(_SetAssetGroupTypeResponse_QNAME, SetAssetGroupTypeResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAssetGroupTypeResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetAssetGroupTypeResponse")
    public JAXBElement<GetAssetGroupTypeResponse> createGetAssetGroupTypeResponse(GetAssetGroupTypeResponse value) {
        return new JAXBElement<GetAssetGroupTypeResponse>(_GetAssetGroupTypeResponse_QNAME, GetAssetGroupTypeResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetAssetGroupType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "SetAssetGroupType")
    public JAXBElement<SetAssetGroupType> createSetAssetGroupType(SetAssetGroupType value) {
        return new JAXBElement<SetAssetGroupType>(_SetAssetGroupType_QNAME, SetAssetGroupType.class, null, value);
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
