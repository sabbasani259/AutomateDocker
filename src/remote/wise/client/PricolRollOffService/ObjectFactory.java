
package remote.wise.client.PricolRollOffService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.PricolRollOffService package. 
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

    private final static QName _RollOffPricolDevice_QNAME = new QName("http://webservice.service.wise.remote/", "rollOffPricolDevice");
    private final static QName _RollOffPricolDeviceResponse_QNAME = new QName("http://webservice.service.wise.remote/", "rollOffPricolDeviceResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.PricolRollOffService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link RollOffPricolDevice }
     * 
     */
    public RollOffPricolDevice createRollOffPricolDevice() {
        return new RollOffPricolDevice();
    }

    /**
     * Create an instance of {@link RollOffPricolDeviceResponse }
     * 
     */
    public RollOffPricolDeviceResponse createRollOffPricolDeviceResponse() {
        return new RollOffPricolDeviceResponse();
    }

    /**
     * Create an instance of {@link PricolRollOffReqContract }
     * 
     */
    public PricolRollOffReqContract createPricolRollOffReqContract() {
        return new PricolRollOffReqContract();
    }

    /**
     * Create an instance of {@link PricolRollOffRespContract }
     * 
     */
    public PricolRollOffRespContract createPricolRollOffRespContract() {
        return new PricolRollOffRespContract();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RollOffPricolDevice }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "rollOffPricolDevice")
    public JAXBElement<RollOffPricolDevice> createRollOffPricolDevice(RollOffPricolDevice value) {
        return new JAXBElement<RollOffPricolDevice>(_RollOffPricolDevice_QNAME, RollOffPricolDevice.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RollOffPricolDeviceResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "rollOffPricolDeviceResponse")
    public JAXBElement<RollOffPricolDeviceResponse> createRollOffPricolDeviceResponse(RollOffPricolDeviceResponse value) {
        return new JAXBElement<RollOffPricolDeviceResponse>(_RollOffPricolDeviceResponse_QNAME, RollOffPricolDeviceResponse.class, null, value);
    }

}
