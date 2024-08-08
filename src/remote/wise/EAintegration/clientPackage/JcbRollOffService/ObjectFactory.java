
package remote.wise.EAintegration.clientPackage.JcbRollOffService;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the remote.wise.EAintegration.clientPackage.JcbRollOffService package. 
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

    private final static QName _SetVinMachineNameMapping_QNAME = new QName("http://webservice.service.wise.remote/", "setVinMachineNameMapping");
    private final static QName _SetVinMachineNameMappingResponse_QNAME = new QName("http://webservice.service.wise.remote/", "setVinMachineNameMappingResponse");
    private final static QName _CustomFault_QNAME = new QName("http://webservice.service.wise.remote/", "CustomFault");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: remote.wise.EAintegration.clientPackage.JcbRollOffService
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SetVinMachineNameMappingResponse }
     * 
     */
    public SetVinMachineNameMappingResponse createSetVinMachineNameMappingResponse() {
        return new SetVinMachineNameMappingResponse();
    }

    /**
     * Create an instance of {@link SetVinMachineNameMapping }
     * 
     */
    public SetVinMachineNameMapping createSetVinMachineNameMapping() {
        return new SetVinMachineNameMapping();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetVinMachineNameMapping }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "setVinMachineNameMapping")
    public JAXBElement<SetVinMachineNameMapping> createSetVinMachineNameMapping(SetVinMachineNameMapping value) {
        return new JAXBElement<SetVinMachineNameMapping>(_SetVinMachineNameMapping_QNAME, SetVinMachineNameMapping.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SetVinMachineNameMappingResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://webservice.service.wise.remote/", name = "setVinMachineNameMappingResponse")
    public JAXBElement<SetVinMachineNameMappingResponse> createSetVinMachineNameMappingResponse(SetVinMachineNameMappingResponse value) {
        return new JAXBElement<SetVinMachineNameMappingResponse>(_SetVinMachineNameMappingResponse_QNAME, SetVinMachineNameMappingResponse.class, null, value);
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
