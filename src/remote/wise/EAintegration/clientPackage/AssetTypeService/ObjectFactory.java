
package remote.wise.EAintegration.clientPackage.AssetTypeService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.EAintegration.clientPackage.AssetTypeService package. 
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

    private final static QName _SetAssetTypeDetails_QNAME = new QName("http://webservice.service.wise.remote/", "setAssetTypeDetails");
    private final static QName _SetAssetTypeDetailsResponse_QNAME = new QName("http://webservice.service.wise.remote/", "setAssetTypeDetailsResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.EAintegration.clientPackage.AssetTypeService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SetAssetTypeDetailsResponse }
     * 
     */
    public SetAssetTypeDetailsResponse createSetAssetTypeDetailsResponse() {
        return new SetAssetTypeDetailsResponse();
    }

    /**
     * Create an instance of {@link SetAssetTypeDetails }
     * 
     */
    public SetAssetTypeDetails createSetAssetTypeDetails() {
        return new SetAssetTypeDetails();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetAssetTypeDetails }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "setAssetTypeDetails")
    public JAXBElement<SetAssetTypeDetails> createSetAssetTypeDetails(SetAssetTypeDetails value) {
        return new JAXBElement<SetAssetTypeDetails>(_SetAssetTypeDetails_QNAME, SetAssetTypeDetails.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetAssetTypeDetailsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "setAssetTypeDetailsResponse")
    public JAXBElement<SetAssetTypeDetailsResponse> createSetAssetTypeDetailsResponse(SetAssetTypeDetailsResponse value) {
        return new JAXBElement<SetAssetTypeDetailsResponse>(_SetAssetTypeDetailsResponse_QNAME, SetAssetTypeDetailsResponse.class, null, value);
    }

}
