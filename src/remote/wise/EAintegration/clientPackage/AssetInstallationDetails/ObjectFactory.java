
package remote.wise.EAintegration.clientPackage.AssetInstallationDetails;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.EAintegration.clientPackage.AssetInstallationDetails package. 
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

    private final static QName _SetAssetServiceSchedule_QNAME = new QName("http://webservice.service.wise.remote/", "setAssetServiceSchedule");
    private final static QName _SetAssetServiceScheduleResponse_QNAME = new QName("http://webservice.service.wise.remote/", "setAssetServiceScheduleResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.EAintegration.clientPackage.AssetInstallationDetails
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SetAssetServiceSchedule }
     * 
     */
    public SetAssetServiceSchedule createSetAssetServiceSchedule() {
        return new SetAssetServiceSchedule();
    }

    /**
     * Create an instance of {@link SetAssetServiceScheduleResponse }
     * 
     */
    public SetAssetServiceScheduleResponse createSetAssetServiceScheduleResponse() {
        return new SetAssetServiceScheduleResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetAssetServiceSchedule }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "setAssetServiceSchedule")
    public JAXBElement<SetAssetServiceSchedule> createSetAssetServiceSchedule(SetAssetServiceSchedule value) {
        return new JAXBElement<SetAssetServiceSchedule>(_SetAssetServiceSchedule_QNAME, SetAssetServiceSchedule.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetAssetServiceScheduleResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "setAssetServiceScheduleResponse")
    public JAXBElement<SetAssetServiceScheduleResponse> createSetAssetServiceScheduleResponse(SetAssetServiceScheduleResponse value) {
        return new JAXBElement<SetAssetServiceScheduleResponse>(_SetAssetServiceScheduleResponse_QNAME, SetAssetServiceScheduleResponse.class, null, value);
    }

}
