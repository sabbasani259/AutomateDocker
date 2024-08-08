
package remote.wise.client.TenancyDetailsService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.TenancyDetailsService package. 
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

    private final static QName _GetTenancyDetailsResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetTenancyDetailsResponse");
    private final static QName _IOException_QNAME = new QName("http://webservice.service.wise.remote/", "IOException");
    private final static QName _GetTenancyDetails_QNAME = new QName("http://webservice.service.wise.remote/", "GetTenancyDetails");
    private final static QName _SetTenancyDetails_QNAME = new QName("http://webservice.service.wise.remote/", "setTenancyDetails");
    private final static QName _SetTenancyDetailsResponse_QNAME = new QName("http://webservice.service.wise.remote/", "setTenancyDetailsResponse");
    private final static QName _ParseException_QNAME = new QName("http://webservice.service.wise.remote/", "ParseException");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.TenancyDetailsService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link IOException }
     * 
     */
    public IOException createIOException() {
        return new IOException();
    }

    /**
     * Create an instance of {@link TenancyDetailsReqContract }
     * 
     */
    public TenancyDetailsReqContract createTenancyDetailsReqContract() {
        return new TenancyDetailsReqContract();
    }

    /**
     * Create an instance of {@link SetTenancyDetailsResponse }
     * 
     */
    public SetTenancyDetailsResponse createSetTenancyDetailsResponse() {
        return new SetTenancyDetailsResponse();
    }

    /**
     * Create an instance of {@link TenancyCreationReqContract }
     * 
     */
    public TenancyCreationReqContract createTenancyCreationReqContract() {
        return new TenancyCreationReqContract();
    }

    /**
     * Create an instance of {@link HashMap }
     * 
     */
    public HashMap createHashMap() {
        return new HashMap();
    }

    /**
     * Create an instance of {@link SetTenancyDetails }
     * 
     */
    public SetTenancyDetails createSetTenancyDetails() {
        return new SetTenancyDetails();
    }

    /**
     * Create an instance of {@link TenancyDetailsRespContract }
     * 
     */
    public TenancyDetailsRespContract createTenancyDetailsRespContract() {
        return new TenancyDetailsRespContract();
    }

    /**
     * Create an instance of {@link GetTenancyDetails }
     * 
     */
    public GetTenancyDetails createGetTenancyDetails() {
        return new GetTenancyDetails();
    }

    /**
     * Create an instance of {@link ParseException }
     * 
     */
    public ParseException createParseException() {
        return new ParseException();
    }

    /**
     * Create an instance of {@link TenancyDetailsRespContract.ParentTenancyUserIdMailIdList }
     * 
     */
    public TenancyDetailsRespContract.ParentTenancyUserIdMailIdList createTenancyDetailsRespContractParentTenancyUserIdMailIdList() {
        return new TenancyDetailsRespContract.ParentTenancyUserIdMailIdList();
    }

    /**
     * Create an instance of {@link TenancyDetailsRespContract.ParentTenancyUserIdMailIdList.Entry }
     * 
     */
    public TenancyDetailsRespContract.ParentTenancyUserIdMailIdList.Entry createTenancyDetailsRespContractParentTenancyUserIdMailIdListEntry() {
        return new TenancyDetailsRespContract.ParentTenancyUserIdMailIdList.Entry();
    }

    /**
     * Create an instance of {@link GetTenancyDetailsResponse }
     * 
     */
    public GetTenancyDetailsResponse createGetTenancyDetailsResponse() {
        return new GetTenancyDetailsResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetTenancyDetailsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetTenancyDetailsResponse")
    public JAXBElement<GetTenancyDetailsResponse> createGetTenancyDetailsResponse(GetTenancyDetailsResponse value) {
        return new JAXBElement<GetTenancyDetailsResponse>(_GetTenancyDetailsResponse_QNAME, GetTenancyDetailsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IOException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "IOException")
    public JAXBElement<IOException> createIOException(IOException value) {
        return new JAXBElement<IOException>(_IOException_QNAME, IOException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetTenancyDetails }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetTenancyDetails")
    public JAXBElement<GetTenancyDetails> createGetTenancyDetails(GetTenancyDetails value) {
        return new JAXBElement<GetTenancyDetails>(_GetTenancyDetails_QNAME, GetTenancyDetails.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetTenancyDetails }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "setTenancyDetails")
    public JAXBElement<SetTenancyDetails> createSetTenancyDetails(SetTenancyDetails value) {
        return new JAXBElement<SetTenancyDetails>(_SetTenancyDetails_QNAME, SetTenancyDetails.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetTenancyDetailsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "setTenancyDetailsResponse")
    public JAXBElement<SetTenancyDetailsResponse> createSetTenancyDetailsResponse(SetTenancyDetailsResponse value) {
        return new JAXBElement<SetTenancyDetailsResponse>(_SetTenancyDetailsResponse_QNAME, SetTenancyDetailsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ParseException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "ParseException")
    public JAXBElement<ParseException> createParseException(ParseException value) {
        return new JAXBElement<ParseException>(_ParseException_QNAME, ParseException.class, null, value);
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
