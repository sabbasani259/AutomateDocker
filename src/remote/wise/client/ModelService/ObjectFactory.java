
package remote.wise.client.ModelService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.ModelService package. 
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

    private final static QName _GetModelMap_QNAME = new QName("http://webservice.service.wise.remote/", "GetModelMap");
    private final static QName _GetModelMapResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetModelMapResponse");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.ModelService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link HashMap }
     * 
     */
    public HashMap createHashMap() {
        return new HashMap();
    }

    /**
     * Create an instance of {@link ModelResponseContract }
     * 
     */
    public ModelResponseContract createModelResponseContract() {
        return new ModelResponseContract();
    }

    /**
     * Create an instance of {@link ModelResponseContract.ModelList.Entry }
     * 
     */
    public ModelResponseContract.ModelList.Entry createModelResponseContractModelListEntry() {
        return new ModelResponseContract.ModelList.Entry();
    }

    /**
     * Create an instance of {@link GetModelMapResponse }
     * 
     */
    public GetModelMapResponse createGetModelMapResponse() {
        return new GetModelMapResponse();
    }

    /**
     * Create an instance of {@link ModelResponseContract.ModelList }
     * 
     */
    public ModelResponseContract.ModelList createModelResponseContractModelList() {
        return new ModelResponseContract.ModelList();
    }

    /**
     * Create an instance of {@link GetModelMap }
     * 
     */
    public GetModelMap createGetModelMap() {
        return new GetModelMap();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetModelMap }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetModelMap")
    public JAXBElement<GetModelMap> createGetModelMap(GetModelMap value) {
        return new JAXBElement<GetModelMap>(_GetModelMap_QNAME, GetModelMap.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetModelMapResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetModelMapResponse")
    public JAXBElement<GetModelMapResponse> createGetModelMapResponse(GetModelMapResponse value) {
        return new JAXBElement<GetModelMapResponse>(_GetModelMapResponse_QNAME, GetModelMapResponse.class, null, value);
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
