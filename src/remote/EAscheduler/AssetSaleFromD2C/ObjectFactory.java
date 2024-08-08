
package remote.EAscheduler.AssetSaleFromD2C;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.EAscheduler.AssetSaleFromD2C package. 
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

    private final static QName _ProcessEAsaleFromD2CResponse_QNAME = new QName("http://webservices.EAintegration.wise.remote/", "processEAsaleFromD2CResponse");
    private final static QName _ProcessEAsaleFromD2C_QNAME = new QName("http://webservices.EAintegration.wise.remote/", "processEAsaleFromD2C");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.EAscheduler.AssetSaleFromD2C
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ProcessEAsaleFromD2C }
     * 
     */
    public ProcessEAsaleFromD2C createProcessEAsaleFromD2C() {
        return new ProcessEAsaleFromD2C();
    }

    /**
     * Create an instance of {@link ProcessEAsaleFromD2CResponse }
     * 
     */
    public ProcessEAsaleFromD2CResponse createProcessEAsaleFromD2CResponse() {
        return new ProcessEAsaleFromD2CResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ProcessEAsaleFromD2CResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservices.EAintegration.wise.remote/", name = "processEAsaleFromD2CResponse")
    public JAXBElement<ProcessEAsaleFromD2CResponse> createProcessEAsaleFromD2CResponse(ProcessEAsaleFromD2CResponse value) {
        return new JAXBElement<ProcessEAsaleFromD2CResponse>(_ProcessEAsaleFromD2CResponse_QNAME, ProcessEAsaleFromD2CResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ProcessEAsaleFromD2C }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservices.EAintegration.wise.remote/", name = "processEAsaleFromD2C")
    public JAXBElement<ProcessEAsaleFromD2C> createProcessEAsaleFromD2C(ProcessEAsaleFromD2C value) {
        return new JAXBElement<ProcessEAsaleFromD2C>(_ProcessEAsaleFromD2C_QNAME, ProcessEAsaleFromD2C.class, null, value);
    }

}
