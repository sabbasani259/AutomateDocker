
package remote.wise.client.StateService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.StateService package. 
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

    private final static QName _GetStatesResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetStatesResponse");
    private final static QName _GetStates_QNAME = new QName("http://webservice.service.wise.remote/", "GetStates");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.StateService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link StateRespContract }
     * 
     */
    public StateRespContract createStateRespContract() {
        return new StateRespContract();
    }

    /**
     * Create an instance of {@link GetStates }
     * 
     */
    public GetStates createGetStates() {
        return new GetStates();
    }

    /**
     * Create an instance of {@link GetStatesResponse }
     * 
     */
    public GetStatesResponse createGetStatesResponse() {
        return new GetStatesResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetStatesResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetStatesResponse")
    public JAXBElement<GetStatesResponse> createGetStatesResponse(GetStatesResponse value) {
        return new JAXBElement<GetStatesResponse>(_GetStatesResponse_QNAME, GetStatesResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetStates }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetStates")
    public JAXBElement<GetStates> createGetStates(GetStates value) {
        return new JAXBElement<GetStates>(_GetStates_QNAME, GetStates.class, null, value);
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
