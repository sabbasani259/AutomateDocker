
package remote.wise.client.LandmarkAssetService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.LandmarkAssetService package. 
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

    private final static QName _SetLandmarkAsset_QNAME = new QName("http://webservice.service.wise.remote/", "SetLandmarkAsset");
    private final static QName _GetLandmarkAsset_QNAME = new QName("http://webservice.service.wise.remote/", "GetLandmarkAsset");
    private final static QName _SetLandmarkAssetResponse_QNAME = new QName("http://webservice.service.wise.remote/", "SetLandmarkAssetResponse");
    private final static QName _GetLandmarkAssetResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetLandmarkAssetResponse");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.LandmarkAssetService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetLandmarkAsset }
     * 
     */
    public GetLandmarkAsset createGetLandmarkAsset() {
        return new GetLandmarkAsset();
    }

    /**
     * Create an instance of {@link LandmarkAssetReqContract }
     * 
     */
    public LandmarkAssetReqContract createLandmarkAssetReqContract() {
        return new LandmarkAssetReqContract();
    }

    /**
     * Create an instance of {@link SetLandmarkAssetResponse }
     * 
     */
    public SetLandmarkAssetResponse createSetLandmarkAssetResponse() {
        return new SetLandmarkAssetResponse();
    }

    /**
     * Create an instance of {@link LandmarkAssetRespContract }
     * 
     */
    public LandmarkAssetRespContract createLandmarkAssetRespContract() {
        return new LandmarkAssetRespContract();
    }

    /**
     * Create an instance of {@link SetLandmarkAsset }
     * 
     */
    public SetLandmarkAsset createSetLandmarkAsset() {
        return new SetLandmarkAsset();
    }

    /**
     * Create an instance of {@link GetLandmarkAssetResponse }
     * 
     */
    public GetLandmarkAssetResponse createGetLandmarkAssetResponse() {
        return new GetLandmarkAssetResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetLandmarkAsset }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "SetLandmarkAsset")
    public JAXBElement<SetLandmarkAsset> createSetLandmarkAsset(SetLandmarkAsset value) {
        return new JAXBElement<SetLandmarkAsset>(_SetLandmarkAsset_QNAME, SetLandmarkAsset.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetLandmarkAsset }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetLandmarkAsset")
    public JAXBElement<GetLandmarkAsset> createGetLandmarkAsset(GetLandmarkAsset value) {
        return new JAXBElement<GetLandmarkAsset>(_GetLandmarkAsset_QNAME, GetLandmarkAsset.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetLandmarkAssetResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "SetLandmarkAssetResponse")
    public JAXBElement<SetLandmarkAssetResponse> createSetLandmarkAssetResponse(SetLandmarkAssetResponse value) {
        return new JAXBElement<SetLandmarkAssetResponse>(_SetLandmarkAssetResponse_QNAME, SetLandmarkAssetResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetLandmarkAssetResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetLandmarkAssetResponse")
    public JAXBElement<GetLandmarkAssetResponse> createGetLandmarkAssetResponse(GetLandmarkAssetResponse value) {
        return new JAXBElement<GetLandmarkAssetResponse>(_GetLandmarkAssetResponse_QNAME, GetLandmarkAssetResponse.class, null, value);
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
