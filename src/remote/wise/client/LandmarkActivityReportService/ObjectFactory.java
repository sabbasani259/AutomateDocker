
package remote.wise.client.LandmarkActivityReportService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.LandmarkActivityReportService package. 
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

    private final static QName _GetLandmarkActivityReport_QNAME = new QName("http://webservice.service.wise.remote/", "GetLandmarkActivityReport");
    private final static QName _GetLandmarkActivityReportResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetLandmarkActivityReportResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.LandmarkActivityReportService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link LandmarkActivityReportReqContract }
     * 
     */
    public LandmarkActivityReportReqContract createLandmarkActivityReportReqContract() {
        return new LandmarkActivityReportReqContract();
    }

    /**
     * Create an instance of {@link GetLandmarkActivityReport }
     * 
     */
    public GetLandmarkActivityReport createGetLandmarkActivityReport() {
        return new GetLandmarkActivityReport();
    }

    /**
     * Create an instance of {@link GetLandmarkActivityReportResponse }
     * 
     */
    public GetLandmarkActivityReportResponse createGetLandmarkActivityReportResponse() {
        return new GetLandmarkActivityReportResponse();
    }

    /**
     * Create an instance of {@link LandmarkActivityReportRespContract }
     * 
     */
    public LandmarkActivityReportRespContract createLandmarkActivityReportRespContract() {
        return new LandmarkActivityReportRespContract();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetLandmarkActivityReport }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetLandmarkActivityReport")
    public JAXBElement<GetLandmarkActivityReport> createGetLandmarkActivityReport(GetLandmarkActivityReport value) {
        return new JAXBElement<GetLandmarkActivityReport>(_GetLandmarkActivityReport_QNAME, GetLandmarkActivityReport.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetLandmarkActivityReportResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetLandmarkActivityReportResponse")
    public JAXBElement<GetLandmarkActivityReportResponse> createGetLandmarkActivityReportResponse(GetLandmarkActivityReportResponse value) {
        return new JAXBElement<GetLandmarkActivityReportResponse>(_GetLandmarkActivityReportResponse_QNAME, GetLandmarkActivityReportResponse.class, null, value);
    }

}
