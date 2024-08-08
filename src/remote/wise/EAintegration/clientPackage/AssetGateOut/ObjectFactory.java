
package remote.wise.EAintegration.clientPackage.AssetGateOut;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.EAintegration.clientPackage.AssetGateOut package. 
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

    private final static QName _SetAssetGateoutService_QNAME = new QName("http://webservice.service.wise.remote/", "setAssetGateoutService");
    private final static QName _SetAssetGateoutServiceResponse_QNAME = new QName("http://webservice.service.wise.remote/", "setAssetGateoutServiceResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.EAintegration.clientPackage.AssetGateOut
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SetAssetGateoutServiceResponse }
     * 
     */
    public SetAssetGateoutServiceResponse createSetAssetGateoutServiceResponse() {
        return new SetAssetGateoutServiceResponse();
    }

    /**
     * Create an instance of {@link SetAssetGateoutService }
     * 
     */
    public SetAssetGateoutService createSetAssetGateoutService() {
        return new SetAssetGateoutService();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetAssetGateoutService }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "setAssetGateoutService")
    public JAXBElement<SetAssetGateoutService> createSetAssetGateoutService(SetAssetGateoutService value) {
        return new JAXBElement<SetAssetGateoutService>(_SetAssetGateoutService_QNAME, SetAssetGateoutService.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetAssetGateoutServiceResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "setAssetGateoutServiceResponse")
    public JAXBElement<SetAssetGateoutServiceResponse> createSetAssetGateoutServiceResponse(SetAssetGateoutServiceResponse value) {
        return new JAXBElement<SetAssetGateoutServiceResponse>(_SetAssetGateoutServiceResponse_QNAME, SetAssetGateoutServiceResponse.class, null, value);
    }

}
