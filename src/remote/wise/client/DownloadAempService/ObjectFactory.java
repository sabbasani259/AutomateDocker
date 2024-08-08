
package remote.wise.client.DownloadAempService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.DownloadAempService package. 
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

    private final static QName _GetDownloadAempResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetDownloadAempResponse");
    private final static QName _GetDownloadAemp_QNAME = new QName("http://webservice.service.wise.remote/", "GetDownloadAemp");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.DownloadAempService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetDownloadAempResponse }
     * 
     */
    public GetDownloadAempResponse createGetDownloadAempResponse() {
        return new GetDownloadAempResponse();
    }

    /**
     * Create an instance of {@link DownloadAempRespContract }
     * 
     */
    public DownloadAempRespContract createDownloadAempRespContract() {
        return new DownloadAempRespContract();
    }

    /**
     * Create an instance of {@link GetDownloadAemp }
     * 
     */
    public GetDownloadAemp createGetDownloadAemp() {
        return new GetDownloadAemp();
    }

    /**
     * Create an instance of {@link DownloadAempReqContract }
     * 
     */
    public DownloadAempReqContract createDownloadAempReqContract() {
        return new DownloadAempReqContract();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetDownloadAempResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetDownloadAempResponse")
    public JAXBElement<GetDownloadAempResponse> createGetDownloadAempResponse(GetDownloadAempResponse value) {
        return new JAXBElement<GetDownloadAempResponse>(_GetDownloadAempResponse_QNAME, GetDownloadAempResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetDownloadAemp }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetDownloadAemp")
    public JAXBElement<GetDownloadAemp> createGetDownloadAemp(GetDownloadAemp value) {
        return new JAXBElement<GetDownloadAemp>(_GetDownloadAemp_QNAME, GetDownloadAemp.class, null, value);
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
