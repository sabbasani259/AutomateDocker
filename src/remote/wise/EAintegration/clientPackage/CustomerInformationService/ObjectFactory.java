
package remote.wise.EAintegration.clientPackage.CustomerInformationService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.EAintegration.clientPackage.CustomerInformationService package. 
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

    private final static QName _SetCustomerDetailsResponse_QNAME = new QName("http://webservice.service.wise.remote/", "setCustomerDetailsResponse");
    private final static QName _SetCustomerDetails_QNAME = new QName("http://webservice.service.wise.remote/", "setCustomerDetails");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.EAintegration.clientPackage.CustomerInformationService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SetCustomerDetailsResponse }
     * 
     */
    public SetCustomerDetailsResponse createSetCustomerDetailsResponse() {
        return new SetCustomerDetailsResponse();
    }

    /**
     * Create an instance of {@link SetCustomerDetails }
     * 
     */
    public SetCustomerDetails createSetCustomerDetails() {
        return new SetCustomerDetails();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetCustomerDetailsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "setCustomerDetailsResponse")
    public JAXBElement<SetCustomerDetailsResponse> createSetCustomerDetailsResponse(SetCustomerDetailsResponse value) {
        return new JAXBElement<SetCustomerDetailsResponse>(_SetCustomerDetailsResponse_QNAME, SetCustomerDetailsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetCustomerDetails }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "setCustomerDetails")
    public JAXBElement<SetCustomerDetails> createSetCustomerDetails(SetCustomerDetails value) {
        return new JAXBElement<SetCustomerDetails>(_SetCustomerDetails_QNAME, SetCustomerDetails.class, null, value);
    }

}
