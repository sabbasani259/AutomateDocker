
package remote.wise.client.UtilizationDetailReportService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.UtilizationDetailReportService package. 
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

    private final static QName _GetUtilizationDetailReport_QNAME = new QName("http://webservice.service.wise.remote/", "GetUtilizationDetailReport");
    private final static QName _GetUtilizationDetailReportResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetUtilizationDetailReportResponse");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.UtilizationDetailReportService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link TreeMap }
     * 
     */
    public TreeMap createTreeMap() {
        return new TreeMap();
    }

    /**
     * Create an instance of {@link GetUtilizationDetailReportResponse }
     * 
     */
    public GetUtilizationDetailReportResponse createGetUtilizationDetailReportResponse() {
        return new GetUtilizationDetailReportResponse();
    }

    /**
     * Create an instance of {@link GetUtilizationDetailReport }
     * 
     */
    public GetUtilizationDetailReport createGetUtilizationDetailReport() {
        return new GetUtilizationDetailReport();
    }

    /**
     * Create an instance of {@link UtilizationDetailReportRespContract }
     * 
     */
    public UtilizationDetailReportRespContract createUtilizationDetailReportRespContract() {
        return new UtilizationDetailReportRespContract();
    }

    /**
     * Create an instance of {@link UtilizationDetailReportReqContract }
     * 
     */
    public UtilizationDetailReportReqContract createUtilizationDetailReportReqContract() {
        return new UtilizationDetailReportReqContract();
    }

    /**
     * Create an instance of {@link UtilizationDetailReportRespContract.TimeMachineStatusMap.Entry }
     * 
     */
    public UtilizationDetailReportRespContract.TimeMachineStatusMap.Entry createUtilizationDetailReportRespContractTimeMachineStatusMapEntry() {
        return new UtilizationDetailReportRespContract.TimeMachineStatusMap.Entry();
    }

    /**
     * Create an instance of {@link UtilizationDetailReportRespContract.TimeMachineStatusMap }
     * 
     */
    public UtilizationDetailReportRespContract.TimeMachineStatusMap createUtilizationDetailReportRespContractTimeMachineStatusMap() {
        return new UtilizationDetailReportRespContract.TimeMachineStatusMap();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetUtilizationDetailReport }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetUtilizationDetailReport")
    public JAXBElement<GetUtilizationDetailReport> createGetUtilizationDetailReport(GetUtilizationDetailReport value) {
        return new JAXBElement<GetUtilizationDetailReport>(_GetUtilizationDetailReport_QNAME, GetUtilizationDetailReport.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetUtilizationDetailReportResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetUtilizationDetailReportResponse")
    public JAXBElement<GetUtilizationDetailReportResponse> createGetUtilizationDetailReportResponse(GetUtilizationDetailReportResponse value) {
        return new JAXBElement<GetUtilizationDetailReportResponse>(_GetUtilizationDetailReportResponse_QNAME, GetUtilizationDetailReportResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "CustomFault")
    public JAXBElement<String> createCustomFault(String value) {
        return new JAXBElement<String>(_CustomFault_QNAME, String.class, null, value);
    }

}
