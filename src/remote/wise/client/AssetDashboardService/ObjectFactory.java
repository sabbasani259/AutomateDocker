
package remote.wise.client.AssetDashboardService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.AssetDashboardService package. 
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

    private final static QName _GetAssetDashboardDetailsResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetAssetDashboardDetailsResponse");
    private final static QName _GetAssetDashboardDetails_QNAME = new QName("http://webservice.service.wise.remote/", "GetAssetDashboardDetails");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.AssetDashboardService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetAssetDashboardDetails }
     * 
     */
    public GetAssetDashboardDetails createGetAssetDashboardDetails() {
        return new GetAssetDashboardDetails();
    }

    /**
     * Create an instance of {@link AssetDashboardReqContract }
     * 
     */
    public AssetDashboardReqContract createAssetDashboardReqContract() {
        return new AssetDashboardReqContract();
    }

    /**
     * Create an instance of {@link GetAssetDashboardDetailsResponse }
     * 
     */
    public GetAssetDashboardDetailsResponse createGetAssetDashboardDetailsResponse() {
        return new GetAssetDashboardDetailsResponse();
    }

    /**
     * Create an instance of {@link AssetDashboardRespContract }
     * 
     */
    public AssetDashboardRespContract createAssetDashboardRespContract() {
        return new AssetDashboardRespContract();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAssetDashboardDetailsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetAssetDashboardDetailsResponse")
    public JAXBElement<GetAssetDashboardDetailsResponse> createGetAssetDashboardDetailsResponse(GetAssetDashboardDetailsResponse value) {
        return new JAXBElement<GetAssetDashboardDetailsResponse>(_GetAssetDashboardDetailsResponse_QNAME, GetAssetDashboardDetailsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAssetDashboardDetails }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetAssetDashboardDetails")
    public JAXBElement<GetAssetDashboardDetails> createGetAssetDashboardDetails(GetAssetDashboardDetails value) {
        return new JAXBElement<GetAssetDashboardDetails>(_GetAssetDashboardDetails_QNAME, GetAssetDashboardDetails.class, null, value);
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
