
package remote.wise.EAintegration.clientPackage.MasterServiceSchedule;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.EAintegration.clientPackage.MasterServiceSchedule package. 
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

    private final static QName _GetMasterServiceScheduleResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetMasterServiceScheduleResponse");
    private final static QName _SetMasterServiceScheduleDetailsResponse_QNAME = new QName("http://webservice.service.wise.remote/", "setMasterServiceScheduleDetailsResponse");
    private final static QName _SetMasterServiceScheduleDetails_QNAME = new QName("http://webservice.service.wise.remote/", "setMasterServiceScheduleDetails");
    private final static QName _GetMasterServiceSchedule_QNAME = new QName("http://webservice.service.wise.remote/", "GetMasterServiceSchedule");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.EAintegration.clientPackage.MasterServiceSchedule
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetMasterServiceSchedule }
     * 
     */
    public GetMasterServiceSchedule createGetMasterServiceSchedule() {
        return new GetMasterServiceSchedule();
    }

    /**
     * Create an instance of {@link SetMasterServiceScheduleDetailsResponse }
     * 
     */
    public SetMasterServiceScheduleDetailsResponse createSetMasterServiceScheduleDetailsResponse() {
        return new SetMasterServiceScheduleDetailsResponse();
    }

    /**
     * Create an instance of {@link MasterServiceScheduleResponseContract }
     * 
     */
    public MasterServiceScheduleResponseContract createMasterServiceScheduleResponseContract() {
        return new MasterServiceScheduleResponseContract();
    }

    /**
     * Create an instance of {@link SetMasterServiceScheduleDetails }
     * 
     */
    public SetMasterServiceScheduleDetails createSetMasterServiceScheduleDetails() {
        return new SetMasterServiceScheduleDetails();
    }

    /**
     * Create an instance of {@link GetMasterServiceScheduleResponse }
     * 
     */
    public GetMasterServiceScheduleResponse createGetMasterServiceScheduleResponse() {
        return new GetMasterServiceScheduleResponse();
    }

    /**
     * Create an instance of {@link MasterServiceScheduleRequestContract }
     * 
     */
    public MasterServiceScheduleRequestContract createMasterServiceScheduleRequestContract() {
        return new MasterServiceScheduleRequestContract();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetMasterServiceScheduleResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetMasterServiceScheduleResponse")
    public JAXBElement<GetMasterServiceScheduleResponse> createGetMasterServiceScheduleResponse(GetMasterServiceScheduleResponse value) {
        return new JAXBElement<GetMasterServiceScheduleResponse>(_GetMasterServiceScheduleResponse_QNAME, GetMasterServiceScheduleResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetMasterServiceScheduleDetailsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "setMasterServiceScheduleDetailsResponse")
    public JAXBElement<SetMasterServiceScheduleDetailsResponse> createSetMasterServiceScheduleDetailsResponse(SetMasterServiceScheduleDetailsResponse value) {
        return new JAXBElement<SetMasterServiceScheduleDetailsResponse>(_SetMasterServiceScheduleDetailsResponse_QNAME, SetMasterServiceScheduleDetailsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetMasterServiceScheduleDetails }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "setMasterServiceScheduleDetails")
    public JAXBElement<SetMasterServiceScheduleDetails> createSetMasterServiceScheduleDetails(SetMasterServiceScheduleDetails value) {
        return new JAXBElement<SetMasterServiceScheduleDetails>(_SetMasterServiceScheduleDetails_QNAME, SetMasterServiceScheduleDetails.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetMasterServiceSchedule }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetMasterServiceSchedule")
    public JAXBElement<GetMasterServiceSchedule> createGetMasterServiceSchedule(GetMasterServiceSchedule value) {
        return new JAXBElement<GetMasterServiceSchedule>(_GetMasterServiceSchedule_QNAME, GetMasterServiceSchedule.class, null, value);
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
