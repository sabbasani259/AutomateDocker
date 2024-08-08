
package remote.wise.client.DetailMachineInfoService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.DetailMachineInfoService package. 
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

    private final static QName _GetDetailMachineInfo_QNAME = new QName("http://webservice.service.wise.remote/", "getDetailMachineInfo");
    private final static QName _GetDetailMachineInfoResponse_QNAME = new QName("http://webservice.service.wise.remote/", "getDetailMachineInfoResponse");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.DetailMachineInfoService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link DetailMachineInfoRespContract }
     * 
     */
    public DetailMachineInfoRespContract createDetailMachineInfoRespContract() {
        return new DetailMachineInfoRespContract();
    }

    /**
     * Create an instance of {@link GetDetailMachineInfoResponse }
     * 
     */
    public GetDetailMachineInfoResponse createGetDetailMachineInfoResponse() {
        return new GetDetailMachineInfoResponse();
    }

    /**
     * Create an instance of {@link GetDetailMachineInfo }
     * 
     */
    public GetDetailMachineInfo createGetDetailMachineInfo() {
        return new GetDetailMachineInfo();
    }

    /**
     * Create an instance of {@link DetailMachineInfoReqContract }
     * 
     */
    public DetailMachineInfoReqContract createDetailMachineInfoReqContract() {
        return new DetailMachineInfoReqContract();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetDetailMachineInfo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "getDetailMachineInfo")
    public JAXBElement<GetDetailMachineInfo> createGetDetailMachineInfo(GetDetailMachineInfo value) {
        return new JAXBElement<GetDetailMachineInfo>(_GetDetailMachineInfo_QNAME, GetDetailMachineInfo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetDetailMachineInfoResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "getDetailMachineInfoResponse")
    public JAXBElement<GetDetailMachineInfoResponse> createGetDetailMachineInfoResponse(GetDetailMachineInfoResponse value) {
        return new JAXBElement<GetDetailMachineInfoResponse>(_GetDetailMachineInfoResponse_QNAME, GetDetailMachineInfoResponse.class, null, value);
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
