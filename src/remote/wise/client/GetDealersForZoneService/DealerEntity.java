
package remote.wise.client.GetDealersForZoneService;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for dealerEntity complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="dealerEntity">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="customersList" type="{http://webservice.service.wise.remote/}customerEntity" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="dealerId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="dealerName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dealerEntity", propOrder = {
    "customersList",
    "dealerId",
    "dealerName"
})
public class DealerEntity {

    @XmlElement(nillable = true)
    protected List<CustomerEntity> customersList;
    protected int dealerId;
    protected String dealerName;

    /**
     * Gets the value of the customersList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the customersList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCustomersList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CustomerEntity }
     * 
     * 
     */
    public List<CustomerEntity> getCustomersList() {
        if (customersList == null) {
            customersList = new ArrayList<CustomerEntity>();
        }
        return this.customersList;
    }

    /**
     * Gets the value of the dealerId property.
     * 
     */
    public int getDealerId() {
        return dealerId;
    }

    /**
     * Sets the value of the dealerId property.
     * 
     */
    public void setDealerId(int value) {
        this.dealerId = value;
    }

    /**
     * Gets the value of the dealerName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDealerName() {
        return dealerName;
    }

    /**
     * Sets the value of the dealerName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDealerName(String value) {
        this.dealerName = value;
    }

}
