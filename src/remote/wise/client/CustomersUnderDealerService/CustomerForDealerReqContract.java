
package remote.wise.client.CustomersUnderDealerService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for customerForDealerReqContract complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="customerForDealerReqContract">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="dealerTenancyId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "customerForDealerReqContract", propOrder = {
    "dealerTenancyId"
})
public class CustomerForDealerReqContract {

    protected int dealerTenancyId;

    /**
     * Gets the value of the dealerTenancyId property.
     * 
     */
    public int getDealerTenancyId() {
        return dealerTenancyId;
    }

    /**
     * Sets the value of the dealerTenancyId property.
     * 
     */
    public void setDealerTenancyId(int value) {
        this.dealerTenancyId = value;
    }

}
