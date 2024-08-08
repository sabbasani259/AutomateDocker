
package remote.wise.client.EAInterfaceService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.EAInterfaceService package. 
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

    private final static QName _GetEADetailData_QNAME = new QName("http://webservice.service.wise.remote/", "GetEADetailData");
    private final static QName _GetEASummaryData_QNAME = new QName("http://webservice.service.wise.remote/", "GetEASummaryData");
    private final static QName _GetEADetailDataResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetEADetailDataResponse");
    private final static QName _GetEASummaryDataResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetEASummaryDataResponse");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.EAInterfaceService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetEASummaryData }
     * 
     */
    public GetEASummaryData createGetEASummaryData() {
        return new GetEASummaryData();
    }

    /**
     * Create an instance of {@link EaInterfaceRespContract }
     * 
     */
    public EaInterfaceRespContract createEaInterfaceRespContract() {
        return new EaInterfaceRespContract();
    }

    /**
     * Create an instance of {@link EaInterfaceDetailRespContract }
     * 
     */
    public EaInterfaceDetailRespContract createEaInterfaceDetailRespContract() {
        return new EaInterfaceDetailRespContract();
    }

    /**
     * Create an instance of {@link EaInterfaceDetailReqContract }
     * 
     */
    public EaInterfaceDetailReqContract createEaInterfaceDetailReqContract() {
        return new EaInterfaceDetailReqContract();
    }

    /**
     * Create an instance of {@link EaInterfaceReqContract }
     * 
     */
    public EaInterfaceReqContract createEaInterfaceReqContract() {
        return new EaInterfaceReqContract();
    }

    /**
     * Create an instance of {@link GetEASummaryDataResponse }
     * 
     */
    public GetEASummaryDataResponse createGetEASummaryDataResponse() {
        return new GetEASummaryDataResponse();
    }

    /**
     * Create an instance of {@link GetEADetailData }
     * 
     */
    public GetEADetailData createGetEADetailData() {
        return new GetEADetailData();
    }

    /**
     * Create an instance of {@link GetEADetailDataResponse }
     * 
     */
    public GetEADetailDataResponse createGetEADetailDataResponse() {
        return new GetEADetailDataResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetEADetailData }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetEADetailData")
    public JAXBElement<GetEADetailData> createGetEADetailData(GetEADetailData value) {
        return new JAXBElement<GetEADetailData>(_GetEADetailData_QNAME, GetEADetailData.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetEASummaryData }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetEASummaryData")
    public JAXBElement<GetEASummaryData> createGetEASummaryData(GetEASummaryData value) {
        return new JAXBElement<GetEASummaryData>(_GetEASummaryData_QNAME, GetEASummaryData.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetEADetailDataResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetEADetailDataResponse")
    public JAXBElement<GetEADetailDataResponse> createGetEADetailDataResponse(GetEADetailDataResponse value) {
        return new JAXBElement<GetEADetailDataResponse>(_GetEADetailDataResponse_QNAME, GetEADetailDataResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetEASummaryDataResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetEASummaryDataResponse")
    public JAXBElement<GetEASummaryDataResponse> createGetEASummaryDataResponse(GetEASummaryDataResponse value) {
        return new JAXBElement<GetEASummaryDataResponse>(_GetEASummaryDataResponse_QNAME, GetEASummaryDataResponse.class, null, value);
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
