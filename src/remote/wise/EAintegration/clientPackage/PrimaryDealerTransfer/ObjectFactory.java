
package remote.wise.EAintegration.clientPackage.PrimaryDealerTransfer;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.EAintegration.clientPackage.PrimaryDealerTransfer package. 
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

    private final static QName _PrimaryDealerTransfer_QNAME = new QName("http://webservice.service.wise.remote/", "primaryDealerTransfer");
    private final static QName _PrimaryDealerTransferResponse_QNAME = new QName("http://webservice.service.wise.remote/", "primaryDealerTransferResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.EAintegration.clientPackage.PrimaryDealerTransfer
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link PrimaryDealerTransferResponse }
     * 
     */
    public PrimaryDealerTransferResponse createPrimaryDealerTransferResponse() {
        return new PrimaryDealerTransferResponse();
    }

    /**
     * Create an instance of {@link PrimaryDealerTransfer }
     * 
     */
    public PrimaryDealerTransfer createPrimaryDealerTransfer() {
        return new PrimaryDealerTransfer();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PrimaryDealerTransfer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "primaryDealerTransfer")
    public JAXBElement<PrimaryDealerTransfer> createPrimaryDealerTransfer(PrimaryDealerTransfer value) {
        return new JAXBElement<PrimaryDealerTransfer>(_PrimaryDealerTransfer_QNAME, PrimaryDealerTransfer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PrimaryDealerTransferResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "primaryDealerTransferResponse")
    public JAXBElement<PrimaryDealerTransferResponse> createPrimaryDealerTransferResponse(PrimaryDealerTransferResponse value) {
        return new JAXBElement<PrimaryDealerTransferResponse>(_PrimaryDealerTransferResponse_QNAME, PrimaryDealerTransferResponse.class, null, value);
    }

}
