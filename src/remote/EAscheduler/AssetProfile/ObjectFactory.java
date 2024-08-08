
package remote.EAscheduler.AssetProfile;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.EAscheduler.AssetProfile package. 
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

    private final static QName _ProcessEAassetProfileDataResponse_QNAME = new QName("http://webservices.EAintegration.wise.remote/", "processEAassetProfileDataResponse");
    private final static QName _ProcessEAassetProfileData_QNAME = new QName("http://webservices.EAintegration.wise.remote/", "processEAassetProfileData");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.EAscheduler.AssetProfile
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ProcessEAassetProfileData }
     * 
     */
    public ProcessEAassetProfileData createProcessEAassetProfileData() {
        return new ProcessEAassetProfileData();
    }

    /**
     * Create an instance of {@link ProcessEAassetProfileDataResponse }
     * 
     */
    public ProcessEAassetProfileDataResponse createProcessEAassetProfileDataResponse() {
        return new ProcessEAassetProfileDataResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ProcessEAassetProfileDataResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservices.EAintegration.wise.remote/", name = "processEAassetProfileDataResponse")
    public JAXBElement<ProcessEAassetProfileDataResponse> createProcessEAassetProfileDataResponse(ProcessEAassetProfileDataResponse value) {
        return new JAXBElement<ProcessEAassetProfileDataResponse>(_ProcessEAassetProfileDataResponse_QNAME, ProcessEAassetProfileDataResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ProcessEAassetProfileData }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservices.EAintegration.wise.remote/", name = "processEAassetProfileData")
    public JAXBElement<ProcessEAassetProfileData> createProcessEAassetProfileData(ProcessEAassetProfileData value) {
        return new JAXBElement<ProcessEAassetProfileData>(_ProcessEAassetProfileData_QNAME, ProcessEAassetProfileData.class, null, value);
    }

}
