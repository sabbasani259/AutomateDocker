
package remote.wise.client.FotaUpdateService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for fotaUpdateReqContract complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="fotaUpdateReqContract">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="fotaSessionId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="fotaVersionId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="fotaimeiNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "fotaUpdateReqContract", propOrder = {
    "fotaSessionId",
    "fotaVersionId",
    "fotaimeiNo"
})
public class FotaUpdateReqContract {

    protected String fotaSessionId;
    protected String fotaVersionId;
    protected String fotaimeiNo;

    /**
     * Gets the value of the fotaSessionId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFotaSessionId() {
        return fotaSessionId;
    }

    /**
     * Sets the value of the fotaSessionId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFotaSessionId(String value) {
        this.fotaSessionId = value;
    }

    /**
     * Gets the value of the fotaVersionId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFotaVersionId() {
        return fotaVersionId;
    }

    /**
     * Sets the value of the fotaVersionId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFotaVersionId(String value) {
        this.fotaVersionId = value;
    }

    /**
     * Gets the value of the fotaimeiNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFotaimeiNo() {
        return fotaimeiNo;
    }

    /**
     * Sets the value of the fotaimeiNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFotaimeiNo(String value) {
        this.fotaimeiNo = value;
    }

}
