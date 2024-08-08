
package remote.wise.client.EventSubscriptionService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.EventSubscriptionService package. 
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

    private final static QName _GetEventSubscription_QNAME = new QName("http://webservice.service.wise.remote/", "GetEventSubscription");
    private final static QName _SetEventSubscription_QNAME = new QName("http://webservice.service.wise.remote/", "SetEventSubscription");
    private final static QName _SetEventSubscriptionResponse_QNAME = new QName("http://webservice.service.wise.remote/", "SetEventSubscriptionResponse");
    private final static QName _GetEventSubscriptionResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetEventSubscriptionResponse");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.EventSubscriptionService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link EventSubscriptionReqContract }
     * 
     */
    public EventSubscriptionReqContract createEventSubscriptionReqContract() {
        return new EventSubscriptionReqContract();
    }

    /**
     * Create an instance of {@link SetEventSubscriptionResponse }
     * 
     */
    public SetEventSubscriptionResponse createSetEventSubscriptionResponse() {
        return new SetEventSubscriptionResponse();
    }

    /**
     * Create an instance of {@link EventSubscriptionRespContract }
     * 
     */
    public EventSubscriptionRespContract createEventSubscriptionRespContract() {
        return new EventSubscriptionRespContract();
    }

    /**
     * Create an instance of {@link SetEventSubscription }
     * 
     */
    public SetEventSubscription createSetEventSubscription() {
        return new SetEventSubscription();
    }

    /**
     * Create an instance of {@link GetEventSubscriptionResponse }
     * 
     */
    public GetEventSubscriptionResponse createGetEventSubscriptionResponse() {
        return new GetEventSubscriptionResponse();
    }

    /**
     * Create an instance of {@link GetEventSubscription }
     * 
     */
    public GetEventSubscription createGetEventSubscription() {
        return new GetEventSubscription();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetEventSubscription }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetEventSubscription")
    public JAXBElement<GetEventSubscription> createGetEventSubscription(GetEventSubscription value) {
        return new JAXBElement<GetEventSubscription>(_GetEventSubscription_QNAME, GetEventSubscription.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetEventSubscription }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "SetEventSubscription")
    public JAXBElement<SetEventSubscription> createSetEventSubscription(SetEventSubscription value) {
        return new JAXBElement<SetEventSubscription>(_SetEventSubscription_QNAME, SetEventSubscription.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetEventSubscriptionResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "SetEventSubscriptionResponse")
    public JAXBElement<SetEventSubscriptionResponse> createSetEventSubscriptionResponse(SetEventSubscriptionResponse value) {
        return new JAXBElement<SetEventSubscriptionResponse>(_SetEventSubscriptionResponse_QNAME, SetEventSubscriptionResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetEventSubscriptionResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetEventSubscriptionResponse")
    public JAXBElement<GetEventSubscriptionResponse> createGetEventSubscriptionResponse(GetEventSubscriptionResponse value) {
        return new JAXBElement<GetEventSubscriptionResponse>(_GetEventSubscriptionResponse_QNAME, GetEventSubscriptionResponse.class, null, value);
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
