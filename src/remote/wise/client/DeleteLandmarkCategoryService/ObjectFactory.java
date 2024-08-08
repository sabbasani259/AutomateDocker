
package remote.wise.client.DeleteLandmarkCategoryService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.DeleteLandmarkCategoryService package. 
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

    private final static QName _SetDeleteLandmarkCategory_QNAME = new QName("http://webservice.service.wise.remote/", "SetDeleteLandmarkCategory");
    private final static QName _SetDeleteLandmarkCategoryResponse_QNAME = new QName("http://webservice.service.wise.remote/", "SetDeleteLandmarkCategoryResponse");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.DeleteLandmarkCategoryService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SetDeleteLandmarkCategoryResponse }
     * 
     */
    public SetDeleteLandmarkCategoryResponse createSetDeleteLandmarkCategoryResponse() {
        return new SetDeleteLandmarkCategoryResponse();
    }

    /**
     * Create an instance of {@link DeleteLandmarkCategoryRespContract }
     * 
     */
    public DeleteLandmarkCategoryRespContract createDeleteLandmarkCategoryRespContract() {
        return new DeleteLandmarkCategoryRespContract();
    }

    /**
     * Create an instance of {@link SetDeleteLandmarkCategory }
     * 
     */
    public SetDeleteLandmarkCategory createSetDeleteLandmarkCategory() {
        return new SetDeleteLandmarkCategory();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetDeleteLandmarkCategory }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "SetDeleteLandmarkCategory")
    public JAXBElement<SetDeleteLandmarkCategory> createSetDeleteLandmarkCategory(SetDeleteLandmarkCategory value) {
        return new JAXBElement<SetDeleteLandmarkCategory>(_SetDeleteLandmarkCategory_QNAME, SetDeleteLandmarkCategory.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetDeleteLandmarkCategoryResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "SetDeleteLandmarkCategoryResponse")
    public JAXBElement<SetDeleteLandmarkCategoryResponse> createSetDeleteLandmarkCategoryResponse(SetDeleteLandmarkCategoryResponse value) {
        return new JAXBElement<SetDeleteLandmarkCategoryResponse>(_SetDeleteLandmarkCategoryResponse_QNAME, SetDeleteLandmarkCategoryResponse.class, null, value);
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
