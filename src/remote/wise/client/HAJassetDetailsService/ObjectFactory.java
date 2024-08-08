
package remote.wise.client.HAJassetDetailsService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.HAJassetDetailsService package. 
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

    private final static QName _SetHAJassetDetails_QNAME = new QName("http://webservice.service.wise.remote/", "SetHAJassetDetails");
    private final static QName _SetHAJassetDetailsResponse_QNAME = new QName("http://webservice.service.wise.remote/", "SetHAJassetDetailsResponse");
    private final static QName _GetHAJassetDetails_QNAME = new QName("http://webservice.service.wise.remote/", "GetHAJassetDetails");
    private final static QName _GetHAJassetDetailsResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetHAJassetDetailsResponse");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.HAJassetDetailsService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetHAJassetDetailsResponse }
     * 
     */
    public GetHAJassetDetailsResponse createGetHAJassetDetailsResponse() {
        return new GetHAJassetDetailsResponse();
    }

    /**
     * Create an instance of {@link HajAssetDetailsRespContract }
     * 
     */
    public HajAssetDetailsRespContract createHajAssetDetailsRespContract() {
        return new HajAssetDetailsRespContract();
    }

    /**
     * Create an instance of {@link SetHAJassetDetailsResponse }
     * 
     */
    public SetHAJassetDetailsResponse createSetHAJassetDetailsResponse() {
        return new SetHAJassetDetailsResponse();
    }

    /**
     * Create an instance of {@link GetHAJassetDetails }
     * 
     */
    public GetHAJassetDetails createGetHAJassetDetails() {
        return new GetHAJassetDetails();
    }

    /**
     * Create an instance of {@link SetHAJassetDetails }
     * 
     */
    public SetHAJassetDetails createSetHAJassetDetails() {
        return new SetHAJassetDetails();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetHAJassetDetails }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "SetHAJassetDetails")
    public JAXBElement<SetHAJassetDetails> createSetHAJassetDetails(SetHAJassetDetails value) {
        return new JAXBElement<SetHAJassetDetails>(_SetHAJassetDetails_QNAME, SetHAJassetDetails.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetHAJassetDetailsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "SetHAJassetDetailsResponse")
    public JAXBElement<SetHAJassetDetailsResponse> createSetHAJassetDetailsResponse(SetHAJassetDetailsResponse value) {
        return new JAXBElement<SetHAJassetDetailsResponse>(_SetHAJassetDetailsResponse_QNAME, SetHAJassetDetailsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetHAJassetDetails }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetHAJassetDetails")
    public JAXBElement<GetHAJassetDetails> createGetHAJassetDetails(GetHAJassetDetails value) {
        return new JAXBElement<GetHAJassetDetails>(_GetHAJassetDetails_QNAME, GetHAJassetDetails.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetHAJassetDetailsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetHAJassetDetailsResponse")
    public JAXBElement<GetHAJassetDetailsResponse> createGetHAJassetDetailsResponse(GetHAJassetDetailsResponse value) {
        return new JAXBElement<GetHAJassetDetailsResponse>(_GetHAJassetDetailsResponse_QNAME, GetHAJassetDetailsResponse.class, null, value);
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
