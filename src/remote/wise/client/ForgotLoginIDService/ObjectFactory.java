
package remote.wise.client.ForgotLoginIDService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.ForgotLoginIDService package. 
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

    private final static QName _ForgotLoginIDResponse_QNAME = new QName("http://webservice.service.wise.remote/", "ForgotLoginIDResponse");
    private final static QName _ForgotLoginID_QNAME = new QName("http://webservice.service.wise.remote/", "ForgotLoginID");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.ForgotLoginIDService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ForgotLoginIDRespContract }
     * 
     */
    public ForgotLoginIDRespContract createForgotLoginIDRespContract() {
        return new ForgotLoginIDRespContract();
    }

    /**
     * Create an instance of {@link ForgotLoginIDReqContract }
     * 
     */
    public ForgotLoginIDReqContract createForgotLoginIDReqContract() {
        return new ForgotLoginIDReqContract();
    }

    /**
     * Create an instance of {@link ForgotLoginIDResponse }
     * 
     */
    public ForgotLoginIDResponse createForgotLoginIDResponse() {
        return new ForgotLoginIDResponse();
    }

    /**
     * Create an instance of {@link ForgotLoginID }
     * 
     */
    public ForgotLoginID createForgotLoginID() {
        return new ForgotLoginID();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ForgotLoginIDResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "ForgotLoginIDResponse")
    public JAXBElement<ForgotLoginIDResponse> createForgotLoginIDResponse(ForgotLoginIDResponse value) {
        return new JAXBElement<ForgotLoginIDResponse>(_ForgotLoginIDResponse_QNAME, ForgotLoginIDResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ForgotLoginID }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "ForgotLoginID")
    public JAXBElement<ForgotLoginID> createForgotLoginID(ForgotLoginID value) {
        return new JAXBElement<ForgotLoginID>(_ForgotLoginID_QNAME, ForgotLoginID.class, null, value);
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
