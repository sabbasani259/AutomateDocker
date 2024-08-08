
package remote.wise.client.ReportSubscriptionService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.ReportSubscriptionService package. 
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

    private final static QName _SetReportSubscriptionService_QNAME = new QName("http://webservice.service.wise.remote/", "SetReportSubscriptionService");
    private final static QName _GetReportSubscriptionService_QNAME = new QName("http://webservice.service.wise.remote/", "GetReportSubscriptionService");
    private final static QName _SetReportSubscriptionServiceResponse_QNAME = new QName("http://webservice.service.wise.remote/", "SetReportSubscriptionServiceResponse");
    private final static QName _GetReportSubscriptionServiceResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetReportSubscriptionServiceResponse");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.ReportSubscriptionService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ReportSubscriptionRespContract }
     * 
     */
    public ReportSubscriptionRespContract createReportSubscriptionRespContract() {
        return new ReportSubscriptionRespContract();
    }

    /**
     * Create an instance of {@link ReportSubscriptionReqContract }
     * 
     */
    public ReportSubscriptionReqContract createReportSubscriptionReqContract() {
        return new ReportSubscriptionReqContract();
    }

    /**
     * Create an instance of {@link SetReportSubscriptionService }
     * 
     */
    public SetReportSubscriptionService createSetReportSubscriptionService() {
        return new SetReportSubscriptionService();
    }

    /**
     * Create an instance of {@link GetReportSubscriptionServiceResponse }
     * 
     */
    public GetReportSubscriptionServiceResponse createGetReportSubscriptionServiceResponse() {
        return new GetReportSubscriptionServiceResponse();
    }

    /**
     * Create an instance of {@link GetReportSubscriptionService }
     * 
     */
    public GetReportSubscriptionService createGetReportSubscriptionService() {
        return new GetReportSubscriptionService();
    }

    /**
     * Create an instance of {@link SetReportSubscriptionServiceResponse }
     * 
     */
    public SetReportSubscriptionServiceResponse createSetReportSubscriptionServiceResponse() {
        return new SetReportSubscriptionServiceResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetReportSubscriptionService }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "SetReportSubscriptionService")
    public JAXBElement<SetReportSubscriptionService> createSetReportSubscriptionService(SetReportSubscriptionService value) {
        return new JAXBElement<SetReportSubscriptionService>(_SetReportSubscriptionService_QNAME, SetReportSubscriptionService.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetReportSubscriptionService }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetReportSubscriptionService")
    public JAXBElement<GetReportSubscriptionService> createGetReportSubscriptionService(GetReportSubscriptionService value) {
        return new JAXBElement<GetReportSubscriptionService>(_GetReportSubscriptionService_QNAME, GetReportSubscriptionService.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetReportSubscriptionServiceResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "SetReportSubscriptionServiceResponse")
    public JAXBElement<SetReportSubscriptionServiceResponse> createSetReportSubscriptionServiceResponse(SetReportSubscriptionServiceResponse value) {
        return new JAXBElement<SetReportSubscriptionServiceResponse>(_SetReportSubscriptionServiceResponse_QNAME, SetReportSubscriptionServiceResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetReportSubscriptionServiceResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetReportSubscriptionServiceResponse")
    public JAXBElement<GetReportSubscriptionServiceResponse> createGetReportSubscriptionServiceResponse(GetReportSubscriptionServiceResponse value) {
        return new JAXBElement<GetReportSubscriptionServiceResponse>(_GetReportSubscriptionServiceResponse_QNAME, GetReportSubscriptionServiceResponse.class, null, value);
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
