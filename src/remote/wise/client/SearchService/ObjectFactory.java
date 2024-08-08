
package remote.wise.client.SearchService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.client.SearchService package. 
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

    private final static QName _GetMGLandMarkDetailsResponse_QNAME = new QName("http://webservice.service.wise.remote/", "getMGLandMarkDetailsResponse");
    private final static QName _GetMGLandMarkDetails_QNAME = new QName("http://webservice.service.wise.remote/", "getMGLandMarkDetails");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.client.SearchService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link MgLandMarkReqContract }
     * 
     */
    public MgLandMarkReqContract createMgLandMarkReqContract() {
        return new MgLandMarkReqContract();
    }

    /**
     * Create an instance of {@link MachineGroupEntity }
     * 
     */
    public MachineGroupEntity createMachineGroupEntity() {
        return new MachineGroupEntity();
    }

    /**
     * Create an instance of {@link LandmarkCategory }
     * 
     */
    public LandmarkCategory createLandmarkCategory() {
        return new LandmarkCategory();
    }

    /**
     * Create an instance of {@link GetMGLandMarkDetailsResponse }
     * 
     */
    public GetMGLandMarkDetailsResponse createGetMGLandMarkDetailsResponse() {
        return new GetMGLandMarkDetailsResponse();
    }

    /**
     * Create an instance of {@link MachineGroupTypeEntity }
     * 
     */
    public MachineGroupTypeEntity createMachineGroupTypeEntity() {
        return new MachineGroupTypeEntity();
    }

    /**
     * Create an instance of {@link Landmark }
     * 
     */
    public Landmark createLandmark() {
        return new Landmark();
    }

    /**
     * Create an instance of {@link GetMGLandMarkDetails }
     * 
     */
    public GetMGLandMarkDetails createGetMGLandMarkDetails() {
        return new GetMGLandMarkDetails();
    }

    /**
     * Create an instance of {@link MgLandMarkRespContract }
     * 
     */
    public MgLandMarkRespContract createMgLandMarkRespContract() {
        return new MgLandMarkRespContract();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetMGLandMarkDetailsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "getMGLandMarkDetailsResponse")
    public JAXBElement<GetMGLandMarkDetailsResponse> createGetMGLandMarkDetailsResponse(GetMGLandMarkDetailsResponse value) {
        return new JAXBElement<GetMGLandMarkDetailsResponse>(_GetMGLandMarkDetailsResponse_QNAME, GetMGLandMarkDetailsResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetMGLandMarkDetails }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "getMGLandMarkDetails")
    public JAXBElement<GetMGLandMarkDetails> createGetMGLandMarkDetails(GetMGLandMarkDetails value) {
        return new JAXBElement<GetMGLandMarkDetails>(_GetMGLandMarkDetails_QNAME, GetMGLandMarkDetails.class, null, value);
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
