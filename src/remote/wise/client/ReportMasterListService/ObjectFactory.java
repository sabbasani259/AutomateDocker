
package remote.wise.client.ReportMasterListService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.ReportMasterListService package. 
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

    private final static QName _GetReportMasterListServiceResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetReportMasterListServiceResponse");
    private final static QName _GetReportMasterListService_QNAME = new QName("http://webservice.service.wise.remote/", "GetReportMasterListService");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.ReportMasterListService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ReportMasterListRespContract }
     * 
     */
    public ReportMasterListRespContract createReportMasterListRespContract() {
        return new ReportMasterListRespContract();
    }

    /**
     * Create an instance of {@link GetReportMasterListService }
     * 
     */
    public GetReportMasterListService createGetReportMasterListService() {
        return new GetReportMasterListService();
    }

    /**
     * Create an instance of {@link ReportMasterListReqContract }
     * 
     */
    public ReportMasterListReqContract createReportMasterListReqContract() {
        return new ReportMasterListReqContract();
    }

    /**
     * Create an instance of {@link GetReportMasterListServiceResponse }
     * 
     */
    public GetReportMasterListServiceResponse createGetReportMasterListServiceResponse() {
        return new GetReportMasterListServiceResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetReportMasterListServiceResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetReportMasterListServiceResponse")
    public JAXBElement<GetReportMasterListServiceResponse> createGetReportMasterListServiceResponse(GetReportMasterListServiceResponse value) {
        return new JAXBElement<GetReportMasterListServiceResponse>(_GetReportMasterListServiceResponse_QNAME, GetReportMasterListServiceResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetReportMasterListService }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetReportMasterListService")
    public JAXBElement<GetReportMasterListService> createGetReportMasterListService(GetReportMasterListService value) {
        return new JAXBElement<GetReportMasterListService>(_GetReportMasterListService_QNAME, GetReportMasterListService.class, null, value);
    }

}
