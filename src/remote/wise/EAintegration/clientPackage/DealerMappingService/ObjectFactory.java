
package remote.wise.EAintegration.clientPackage.DealerMappingService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.EAintegration.clientPackage.DealerMappingService package. 
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

    private final static QName _SetDealerMapping_QNAME = new QName("http://webservice.service.wise.remote/", "setDealerMapping");
    private final static QName _SetDealerMappingResponse_QNAME = new QName("http://webservice.service.wise.remote/", "setDealerMappingResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.EAintegration.clientPackage.DealerMappingService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SetDealerMapping }
     * 
     */
    public SetDealerMapping createSetDealerMapping() {
        return new SetDealerMapping();
    }

    /**
     * Create an instance of {@link SetDealerMappingResponse }
     * 
     */
    public SetDealerMappingResponse createSetDealerMappingResponse() {
        return new SetDealerMappingResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetDealerMapping }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "setDealerMapping")
    public JAXBElement<SetDealerMapping> createSetDealerMapping(SetDealerMapping value) {
        return new JAXBElement<SetDealerMapping>(_SetDealerMapping_QNAME, SetDealerMapping.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetDealerMappingResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "setDealerMappingResponse")
    public JAXBElement<SetDealerMappingResponse> createSetDealerMappingResponse(SetDealerMappingResponse value) {
        return new JAXBElement<SetDealerMappingResponse>(_SetDealerMappingResponse_QNAME, SetDealerMappingResponse.class, null, value);
    }

}
