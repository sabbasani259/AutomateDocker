
package remote.EAscheduler.DealerMapping;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.EAscheduler.DealerMapping package. 
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

    private final static QName _ProcessEAdealerMappingDataResponse_QNAME = new QName("http://webservices.EAintegration.wise.remote/", "processEAdealerMappingDataResponse");
    private final static QName _ProcessEAdealerMappingData_QNAME = new QName("http://webservices.EAintegration.wise.remote/", "processEAdealerMappingData");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.EAscheduler.DealerMapping
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ProcessEAdealerMappingDataResponse }
     * 
     */
    public ProcessEAdealerMappingDataResponse createProcessEAdealerMappingDataResponse() {
        return new ProcessEAdealerMappingDataResponse();
    }

    /**
     * Create an instance of {@link ProcessEAdealerMappingData }
     * 
     */
    public ProcessEAdealerMappingData createProcessEAdealerMappingData() {
        return new ProcessEAdealerMappingData();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ProcessEAdealerMappingDataResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservices.EAintegration.wise.remote/", name = "processEAdealerMappingDataResponse")
    public JAXBElement<ProcessEAdealerMappingDataResponse> createProcessEAdealerMappingDataResponse(ProcessEAdealerMappingDataResponse value) {
        return new JAXBElement<ProcessEAdealerMappingDataResponse>(_ProcessEAdealerMappingDataResponse_QNAME, ProcessEAdealerMappingDataResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ProcessEAdealerMappingData }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservices.EAintegration.wise.remote/", name = "processEAdealerMappingData")
    public JAXBElement<ProcessEAdealerMappingData> createProcessEAdealerMappingData(ProcessEAdealerMappingData value) {
        return new JAXBElement<ProcessEAdealerMappingData>(_ProcessEAdealerMappingData_QNAME, ProcessEAdealerMappingData.class, null, value);
    }

}
