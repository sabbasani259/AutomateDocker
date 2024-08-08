
package remote.wise.client.GetForgotLoginIDService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.GetForgotLoginIDService package. 
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

    private final static QName _AuthenicateLoginIDOrMobileNo_QNAME = new QName("http://webservice.service.wise.remote/", "AuthenicateLoginIDOrMobileNo");
    private final static QName _AuthenicateLoginIDOrMobileNoResponse_QNAME = new QName("http://webservice.service.wise.remote/", "AuthenicateLoginIDOrMobileNoResponse");
    private final static QName _GetForgotLoginIDServiceResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetForgotLoginIDServiceResponse");
    private final static QName _GetForgotLoginIDService_QNAME = new QName("http://webservice.service.wise.remote/", "GetForgotLoginIDService");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.GetForgotLoginIDService
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
     * Create an instance of {@link AuthenicateLoginIDOrMobileNo }
     * 
     */
    public AuthenicateLoginIDOrMobileNo createAuthenicateLoginIDOrMobileNo() {
        return new AuthenicateLoginIDOrMobileNo();
    }

    /**
     * Create an instance of {@link GetForgotLoginIDService_Type }
     * 
     */
    public GetForgotLoginIDService_Type createGetForgotLoginIDService_Type() {
        return new GetForgotLoginIDService_Type();
    }

    /**
     * Create an instance of {@link AuthenicateLoginIDOrMobileNoResponse }
     * 
     */
    public AuthenicateLoginIDOrMobileNoResponse createAuthenicateLoginIDOrMobileNoResponse() {
        return new AuthenicateLoginIDOrMobileNoResponse();
    }

    /**
     * Create an instance of {@link GetForgotLoginIDServiceResponse }
     * 
     */
    public GetForgotLoginIDServiceResponse createGetForgotLoginIDServiceResponse() {
        return new GetForgotLoginIDServiceResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AuthenicateLoginIDOrMobileNo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "AuthenicateLoginIDOrMobileNo")
    public JAXBElement<AuthenicateLoginIDOrMobileNo> createAuthenicateLoginIDOrMobileNo(AuthenicateLoginIDOrMobileNo value) {
        return new JAXBElement<AuthenicateLoginIDOrMobileNo>(_AuthenicateLoginIDOrMobileNo_QNAME, AuthenicateLoginIDOrMobileNo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AuthenicateLoginIDOrMobileNoResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "AuthenicateLoginIDOrMobileNoResponse")
    public JAXBElement<AuthenicateLoginIDOrMobileNoResponse> createAuthenicateLoginIDOrMobileNoResponse(AuthenicateLoginIDOrMobileNoResponse value) {
        return new JAXBElement<AuthenicateLoginIDOrMobileNoResponse>(_AuthenicateLoginIDOrMobileNoResponse_QNAME, AuthenicateLoginIDOrMobileNoResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetForgotLoginIDServiceResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetForgotLoginIDServiceResponse")
    public JAXBElement<GetForgotLoginIDServiceResponse> createGetForgotLoginIDServiceResponse(GetForgotLoginIDServiceResponse value) {
        return new JAXBElement<GetForgotLoginIDServiceResponse>(_GetForgotLoginIDServiceResponse_QNAME, GetForgotLoginIDServiceResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetForgotLoginIDService_Type }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetForgotLoginIDService")
    public JAXBElement<GetForgotLoginIDService_Type> createGetForgotLoginIDService(GetForgotLoginIDService_Type value) {
        return new JAXBElement<GetForgotLoginIDService_Type>(_GetForgotLoginIDService_QNAME, GetForgotLoginIDService_Type.class, null, value);
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
