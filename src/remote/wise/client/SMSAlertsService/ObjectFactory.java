
package remote.wise.client.SMSAlertsService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.SMSAlertsService package. 
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

    private final static QName _GetSMSAlertsResponse_QNAME = new QName("http://webservice.service.wise.remote/", "getSMSAlertsResponse");
    private final static QName _GetSMSAlerts_QNAME = new QName("http://webservice.service.wise.remote/", "getSMSAlerts");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.SMSAlertsService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SmsAlertsRespContract }
     * 
     */
    public SmsAlertsRespContract createSmsAlertsRespContract() {
        return new SmsAlertsRespContract();
    }

    /**
     * Create an instance of {@link GetSMSAlertsResponse }
     * 
     */
    public GetSMSAlertsResponse createGetSMSAlertsResponse() {
        return new GetSMSAlertsResponse();
    }

    /**
     * Create an instance of {@link SmsAlertsReqContract }
     * 
     */
    public SmsAlertsReqContract createSmsAlertsReqContract() {
        return new SmsAlertsReqContract();
    }

    /**
     * Create an instance of {@link GetSMSAlerts }
     * 
     */
    public GetSMSAlerts createGetSMSAlerts() {
        return new GetSMSAlerts();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetSMSAlertsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "getSMSAlertsResponse")
    public JAXBElement<GetSMSAlertsResponse> createGetSMSAlertsResponse(GetSMSAlertsResponse value) {
        return new JAXBElement<GetSMSAlertsResponse>(_GetSMSAlertsResponse_QNAME, GetSMSAlertsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetSMSAlerts }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "getSMSAlerts")
    public JAXBElement<GetSMSAlerts> createGetSMSAlerts(GetSMSAlerts value) {
        return new JAXBElement<GetSMSAlerts>(_GetSMSAlerts_QNAME, GetSMSAlerts.class, null, value);
    }

}
