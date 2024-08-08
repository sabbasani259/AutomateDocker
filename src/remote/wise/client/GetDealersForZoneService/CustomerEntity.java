
package remote.wise.client.GetDealersForZoneService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for customerEntity complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="customerEntity">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="customerTenancyId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="customerTenancyName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "customerEntity", propOrder = {
    "customerTenancyId",
    "customerTenancyName"
})
public class CustomerEntity {

    protected int customerTenancyId;
    protected String customerTenancyName;

    /**
     * Gets the value of the customerTenancyId property.
     * 
     */
    public int getCustomerTenancyId() {
        return customerTenancyId;
    }

    /**
     * Sets the value of the customerTenancyId property.
     * 
     */
    public void setCustomerTenancyId(int value) {
        this.customerTenancyId = value;
    }

    /**
     * Gets the value of the customerTenancyName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomerTenancyName() {
        return customerTenancyName;
    }

    /**
     * Sets the value of the customerTenancyName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomerTenancyName(String value) {
        this.customerTenancyName = value;
    }

}
