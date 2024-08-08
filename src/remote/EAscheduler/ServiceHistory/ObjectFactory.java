
package remote.EAscheduler.ServiceHistory;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.EAscheduler.ServiceHistory package. 
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

    private final static QName _ProcessEAServiceHistory_QNAME = new QName("http://webservices.EAintegration.wise.remote/", "processEAServiceHistory");
    private final static QName _ProcessEAServiceHistoryResponse_QNAME = new QName("http://webservices.EAintegration.wise.remote/", "processEAServiceHistoryResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.EAscheduler.ServiceHistory
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ProcessEAServiceHistory }
     * 
     */
    public ProcessEAServiceHistory createProcessEAServiceHistory() {
        return new ProcessEAServiceHistory();
    }

    /**
     * Create an instance of {@link ProcessEAServiceHistoryResponse }
     * 
     */
    public ProcessEAServiceHistoryResponse createProcessEAServiceHistoryResponse() {
        return new ProcessEAServiceHistoryResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ProcessEAServiceHistory }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservices.EAintegration.wise.remote/", name = "processEAServiceHistory")
    public JAXBElement<ProcessEAServiceHistory> createProcessEAServiceHistory(ProcessEAServiceHistory value) {
        return new JAXBElement<ProcessEAServiceHistory>(_ProcessEAServiceHistory_QNAME, ProcessEAServiceHistory.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ProcessEAServiceHistoryResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservices.EAintegration.wise.remote/", name = "processEAServiceHistoryResponse")
    public JAXBElement<ProcessEAServiceHistoryResponse> createProcessEAServiceHistoryResponse(ProcessEAServiceHistoryResponse value) {
        return new JAXBElement<ProcessEAServiceHistoryResponse>(_ProcessEAServiceHistoryResponse_QNAME, ProcessEAServiceHistoryResponse.class, null, value);
    }

}
