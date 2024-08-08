
package remote.wise.clientLandmarkCategoryService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.clientLandmarkCategoryService package. 
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

    private final static QName _SetLandmarkCategoryResponse_QNAME = new QName("http://webservice.service.wise.remote/", "SetLandmarkCategoryResponse");
    private final static QName _GetLandmarkCategoryResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetLandmarkCategoryResponse");
    private final static QName _GetLandmarkCategory_QNAME = new QName("http://webservice.service.wise.remote/", "GetLandmarkCategory");
    private final static QName _SetLandmarkCategory_QNAME = new QName("http://webservice.service.wise.remote/", "SetLandmarkCategory");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.clientLandmarkCategoryService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetLandmarkCategoryResponse }
     * 
     */
    public GetLandmarkCategoryResponse createGetLandmarkCategoryResponse() {
        return new GetLandmarkCategoryResponse();
    }

    /**
     * Create an instance of {@link SetLandmarkCategory }
     * 
     */
    public SetLandmarkCategory createSetLandmarkCategory() {
        return new SetLandmarkCategory();
    }

    /**
     * Create an instance of {@link LandmarkCategoryReqContract }
     * 
     */
    public LandmarkCategoryReqContract createLandmarkCategoryReqContract() {
        return new LandmarkCategoryReqContract();
    }

    /**
     * Create an instance of {@link GetLandmarkCategory }
     * 
     */
    public GetLandmarkCategory createGetLandmarkCategory() {
        return new GetLandmarkCategory();
    }

    /**
     * Create an instance of {@link LandmarkCategoryRespContract }
     * 
     */
    public LandmarkCategoryRespContract createLandmarkCategoryRespContract() {
        return new LandmarkCategoryRespContract();
    }

    /**
     * Create an instance of {@link SetLandmarkCategoryResponse }
     * 
     */
    public SetLandmarkCategoryResponse createSetLandmarkCategoryResponse() {
        return new SetLandmarkCategoryResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetLandmarkCategoryResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "SetLandmarkCategoryResponse")
    public JAXBElement<SetLandmarkCategoryResponse> createSetLandmarkCategoryResponse(SetLandmarkCategoryResponse value) {
        return new JAXBElement<SetLandmarkCategoryResponse>(_SetLandmarkCategoryResponse_QNAME, SetLandmarkCategoryResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetLandmarkCategoryResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetLandmarkCategoryResponse")
    public JAXBElement<GetLandmarkCategoryResponse> createGetLandmarkCategoryResponse(GetLandmarkCategoryResponse value) {
        return new JAXBElement<GetLandmarkCategoryResponse>(_GetLandmarkCategoryResponse_QNAME, GetLandmarkCategoryResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetLandmarkCategory }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetLandmarkCategory")
    public JAXBElement<GetLandmarkCategory> createGetLandmarkCategory(GetLandmarkCategory value) {
        return new JAXBElement<GetLandmarkCategory>(_GetLandmarkCategory_QNAME, GetLandmarkCategory.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetLandmarkCategory }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "SetLandmarkCategory")
    public JAXBElement<SetLandmarkCategory> createSetLandmarkCategory(SetLandmarkCategory value) {
        return new JAXBElement<SetLandmarkCategory>(_SetLandmarkCategory_QNAME, SetLandmarkCategory.class, null, value);
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
