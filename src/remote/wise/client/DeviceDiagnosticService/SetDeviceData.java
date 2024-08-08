
package remote.wise.client.DeviceDiagnosticService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for setDeviceData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="setDeviceData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="xmlInput" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "setDeviceData", propOrder = {
    "xmlInput"
})
public class SetDeviceData {

    protected String xmlInput;

    /**
     * Gets the value of the xmlInput property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getXmlInput() {
        return xmlInput;
    }

    /**
     * Sets the value of the xmlInput property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setXmlInput(String value) {
        this.xmlInput = value;
    }

}
