
package remote.wise.client.MachineRPMBandTrendDataService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.MachineRPMBandTrendDataService package. 
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

    private final static QName _GetMachineRPMBandTrendDataResponse_QNAME = new QName("http://webservice.service.wise.remote/", "getMachineRPMBandTrendDataResponse");
    private final static QName _GetMachineRPMBandTrendDataForAllZonesResponse_QNAME = new QName("http://webservice.service.wise.remote/", "getMachineRPMBandTrendDataForAllZonesResponse");
    private final static QName _GetMachineRPMBandTrendDataForAllZones_QNAME = new QName("http://webservice.service.wise.remote/", "getMachineRPMBandTrendDataForAllZones");
    private final static QName _GetMachineRPMBandTrendData_QNAME = new QName("http://webservice.service.wise.remote/", "getMachineRPMBandTrendData");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.MachineRPMBandTrendDataService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetMachineRPMBandTrendData }
     * 
     */
    public GetMachineRPMBandTrendData createGetMachineRPMBandTrendData() {
        return new GetMachineRPMBandTrendData();
    }

    /**
     * Create an instance of {@link GetMachineRPMBandTrendDataForAllZonesResponse }
     * 
     */
    public GetMachineRPMBandTrendDataForAllZonesResponse createGetMachineRPMBandTrendDataForAllZonesResponse() {
        return new GetMachineRPMBandTrendDataForAllZonesResponse();
    }

    /**
     * Create an instance of {@link GetMachineRPMBandTrendDataResponse }
     * 
     */
    public GetMachineRPMBandTrendDataResponse createGetMachineRPMBandTrendDataResponse() {
        return new GetMachineRPMBandTrendDataResponse();
    }

    /**
     * Create an instance of {@link GetMachineRPMBandTrendDataForAllZones }
     * 
     */
    public GetMachineRPMBandTrendDataForAllZones createGetMachineRPMBandTrendDataForAllZones() {
        return new GetMachineRPMBandTrendDataForAllZones();
    }

    /**
     * Create an instance of {@link MachineRPMBandDataReportRespContract.DealerData }
     * 
     */
    public MachineRPMBandDataReportRespContract.DealerData createMachineRPMBandDataReportRespContractDealerData() {
        return new MachineRPMBandDataReportRespContract.DealerData();
    }

    /**
     * Create an instance of {@link MachineRPMBandDataReportRespContract }
     * 
     */
    public MachineRPMBandDataReportRespContract createMachineRPMBandDataReportRespContract() {
        return new MachineRPMBandDataReportRespContract();
    }

    /**
     * Create an instance of {@link MachineRPMBandDataReportReqContract }
     * 
     */
    public MachineRPMBandDataReportReqContract createMachineRPMBandDataReportReqContract() {
        return new MachineRPMBandDataReportReqContract();
    }

    /**
     * Create an instance of {@link MachineRPMBandDataReportRespContract.DealerData.Entry }
     * 
     */
    public MachineRPMBandDataReportRespContract.DealerData.Entry createMachineRPMBandDataReportRespContractDealerDataEntry() {
        return new MachineRPMBandDataReportRespContract.DealerData.Entry();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetMachineRPMBandTrendDataResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "getMachineRPMBandTrendDataResponse")
    public JAXBElement<GetMachineRPMBandTrendDataResponse> createGetMachineRPMBandTrendDataResponse(GetMachineRPMBandTrendDataResponse value) {
        return new JAXBElement<GetMachineRPMBandTrendDataResponse>(_GetMachineRPMBandTrendDataResponse_QNAME, GetMachineRPMBandTrendDataResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetMachineRPMBandTrendDataForAllZonesResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "getMachineRPMBandTrendDataForAllZonesResponse")
    public JAXBElement<GetMachineRPMBandTrendDataForAllZonesResponse> createGetMachineRPMBandTrendDataForAllZonesResponse(GetMachineRPMBandTrendDataForAllZonesResponse value) {
        return new JAXBElement<GetMachineRPMBandTrendDataForAllZonesResponse>(_GetMachineRPMBandTrendDataForAllZonesResponse_QNAME, GetMachineRPMBandTrendDataForAllZonesResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetMachineRPMBandTrendDataForAllZones }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "getMachineRPMBandTrendDataForAllZones")
    public JAXBElement<GetMachineRPMBandTrendDataForAllZones> createGetMachineRPMBandTrendDataForAllZones(GetMachineRPMBandTrendDataForAllZones value) {
        return new JAXBElement<GetMachineRPMBandTrendDataForAllZones>(_GetMachineRPMBandTrendDataForAllZones_QNAME, GetMachineRPMBandTrendDataForAllZones.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetMachineRPMBandTrendData }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "getMachineRPMBandTrendData")
    public JAXBElement<GetMachineRPMBandTrendData> createGetMachineRPMBandTrendData(GetMachineRPMBandTrendData value) {
        return new JAXBElement<GetMachineRPMBandTrendData>(_GetMachineRPMBandTrendData_QNAME, GetMachineRPMBandTrendData.class, null, value);
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
