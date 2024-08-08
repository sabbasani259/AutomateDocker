
package remote.wise.client.RolledOffMachinesService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.RolledOffMachinesService package. 
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

    private final static QName _GetRolledOffMachines_QNAME = new QName("http://webservice.service.wise.remote/", "getRolledOffMachines");
    private final static QName _GetRolledOffMachinesResponse_QNAME = new QName("http://webservice.service.wise.remote/", "getRolledOffMachinesResponse");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.RolledOffMachinesService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetRolledOffMachinesResponse }
     * 
     */
    public GetRolledOffMachinesResponse createGetRolledOffMachinesResponse() {
        return new GetRolledOffMachinesResponse();
    }

    /**
     * Create an instance of {@link GetRolledOffMachines }
     * 
     */
    public GetRolledOffMachines createGetRolledOffMachines() {
        return new GetRolledOffMachines();
    }

    /**
     * Create an instance of {@link RolledOffMachinesReqContract }
     * 
     */
    public RolledOffMachinesReqContract createRolledOffMachinesReqContract() {
        return new RolledOffMachinesReqContract();
    }

    /**
     * Create an instance of {@link RolledOffMachinesRespContract }
     * 
     */
    public RolledOffMachinesRespContract createRolledOffMachinesRespContract() {
        return new RolledOffMachinesRespContract();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetRolledOffMachines }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "getRolledOffMachines")
    public JAXBElement<GetRolledOffMachines> createGetRolledOffMachines(GetRolledOffMachines value) {
        return new JAXBElement<GetRolledOffMachines>(_GetRolledOffMachines_QNAME, GetRolledOffMachines.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetRolledOffMachinesResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "getRolledOffMachinesResponse")
    public JAXBElement<GetRolledOffMachinesResponse> createGetRolledOffMachinesResponse(GetRolledOffMachinesResponse value) {
        return new JAXBElement<GetRolledOffMachinesResponse>(_GetRolledOffMachinesResponse_QNAME, GetRolledOffMachinesResponse.class, null, value);
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
