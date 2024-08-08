
package remote.EAscheduler.AssetInstallationDetails;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.EAscheduler.AssetInstallationDetails package. 
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

    private final static QName _ProcessEAassetInstData_QNAME = new QName("http://webservices.EAintegration.wise.remote/", "processEAassetInstData");
    private final static QName _ProcessEAassetInstDataResponse_QNAME = new QName("http://webservices.EAintegration.wise.remote/", "processEAassetInstDataResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.EAscheduler.AssetInstallationDetails
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ProcessEAassetInstData }
     * 
     */
    public ProcessEAassetInstData createProcessEAassetInstData() {
        return new ProcessEAassetInstData();
    }

    /**
     * Create an instance of {@link ProcessEAassetInstDataResponse }
     * 
     */
    public ProcessEAassetInstDataResponse createProcessEAassetInstDataResponse() {
        return new ProcessEAassetInstDataResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ProcessEAassetInstData }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservices.EAintegration.wise.remote/", name = "processEAassetInstData")
    public JAXBElement<ProcessEAassetInstData> createProcessEAassetInstData(ProcessEAassetInstData value) {
        return new JAXBElement<ProcessEAassetInstData>(_ProcessEAassetInstData_QNAME, ProcessEAassetInstData.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ProcessEAassetInstDataResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservices.EAintegration.wise.remote/", name = "processEAassetInstDataResponse")
    public JAXBElement<ProcessEAassetInstDataResponse> createProcessEAassetInstDataResponse(ProcessEAassetInstDataResponse value) {
        return new JAXBElement<ProcessEAassetInstDataResponse>(_ProcessEAassetInstDataResponse_QNAME, ProcessEAassetInstDataResponse.class, null, value);
    }

}
