
package remote.wise.client.HAJassetLocationDetailsService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.HAJassetLocationDetailsService package. 
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

    private final static QName _GetHAJassetLocationDetails_QNAME = new QName("http://webservice.service.wise.remote/", "GetHAJassetLocationDetails");
    private final static QName _GetHAJassetLocationDetailsResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetHAJassetLocationDetailsResponse");
    private final static QName _SetassetLocationDetailsResponse_QNAME = new QName("http://webservice.service.wise.remote/", "SetassetLocationDetailsResponse");
    private final static QName _SetassetLocationDetails_QNAME = new QName("http://webservice.service.wise.remote/", "SetassetLocationDetails");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.HAJassetLocationDetailsService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link HajAssetLocationDetailsRespContract }
     * 
     */
    public HajAssetLocationDetailsRespContract createHajAssetLocationDetailsRespContract() {
        return new HajAssetLocationDetailsRespContract();
    }

    /**
     * Create an instance of {@link SetassetLocationDetails }
     * 
     */
    public SetassetLocationDetails createSetassetLocationDetails() {
        return new SetassetLocationDetails();
    }

    /**
     * Create an instance of {@link SetassetLocationDetailsResponse }
     * 
     */
    public SetassetLocationDetailsResponse createSetassetLocationDetailsResponse() {
        return new SetassetLocationDetailsResponse();
    }

    /**
     * Create an instance of {@link GetHAJassetLocationDetails }
     * 
     */
    public GetHAJassetLocationDetails createGetHAJassetLocationDetails() {
        return new GetHAJassetLocationDetails();
    }

    /**
     * Create an instance of {@link GetHAJassetLocationDetailsResponse }
     * 
     */
    public GetHAJassetLocationDetailsResponse createGetHAJassetLocationDetailsResponse() {
        return new GetHAJassetLocationDetailsResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetHAJassetLocationDetails }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetHAJassetLocationDetails")
    public JAXBElement<GetHAJassetLocationDetails> createGetHAJassetLocationDetails(GetHAJassetLocationDetails value) {
        return new JAXBElement<GetHAJassetLocationDetails>(_GetHAJassetLocationDetails_QNAME, GetHAJassetLocationDetails.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetHAJassetLocationDetailsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetHAJassetLocationDetailsResponse")
    public JAXBElement<GetHAJassetLocationDetailsResponse> createGetHAJassetLocationDetailsResponse(GetHAJassetLocationDetailsResponse value) {
        return new JAXBElement<GetHAJassetLocationDetailsResponse>(_GetHAJassetLocationDetailsResponse_QNAME, GetHAJassetLocationDetailsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetassetLocationDetailsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "SetassetLocationDetailsResponse")
    public JAXBElement<SetassetLocationDetailsResponse> createSetassetLocationDetailsResponse(SetassetLocationDetailsResponse value) {
        return new JAXBElement<SetassetLocationDetailsResponse>(_SetassetLocationDetailsResponse_QNAME, SetassetLocationDetailsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetassetLocationDetails }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "SetassetLocationDetails")
    public JAXBElement<SetassetLocationDetails> createSetassetLocationDetails(SetassetLocationDetails value) {
        return new JAXBElement<SetassetLocationDetails>(_SetassetLocationDetails_QNAME, SetassetLocationDetails.class, null, value);
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
