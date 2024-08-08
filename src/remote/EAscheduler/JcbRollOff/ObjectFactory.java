
package remote.EAscheduler.JcbRollOff;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.EAscheduler.JcbRollOff package. 
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

    private final static QName _ProcessEARollOffdata_QNAME = new QName("http://webservices.EAintegration.wise.remote/", "processEARollOffdata");
    private final static QName _ProcessEARollOffdataResponse_QNAME = new QName("http://webservices.EAintegration.wise.remote/", "processEARollOffdataResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.EAscheduler.JcbRollOff
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ProcessEARollOffdataResponse }
     * 
     */
    public ProcessEARollOffdataResponse createProcessEARollOffdataResponse() {
        return new ProcessEARollOffdataResponse();
    }

    /**
     * Create an instance of {@link ProcessEARollOffdata }
     * 
     */
    public ProcessEARollOffdata createProcessEARollOffdata() {
        return new ProcessEARollOffdata();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ProcessEARollOffdata }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservices.EAintegration.wise.remote/", name = "processEARollOffdata")
    public JAXBElement<ProcessEARollOffdata> createProcessEARollOffdata(ProcessEARollOffdata value) {
        return new JAXBElement<ProcessEARollOffdata>(_ProcessEARollOffdata_QNAME, ProcessEARollOffdata.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ProcessEARollOffdataResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservices.EAintegration.wise.remote/", name = "processEARollOffdataResponse")
    public JAXBElement<ProcessEARollOffdataResponse> createProcessEARollOffdataResponse(ProcessEARollOffdataResponse value) {
        return new JAXBElement<ProcessEARollOffdataResponse>(_ProcessEARollOffdataResponse_QNAME, ProcessEARollOffdataResponse.class, null, value);
    }

}
