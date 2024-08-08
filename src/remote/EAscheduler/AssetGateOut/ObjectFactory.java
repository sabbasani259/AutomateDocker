
package remote.EAscheduler.AssetGateOut;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.EAscheduler.AssetGateOut package. 
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

    private final static QName _ProcessEAassetGateOut_QNAME = new QName("http://webservices.EAintegration.wise.remote/", "processEAassetGateOut");
    private final static QName _ProcessEAassetGateOutResponse_QNAME = new QName("http://webservices.EAintegration.wise.remote/", "processEAassetGateOutResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.EAscheduler.AssetGateOut
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ProcessEAassetGateOut }
     * 
     */
    public ProcessEAassetGateOut createProcessEAassetGateOut() {
        return new ProcessEAassetGateOut();
    }

    /**
     * Create an instance of {@link ProcessEAassetGateOutResponse }
     * 
     */
    public ProcessEAassetGateOutResponse createProcessEAassetGateOutResponse() {
        return new ProcessEAassetGateOutResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ProcessEAassetGateOut }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservices.EAintegration.wise.remote/", name = "processEAassetGateOut")
    public JAXBElement<ProcessEAassetGateOut> createProcessEAassetGateOut(ProcessEAassetGateOut value) {
        return new JAXBElement<ProcessEAassetGateOut>(_ProcessEAassetGateOut_QNAME, ProcessEAassetGateOut.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ProcessEAassetGateOutResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservices.EAintegration.wise.remote/", name = "processEAassetGateOutResponse")
    public JAXBElement<ProcessEAassetGateOutResponse> createProcessEAassetGateOutResponse(ProcessEAassetGateOutResponse value) {
        return new JAXBElement<ProcessEAassetGateOutResponse>(_ProcessEAassetGateOutResponse_QNAME, ProcessEAassetGateOutResponse.class, null, value);
    }

}
