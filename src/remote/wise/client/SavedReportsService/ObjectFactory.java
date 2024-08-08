
package remote.wise.client.SavedReportsService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.SavedReportsService package. 
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

    private final static QName _SetSavedReportServiceResponse_QNAME = new QName("http://webservice.service.wise.remote/", "SetSavedReportServiceResponse");
    private final static QName _GetSavedReportService_QNAME = new QName("http://webservice.service.wise.remote/", "GetSavedReportService");
    private final static QName _SetSavedReportService_QNAME = new QName("http://webservice.service.wise.remote/", "SetSavedReportService");
    private final static QName _GetSavedReportServiceResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetSavedReportServiceResponse");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.SavedReportsService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SavedReportRespContract.FilterNameFieldValue.Entry }
     * 
     */
    public SavedReportRespContract.FilterNameFieldValue.Entry createSavedReportRespContractFilterNameFieldValueEntry() {
        return new SavedReportRespContract.FilterNameFieldValue.Entry();
    }

    /**
     * Create an instance of {@link HashMap }
     * 
     */
    public HashMap createHashMap() {
        return new HashMap();
    }

    /**
     * Create an instance of {@link GetSavedReportService }
     * 
     */
    public GetSavedReportService createGetSavedReportService() {
        return new GetSavedReportService();
    }

    /**
     * Create an instance of {@link SavedReportReqContract }
     * 
     */
    public SavedReportReqContract createSavedReportReqContract() {
        return new SavedReportReqContract();
    }

    /**
     * Create an instance of {@link SetSavedReportServiceResponse }
     * 
     */
    public SetSavedReportServiceResponse createSetSavedReportServiceResponse() {
        return new SetSavedReportServiceResponse();
    }

    /**
     * Create an instance of {@link SavedReportRespContract.FilterNameFieldValue }
     * 
     */
    public SavedReportRespContract.FilterNameFieldValue createSavedReportRespContractFilterNameFieldValue() {
        return new SavedReportRespContract.FilterNameFieldValue();
    }

    /**
     * Create an instance of {@link GetSavedReportServiceResponse }
     * 
     */
    public GetSavedReportServiceResponse createGetSavedReportServiceResponse() {
        return new GetSavedReportServiceResponse();
    }

    /**
     * Create an instance of {@link SetSavedReportService }
     * 
     */
    public SetSavedReportService createSetSavedReportService() {
        return new SetSavedReportService();
    }

    /**
     * Create an instance of {@link SavedReportRespContract }
     * 
     */
    public SavedReportRespContract createSavedReportRespContract() {
        return new SavedReportRespContract();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetSavedReportServiceResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "SetSavedReportServiceResponse")
    public JAXBElement<SetSavedReportServiceResponse> createSetSavedReportServiceResponse(SetSavedReportServiceResponse value) {
        return new JAXBElement<SetSavedReportServiceResponse>(_SetSavedReportServiceResponse_QNAME, SetSavedReportServiceResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetSavedReportService }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetSavedReportService")
    public JAXBElement<GetSavedReportService> createGetSavedReportService(GetSavedReportService value) {
        return new JAXBElement<GetSavedReportService>(_GetSavedReportService_QNAME, GetSavedReportService.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetSavedReportService }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "SetSavedReportService")
    public JAXBElement<SetSavedReportService> createSetSavedReportService(SetSavedReportService value) {
        return new JAXBElement<SetSavedReportService>(_SetSavedReportService_QNAME, SetSavedReportService.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetSavedReportServiceResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetSavedReportServiceResponse")
    public JAXBElement<GetSavedReportServiceResponse> createGetSavedReportServiceResponse(GetSavedReportServiceResponse value) {
        return new JAXBElement<GetSavedReportServiceResponse>(_GetSavedReportServiceResponse_QNAME, GetSavedReportServiceResponse.class, null, value);
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
