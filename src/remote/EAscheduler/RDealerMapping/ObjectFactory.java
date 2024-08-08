
package remote.EAscheduler.RDealerMapping;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.EAscheduler.RDealerMapping package. 
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

    private final static QName _ReprocessEADealerMappingDataResponse_QNAME = new QName("http://webservices.EAintegration.wise.remote/", "reprocessEADealerMappingDataResponse");
    private final static QName _ReprocessEADealerMappingData_QNAME = new QName("http://webservices.EAintegration.wise.remote/", "reprocessEADealerMappingData");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.EAscheduler.RDealerMapping
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ReprocessEADealerMappingDataResponse }
     * 
     */
    public ReprocessEADealerMappingDataResponse createReprocessEADealerMappingDataResponse() {
        return new ReprocessEADealerMappingDataResponse();
    }

    /**
     * Create an instance of {@link ReprocessEADealerMappingData }
     * 
     */
    public ReprocessEADealerMappingData createReprocessEADealerMappingData() {
        return new ReprocessEADealerMappingData();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReprocessEADealerMappingDataResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservices.EAintegration.wise.remote/", name = "reprocessEADealerMappingDataResponse")
    public JAXBElement<ReprocessEADealerMappingDataResponse> createReprocessEADealerMappingDataResponse(ReprocessEADealerMappingDataResponse value) {
        return new JAXBElement<ReprocessEADealerMappingDataResponse>(_ReprocessEADealerMappingDataResponse_QNAME, ReprocessEADealerMappingDataResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReprocessEADealerMappingData }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservices.EAintegration.wise.remote/", name = "reprocessEADealerMappingData")
    public JAXBElement<ReprocessEADealerMappingData> createReprocessEADealerMappingData(ReprocessEADealerMappingData value) {
        return new JAXBElement<ReprocessEADealerMappingData>(_ReprocessEADealerMappingData_QNAME, ReprocessEADealerMappingData.class, null, value);
    }

}
