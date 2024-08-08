
package remote.wise.client.TenancyAssetsService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.TenancyAssetsService package. 
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

    private final static QName _GetTenancyAssetsResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetTenancyAssetsResponse");
    private final static QName _GetTenancyAssets_QNAME = new QName("http://webservice.service.wise.remote/", "GetTenancyAssets");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.TenancyAssetsService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetTenancyAssets }
     * 
     */
    public GetTenancyAssets createGetTenancyAssets() {
        return new GetTenancyAssets();
    }

    /**
     * Create an instance of {@link GetTenancyAssetsResponse }
     * 
     */
    public GetTenancyAssetsResponse createGetTenancyAssetsResponse() {
        return new GetTenancyAssetsResponse();
    }

    /**
     * Create an instance of {@link TenancyAssetsRespContract }
     * 
     */
    public TenancyAssetsRespContract createTenancyAssetsRespContract() {
        return new TenancyAssetsRespContract();
    }

    /**
     * Create an instance of {@link TenancyAssetsReqContract }
     * 
     */
    public TenancyAssetsReqContract createTenancyAssetsReqContract() {
        return new TenancyAssetsReqContract();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetTenancyAssetsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetTenancyAssetsResponse")
    public JAXBElement<GetTenancyAssetsResponse> createGetTenancyAssetsResponse(GetTenancyAssetsResponse value) {
        return new JAXBElement<GetTenancyAssetsResponse>(_GetTenancyAssetsResponse_QNAME, GetTenancyAssetsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetTenancyAssets }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetTenancyAssets")
    public JAXBElement<GetTenancyAssets> createGetTenancyAssets(GetTenancyAssets value) {
        return new JAXBElement<GetTenancyAssets>(_GetTenancyAssets_QNAME, GetTenancyAssets.class, null, value);
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
