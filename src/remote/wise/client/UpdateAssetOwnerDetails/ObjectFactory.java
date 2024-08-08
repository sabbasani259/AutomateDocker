
package remote.wise.client.UpdateAssetOwnerDetails;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.UpdateAssetOwnerDetails package. 
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

    private final static QName _SetCurrentOwnerDetailsResponse_QNAME = new QName("http://webservice.service.wise.remote/", "setCurrentOwnerDetailsResponse");
    private final static QName _SetCurrentOwnerDetails_QNAME = new QName("http://webservice.service.wise.remote/", "setCurrentOwnerDetails");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.UpdateAssetOwnerDetails
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SetCurrentOwnerDetails }
     * 
     */
    public SetCurrentOwnerDetails createSetCurrentOwnerDetails() {
        return new SetCurrentOwnerDetails();
    }

    /**
     * Create an instance of {@link SetCurrentOwnerDetailsResponse }
     * 
     */
    public SetCurrentOwnerDetailsResponse createSetCurrentOwnerDetailsResponse() {
        return new SetCurrentOwnerDetailsResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetCurrentOwnerDetailsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "setCurrentOwnerDetailsResponse")
    public JAXBElement<SetCurrentOwnerDetailsResponse> createSetCurrentOwnerDetailsResponse(SetCurrentOwnerDetailsResponse value) {
        return new JAXBElement<SetCurrentOwnerDetailsResponse>(_SetCurrentOwnerDetailsResponse_QNAME, SetCurrentOwnerDetailsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetCurrentOwnerDetails }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "setCurrentOwnerDetails")
    public JAXBElement<SetCurrentOwnerDetails> createSetCurrentOwnerDetails(SetCurrentOwnerDetails value) {
        return new JAXBElement<SetCurrentOwnerDetails>(_SetCurrentOwnerDetails_QNAME, SetCurrentOwnerDetails.class, null, value);
    }

}
