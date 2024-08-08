
package remote.wise.client.MachineDueOverdueReportService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.MachineDueOverdueReportService package. 
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

    private final static QName _GetDueOverDueMachines_QNAME = new QName("http://webservice.service.wise.remote/", "GetDueOverDueMachines");
    private final static QName _GetDueOverDueMachinesResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetDueOverDueMachinesResponse");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.MachineDueOverdueReportService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link DueOverDueReportReqContract }
     * 
     */
    public DueOverDueReportReqContract createDueOverDueReportReqContract() {
        return new DueOverDueReportReqContract();
    }

    /**
     * Create an instance of {@link DueOverDueReportRespContract }
     * 
     */
    public DueOverDueReportRespContract createDueOverDueReportRespContract() {
        return new DueOverDueReportRespContract();
    }

    /**
     * Create an instance of {@link GetDueOverDueMachinesResponse }
     * 
     */
    public GetDueOverDueMachinesResponse createGetDueOverDueMachinesResponse() {
        return new GetDueOverDueMachinesResponse();
    }

    /**
     * Create an instance of {@link GetDueOverDueMachines }
     * 
     */
    public GetDueOverDueMachines createGetDueOverDueMachines() {
        return new GetDueOverDueMachines();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetDueOverDueMachines }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetDueOverDueMachines")
    public JAXBElement<GetDueOverDueMachines> createGetDueOverDueMachines(GetDueOverDueMachines value) {
        return new JAXBElement<GetDueOverDueMachines>(_GetDueOverDueMachines_QNAME, GetDueOverDueMachines.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetDueOverDueMachinesResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetDueOverDueMachinesResponse")
    public JAXBElement<GetDueOverDueMachinesResponse> createGetDueOverDueMachinesResponse(GetDueOverDueMachinesResponse value) {
        return new JAXBElement<GetDueOverDueMachinesResponse>(_GetDueOverDueMachinesResponse_QNAME, GetDueOverDueMachinesResponse.class, null, value);
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
