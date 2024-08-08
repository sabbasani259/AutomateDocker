
package remote.wise.client.AlertDetailsService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.AlertDetailsService package. 
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

    private final static QName _SetAlertCommentsResponse_QNAME = new QName("http://webservice.service.wise.remote/", "setAlertCommentsResponse");
    private final static QName _GetAlertDetails_QNAME = new QName("http://webservice.service.wise.remote/", "GetAlertDetails");
    private final static QName _GetAlertDetailsResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetAlertDetailsResponse");
    private final static QName _SetAlertComments_QNAME = new QName("http://webservice.service.wise.remote/", "setAlertComments");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.AlertDetailsService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetAlertDetails }
     * 
     */
    public GetAlertDetails createGetAlertDetails() {
        return new GetAlertDetails();
    }

    /**
     * Create an instance of {@link SetAlertCommentsResponse }
     * 
     */
    public SetAlertCommentsResponse createSetAlertCommentsResponse() {
        return new SetAlertCommentsResponse();
    }

    /**
     * Create an instance of {@link AlertDetailsReqContract }
     * 
     */
    public AlertDetailsReqContract createAlertDetailsReqContract() {
        return new AlertDetailsReqContract();
    }

    /**
     * Create an instance of {@link GetAlertDetailsResponse }
     * 
     */
    public GetAlertDetailsResponse createGetAlertDetailsResponse() {
        return new GetAlertDetailsResponse();
    }

    /**
     * Create an instance of {@link AlertDetailsRespContract }
     * 
     */
    public AlertDetailsRespContract createAlertDetailsRespContract() {
        return new AlertDetailsRespContract();
    }

    /**
     * Create an instance of {@link SetAlertComments }
     * 
     */
    public SetAlertComments createSetAlertComments() {
        return new SetAlertComments();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetAlertCommentsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "setAlertCommentsResponse")
    public JAXBElement<SetAlertCommentsResponse> createSetAlertCommentsResponse(SetAlertCommentsResponse value) {
        return new JAXBElement<SetAlertCommentsResponse>(_SetAlertCommentsResponse_QNAME, SetAlertCommentsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAlertDetails }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetAlertDetails")
    public JAXBElement<GetAlertDetails> createGetAlertDetails(GetAlertDetails value) {
        return new JAXBElement<GetAlertDetails>(_GetAlertDetails_QNAME, GetAlertDetails.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAlertDetailsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetAlertDetailsResponse")
    public JAXBElement<GetAlertDetailsResponse> createGetAlertDetailsResponse(GetAlertDetailsResponse value) {
        return new JAXBElement<GetAlertDetailsResponse>(_GetAlertDetailsResponse_QNAME, GetAlertDetailsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetAlertComments }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "setAlertComments")
    public JAXBElement<SetAlertComments> createSetAlertComments(SetAlertComments value) {
        return new JAXBElement<SetAlertComments>(_SetAlertComments_QNAME, SetAlertComments.class, null, value);
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
