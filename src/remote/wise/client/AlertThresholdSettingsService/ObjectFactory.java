
package remote.wise.client.AlertThresholdSettingsService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.AlertThresholdSettingsService package. 
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

    private final static QName _GetAlertThresholdResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetAlertThresholdResponse");
    private final static QName _SetAlertThreshold_QNAME = new QName("http://webservice.service.wise.remote/", "SetAlertThreshold");
    private final static QName _GetAlertThreshold_QNAME = new QName("http://webservice.service.wise.remote/", "GetAlertThreshold");
    private final static QName _SetAlertThresholdResponse_QNAME = new QName("http://webservice.service.wise.remote/", "SetAlertThresholdResponse");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.AlertThresholdSettingsService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SetAlertThreshold }
     * 
     */
    public SetAlertThreshold createSetAlertThreshold() {
        return new SetAlertThreshold();
    }

    /**
     * Create an instance of {@link GetAlertThreshold }
     * 
     */
    public GetAlertThreshold createGetAlertThreshold() {
        return new GetAlertThreshold();
    }

    /**
     * Create an instance of {@link AlertThresholdReqContract }
     * 
     */
    public AlertThresholdReqContract createAlertThresholdReqContract() {
        return new AlertThresholdReqContract();
    }

    /**
     * Create an instance of {@link AlertThresholdRespContract }
     * 
     */
    public AlertThresholdRespContract createAlertThresholdRespContract() {
        return new AlertThresholdRespContract();
    }

    /**
     * Create an instance of {@link SetAlertThresholdResponse }
     * 
     */
    public SetAlertThresholdResponse createSetAlertThresholdResponse() {
        return new SetAlertThresholdResponse();
    }

    /**
     * Create an instance of {@link GetAlertThresholdResponse }
     * 
     */
    public GetAlertThresholdResponse createGetAlertThresholdResponse() {
        return new GetAlertThresholdResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAlertThresholdResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetAlertThresholdResponse")
    public JAXBElement<GetAlertThresholdResponse> createGetAlertThresholdResponse(GetAlertThresholdResponse value) {
        return new JAXBElement<GetAlertThresholdResponse>(_GetAlertThresholdResponse_QNAME, GetAlertThresholdResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetAlertThreshold }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "SetAlertThreshold")
    public JAXBElement<SetAlertThreshold> createSetAlertThreshold(SetAlertThreshold value) {
        return new JAXBElement<SetAlertThreshold>(_SetAlertThreshold_QNAME, SetAlertThreshold.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetAlertThreshold }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetAlertThreshold")
    public JAXBElement<GetAlertThreshold> createGetAlertThreshold(GetAlertThreshold value) {
        return new JAXBElement<GetAlertThreshold>(_GetAlertThreshold_QNAME, GetAlertThreshold.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetAlertThresholdResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "SetAlertThresholdResponse")
    public JAXBElement<SetAlertThresholdResponse> createSetAlertThresholdResponse(SetAlertThresholdResponse value) {
        return new JAXBElement<SetAlertThresholdResponse>(_SetAlertThresholdResponse_QNAME, SetAlertThresholdResponse.class, null, value);
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
