
package remote.wise.client.ForgotPasswordService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.ForgotPasswordService package. 
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

    private final static QName _GetForgottenPasswordResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetForgottenPasswordResponse");
    private final static QName _GetForgottenPassword_QNAME = new QName("http://webservice.service.wise.remote/", "GetForgottenPassword");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.ForgotPasswordService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetForgottenPasswordResponse }
     * 
     */
    public GetForgottenPasswordResponse createGetForgottenPasswordResponse() {
        return new GetForgottenPasswordResponse();
    }

    /**
     * Create an instance of {@link GetForgottenPassword }
     * 
     */
    public GetForgottenPassword createGetForgottenPassword() {
        return new GetForgottenPassword();
    }

    /**
     * Create an instance of {@link ForgotPasswordReqContract }
     * 
     */
    public ForgotPasswordReqContract createForgotPasswordReqContract() {
        return new ForgotPasswordReqContract();
    }

    /**
     * Create an instance of {@link ForgotPasswordRespContract }
     * 
     */
    public ForgotPasswordRespContract createForgotPasswordRespContract() {
        return new ForgotPasswordRespContract();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetForgottenPasswordResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetForgottenPasswordResponse")
    public JAXBElement<GetForgottenPasswordResponse> createGetForgottenPasswordResponse(GetForgottenPasswordResponse value) {
        return new JAXBElement<GetForgottenPasswordResponse>(_GetForgottenPasswordResponse_QNAME, GetForgottenPasswordResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetForgottenPassword }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetForgottenPassword")
    public JAXBElement<GetForgottenPassword> createGetForgottenPassword(GetForgottenPassword value) {
        return new JAXBElement<GetForgottenPassword>(_GetForgottenPassword_QNAME, GetForgottenPassword.class, null, value);
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
