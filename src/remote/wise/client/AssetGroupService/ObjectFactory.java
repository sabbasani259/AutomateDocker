
package remote.wise.client.AssetGroupService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.AssetGroupService package. 
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

    private final static QName _GetCustomAssetGroupResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetCustomAssetGroupResponse");
    private final static QName _DeleteCustomAssetGroup_QNAME = new QName("http://webservice.service.wise.remote/", "DeleteCustomAssetGroup");
    private final static QName _DeleteCustomAssetGroupResponse_QNAME = new QName("http://webservice.service.wise.remote/", "DeleteCustomAssetGroupResponse");
    private final static QName _SetCustomAssetGroupResponse_QNAME = new QName("http://webservice.service.wise.remote/", "SetCustomAssetGroupResponse");
    private final static QName _SetCustomAssetGroup_QNAME = new QName("http://webservice.service.wise.remote/", "SetCustomAssetGroup");
    private final static QName _GetCustomAssetGroup_QNAME = new QName("http://webservice.service.wise.remote/", "GetCustomAssetGroup");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.AssetGroupService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link AssetGroupReqContract }
     * 
     */
    public AssetGroupReqContract createAssetGroupReqContract() {
        return new AssetGroupReqContract();
    }

    /**
     * Create an instance of {@link GetCustomAssetGroup }
     * 
     */
    public GetCustomAssetGroup createGetCustomAssetGroup() {
        return new GetCustomAssetGroup();
    }

    /**
     * Create an instance of {@link GetCustomAssetGroupResponse }
     * 
     */
    public GetCustomAssetGroupResponse createGetCustomAssetGroupResponse() {
        return new GetCustomAssetGroupResponse();
    }

    /**
     * Create an instance of {@link DeleteCustomAssetGroup }
     * 
     */
    public DeleteCustomAssetGroup createDeleteCustomAssetGroup() {
        return new DeleteCustomAssetGroup();
    }

    /**
     * Create an instance of {@link SetCustomAssetGroupResponse }
     * 
     */
    public SetCustomAssetGroupResponse createSetCustomAssetGroupResponse() {
        return new SetCustomAssetGroupResponse();
    }

    /**
     * Create an instance of {@link DeleteCustomAssetGroupResponse }
     * 
     */
    public DeleteCustomAssetGroupResponse createDeleteCustomAssetGroupResponse() {
        return new DeleteCustomAssetGroupResponse();
    }

    /**
     * Create an instance of {@link AssetGroupRespContract }
     * 
     */
    public AssetGroupRespContract createAssetGroupRespContract() {
        return new AssetGroupRespContract();
    }

    /**
     * Create an instance of {@link SetCustomAssetGroup }
     * 
     */
    public SetCustomAssetGroup createSetCustomAssetGroup() {
        return new SetCustomAssetGroup();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetCustomAssetGroupResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetCustomAssetGroupResponse")
    public JAXBElement<GetCustomAssetGroupResponse> createGetCustomAssetGroupResponse(GetCustomAssetGroupResponse value) {
        return new JAXBElement<GetCustomAssetGroupResponse>(_GetCustomAssetGroupResponse_QNAME, GetCustomAssetGroupResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteCustomAssetGroup }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "DeleteCustomAssetGroup")
    public JAXBElement<DeleteCustomAssetGroup> createDeleteCustomAssetGroup(DeleteCustomAssetGroup value) {
        return new JAXBElement<DeleteCustomAssetGroup>(_DeleteCustomAssetGroup_QNAME, DeleteCustomAssetGroup.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteCustomAssetGroupResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "DeleteCustomAssetGroupResponse")
    public JAXBElement<DeleteCustomAssetGroupResponse> createDeleteCustomAssetGroupResponse(DeleteCustomAssetGroupResponse value) {
        return new JAXBElement<DeleteCustomAssetGroupResponse>(_DeleteCustomAssetGroupResponse_QNAME, DeleteCustomAssetGroupResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetCustomAssetGroupResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "SetCustomAssetGroupResponse")
    public JAXBElement<SetCustomAssetGroupResponse> createSetCustomAssetGroupResponse(SetCustomAssetGroupResponse value) {
        return new JAXBElement<SetCustomAssetGroupResponse>(_SetCustomAssetGroupResponse_QNAME, SetCustomAssetGroupResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetCustomAssetGroup }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "SetCustomAssetGroup")
    public JAXBElement<SetCustomAssetGroup> createSetCustomAssetGroup(SetCustomAssetGroup value) {
        return new JAXBElement<SetCustomAssetGroup>(_SetCustomAssetGroup_QNAME, SetCustomAssetGroup.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetCustomAssetGroup }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetCustomAssetGroup")
    public JAXBElement<GetCustomAssetGroup> createGetCustomAssetGroup(GetCustomAssetGroup value) {
        return new JAXBElement<GetCustomAssetGroup>(_GetCustomAssetGroup_QNAME, GetCustomAssetGroup.class, null, value);
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
