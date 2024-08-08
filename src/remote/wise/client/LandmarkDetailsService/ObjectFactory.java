
package remote.wise.client.LandmarkDetailsService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.LandmarkDetailsService package. 
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

    private final static QName _GetLandmarkDetailsResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetLandmarkDetailsResponse");
    private final static QName _GetLandmarkDetails_QNAME = new QName("http://webservice.service.wise.remote/", "GetLandmarkDetails");
    private final static QName _SetLandmarkDetailsResponse_QNAME = new QName("http://webservice.service.wise.remote/", "SetLandmarkDetailsResponse");
    private final static QName _SetLandmarkDetails_QNAME = new QName("http://webservice.service.wise.remote/", "SetLandmarkDetails");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.LandmarkDetailsService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link LandmarkDetailsRespContract }
     * 
     */
    public LandmarkDetailsRespContract createLandmarkDetailsRespContract() {
        return new LandmarkDetailsRespContract();
    }

    /**
     * Create an instance of {@link LandmarkDetailsReqContract }
     * 
     */
    public LandmarkDetailsReqContract createLandmarkDetailsReqContract() {
        return new LandmarkDetailsReqContract();
    }

    /**
     * Create an instance of {@link GetLandmarkDetailsResponse }
     * 
     */
    public GetLandmarkDetailsResponse createGetLandmarkDetailsResponse() {
        return new GetLandmarkDetailsResponse();
    }

    /**
     * Create an instance of {@link SetLandmarkDetailsResponse }
     * 
     */
    public SetLandmarkDetailsResponse createSetLandmarkDetailsResponse() {
        return new SetLandmarkDetailsResponse();
    }

    /**
     * Create an instance of {@link GetLandmarkDetails }
     * 
     */
    public GetLandmarkDetails createGetLandmarkDetails() {
        return new GetLandmarkDetails();
    }

    /**
     * Create an instance of {@link SetLandmarkDetails }
     * 
     */
    public SetLandmarkDetails createSetLandmarkDetails() {
        return new SetLandmarkDetails();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetLandmarkDetailsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetLandmarkDetailsResponse")
    public JAXBElement<GetLandmarkDetailsResponse> createGetLandmarkDetailsResponse(GetLandmarkDetailsResponse value) {
        return new JAXBElement<GetLandmarkDetailsResponse>(_GetLandmarkDetailsResponse_QNAME, GetLandmarkDetailsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetLandmarkDetails }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetLandmarkDetails")
    public JAXBElement<GetLandmarkDetails> createGetLandmarkDetails(GetLandmarkDetails value) {
        return new JAXBElement<GetLandmarkDetails>(_GetLandmarkDetails_QNAME, GetLandmarkDetails.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetLandmarkDetailsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "SetLandmarkDetailsResponse")
    public JAXBElement<SetLandmarkDetailsResponse> createSetLandmarkDetailsResponse(SetLandmarkDetailsResponse value) {
        return new JAXBElement<SetLandmarkDetailsResponse>(_SetLandmarkDetailsResponse_QNAME, SetLandmarkDetailsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetLandmarkDetails }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "SetLandmarkDetails")
    public JAXBElement<SetLandmarkDetails> createSetLandmarkDetails(SetLandmarkDetails value) {
        return new JAXBElement<SetLandmarkDetails>(_SetLandmarkDetails_QNAME, SetLandmarkDetails.class, null, value);
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
