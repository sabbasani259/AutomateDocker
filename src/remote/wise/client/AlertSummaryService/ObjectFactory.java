
package remote.wise.client.AlertSummaryService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.AlertSummaryService package. 
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

    private final static QName _AlertSummaryDetailsResponse_QNAME = new QName("http://webservice.service.wise.remote/", "AlertSummaryDetailsResponse");
    private final static QName _AlertSummaryDetails_QNAME = new QName("http://webservice.service.wise.remote/", "AlertSummaryDetails");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.AlertSummaryService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link AlertSummaryRespContract }
     * 
     */
    public AlertSummaryRespContract createAlertSummaryRespContract() {
        return new AlertSummaryRespContract();
    }

    /**
     * Create an instance of {@link AlertSummaryReqContract }
     * 
     */
    public AlertSummaryReqContract createAlertSummaryReqContract() {
        return new AlertSummaryReqContract();
    }

    /**
     * Create an instance of {@link AlertSummaryDetails }
     * 
     */
    public AlertSummaryDetails createAlertSummaryDetails() {
        return new AlertSummaryDetails();
    }

    /**
     * Create an instance of {@link AlertSummaryDetailsResponse }
     * 
     */
    public AlertSummaryDetailsResponse createAlertSummaryDetailsResponse() {
        return new AlertSummaryDetailsResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AlertSummaryDetailsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "AlertSummaryDetailsResponse")
    public JAXBElement<AlertSummaryDetailsResponse> createAlertSummaryDetailsResponse(AlertSummaryDetailsResponse value) {
        return new JAXBElement<AlertSummaryDetailsResponse>(_AlertSummaryDetailsResponse_QNAME, AlertSummaryDetailsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AlertSummaryDetails }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "AlertSummaryDetails")
    public JAXBElement<AlertSummaryDetails> createAlertSummaryDetails(AlertSummaryDetails value) {
        return new JAXBElement<AlertSummaryDetails>(_AlertSummaryDetails_QNAME, AlertSummaryDetails.class, null, value);
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
