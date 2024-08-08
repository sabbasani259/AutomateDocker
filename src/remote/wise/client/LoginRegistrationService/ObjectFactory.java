
package remote.wise.client.LoginRegistrationService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.LoginRegistrationService package. 
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

    private final static QName _GetSecretQuestionsResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetSecretQuestionsResponse");
    private final static QName _SetSecretQuestionsToUserResponse_QNAME = new QName("http://webservice.service.wise.remote/", "SetSecretQuestionsToUserResponse");
    private final static QName _GetSecretQuestions_QNAME = new QName("http://webservice.service.wise.remote/", "GetSecretQuestions");
    private final static QName _SetSecretQuestionsToUser_QNAME = new QName("http://webservice.service.wise.remote/", "SetSecretQuestionsToUser");
    private final static QName _GetQuestionsToUser_QNAME = new QName("http://webservice.service.wise.remote/", "GetQuestionsToUser");
    private final static QName _GetQuestionsToUserResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetQuestionsToUserResponse");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.LoginRegistrationService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetSecretQuestions }
     * 
     */
    public GetSecretQuestions createGetSecretQuestions() {
        return new GetSecretQuestions();
    }

    /**
     * Create an instance of {@link GetQuestionsToUserResponse }
     * 
     */
    public GetQuestionsToUserResponse createGetQuestionsToUserResponse() {
        return new GetQuestionsToUserResponse();
    }

    /**
     * Create an instance of {@link LoginRegistrationReqContract }
     * 
     */
    public LoginRegistrationReqContract createLoginRegistrationReqContract() {
        return new LoginRegistrationReqContract();
    }

    /**
     * Create an instance of {@link SecretQuestionsRespContract }
     * 
     */
    public SecretQuestionsRespContract createSecretQuestionsRespContract() {
        return new SecretQuestionsRespContract();
    }

    /**
     * Create an instance of {@link GetQuestionsToUser }
     * 
     */
    public GetQuestionsToUser createGetQuestionsToUser() {
        return new GetQuestionsToUser();
    }

    /**
     * Create an instance of {@link SetSecretQuestionsToUserResponse }
     * 
     */
    public SetSecretQuestionsToUserResponse createSetSecretQuestionsToUserResponse() {
        return new SetSecretQuestionsToUserResponse();
    }

    /**
     * Create an instance of {@link GetSecretQuestionsResponse }
     * 
     */
    public GetSecretQuestionsResponse createGetSecretQuestionsResponse() {
        return new GetSecretQuestionsResponse();
    }

    /**
     * Create an instance of {@link SetSecretQuestionsToUser }
     * 
     */
    public SetSecretQuestionsToUser createSetSecretQuestionsToUser() {
        return new SetSecretQuestionsToUser();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetSecretQuestionsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetSecretQuestionsResponse")
    public JAXBElement<GetSecretQuestionsResponse> createGetSecretQuestionsResponse(GetSecretQuestionsResponse value) {
        return new JAXBElement<GetSecretQuestionsResponse>(_GetSecretQuestionsResponse_QNAME, GetSecretQuestionsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetSecretQuestionsToUserResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "SetSecretQuestionsToUserResponse")
    public JAXBElement<SetSecretQuestionsToUserResponse> createSetSecretQuestionsToUserResponse(SetSecretQuestionsToUserResponse value) {
        return new JAXBElement<SetSecretQuestionsToUserResponse>(_SetSecretQuestionsToUserResponse_QNAME, SetSecretQuestionsToUserResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetSecretQuestions }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetSecretQuestions")
    public JAXBElement<GetSecretQuestions> createGetSecretQuestions(GetSecretQuestions value) {
        return new JAXBElement<GetSecretQuestions>(_GetSecretQuestions_QNAME, GetSecretQuestions.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetSecretQuestionsToUser }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "SetSecretQuestionsToUser")
    public JAXBElement<SetSecretQuestionsToUser> createSetSecretQuestionsToUser(SetSecretQuestionsToUser value) {
        return new JAXBElement<SetSecretQuestionsToUser>(_SetSecretQuestionsToUser_QNAME, SetSecretQuestionsToUser.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetQuestionsToUser }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetQuestionsToUser")
    public JAXBElement<GetQuestionsToUser> createGetQuestionsToUser(GetQuestionsToUser value) {
        return new JAXBElement<GetQuestionsToUser>(_GetQuestionsToUser_QNAME, GetQuestionsToUser.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetQuestionsToUserResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetQuestionsToUserResponse")
    public JAXBElement<GetQuestionsToUserResponse> createGetQuestionsToUserResponse(GetQuestionsToUserResponse value) {
        return new JAXBElement<GetQuestionsToUserResponse>(_GetQuestionsToUserResponse_QNAME, GetQuestionsToUserResponse.class, null, value);
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
