
package remote.wise.client.FuelUtilizationDetailService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.FuelUtilizationDetailService package. 
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

    private final static QName _GetFuelUtilizationDetail_QNAME = new QName("http://webservice.service.wise.remote/", "GetFuelUtilizationDetail");
    private final static QName _GetFuelUtilizationDetailResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetFuelUtilizationDetailResponse");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.FuelUtilizationDetailService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link FuelUtilizationDetailRespContract.HourFuelLevelMap.Entry }
     * 
     */
    public FuelUtilizationDetailRespContract.HourFuelLevelMap.Entry createFuelUtilizationDetailRespContractHourFuelLevelMapEntry() {
        return new FuelUtilizationDetailRespContract.HourFuelLevelMap.Entry();
    }

    /**
     * Create an instance of {@link HashMap }
     * 
     */
    public HashMap createHashMap() {
        return new HashMap();
    }

    /**
     * Create an instance of {@link FuelUtilizationDetailRespContract.HourFuelLevelMap }
     * 
     */
    public FuelUtilizationDetailRespContract.HourFuelLevelMap createFuelUtilizationDetailRespContractHourFuelLevelMap() {
        return new FuelUtilizationDetailRespContract.HourFuelLevelMap();
    }

    /**
     * Create an instance of {@link FuelUtilizationDetailReqContract }
     * 
     */
    public FuelUtilizationDetailReqContract createFuelUtilizationDetailReqContract() {
        return new FuelUtilizationDetailReqContract();
    }

    /**
     * Create an instance of {@link FuelUtilizationDetailRespContract }
     * 
     */
    public FuelUtilizationDetailRespContract createFuelUtilizationDetailRespContract() {
        return new FuelUtilizationDetailRespContract();
    }

    /**
     * Create an instance of {@link GetFuelUtilizationDetailResponse }
     * 
     */
    public GetFuelUtilizationDetailResponse createGetFuelUtilizationDetailResponse() {
        return new GetFuelUtilizationDetailResponse();
    }

    /**
     * Create an instance of {@link GetFuelUtilizationDetail }
     * 
     */
    public GetFuelUtilizationDetail createGetFuelUtilizationDetail() {
        return new GetFuelUtilizationDetail();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetFuelUtilizationDetail }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetFuelUtilizationDetail")
    public JAXBElement<GetFuelUtilizationDetail> createGetFuelUtilizationDetail(GetFuelUtilizationDetail value) {
        return new JAXBElement<GetFuelUtilizationDetail>(_GetFuelUtilizationDetail_QNAME, GetFuelUtilizationDetail.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetFuelUtilizationDetailResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetFuelUtilizationDetailResponse")
    public JAXBElement<GetFuelUtilizationDetailResponse> createGetFuelUtilizationDetailResponse(GetFuelUtilizationDetailResponse value) {
        return new JAXBElement<GetFuelUtilizationDetailResponse>(_GetFuelUtilizationDetailResponse_QNAME, GetFuelUtilizationDetailResponse.class, null, value);
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
