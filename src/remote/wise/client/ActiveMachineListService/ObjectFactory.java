
package remote.wise.client.ActiveMachineListService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.ActiveMachineListService package. 
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

    private final static QName _GetActiveMachineList_QNAME = new QName("http://webservice.service.wise.remote/", "GetActiveMachineList");
    private final static QName _GetActiveMachineListResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetActiveMachineListResponse");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.ActiveMachineListService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetActiveMachineList }
     * 
     */
    public GetActiveMachineList createGetActiveMachineList() {
        return new GetActiveMachineList();
    }

    /**
     * Create an instance of {@link ActiveMachineListOutputContract }
     * 
     */
    public ActiveMachineListOutputContract createActiveMachineListOutputContract() {
        return new ActiveMachineListOutputContract();
    }

    /**
     * Create an instance of {@link ActiveMachineListInputContract }
     * 
     */
    public ActiveMachineListInputContract createActiveMachineListInputContract() {
        return new ActiveMachineListInputContract();
    }

    /**
     * Create an instance of {@link GetActiveMachineListResponse }
     * 
     */
    public GetActiveMachineListResponse createGetActiveMachineListResponse() {
        return new GetActiveMachineListResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetActiveMachineList }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetActiveMachineList")
    public JAXBElement<GetActiveMachineList> createGetActiveMachineList(GetActiveMachineList value) {
        return new JAXBElement<GetActiveMachineList>(_GetActiveMachineList_QNAME, GetActiveMachineList.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetActiveMachineListResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetActiveMachineListResponse")
    public JAXBElement<GetActiveMachineListResponse> createGetActiveMachineListResponse(GetActiveMachineListResponse value) {
        return new JAXBElement<GetActiveMachineListResponse>(_GetActiveMachineListResponse_QNAME, GetActiveMachineListResponse.class, null, value);
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
