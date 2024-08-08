
package remote.wise.EAintegration.clientPackage.AssetPersonalityService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.EAintegration.clientPackage.AssetPersonalityService package. 
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

    private final static QName _SetAssetPersonalityDetails_QNAME = new QName("http://webservice.service.wise.remote/", "SetAssetPersonalityDetails");
    private final static QName _SetAssetPersonalityDetailsResponse_QNAME = new QName("http://webservice.service.wise.remote/", "SetAssetPersonalityDetailsResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.EAintegration.clientPackage.AssetPersonalityService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SetAssetPersonalityDetails }
     * 
     */
    public SetAssetPersonalityDetails createSetAssetPersonalityDetails() {
        return new SetAssetPersonalityDetails();
    }

    /**
     * Create an instance of {@link SetAssetPersonalityDetailsResponse }
     * 
     */
    public SetAssetPersonalityDetailsResponse createSetAssetPersonalityDetailsResponse() {
        return new SetAssetPersonalityDetailsResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetAssetPersonalityDetails }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "SetAssetPersonalityDetails")
    public JAXBElement<SetAssetPersonalityDetails> createSetAssetPersonalityDetails(SetAssetPersonalityDetails value) {
        return new JAXBElement<SetAssetPersonalityDetails>(_SetAssetPersonalityDetails_QNAME, SetAssetPersonalityDetails.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetAssetPersonalityDetailsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "SetAssetPersonalityDetailsResponse")
    public JAXBElement<SetAssetPersonalityDetailsResponse> createSetAssetPersonalityDetailsResponse(SetAssetPersonalityDetailsResponse value) {
        return new JAXBElement<SetAssetPersonalityDetailsResponse>(_SetAssetPersonalityDetailsResponse_QNAME, SetAssetPersonalityDetailsResponse.class, null, value);
    }

}
