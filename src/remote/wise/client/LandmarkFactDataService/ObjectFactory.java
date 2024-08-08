
package remote.wise.client.LandmarkFactDataService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.LandmarkFactDataService package. 
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

    private final static QName _SetLandmarkFactDataResponse_QNAME = new QName("http://webservice.service.wise.remote/", "setLandmarkFactDataResponse");
    private final static QName _SetLandmarkFactData_QNAME = new QName("http://webservice.service.wise.remote/", "setLandmarkFactData");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.LandmarkFactDataService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SetLandmarkFactDataResponse }
     * 
     */
    public SetLandmarkFactDataResponse createSetLandmarkFactDataResponse() {
        return new SetLandmarkFactDataResponse();
    }

    /**
     * Create an instance of {@link SetLandmarkFactData }
     * 
     */
    public SetLandmarkFactData createSetLandmarkFactData() {
        return new SetLandmarkFactData();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetLandmarkFactDataResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "setLandmarkFactDataResponse")
    public JAXBElement<SetLandmarkFactDataResponse> createSetLandmarkFactDataResponse(SetLandmarkFactDataResponse value) {
        return new JAXBElement<SetLandmarkFactDataResponse>(_SetLandmarkFactDataResponse_QNAME, SetLandmarkFactDataResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetLandmarkFactData }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "setLandmarkFactData")
    public JAXBElement<SetLandmarkFactData> createSetLandmarkFactData(SetLandmarkFactData value) {
        return new JAXBElement<SetLandmarkFactData>(_SetLandmarkFactData_QNAME, SetLandmarkFactData.class, null, value);
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
