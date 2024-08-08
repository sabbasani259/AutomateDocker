
package remote.wise.client.FotaAuthenticationService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for fotaAuthenticationReqContract complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="fotaAuthenticationReqContract">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="fotaimeiNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="fotasessionId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "fotaAuthenticationReqContract", propOrder = {
    "fotaimeiNumber",
    "fotasessionId"
})
public class FotaAuthenticationReqContract {

    protected String fotaimeiNumber;
    protected String fotasessionId;

    /**
     * Gets the value of the fotaimeiNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFotaimeiNumber() {
        return fotaimeiNumber;
    }

    /**
     * Sets the value of the fotaimeiNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFotaimeiNumber(String value) {
        this.fotaimeiNumber = value;
    }

    /**
     * Gets the value of the fotasessionId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFotasessionId() {
        return fotasessionId;
    }

    /**
     * Sets the value of the fotasessionId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFotasessionId(String value) {
        this.fotasessionId = value;
    }

}
