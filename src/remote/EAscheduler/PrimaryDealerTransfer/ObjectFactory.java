
package remote.EAscheduler.PrimaryDealerTransfer;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.EAscheduler.PrimaryDealerTransfer package. 
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

    private final static QName _ProcessEADealerTransfer_QNAME = new QName("http://webservices.EAintegration.wise.remote/", "processEADealerTransfer");
    private final static QName _ProcessEADealerTransferResponse_QNAME = new QName("http://webservices.EAintegration.wise.remote/", "processEADealerTransferResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.EAscheduler.PrimaryDealerTransfer
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ProcessEADealerTransferResponse }
     * 
     */
    public ProcessEADealerTransferResponse createProcessEADealerTransferResponse() {
        return new ProcessEADealerTransferResponse();
    }

    /**
     * Create an instance of {@link ProcessEADealerTransfer }
     * 
     */
    public ProcessEADealerTransfer createProcessEADealerTransfer() {
        return new ProcessEADealerTransfer();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ProcessEADealerTransfer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservices.EAintegration.wise.remote/", name = "processEADealerTransfer")
    public JAXBElement<ProcessEADealerTransfer> createProcessEADealerTransfer(ProcessEADealerTransfer value) {
        return new JAXBElement<ProcessEADealerTransfer>(_ProcessEADealerTransfer_QNAME, ProcessEADealerTransfer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ProcessEADealerTransferResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservices.EAintegration.wise.remote/", name = "processEADealerTransferResponse")
    public JAXBElement<ProcessEADealerTransferResponse> createProcessEADealerTransferResponse(ProcessEADealerTransferResponse value) {
        return new JAXBElement<ProcessEADealerTransferResponse>(_ProcessEADealerTransferResponse_QNAME, ProcessEADealerTransferResponse.class, null, value);
    }

}
