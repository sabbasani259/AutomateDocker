
package remote.wise.client.MapService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.MapService package. 
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

    private final static QName _GetMap_QNAME = new QName("http://webservice.service.wise.remote/", "GetMap");
    private final static QName _GetMapResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetMapResponse");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.MapService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link MapReqContract }
     * 
     */
    public MapReqContract createMapReqContract() {
        return new MapReqContract();
    }

    /**
     * Create an instance of {@link GetMapResponse }
     * 
     */
    public GetMapResponse createGetMapResponse() {
        return new GetMapResponse();
    }

    /**
     * Create an instance of {@link MapRespContract }
     * 
     */
    public MapRespContract createMapRespContract() {
        return new MapRespContract();
    }

    /**
     * Create an instance of {@link GetMap }
     * 
     */
    public GetMap createGetMap() {
        return new GetMap();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetMap }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetMap")
    public JAXBElement<GetMap> createGetMap(GetMap value) {
        return new JAXBElement<GetMap>(_GetMap_QNAME, GetMap.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetMapResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetMapResponse")
    public JAXBElement<GetMapResponse> createGetMapResponse(GetMapResponse value) {
        return new JAXBElement<GetMapResponse>(_GetMapResponse_QNAME, GetMapResponse.class, null, value);
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
