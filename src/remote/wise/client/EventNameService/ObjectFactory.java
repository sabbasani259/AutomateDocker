
package remote.wise.client.EventNameService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.EventNameService package. 
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

    private final static QName _GetReportEvents_QNAME = new QName("http://webservice.service.wise.remote/", "GetReportEvents");
    private final static QName _GetReportEventsResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetReportEventsResponse");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.EventNameService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link EventNameRespContract }
     * 
     */
    public EventNameRespContract createEventNameRespContract() {
        return new EventNameRespContract();
    }

    /**
     * Create an instance of {@link EventNameReqContract }
     * 
     */
    public EventNameReqContract createEventNameReqContract() {
        return new EventNameReqContract();
    }

    /**
     * Create an instance of {@link GetReportEventsResponse }
     * 
     */
    public GetReportEventsResponse createGetReportEventsResponse() {
        return new GetReportEventsResponse();
    }

    /**
     * Create an instance of {@link GetReportEvents }
     * 
     */
    public GetReportEvents createGetReportEvents() {
        return new GetReportEvents();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetReportEvents }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetReportEvents")
    public JAXBElement<GetReportEvents> createGetReportEvents(GetReportEvents value) {
        return new JAXBElement<GetReportEvents>(_GetReportEvents_QNAME, GetReportEvents.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetReportEventsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetReportEventsResponse")
    public JAXBElement<GetReportEventsResponse> createGetReportEventsResponse(GetReportEventsResponse value) {
        return new JAXBElement<GetReportEventsResponse>(_GetReportEventsResponse_QNAME, GetReportEventsResponse.class, null, value);
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
