
package remote.wise.client.AssetExtendedService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.AssetExtendedService package. 
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

    private final static QName _SetAssetExtendedDetails_QNAME = new QName("http://webservice.service.wise.remote/", "SetAssetExtendedDetails");
    private final static QName _UpdateCHMRDetailsResponse_QNAME = new QName("http://webservice.service.wise.remote/", "UpdateCHMRDetailsResponse");
    private final static QName _UpdateCHMRDetails_QNAME = new QName("http://webservice.service.wise.remote/", "UpdateCHMRDetails");
    private final static QName _SetAssetExtendedDetailsResponse_QNAME = new QName("http://webservice.service.wise.remote/", "SetAssetExtendedDetailsResponse");
    private final static QName _GetAssetExtendedDetailsResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetAssetExtendedDetailsResponse");
    private final static QName _GetAssetExtendedDetails_QNAME = new QName("http://webservice.service.wise.remote/", "GetAssetExtendedDetails");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.AssetExtendedService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link AssetExtendedReqContract }
     * 
     */
    public AssetExtendedReqContract createAssetExtendedReqContract() {
        return new AssetExtendedReqContract();
    }

    /**
     * Create an instance of {@link UpdateCHMRDetailsResponse }
     * 
     */
    public UpdateCHMRDetailsResponse createUpdateCHMRDetailsResponse() {
        return new UpdateCHMRDetailsResponse();
    }

    /**
     * Create an instance of {@link GetAssetExtendedDetailsResponse }
     * 
     */
    public GetAssetExtendedDetailsResponse createGetAssetExtendedDetailsResponse() {
        return new GetAssetExtendedDetailsResponse();
    }

    /**
     * Create an instance of {@link UpdateCHMRDetails }
     * 
     */
    public UpdateCHMRDetails createUpdateCHMRDetails() {
        return new UpdateCHMRDetails();
    }

    /**
     * Create an instance of {@link SetAssetExtendedDetails }
     * 
     */
    public SetAssetExtendedDetails createSetAssetExtendedDetails() {
        return new SetAssetExtendedDetails();
    }

    /**
     * Create an instance of {@link SetAssetExtendedDetailsResponse }
     * 
     */
    public SetAssetExtendedDetailsResponse createSetAssetExtendedDetailsResponse() {
        return new SetAssetExtendedDetailsResponse();
    }

    /**
     * Create an instance of {@link GetAssetExtendedDetails }
     * 
     */
    public GetAssetExtendedDetails createGetAssetExtendedDetails() {
        return new GetAssetExtendedDetails();
    }

    /**
     * Create an instance of {@link AssetExtendedRespContract }
     * 
     */
    public AssetExtendedRespContract createAssetExtendedRespContract() {
        return new AssetExtendedRespContract();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetAssetExtendedDetails }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "SetAssetExtendedDetails")
    public JAXBElement<SetAssetExtendedDetails> createSetAssetExtendedDetails(SetAssetExtendedDetails value) {
        return new JAXBElement<SetAssetExtendedDetails>(_SetAssetExtendedDetails_QNAME, SetAssetExtendedDetails.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdateCHMRDetailsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "UpdateCHMRDetailsResponse")
    public JAXBElement<UpdateCHMRDetailsResponse> createUpdateCHMRDetailsResponse(UpdateCHMRDetailsResponse value) {
        return new JAXBElement<UpdateCHMRDetailsResponse>(_UpdateCHMRDetailsResponse_QNAME, UpdateCHMRDetailsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdateCHMRDetails }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "UpdateCHMRDetails")
    public JAXBElement<UpdateCHMRDetails> createUpdateCHMRDetails(UpdateCHMRDetails value) {
        return new JAXBElement<UpdateCHMRDetails>(_UpdateCHMRDetails_QNAME, UpdateCHMRDetails.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetAssetExtendedDetailsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "SetAssetExtendedDetailsResponse")
    public JAXBElement<SetAssetExtendedDetailsResponse> createSetAssetExtendedDetailsResponse(SetAssetExtendedDetailsResponse value) {
        return new JAXBElement<SetAssetExtendedDetailsResponse>(_SetAssetExtendedDetailsResponse_QNAME, SetAssetExtendedDetailsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAssetExtendedDetailsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetAssetExtendedDetailsResponse")
    public JAXBElement<GetAssetExtendedDetailsResponse> createGetAssetExtendedDetailsResponse(GetAssetExtendedDetailsResponse value) {
        return new JAXBElement<GetAssetExtendedDetailsResponse>(_GetAssetExtendedDetailsResponse_QNAME, GetAssetExtendedDetailsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAssetExtendedDetails }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetAssetExtendedDetails")
    public JAXBElement<GetAssetExtendedDetails> createGetAssetExtendedDetails(GetAssetExtendedDetails value) {
        return new JAXBElement<GetAssetExtendedDetails>(_GetAssetExtendedDetails_QNAME, GetAssetExtendedDetails.class, null, value);
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
