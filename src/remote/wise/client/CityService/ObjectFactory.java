
package remote.wise.client.CityService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.CityService package. 
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

    private final static QName _GetCitiesResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetCitiesResponse");
    private final static QName _GetCities_QNAME = new QName("http://webservice.service.wise.remote/", "GetCities");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.CityService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link CityRespContract }
     * 
     */
    public CityRespContract createCityRespContract() {
        return new CityRespContract();
    }

    /**
     * Create an instance of {@link GetCities }
     * 
     */
    public GetCities createGetCities() {
        return new GetCities();
    }

    /**
     * Create an instance of {@link GetCitiesResponse }
     * 
     */
    public GetCitiesResponse createGetCitiesResponse() {
        return new GetCitiesResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetCitiesResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetCitiesResponse")
    public JAXBElement<GetCitiesResponse> createGetCitiesResponse(GetCitiesResponse value) {
        return new JAXBElement<GetCitiesResponse>(_GetCitiesResponse_QNAME, GetCitiesResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetCities }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetCities")
    public JAXBElement<GetCities> createGetCities(GetCities value) {
        return new JAXBElement<GetCities>(_GetCities_QNAME, GetCities.class, null, value);
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
