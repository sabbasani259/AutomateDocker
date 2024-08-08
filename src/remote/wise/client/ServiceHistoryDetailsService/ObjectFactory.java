
package remote.wise.client.ServiceHistoryDetailsService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.ServiceHistoryDetailsService package. 
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

    private final static QName _SetServiceHistoryDetails_QNAME = new QName("http://webservice.service.wise.remote/", "SetServiceHistoryDetails");
    private final static QName _GetServiceHistoryDetailsResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetServiceHistoryDetailsResponse");
    private final static QName _GetServiceHistoryDetails_QNAME = new QName("http://webservice.service.wise.remote/", "GetServiceHistoryDetails");
    private final static QName _SetServiceHistoryDetailsResponse_QNAME = new QName("http://webservice.service.wise.remote/", "SetServiceHistoryDetailsResponse");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.ServiceHistoryDetailsService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ServiceHistoryRespContract }
     * 
     */
    public ServiceHistoryRespContract createServiceHistoryRespContract() {
        return new ServiceHistoryRespContract();
    }

    /**
     * Create an instance of {@link GetServiceHistoryDetails }
     * 
     */
    public GetServiceHistoryDetails createGetServiceHistoryDetails() {
        return new GetServiceHistoryDetails();
    }

    /**
     * Create an instance of {@link SetServiceHistoryDetailsResponse }
     * 
     */
    public SetServiceHistoryDetailsResponse createSetServiceHistoryDetailsResponse() {
        return new SetServiceHistoryDetailsResponse();
    }

    /**
     * Create an instance of {@link SetServiceHistoryDetails }
     * 
     */
    public SetServiceHistoryDetails createSetServiceHistoryDetails() {
        return new SetServiceHistoryDetails();
    }

    /**
     * Create an instance of {@link GetServiceHistoryDetailsResponse }
     * 
     */
    public GetServiceHistoryDetailsResponse createGetServiceHistoryDetailsResponse() {
        return new GetServiceHistoryDetailsResponse();
    }

    /**
     * Create an instance of {@link ServiceScheduleRespContract }
     * 
     */
    public ServiceScheduleRespContract createServiceScheduleRespContract() {
        return new ServiceScheduleRespContract();
    }

    /**
     * Create an instance of {@link ServiceHistoryReqContract }
     * 
     */
    public ServiceHistoryReqContract createServiceHistoryReqContract() {
        return new ServiceHistoryReqContract();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetServiceHistoryDetails }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "SetServiceHistoryDetails")
    public JAXBElement<SetServiceHistoryDetails> createSetServiceHistoryDetails(SetServiceHistoryDetails value) {
        return new JAXBElement<SetServiceHistoryDetails>(_SetServiceHistoryDetails_QNAME, SetServiceHistoryDetails.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetServiceHistoryDetailsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetServiceHistoryDetailsResponse")
    public JAXBElement<GetServiceHistoryDetailsResponse> createGetServiceHistoryDetailsResponse(GetServiceHistoryDetailsResponse value) {
        return new JAXBElement<GetServiceHistoryDetailsResponse>(_GetServiceHistoryDetailsResponse_QNAME, GetServiceHistoryDetailsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetServiceHistoryDetails }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetServiceHistoryDetails")
    public JAXBElement<GetServiceHistoryDetails> createGetServiceHistoryDetails(GetServiceHistoryDetails value) {
        return new JAXBElement<GetServiceHistoryDetails>(_GetServiceHistoryDetails_QNAME, GetServiceHistoryDetails.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetServiceHistoryDetailsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "SetServiceHistoryDetailsResponse")
    public JAXBElement<SetServiceHistoryDetailsResponse> createSetServiceHistoryDetailsResponse(SetServiceHistoryDetailsResponse value) {
        return new JAXBElement<SetServiceHistoryDetailsResponse>(_SetServiceHistoryDetailsResponse_QNAME, SetServiceHistoryDetailsResponse.class, null, value);
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
