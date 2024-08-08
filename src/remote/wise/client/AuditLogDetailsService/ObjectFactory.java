
package remote.wise.client.AuditLogDetailsService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.AuditLogDetailsService package. 
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

    private final static QName _GetAuditLogDetails_QNAME = new QName("http://webservice.service.wise.remote/", "GetAuditLogDetails");
    private final static QName _GetAuditLogDetailsResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetAuditLogDetailsResponse");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.AuditLogDetailsService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link AuditLogReqContract }
     * 
     */
    public AuditLogReqContract createAuditLogReqContract() {
        return new AuditLogReqContract();
    }

    /**
     * Create an instance of {@link GetAuditLogDetailsResponse }
     * 
     */
    public GetAuditLogDetailsResponse createGetAuditLogDetailsResponse() {
        return new GetAuditLogDetailsResponse();
    }

    /**
     * Create an instance of {@link AuditLogRespContract }
     * 
     */
    public AuditLogRespContract createAuditLogRespContract() {
        return new AuditLogRespContract();
    }

    /**
     * Create an instance of {@link GetAuditLogDetails }
     * 
     */
    public GetAuditLogDetails createGetAuditLogDetails() {
        return new GetAuditLogDetails();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAuditLogDetails }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetAuditLogDetails")
    public JAXBElement<GetAuditLogDetails> createGetAuditLogDetails(GetAuditLogDetails value) {
        return new JAXBElement<GetAuditLogDetails>(_GetAuditLogDetails_QNAME, GetAuditLogDetails.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAuditLogDetailsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetAuditLogDetailsResponse")
    public JAXBElement<GetAuditLogDetailsResponse> createGetAuditLogDetailsResponse(GetAuditLogDetailsResponse value) {
        return new JAXBElement<GetAuditLogDetailsResponse>(_GetAuditLogDetailsResponse_QNAME, GetAuditLogDetailsResponse.class, null, value);
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
