
package remote.wise.EAintegration.clientPackage.DealerInformationService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.EAintegration.clientPackage.DealerInformationService package. 
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

    private final static QName _SetDealerDetails_QNAME = new QName("http://webservice.service.wise.remote/", "setDealerDetails");
    private final static QName _SetDealerDetailsResponse_QNAME = new QName("http://webservice.service.wise.remote/", "setDealerDetailsResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.EAintegration.clientPackage.DealerInformationService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SetDealerDetails }
     * 
     */
    public SetDealerDetails createSetDealerDetails() {
        return new SetDealerDetails();
    }

    /**
     * Create an instance of {@link SetDealerDetailsResponse }
     * 
     */
    public SetDealerDetailsResponse createSetDealerDetailsResponse() {
        return new SetDealerDetailsResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetDealerDetails }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "setDealerDetails")
    public JAXBElement<SetDealerDetails> createSetDealerDetails(SetDealerDetails value) {
        return new JAXBElement<SetDealerDetails>(_SetDealerDetails_QNAME, SetDealerDetails.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetDealerDetailsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "setDealerDetailsResponse")
    public JAXBElement<SetDealerDetailsResponse> createSetDealerDetailsResponse(SetDealerDetailsResponse value) {
        return new JAXBElement<SetDealerDetailsResponse>(_SetDealerDetailsResponse_QNAME, SetDealerDetailsResponse.class, null, value);
    }

}
