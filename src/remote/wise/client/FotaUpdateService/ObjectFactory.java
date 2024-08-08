
package remote.wise.client.FotaUpdateService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.FotaUpdateService package. 
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

    private final static QName _FotaUpdateResponse_QNAME = new QName("http://webservice.service.wise.remote/", "FotaUpdateResponse");
    private final static QName _FotaUpdate_QNAME = new QName("http://webservice.service.wise.remote/", "FotaUpdate");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.FotaUpdateService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link FotaUpdateReqContract }
     * 
     */
    public FotaUpdateReqContract createFotaUpdateReqContract() {
        return new FotaUpdateReqContract();
    }

    /**
     * Create an instance of {@link FotaUpdateRespContract }
     * 
     */
    public FotaUpdateRespContract createFotaUpdateRespContract() {
        return new FotaUpdateRespContract();
    }

    /**
     * Create an instance of {@link FotaUpdate }
     * 
     */
    public FotaUpdate createFotaUpdate() {
        return new FotaUpdate();
    }

    /**
     * Create an instance of {@link FotaUpdateResponse }
     * 
     */
    public FotaUpdateResponse createFotaUpdateResponse() {
        return new FotaUpdateResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FotaUpdateResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "FotaUpdateResponse")
    public JAXBElement<FotaUpdateResponse> createFotaUpdateResponse(FotaUpdateResponse value) {
        return new JAXBElement<FotaUpdateResponse>(_FotaUpdateResponse_QNAME, FotaUpdateResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FotaUpdate }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "FotaUpdate")
    public JAXBElement<FotaUpdate> createFotaUpdate(FotaUpdate value) {
        return new JAXBElement<FotaUpdate>(_FotaUpdate_QNAME, FotaUpdate.class, null, value);
    }

}
