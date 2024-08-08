
package remote.wise.client.UtilizationDetailService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.UtilizationDetailService package. 
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

    private final static QName _GetUtilizationDetailService_QNAME = new QName("http://webservice.service.wise.remote/", "GetUtilizationDetailService");
    private final static QName _GetUtilizationDetailServiceResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetUtilizationDetailServiceResponse");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.UtilizationDetailService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetUtilizationDetailService }
     * 
     */
    public GetUtilizationDetailService createGetUtilizationDetailService() {
        return new GetUtilizationDetailService();
    }

    /**
     * Create an instance of {@link TreeMap }
     * 
     */
    public TreeMap createTreeMap() {
        return new TreeMap();
    }

    /**
     * Create an instance of {@link UtilizationDetailServiceReqContract }
     * 
     */
    public UtilizationDetailServiceReqContract createUtilizationDetailServiceReqContract() {
        return new UtilizationDetailServiceReqContract();
    }

    /**
     * Create an instance of {@link UtilizationDetailServiceRespContract.TimeMachineStatusMap.Entry }
     * 
     */
    public UtilizationDetailServiceRespContract.TimeMachineStatusMap.Entry createUtilizationDetailServiceRespContractTimeMachineStatusMapEntry() {
        return new UtilizationDetailServiceRespContract.TimeMachineStatusMap.Entry();
    }

    /**
     * Create an instance of {@link UtilizationDetailServiceRespContract }
     * 
     */
    public UtilizationDetailServiceRespContract createUtilizationDetailServiceRespContract() {
        return new UtilizationDetailServiceRespContract();
    }

    /**
     * Create an instance of {@link GetUtilizationDetailServiceResponse }
     * 
     */
    public GetUtilizationDetailServiceResponse createGetUtilizationDetailServiceResponse() {
        return new GetUtilizationDetailServiceResponse();
    }

    /**
     * Create an instance of {@link UtilizationDetailServiceRespContract.TimeMachineStatusMap }
     * 
     */
    public UtilizationDetailServiceRespContract.TimeMachineStatusMap createUtilizationDetailServiceRespContractTimeMachineStatusMap() {
        return new UtilizationDetailServiceRespContract.TimeMachineStatusMap();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetUtilizationDetailService }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetUtilizationDetailService")
    public JAXBElement<GetUtilizationDetailService> createGetUtilizationDetailService(GetUtilizationDetailService value) {
        return new JAXBElement<GetUtilizationDetailService>(_GetUtilizationDetailService_QNAME, GetUtilizationDetailService.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetUtilizationDetailServiceResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetUtilizationDetailServiceResponse")
    public JAXBElement<GetUtilizationDetailServiceResponse> createGetUtilizationDetailServiceResponse(GetUtilizationDetailServiceResponse value) {
        return new JAXBElement<GetUtilizationDetailServiceResponse>(_GetUtilizationDetailServiceResponse_QNAME, GetUtilizationDetailServiceResponse.class, null, value);
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
