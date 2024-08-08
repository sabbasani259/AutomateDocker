
package remote.wise.client.UserAssetDetailsService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.UserAssetDetailsService package. 
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

    private final static QName _GetUserAssetDetails_QNAME = new QName("http://webservice.service.wise.remote/", "GetUserAssetDetails");
    private final static QName _GetUserAssetDetailsResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetUserAssetDetailsResponse");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.UserAssetDetailsService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetUserAssetDetailsResponse }
     * 
     */
    public GetUserAssetDetailsResponse createGetUserAssetDetailsResponse() {
        return new GetUserAssetDetailsResponse();
    }

    /**
     * Create an instance of {@link UserAssetDetailsRespContract }
     * 
     */
    public UserAssetDetailsRespContract createUserAssetDetailsRespContract() {
        return new UserAssetDetailsRespContract();
    }

    /**
     * Create an instance of {@link UserAssetDetailsReqContract }
     * 
     */
    public UserAssetDetailsReqContract createUserAssetDetailsReqContract() {
        return new UserAssetDetailsReqContract();
    }

    /**
     * Create an instance of {@link GetUserAssetDetails }
     * 
     */
    public GetUserAssetDetails createGetUserAssetDetails() {
        return new GetUserAssetDetails();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetUserAssetDetails }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetUserAssetDetails")
    public JAXBElement<GetUserAssetDetails> createGetUserAssetDetails(GetUserAssetDetails value) {
        return new JAXBElement<GetUserAssetDetails>(_GetUserAssetDetails_QNAME, GetUserAssetDetails.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetUserAssetDetailsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetUserAssetDetailsResponse")
    public JAXBElement<GetUserAssetDetailsResponse> createGetUserAssetDetailsResponse(GetUserAssetDetailsResponse value) {
        return new JAXBElement<GetUserAssetDetailsResponse>(_GetUserAssetDetailsResponse_QNAME, GetUserAssetDetailsResponse.class, null, value);
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
