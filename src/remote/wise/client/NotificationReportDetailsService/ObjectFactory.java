
package remote.wise.client.NotificationReportDetailsService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.NotificationReportDetailsService package. 
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

    private final static QName _GetNotificationReportDetailsService_QNAME = new QName("http://webservice.service.wise.remote/", "GetNotificationReportDetailsService");
    private final static QName _GetNotificationReportDetailsServiceResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetNotificationReportDetailsServiceResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.NotificationReportDetailsService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link NotificationReportDetailsRespContract }
     * 
     */
    public NotificationReportDetailsRespContract createNotificationReportDetailsRespContract() {
        return new NotificationReportDetailsRespContract();
    }

    /**
     * Create an instance of {@link GetNotificationReportDetailsService }
     * 
     */
    public GetNotificationReportDetailsService createGetNotificationReportDetailsService() {
        return new GetNotificationReportDetailsService();
    }

    /**
     * Create an instance of {@link NotificationReportDetailsReqContract }
     * 
     */
    public NotificationReportDetailsReqContract createNotificationReportDetailsReqContract() {
        return new NotificationReportDetailsReqContract();
    }

    /**
     * Create an instance of {@link GetNotificationReportDetailsServiceResponse }
     * 
     */
    public GetNotificationReportDetailsServiceResponse createGetNotificationReportDetailsServiceResponse() {
        return new GetNotificationReportDetailsServiceResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetNotificationReportDetailsService }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetNotificationReportDetailsService")
    public JAXBElement<GetNotificationReportDetailsService> createGetNotificationReportDetailsService(GetNotificationReportDetailsService value) {
        return new JAXBElement<GetNotificationReportDetailsService>(_GetNotificationReportDetailsService_QNAME, GetNotificationReportDetailsService.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetNotificationReportDetailsServiceResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetNotificationReportDetailsServiceResponse")
    public JAXBElement<GetNotificationReportDetailsServiceResponse> createGetNotificationReportDetailsServiceResponse(GetNotificationReportDetailsServiceResponse value) {
        return new JAXBElement<GetNotificationReportDetailsServiceResponse>(_GetNotificationReportDetailsServiceResponse_QNAME, GetNotificationReportDetailsServiceResponse.class, null, value);
    }

}
