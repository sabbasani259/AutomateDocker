
package remote.wise.client.ModelCodeService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.ModelCodeService package. 
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

    private final static QName _GetModelCodeMapResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetModelCodeMapResponse");
    private final static QName _GetModelCodeMap_QNAME = new QName("http://webservice.service.wise.remote/", "GetModelCodeMap");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.ModelCodeService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetModelCodeMap }
     * 
     */
    public GetModelCodeMap createGetModelCodeMap() {
        return new GetModelCodeMap();
    }

    /**
     * Create an instance of {@link ModelCodeResponseContract.ModelCodeMap.Entry }
     * 
     */
    public ModelCodeResponseContract.ModelCodeMap.Entry createModelCodeResponseContractModelCodeMapEntry() {
        return new ModelCodeResponseContract.ModelCodeMap.Entry();
    }

    /**
     * Create an instance of {@link GetModelCodeMapResponse }
     * 
     */
    public GetModelCodeMapResponse createGetModelCodeMapResponse() {
        return new GetModelCodeMapResponse();
    }

    /**
     * Create an instance of {@link HashMap }
     * 
     */
    public HashMap createHashMap() {
        return new HashMap();
    }

    /**
     * Create an instance of {@link ModelCodeResponseContract.ModelCodeMap }
     * 
     */
    public ModelCodeResponseContract.ModelCodeMap createModelCodeResponseContractModelCodeMap() {
        return new ModelCodeResponseContract.ModelCodeMap();
    }

    /**
     * Create an instance of {@link ModelCodeResponseContract }
     * 
     */
    public ModelCodeResponseContract createModelCodeResponseContract() {
        return new ModelCodeResponseContract();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetModelCodeMapResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetModelCodeMapResponse")
    public JAXBElement<GetModelCodeMapResponse> createGetModelCodeMapResponse(GetModelCodeMapResponse value) {
        return new JAXBElement<GetModelCodeMapResponse>(_GetModelCodeMapResponse_QNAME, GetModelCodeMapResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetModelCodeMap }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetModelCodeMap")
    public JAXBElement<GetModelCodeMap> createGetModelCodeMap(GetModelCodeMap value) {
        return new JAXBElement<GetModelCodeMap>(_GetModelCodeMap_QNAME, GetModelCodeMap.class, null, value);
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
