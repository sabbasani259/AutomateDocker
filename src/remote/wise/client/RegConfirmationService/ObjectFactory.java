
package remote.wise.client.RegConfirmationService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.RegConfirmationService package. 
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

    private final static QName _GetRegistrationConfirmation_QNAME = new QName("http://webservice.service.wise.remote/", "GetRegistrationConfirmation");
    private final static QName _SetRegConfirmationResponse_QNAME = new QName("http://webservice.service.wise.remote/", "SetRegConfirmationResponse");
    private final static QName _DeleteRegistrationConfirmation_QNAME = new QName("http://webservice.service.wise.remote/", "DeleteRegistrationConfirmation");
    private final static QName _SetRegConfirmation_QNAME = new QName("http://webservice.service.wise.remote/", "SetRegConfirmation");
    private final static QName _GetRegistrationConfirmationResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetRegistrationConfirmationResponse");
    private final static QName _DeleteRegistrationConfirmationResponse_QNAME = new QName("http://webservice.service.wise.remote/", "DeleteRegistrationConfirmationResponse");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.RegConfirmationService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SetRegConfirmationResponse }
     * 
     */
    public SetRegConfirmationResponse createSetRegConfirmationResponse() {
        return new SetRegConfirmationResponse();
    }

    /**
     * Create an instance of {@link DeleteRegistrationConfirmationResponse }
     * 
     */
    public DeleteRegistrationConfirmationResponse createDeleteRegistrationConfirmationResponse() {
        return new DeleteRegistrationConfirmationResponse();
    }

    /**
     * Create an instance of {@link SetRegConfirmation }
     * 
     */
    public SetRegConfirmation createSetRegConfirmation() {
        return new SetRegConfirmation();
    }

    /**
     * Create an instance of {@link RegConfirmationReqContract }
     * 
     */
    public RegConfirmationReqContract createRegConfirmationReqContract() {
        return new RegConfirmationReqContract();
    }

    /**
     * Create an instance of {@link GetRegistrationConfirmation }
     * 
     */
    public GetRegistrationConfirmation createGetRegistrationConfirmation() {
        return new GetRegistrationConfirmation();
    }

    /**
     * Create an instance of {@link DeleteRegistrationConfirmation }
     * 
     */
    public DeleteRegistrationConfirmation createDeleteRegistrationConfirmation() {
        return new DeleteRegistrationConfirmation();
    }

    /**
     * Create an instance of {@link RegConfirmationRespContract }
     * 
     */
    public RegConfirmationRespContract createRegConfirmationRespContract() {
        return new RegConfirmationRespContract();
    }

    /**
     * Create an instance of {@link GetRegistrationConfirmationResponse }
     * 
     */
    public GetRegistrationConfirmationResponse createGetRegistrationConfirmationResponse() {
        return new GetRegistrationConfirmationResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetRegistrationConfirmation }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetRegistrationConfirmation")
    public JAXBElement<GetRegistrationConfirmation> createGetRegistrationConfirmation(GetRegistrationConfirmation value) {
        return new JAXBElement<GetRegistrationConfirmation>(_GetRegistrationConfirmation_QNAME, GetRegistrationConfirmation.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetRegConfirmationResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "SetRegConfirmationResponse")
    public JAXBElement<SetRegConfirmationResponse> createSetRegConfirmationResponse(SetRegConfirmationResponse value) {
        return new JAXBElement<SetRegConfirmationResponse>(_SetRegConfirmationResponse_QNAME, SetRegConfirmationResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteRegistrationConfirmation }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "DeleteRegistrationConfirmation")
    public JAXBElement<DeleteRegistrationConfirmation> createDeleteRegistrationConfirmation(DeleteRegistrationConfirmation value) {
        return new JAXBElement<DeleteRegistrationConfirmation>(_DeleteRegistrationConfirmation_QNAME, DeleteRegistrationConfirmation.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetRegConfirmation }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "SetRegConfirmation")
    public JAXBElement<SetRegConfirmation> createSetRegConfirmation(SetRegConfirmation value) {
        return new JAXBElement<SetRegConfirmation>(_SetRegConfirmation_QNAME, SetRegConfirmation.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetRegistrationConfirmationResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetRegistrationConfirmationResponse")
    public JAXBElement<GetRegistrationConfirmationResponse> createGetRegistrationConfirmationResponse(GetRegistrationConfirmationResponse value) {
        return new JAXBElement<GetRegistrationConfirmationResponse>(_GetRegistrationConfirmationResponse_QNAME, GetRegistrationConfirmationResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteRegistrationConfirmationResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "DeleteRegistrationConfirmationResponse")
    public JAXBElement<DeleteRegistrationConfirmationResponse> createDeleteRegistrationConfirmationResponse(DeleteRegistrationConfirmationResponse value) {
        return new JAXBElement<DeleteRegistrationConfirmationResponse>(_DeleteRegistrationConfirmationResponse_QNAME, DeleteRegistrationConfirmationResponse.class, null, value);
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
