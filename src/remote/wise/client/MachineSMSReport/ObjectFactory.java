
package remote.wise.client.MachineSMSReport;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.MachineSMSReport package. 
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

    private final static QName _GetMachineSMSDetails_QNAME = new QName("http://webservice.service.wise.remote/", "GetMachineSMSDetails");
    private final static QName _GetMachineSMSDetailsResponse_QNAME = new QName("http://webservice.service.wise.remote/", "GetMachineSMSDetailsResponse");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.MachineSMSReport
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetMachineSMSDetailsResponse }
     * 
     */
    public GetMachineSMSDetailsResponse createGetMachineSMSDetailsResponse() {
        return new GetMachineSMSDetailsResponse();
    }

    /**
     * Create an instance of {@link MachineSMSOutputContract }
     * 
     */
    public MachineSMSOutputContract createMachineSMSOutputContract() {
        return new MachineSMSOutputContract();
    }

    /**
     * Create an instance of {@link GetMachineSMSDetails }
     * 
     */
    public GetMachineSMSDetails createGetMachineSMSDetails() {
        return new GetMachineSMSDetails();
    }

    /**
     * Create an instance of {@link MachineSMSInputContract }
     * 
     */
    public MachineSMSInputContract createMachineSMSInputContract() {
        return new MachineSMSInputContract();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetMachineSMSDetails }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetMachineSMSDetails")
    public JAXBElement<GetMachineSMSDetails> createGetMachineSMSDetails(GetMachineSMSDetails value) {
        return new JAXBElement<GetMachineSMSDetails>(_GetMachineSMSDetails_QNAME, GetMachineSMSDetails.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetMachineSMSDetailsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "GetMachineSMSDetailsResponse")
    public JAXBElement<GetMachineSMSDetailsResponse> createGetMachineSMSDetailsResponse(GetMachineSMSDetailsResponse value) {
        return new JAXBElement<GetMachineSMSDetailsResponse>(_GetMachineSMSDetailsResponse_QNAME, GetMachineSMSDetailsResponse.class, null, value);
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
