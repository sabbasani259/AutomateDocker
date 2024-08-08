
package remote.EAscheduler.AssetPersonality;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.EAscheduler.AssetPersonality package. 
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

    private final static QName _ProcessEAassetPersData_QNAME = new QName("http://webservices.EAintegration.wise.remote/", "processEAassetPersData");
    private final static QName _ProcessEAassetPersDataResponse_QNAME = new QName("http://webservices.EAintegration.wise.remote/", "processEAassetPersDataResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.EAscheduler.AssetPersonality
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ProcessEAassetPersData }
     * 
     */
    public ProcessEAassetPersData createProcessEAassetPersData() {
        return new ProcessEAassetPersData();
    }

    /**
     * Create an instance of {@link ProcessEAassetPersDataResponse }
     * 
     */
    public ProcessEAassetPersDataResponse createProcessEAassetPersDataResponse() {
        return new ProcessEAassetPersDataResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ProcessEAassetPersData }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservices.EAintegration.wise.remote/", name = "processEAassetPersData")
    public JAXBElement<ProcessEAassetPersData> createProcessEAassetPersData(ProcessEAassetPersData value) {
        return new JAXBElement<ProcessEAassetPersData>(_ProcessEAassetPersData_QNAME, ProcessEAassetPersData.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ProcessEAassetPersDataResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservices.EAintegration.wise.remote/", name = "processEAassetPersDataResponse")
    public JAXBElement<ProcessEAassetPersDataResponse> createProcessEAassetPersDataResponse(ProcessEAassetPersDataResponse value) {
        return new JAXBElement<ProcessEAassetPersDataResponse>(_ProcessEAassetPersDataResponse_QNAME, ProcessEAassetPersDataResponse.class, null, value);
    }

}
