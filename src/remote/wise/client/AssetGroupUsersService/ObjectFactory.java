
package remote.wise.client.AssetGroupUsersService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.AssetGroupUsersService package. 
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

    private final static QName _GetAssetGroupUsers_QNAME = new QName("http://webservice.service.wise.remote/", "GetAssetGroupUsers");
    private final static QName _SetAssetGroupUsers_QNAME = new QName("http://webservice.service.wise.remote/", "SetAssetGroupUsers");
    private final static QName _GetAssetGroupUsersResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetAssetGroupUsersResponse");
    private final static QName _SetAssetGroupUsersResponse_QNAME = new QName("http://webservice.service.wise.remote/", "SetAssetGroupUsersResponse");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.AssetGroupUsersService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SetAssetGroupUsersResponse }
     * 
     */
    public SetAssetGroupUsersResponse createSetAssetGroupUsersResponse() {
        return new SetAssetGroupUsersResponse();
    }

    /**
     * Create an instance of {@link SetAssetGroupUsers }
     * 
     */
    public SetAssetGroupUsers createSetAssetGroupUsers() {
        return new SetAssetGroupUsers();
    }

    /**
     * Create an instance of {@link GetAssetGroupUsers }
     * 
     */
    public GetAssetGroupUsers createGetAssetGroupUsers() {
        return new GetAssetGroupUsers();
    }

    /**
     * Create an instance of {@link AssetGroupUsersReqContract }
     * 
     */
    public AssetGroupUsersReqContract createAssetGroupUsersReqContract() {
        return new AssetGroupUsersReqContract();
    }

    /**
     * Create an instance of {@link GetAssetGroupUsersResponse }
     * 
     */
    public GetAssetGroupUsersResponse createGetAssetGroupUsersResponse() {
        return new GetAssetGroupUsersResponse();
    }

    /**
     * Create an instance of {@link AssetGroupUsersRespContract }
     * 
     */
    public AssetGroupUsersRespContract createAssetGroupUsersRespContract() {
        return new AssetGroupUsersRespContract();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAssetGroupUsers }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetAssetGroupUsers")
    public JAXBElement<GetAssetGroupUsers> createGetAssetGroupUsers(GetAssetGroupUsers value) {
        return new JAXBElement<GetAssetGroupUsers>(_GetAssetGroupUsers_QNAME, GetAssetGroupUsers.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetAssetGroupUsers }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "SetAssetGroupUsers")
    public JAXBElement<SetAssetGroupUsers> createSetAssetGroupUsers(SetAssetGroupUsers value) {
        return new JAXBElement<SetAssetGroupUsers>(_SetAssetGroupUsers_QNAME, SetAssetGroupUsers.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAssetGroupUsersResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetAssetGroupUsersResponse")
    public JAXBElement<GetAssetGroupUsersResponse> createGetAssetGroupUsersResponse(GetAssetGroupUsersResponse value) {
        return new JAXBElement<GetAssetGroupUsersResponse>(_GetAssetGroupUsersResponse_QNAME, GetAssetGroupUsersResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetAssetGroupUsersResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "SetAssetGroupUsersResponse")
    public JAXBElement<SetAssetGroupUsersResponse> createSetAssetGroupUsersResponse(SetAssetGroupUsersResponse value) {
        return new JAXBElement<SetAssetGroupUsersResponse>(_SetAssetGroupUsersResponse_QNAME, SetAssetGroupUsersResponse.class, null, value);
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
