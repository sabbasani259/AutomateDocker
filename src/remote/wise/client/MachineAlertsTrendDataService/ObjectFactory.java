
package remote.wise.client.MachineAlertsTrendDataService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.MachineAlertsTrendDataService package. 
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

    private final static QName _GetMachineAlertsTrendDataForAllZones_QNAME = new QName("http://webservice.service.wise.remote/", "getMachineAlertsTrendDataForAllZones");
    private final static QName _GetMachineAlertsTrendData_QNAME = new QName("http://webservice.service.wise.remote/", "getMachineAlertsTrendData");
    private final static QName _GetMachineAlertsTrendDataForAllZonesResponse_QNAME = new QName("http://webservice.service.wise.remote/", "getMachineAlertsTrendDataForAllZonesResponse");
    private final static QName _GetMachineAlertsTrendDataResponse_QNAME = new QName("http://webservice.service.wise.remote/", "getMachineAlertsTrendDataResponse");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.MachineAlertsTrendDataService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetMachineAlertsTrendDataResponse }
     * 
     */
    public GetMachineAlertsTrendDataResponse createGetMachineAlertsTrendDataResponse() {
        return new GetMachineAlertsTrendDataResponse();
    }

    /**
     * Create an instance of {@link GetMachineAlertsTrendDataForAllZonesResponse }
     * 
     */
    public GetMachineAlertsTrendDataForAllZonesResponse createGetMachineAlertsTrendDataForAllZonesResponse() {
        return new GetMachineAlertsTrendDataForAllZonesResponse();
    }

    /**
     * Create an instance of {@link MachineAlertsTrendDataRespContract.DealerData }
     * 
     */
    public MachineAlertsTrendDataRespContract.DealerData createMachineAlertsTrendDataRespContractDealerData() {
        return new MachineAlertsTrendDataRespContract.DealerData();
    }

    /**
     * Create an instance of {@link MachineAlertsTrendDataReqContract }
     * 
     */
    public MachineAlertsTrendDataReqContract createMachineAlertsTrendDataReqContract() {
        return new MachineAlertsTrendDataReqContract();
    }

    /**
     * Create an instance of {@link GetMachineAlertsTrendData }
     * 
     */
    public GetMachineAlertsTrendData createGetMachineAlertsTrendData() {
        return new GetMachineAlertsTrendData();
    }

    /**
     * Create an instance of {@link MachineAlertsTrendDataRespContract.DealerData.Entry }
     * 
     */
    public MachineAlertsTrendDataRespContract.DealerData.Entry createMachineAlertsTrendDataRespContractDealerDataEntry() {
        return new MachineAlertsTrendDataRespContract.DealerData.Entry();
    }

    /**
     * Create an instance of {@link HashMap }
     * 
     */
    public HashMap createHashMap() {
        return new HashMap();
    }

    /**
     * Create an instance of {@link MachineAlertsTrendDataRespContract }
     * 
     */
    public MachineAlertsTrendDataRespContract createMachineAlertsTrendDataRespContract() {
        return new MachineAlertsTrendDataRespContract();
    }

    /**
     * Create an instance of {@link GetMachineAlertsTrendDataForAllZones }
     * 
     */
    public GetMachineAlertsTrendDataForAllZones createGetMachineAlertsTrendDataForAllZones() {
        return new GetMachineAlertsTrendDataForAllZones();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetMachineAlertsTrendDataForAllZones }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "getMachineAlertsTrendDataForAllZones")
    public JAXBElement<GetMachineAlertsTrendDataForAllZones> createGetMachineAlertsTrendDataForAllZones(GetMachineAlertsTrendDataForAllZones value) {
        return new JAXBElement<GetMachineAlertsTrendDataForAllZones>(_GetMachineAlertsTrendDataForAllZones_QNAME, GetMachineAlertsTrendDataForAllZones.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetMachineAlertsTrendData }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "getMachineAlertsTrendData")
    public JAXBElement<GetMachineAlertsTrendData> createGetMachineAlertsTrendData(GetMachineAlertsTrendData value) {
        return new JAXBElement<GetMachineAlertsTrendData>(_GetMachineAlertsTrendData_QNAME, GetMachineAlertsTrendData.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetMachineAlertsTrendDataForAllZonesResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "getMachineAlertsTrendDataForAllZonesResponse")
    public JAXBElement<GetMachineAlertsTrendDataForAllZonesResponse> createGetMachineAlertsTrendDataForAllZonesResponse(GetMachineAlertsTrendDataForAllZonesResponse value) {
        return new JAXBElement<GetMachineAlertsTrendDataForAllZonesResponse>(_GetMachineAlertsTrendDataForAllZonesResponse_QNAME, GetMachineAlertsTrendDataForAllZonesResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetMachineAlertsTrendDataResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "getMachineAlertsTrendDataResponse")
    public JAXBElement<GetMachineAlertsTrendDataResponse> createGetMachineAlertsTrendDataResponse(GetMachineAlertsTrendDataResponse value) {
        return new JAXBElement<GetMachineAlertsTrendDataResponse>(_GetMachineAlertsTrendDataResponse_QNAME, GetMachineAlertsTrendDataResponse.class, null, value);
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
