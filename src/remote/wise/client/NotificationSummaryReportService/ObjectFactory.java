
package remote.wise.client.NotificationSummaryReportService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.NotificationSummaryReportService package. 
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

    private final static QName _NotificationSummaryResponse_QNAME = new QName("http://webservice.service.wise.remote/", "NotificationSummaryResponse");
    private final static QName _NotificationSummary_QNAME = new QName("http://webservice.service.wise.remote/", "NotificationSummary");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.NotificationSummaryReportService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link NotificationSummaryReportRespContract }
     * 
     */
    public NotificationSummaryReportRespContract createNotificationSummaryReportRespContract() {
        return new NotificationSummaryReportRespContract();
    }

    /**
     * Create an instance of {@link NotificationSummaryReportRespContract.NameCount.Entry }
     * 
     */
    public NotificationSummaryReportRespContract.NameCount.Entry createNotificationSummaryReportRespContractNameCountEntry() {
        return new NotificationSummaryReportRespContract.NameCount.Entry();
    }

    /**
     * Create an instance of {@link NotificationSummaryReportRespContract.NameCount }
     * 
     */
    public NotificationSummaryReportRespContract.NameCount createNotificationSummaryReportRespContractNameCount() {
        return new NotificationSummaryReportRespContract.NameCount();
    }

    /**
     * Create an instance of {@link NotificationSummaryResponse }
     * 
     */
    public NotificationSummaryResponse createNotificationSummaryResponse() {
        return new NotificationSummaryResponse();
    }

    /**
     * Create an instance of {@link HashMap }
     * 
     */
    public HashMap createHashMap() {
        return new HashMap();
    }

    /**
     * Create an instance of {@link NotificationSummaryReportReqContract }
     * 
     */
    public NotificationSummaryReportReqContract createNotificationSummaryReportReqContract() {
        return new NotificationSummaryReportReqContract();
    }

    /**
     * Create an instance of {@link NotificationSummary }
     * 
     */
    public NotificationSummary createNotificationSummary() {
        return new NotificationSummary();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NotificationSummaryResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "NotificationSummaryResponse")
    public JAXBElement<NotificationSummaryResponse> createNotificationSummaryResponse(NotificationSummaryResponse value) {
        return new JAXBElement<NotificationSummaryResponse>(_NotificationSummaryResponse_QNAME, NotificationSummaryResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NotificationSummary }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "NotificationSummary")
    public JAXBElement<NotificationSummary> createNotificationSummary(NotificationSummary value) {
        return new JAXBElement<NotificationSummary>(_NotificationSummary_QNAME, NotificationSummary.class, null, value);
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
