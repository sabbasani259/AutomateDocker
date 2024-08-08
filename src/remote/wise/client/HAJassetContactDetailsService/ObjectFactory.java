
package remote.wise.client.HAJassetContactDetailsService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.HAJassetContactDetailsService package. 
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

    private final static QName _SetHAJassetContactDetailsResponse_QNAME = new QName("http://webservice.service.wise.remote/", "SetHAJassetContactDetailsResponse");
    private final static QName _GetHAJassetContactDetails_QNAME = new QName("http://webservice.service.wise.remote/", "GetHAJassetContactDetails");
    private final static QName _GetHAJassetContactDetailsResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetHAJassetContactDetailsResponse");
    private final static QName _SetHAJassetContactDetails_QNAME = new QName("http://webservice.service.wise.remote/", "SetHAJassetContactDetails");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.HAJassetContactDetailsService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link HajAssetContactDetailsRespContract }
     * 
     */
    public HajAssetContactDetailsRespContract createHajAssetContactDetailsRespContract() {
        return new HajAssetContactDetailsRespContract();
    }

    /**
     * Create an instance of {@link SetHAJassetContactDetailsResponse }
     * 
     */
    public SetHAJassetContactDetailsResponse createSetHAJassetContactDetailsResponse() {
        return new SetHAJassetContactDetailsResponse();
    }

    /**
     * Create an instance of {@link SetHAJassetContactDetails }
     * 
     */
    public SetHAJassetContactDetails createSetHAJassetContactDetails() {
        return new SetHAJassetContactDetails();
    }

    /**
     * Create an instance of {@link GetHAJassetContactDetailsResponse }
     * 
     */
    public GetHAJassetContactDetailsResponse createGetHAJassetContactDetailsResponse() {
        return new GetHAJassetContactDetailsResponse();
    }

    /**
     * Create an instance of {@link GetHAJassetContactDetails }
     * 
     */
    public GetHAJassetContactDetails createGetHAJassetContactDetails() {
        return new GetHAJassetContactDetails();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetHAJassetContactDetailsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "SetHAJassetContactDetailsResponse")
    public JAXBElement<SetHAJassetContactDetailsResponse> createSetHAJassetContactDetailsResponse(SetHAJassetContactDetailsResponse value) {
        return new JAXBElement<SetHAJassetContactDetailsResponse>(_SetHAJassetContactDetailsResponse_QNAME, SetHAJassetContactDetailsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetHAJassetContactDetails }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetHAJassetContactDetails")
    public JAXBElement<GetHAJassetContactDetails> createGetHAJassetContactDetails(GetHAJassetContactDetails value) {
        return new JAXBElement<GetHAJassetContactDetails>(_GetHAJassetContactDetails_QNAME, GetHAJassetContactDetails.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetHAJassetContactDetailsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetHAJassetContactDetailsResponse")
    public JAXBElement<GetHAJassetContactDetailsResponse> createGetHAJassetContactDetailsResponse(GetHAJassetContactDetailsResponse value) {
        return new JAXBElement<GetHAJassetContactDetailsResponse>(_GetHAJassetContactDetailsResponse_QNAME, GetHAJassetContactDetailsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetHAJassetContactDetails }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "SetHAJassetContactDetails")
    public JAXBElement<SetHAJassetContactDetails> createSetHAJassetContactDetails(SetHAJassetContactDetails value) {
        return new JAXBElement<SetHAJassetContactDetails>(_SetHAJassetContactDetails_QNAME, SetHAJassetContactDetails.class, null, value);
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
