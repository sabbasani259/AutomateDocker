
package remote.wise.client.AssetGroupVersionVolumeSummaryService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.AssetGroupVersionVolumeSummaryService package. 
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

    private final static QName _SetGropTypeForDeviceStatusInfo_QNAME = new QName("http://webservice.service.wise.remote/", "setGropTypeForDeviceStatusInfo");
    private final static QName _SetGroupVersionVolume_QNAME = new QName("http://webservice.service.wise.remote/", "setGroupVersionVolume");
    private final static QName _SetGropTypeForDeviceStatusInfoResponse_QNAME = new QName("http://webservice.service.wise.remote/", "setGropTypeForDeviceStatusInfoResponse");
    private final static QName _SetGroupVersionVolumeResponse_QNAME = new QName("http://webservice.service.wise.remote/", "setGroupVersionVolumeResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.AssetGroupVersionVolumeSummaryService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SetGropTypeForDeviceStatusInfoResponse }
     * 
     */
    public SetGropTypeForDeviceStatusInfoResponse createSetGropTypeForDeviceStatusInfoResponse() {
        return new SetGropTypeForDeviceStatusInfoResponse();
    }

    /**
     * Create an instance of {@link SetGroupVersionVolume }
     * 
     */
    public SetGroupVersionVolume createSetGroupVersionVolume() {
        return new SetGroupVersionVolume();
    }

    /**
     * Create an instance of {@link SetGroupVersionVolumeResponse }
     * 
     */
    public SetGroupVersionVolumeResponse createSetGroupVersionVolumeResponse() {
        return new SetGroupVersionVolumeResponse();
    }

    /**
     * Create an instance of {@link SetGropTypeForDeviceStatusInfo }
     * 
     */
    public SetGropTypeForDeviceStatusInfo createSetGropTypeForDeviceStatusInfo() {
        return new SetGropTypeForDeviceStatusInfo();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetGropTypeForDeviceStatusInfo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "setGropTypeForDeviceStatusInfo")
    public JAXBElement<SetGropTypeForDeviceStatusInfo> createSetGropTypeForDeviceStatusInfo(SetGropTypeForDeviceStatusInfo value) {
        return new JAXBElement<SetGropTypeForDeviceStatusInfo>(_SetGropTypeForDeviceStatusInfo_QNAME, SetGropTypeForDeviceStatusInfo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetGroupVersionVolume }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "setGroupVersionVolume")
    public JAXBElement<SetGroupVersionVolume> createSetGroupVersionVolume(SetGroupVersionVolume value) {
        return new JAXBElement<SetGroupVersionVolume>(_SetGroupVersionVolume_QNAME, SetGroupVersionVolume.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetGropTypeForDeviceStatusInfoResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "setGropTypeForDeviceStatusInfoResponse")
    public JAXBElement<SetGropTypeForDeviceStatusInfoResponse> createSetGropTypeForDeviceStatusInfoResponse(SetGropTypeForDeviceStatusInfoResponse value) {
        return new JAXBElement<SetGropTypeForDeviceStatusInfoResponse>(_SetGropTypeForDeviceStatusInfoResponse_QNAME, SetGropTypeForDeviceStatusInfoResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetGroupVersionVolumeResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "setGroupVersionVolumeResponse")
    public JAXBElement<SetGroupVersionVolumeResponse> createSetGroupVersionVolumeResponse(SetGroupVersionVolumeResponse value) {
        return new JAXBElement<SetGroupVersionVolumeResponse>(_SetGroupVersionVolumeResponse_QNAME, SetGroupVersionVolumeResponse.class, null, value);
    }

}
