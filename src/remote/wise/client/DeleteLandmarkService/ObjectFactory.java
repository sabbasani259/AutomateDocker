
package remote.wise.client.DeleteLandmarkService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.DeleteLandmarkService package. 
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

    private final static QName _SetDeleteLandmarkResponse_QNAME = new QName("http://webservice.service.wise.remote/", "SetDeleteLandmarkResponse");
    private final static QName _SetDeleteLandmark_QNAME = new QName("http://webservice.service.wise.remote/", "SetDeleteLandmark");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.DeleteLandmarkService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link DeleteLandmarkRespContract }
     * 
     */
    public DeleteLandmarkRespContract createDeleteLandmarkRespContract() {
        return new DeleteLandmarkRespContract();
    }

    /**
     * Create an instance of {@link SetDeleteLandmarkResponse }
     * 
     */
    public SetDeleteLandmarkResponse createSetDeleteLandmarkResponse() {
        return new SetDeleteLandmarkResponse();
    }

    /**
     * Create an instance of {@link SetDeleteLandmark }
     * 
     */
    public SetDeleteLandmark createSetDeleteLandmark() {
        return new SetDeleteLandmark();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetDeleteLandmarkResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "SetDeleteLandmarkResponse")
    public JAXBElement<SetDeleteLandmarkResponse> createSetDeleteLandmarkResponse(SetDeleteLandmarkResponse value) {
        return new JAXBElement<SetDeleteLandmarkResponse>(_SetDeleteLandmarkResponse_QNAME, SetDeleteLandmarkResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetDeleteLandmark }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "SetDeleteLandmark")
    public JAXBElement<SetDeleteLandmark> createSetDeleteLandmark(SetDeleteLandmark value) {
        return new JAXBElement<SetDeleteLandmark>(_SetDeleteLandmark_QNAME, SetDeleteLandmark.class, null, value);
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
