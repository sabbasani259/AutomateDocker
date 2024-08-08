
package remote.wise.client.ZonalAccountCodeService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for dealerCodeEntity complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="dealerCodeEntity">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="dealerAccountCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dealerAccountCodeName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dealerCodeEntity", propOrder = {
    "dealerAccountCode",
    "dealerAccountCodeName"
})
public class DealerCodeEntity {

    protected String dealerAccountCode;
    protected String dealerAccountCodeName;

    /**
     * Gets the value of the dealerAccountCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDealerAccountCode() {
        return dealerAccountCode;
    }

    /**
     * Sets the value of the dealerAccountCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDealerAccountCode(String value) {
        this.dealerAccountCode = value;
    }

    /**
     * Gets the value of the dealerAccountCodeName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDealerAccountCodeName() {
        return dealerAccountCodeName;
    }

    /**
     * Sets the value of the dealerAccountCodeName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDealerAccountCodeName(String value) {
        this.dealerAccountCodeName = value;
    }

}
