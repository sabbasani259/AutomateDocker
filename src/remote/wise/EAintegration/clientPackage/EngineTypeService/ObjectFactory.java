
package remote.wise.EAintegration.clientPackage.EngineTypeService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.EAintegration.clientPackage.EngineTypeService package. 
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

    private final static QName _SetEngineTypeDetails_QNAME = new QName("http://webservice.service.wise.remote/", "setEngineTypeDetails");
    private final static QName _SetEngineTypeDetailsResponse_QNAME = new QName("http://webservice.service.wise.remote/", "setEngineTypeDetailsResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.EAintegration.clientPackage.EngineTypeService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SetEngineTypeDetailsResponse }
     * 
     */
    public SetEngineTypeDetailsResponse createSetEngineTypeDetailsResponse() {
        return new SetEngineTypeDetailsResponse();
    }

    /**
     * Create an instance of {@link SetEngineTypeDetails }
     * 
     */
    public SetEngineTypeDetails createSetEngineTypeDetails() {
        return new SetEngineTypeDetails();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetEngineTypeDetails }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "setEngineTypeDetails")
    public JAXBElement<SetEngineTypeDetails> createSetEngineTypeDetails(SetEngineTypeDetails value) {
        return new JAXBElement<SetEngineTypeDetails>(_SetEngineTypeDetails_QNAME, SetEngineTypeDetails.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetEngineTypeDetailsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "setEngineTypeDetailsResponse")
    public JAXBElement<SetEngineTypeDetailsResponse> createSetEngineTypeDetailsResponse(SetEngineTypeDetailsResponse value) {
        return new JAXBElement<SetEngineTypeDetailsResponse>(_SetEngineTypeDetailsResponse_QNAME, SetEngineTypeDetailsResponse.class, null, value);
    }

}
