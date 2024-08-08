
package remote.wise.client.SMSAlertDetailsService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.SMSAlertDetailsService package. 
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

    private final static QName _GetSMSAlertDetailsResponse_QNAME = new QName("http://webservice.service.wise.remote/", "getSMSAlertDetailsResponse");
    private final static QName _GetSMSAlertDetails_QNAME = new QName("http://webservice.service.wise.remote/", "getSMSAlertDetails");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.SMSAlertDetailsService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetSMSAlertDetailsResponse }
     * 
     */
    public GetSMSAlertDetailsResponse createGetSMSAlertDetailsResponse() {
        return new GetSMSAlertDetailsResponse();
    }

    /**
     * Create an instance of {@link SmsAlertsRespContract }
     * 
     */
    public SmsAlertsRespContract createSmsAlertsRespContract() {
        return new SmsAlertsRespContract();
    }

    /**
     * Create an instance of {@link SmsAlertDetailReqContract }
     * 
     */
    public SmsAlertDetailReqContract createSmsAlertDetailReqContract() {
        return new SmsAlertDetailReqContract();
    }

    /**
     * Create an instance of {@link GetSMSAlertDetails }
     * 
     */
    public GetSMSAlertDetails createGetSMSAlertDetails() {
        return new GetSMSAlertDetails();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetSMSAlertDetailsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "getSMSAlertDetailsResponse")
    public JAXBElement<GetSMSAlertDetailsResponse> createGetSMSAlertDetailsResponse(GetSMSAlertDetailsResponse value) {
        return new JAXBElement<GetSMSAlertDetailsResponse>(_GetSMSAlertDetailsResponse_QNAME, GetSMSAlertDetailsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetSMSAlertDetails }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "getSMSAlertDetails")
    public JAXBElement<GetSMSAlertDetails> createGetSMSAlertDetails(GetSMSAlertDetails value) {
        return new JAXBElement<GetSMSAlertDetails>(_GetSMSAlertDetails_QNAME, GetSMSAlertDetails.class, null, value);
    }

}
