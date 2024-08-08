
package remote.wise.client.UnderUtilizedMachinesService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.UnderUtilizedMachinesService package. 
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

    private final static QName _GetUnderUtilizedMachinesResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetUnderUtilizedMachinesResponse");
    private final static QName _GetUnderUtilizedMachines_QNAME = new QName("http://webservice.service.wise.remote/", "GetUnderUtilizedMachines");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.UnderUtilizedMachinesService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetUnderUtilizedMachinesResponse }
     * 
     */
    public GetUnderUtilizedMachinesResponse createGetUnderUtilizedMachinesResponse() {
        return new GetUnderUtilizedMachinesResponse();
    }

    /**
     * Create an instance of {@link UnderUtilizedMachinesReqContract }
     * 
     */
    public UnderUtilizedMachinesReqContract createUnderUtilizedMachinesReqContract() {
        return new UnderUtilizedMachinesReqContract();
    }

    /**
     * Create an instance of {@link GetUnderUtilizedMachines }
     * 
     */
    public GetUnderUtilizedMachines createGetUnderUtilizedMachines() {
        return new GetUnderUtilizedMachines();
    }

    /**
     * Create an instance of {@link UnderUtilizedMachinesRespContract }
     * 
     */
    public UnderUtilizedMachinesRespContract createUnderUtilizedMachinesRespContract() {
        return new UnderUtilizedMachinesRespContract();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetUnderUtilizedMachinesResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetUnderUtilizedMachinesResponse")
    public JAXBElement<GetUnderUtilizedMachinesResponse> createGetUnderUtilizedMachinesResponse(GetUnderUtilizedMachinesResponse value) {
        return new JAXBElement<GetUnderUtilizedMachinesResponse>(_GetUnderUtilizedMachinesResponse_QNAME, GetUnderUtilizedMachinesResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetUnderUtilizedMachines }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetUnderUtilizedMachines")
    public JAXBElement<GetUnderUtilizedMachines> createGetUnderUtilizedMachines(GetUnderUtilizedMachines value) {
        return new JAXBElement<GetUnderUtilizedMachines>(_GetUnderUtilizedMachines_QNAME, GetUnderUtilizedMachines.class, null, value);
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
