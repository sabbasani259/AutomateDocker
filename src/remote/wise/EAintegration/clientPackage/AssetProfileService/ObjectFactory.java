
package remote.wise.EAintegration.clientPackage.AssetProfileService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.EAintegration.clientPackage.AssetProfileService package. 
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

    private final static QName _SetAssetProfileDetailsResponse_QNAME = new QName("http://webservice.service.wise.remote/", "setAssetProfileDetailsResponse");
    private final static QName _SetAssetProfileDetails_QNAME = new QName("http://webservice.service.wise.remote/", "setAssetProfileDetails");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.EAintegration.clientPackage.AssetProfileService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SetAssetProfileDetailsResponse }
     * 
     */
    public SetAssetProfileDetailsResponse createSetAssetProfileDetailsResponse() {
        return new SetAssetProfileDetailsResponse();
    }

    /**
     * Create an instance of {@link SetAssetProfileDetails }
     * 
     */
    public SetAssetProfileDetails createSetAssetProfileDetails() {
        return new SetAssetProfileDetails();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetAssetProfileDetailsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "setAssetProfileDetailsResponse")
    public JAXBElement<SetAssetProfileDetailsResponse> createSetAssetProfileDetailsResponse(SetAssetProfileDetailsResponse value) {
        return new JAXBElement<SetAssetProfileDetailsResponse>(_SetAssetProfileDetailsResponse_QNAME, SetAssetProfileDetailsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetAssetProfileDetails }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "setAssetProfileDetails")
    public JAXBElement<SetAssetProfileDetails> createSetAssetProfileDetails(SetAssetProfileDetails value) {
        return new JAXBElement<SetAssetProfileDetails>(_SetAssetProfileDetails_QNAME, SetAssetProfileDetails.class, null, value);
    }

}
