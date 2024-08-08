
package remote.wise.client.NotificationSummaryService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.NotificationSummaryService package. 
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

    private final static QName _GetNotificationSummaryService_QNAME = new QName("http://webservice.service.wise.remote/", "GetNotificationSummaryService");
    private final static QName _GetNotificationSummaryServiceResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetNotificationSummaryServiceResponse");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.NotificationSummaryService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link NotificationSummaryRespContract }
     * 
     */
    public NotificationSummaryRespContract createNotificationSummaryRespContract() {
        return new NotificationSummaryRespContract();
    }

    /**
     * Create an instance of {@link GetNotificationSummaryServiceResponse }
     * 
     */
    public GetNotificationSummaryServiceResponse createGetNotificationSummaryServiceResponse() {
        return new GetNotificationSummaryServiceResponse();
    }

    /**
     * Create an instance of {@link NotificationSummaryReqContract }
     * 
     */
    public NotificationSummaryReqContract createNotificationSummaryReqContract() {
        return new NotificationSummaryReqContract();
    }

    /**
     * Create an instance of {@link GetNotificationSummaryService }
     * 
     */
    public GetNotificationSummaryService createGetNotificationSummaryService() {
        return new GetNotificationSummaryService();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetNotificationSummaryService }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetNotificationSummaryService")
    public JAXBElement<GetNotificationSummaryService> createGetNotificationSummaryService(GetNotificationSummaryService value) {
        return new JAXBElement<GetNotificationSummaryService>(_GetNotificationSummaryService_QNAME, GetNotificationSummaryService.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetNotificationSummaryServiceResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetNotificationSummaryServiceResponse")
    public JAXBElement<GetNotificationSummaryServiceResponse> createGetNotificationSummaryServiceResponse(GetNotificationSummaryServiceResponse value) {
        return new JAXBElement<GetNotificationSummaryServiceResponse>(_GetNotificationSummaryServiceResponse_QNAME, GetNotificationSummaryServiceResponse.class, null, value);
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
