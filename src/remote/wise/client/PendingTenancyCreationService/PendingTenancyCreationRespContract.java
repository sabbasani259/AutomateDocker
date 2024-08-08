
package remote.wise.client.PendingTenancyCreationService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for pendingTenancyCreationRespContract complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="pendingTenancyCreationRespContract">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="accountId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="accountName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="parentTenancyId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="parentTenancyName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "pendingTenancyCreationRespContract", propOrder = {
    "accountId",
    "accountName",
    "parentTenancyId",
    "parentTenancyName"
})
public class PendingTenancyCreationRespContract {

    protected int accountId;
    protected String accountName;
    protected int parentTenancyId;
    protected String parentTenancyName;

    /**
     * Gets the value of the accountId property.
     * 
     */
    public int getAccountId() {
        return accountId;
    }

    /**
     * Sets the value of the accountId property.
     * 
     */
    public void setAccountId(int value) {
        this.accountId = value;
    }

    /**
     * Gets the value of the accountName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAccountName() {
        return accountName;
    }

    /**
     * Sets the value of the accountName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAccountName(String value) {
        this.accountName = value;
    }

    /**
     * Gets the value of the parentTenancyId property.
     * 
     */
    public int getParentTenancyId() {
        return parentTenancyId;
    }

    /**
     * Sets the value of the parentTenancyId property.
     * 
     */
    public void setParentTenancyId(int value) {
        this.parentTenancyId = value;
    }

    /**
     * Gets the value of the parentTenancyName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParentTenancyName() {
        return parentTenancyName;
    }

    /**
     * Sets the value of the parentTenancyName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParentTenancyName(String value) {
        this.parentTenancyName = value;
    }

}
