
package remote.wise.client.MachineHourMeterTrendDataReportService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.MachineHourMeterTrendDataReportService package. 
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

    private final static QName _GetMachineTrendDataForAllZones_QNAME = new QName("http://webservice.service.wise.remote/", "getMachineTrendDataForAllZones");
    private final static QName _GetMachineHourMeterTrendDataResponse_QNAME = new QName("http://webservice.service.wise.remote/", "getMachineHourMeterTrendDataResponse");
    private final static QName _GetMachineHourMeterTrendData_QNAME = new QName("http://webservice.service.wise.remote/", "getMachineHourMeterTrendData");
    private final static QName _GetMachineTrendDataForAllZonesResponse_QNAME = new QName("http://webservice.service.wise.remote/", "getMachineTrendDataForAllZonesResponse");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.MachineHourMeterTrendDataReportService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link MachineHourMeterTrendDataReportReqContract }
     * 
     */
    public MachineHourMeterTrendDataReportReqContract createMachineHourMeterTrendDataReportReqContract() {
        return new MachineHourMeterTrendDataReportReqContract();
    }

    /**
     * Create an instance of {@link GetMachineHourMeterTrendData }
     * 
     */
    public GetMachineHourMeterTrendData createGetMachineHourMeterTrendData() {
        return new GetMachineHourMeterTrendData();
    }

    /**
     * Create an instance of {@link GetMachineHourMeterTrendDataResponse }
     * 
     */
    public GetMachineHourMeterTrendDataResponse createGetMachineHourMeterTrendDataResponse() {
        return new GetMachineHourMeterTrendDataResponse();
    }

    /**
     * Create an instance of {@link MachineHourMeterTrendDataReportRespContract }
     * 
     */
    public MachineHourMeterTrendDataReportRespContract createMachineHourMeterTrendDataReportRespContract() {
        return new MachineHourMeterTrendDataReportRespContract();
    }

    /**
     * Create an instance of {@link HashMap }
     * 
     */
    public HashMap createHashMap() {
        return new HashMap();
    }

    /**
     * Create an instance of {@link GetMachineTrendDataForAllZonesResponse }
     * 
     */
    public GetMachineTrendDataForAllZonesResponse createGetMachineTrendDataForAllZonesResponse() {
        return new GetMachineTrendDataForAllZonesResponse();
    }

    /**
     * Create an instance of {@link GetMachineTrendDataForAllZones }
     * 
     */
    public GetMachineTrendDataForAllZones createGetMachineTrendDataForAllZones() {
        return new GetMachineTrendDataForAllZones();
    }

    /**
     * Create an instance of {@link MachineHourMeterTrendDataReportRespContract.DealerData.Entry }
     * 
     */
    public MachineHourMeterTrendDataReportRespContract.DealerData.Entry createMachineHourMeterTrendDataReportRespContractDealerDataEntry() {
        return new MachineHourMeterTrendDataReportRespContract.DealerData.Entry();
    }

    /**
     * Create an instance of {@link MachineHourMeterTrendDataReportRespContract.DealerData }
     * 
     */
    public MachineHourMeterTrendDataReportRespContract.DealerData createMachineHourMeterTrendDataReportRespContractDealerData() {
        return new MachineHourMeterTrendDataReportRespContract.DealerData();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetMachineTrendDataForAllZones }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "getMachineTrendDataForAllZones")
    public JAXBElement<GetMachineTrendDataForAllZones> createGetMachineTrendDataForAllZones(GetMachineTrendDataForAllZones value) {
        return new JAXBElement<GetMachineTrendDataForAllZones>(_GetMachineTrendDataForAllZones_QNAME, GetMachineTrendDataForAllZones.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetMachineHourMeterTrendDataResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "getMachineHourMeterTrendDataResponse")
    public JAXBElement<GetMachineHourMeterTrendDataResponse> createGetMachineHourMeterTrendDataResponse(GetMachineHourMeterTrendDataResponse value) {
        return new JAXBElement<GetMachineHourMeterTrendDataResponse>(_GetMachineHourMeterTrendDataResponse_QNAME, GetMachineHourMeterTrendDataResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetMachineHourMeterTrendData }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "getMachineHourMeterTrendData")
    public JAXBElement<GetMachineHourMeterTrendData> createGetMachineHourMeterTrendData(GetMachineHourMeterTrendData value) {
        return new JAXBElement<GetMachineHourMeterTrendData>(_GetMachineHourMeterTrendData_QNAME, GetMachineHourMeterTrendData.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetMachineTrendDataForAllZonesResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "getMachineTrendDataForAllZonesResponse")
    public JAXBElement<GetMachineTrendDataForAllZonesResponse> createGetMachineTrendDataForAllZonesResponse(GetMachineTrendDataForAllZonesResponse value) {
        return new JAXBElement<GetMachineTrendDataForAllZonesResponse>(_GetMachineTrendDataForAllZonesResponse_QNAME, GetMachineTrendDataForAllZonesResponse.class, null, value);
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
