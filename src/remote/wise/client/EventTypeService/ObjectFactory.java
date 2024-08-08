
package remote.wise.client.EventTypeService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.EventTypeService package. 
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

    private final static QName _GetEventTypesResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetEventTypesResponse");
    private final static QName _GetEventTypes_QNAME = new QName("http://webservice.service.wise.remote/", "GetEventTypes");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.EventTypeService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetEventTypes }
     * 
     */
    public GetEventTypes createGetEventTypes() {
        return new GetEventTypes();
    }

    /**
     * Create an instance of {@link GetEventTypesResponse }
     * 
     */
    public GetEventTypesResponse createGetEventTypesResponse() {
        return new GetEventTypesResponse();
    }

    /**
     * Create an instance of {@link EventTypeRespContract }
     * 
     */
    public EventTypeRespContract createEventTypeRespContract() {
        return new EventTypeRespContract();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetEventTypesResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetEventTypesResponse")
    public JAXBElement<GetEventTypesResponse> createGetEventTypesResponse(GetEventTypesResponse value) {
        return new JAXBElement<GetEventTypesResponse>(_GetEventTypesResponse_QNAME, GetEventTypesResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetEventTypes }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetEventTypes")
    public JAXBElement<GetEventTypes> createGetEventTypes(GetEventTypes value) {
        return new JAXBElement<GetEventTypes>(_GetEventTypes_QNAME, GetEventTypes.class, null, value);
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
