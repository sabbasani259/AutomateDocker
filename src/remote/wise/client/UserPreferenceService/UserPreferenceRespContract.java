
package remote.wise.client.UserPreferenceService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for userPreferenceRespContract complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="userPreferenceRespContract">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="catalogId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="catalogName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="catalogValue" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="catalogValueId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="contactId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "userPreferenceRespContract", propOrder = {
    "catalogId",
    "catalogName",
    "catalogValue",
    "catalogValueId",
    "contactId"
})
public class UserPreferenceRespContract {

    protected int catalogId;
    protected String catalogName;
    protected String catalogValue;
    protected int catalogValueId;
    protected String contactId;

    /**
     * Gets the value of the catalogId property.
     * 
     */
    public int getCatalogId() {
        return catalogId;
    }

    /**
     * Sets the value of the catalogId property.
     * 
     */
    public void setCatalogId(int value) {
        this.catalogId = value;
    }

    /**
     * Gets the value of the catalogName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCatalogName() {
        return catalogName;
    }

    /**
     * Sets the value of the catalogName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCatalogName(String value) {
        this.catalogName = value;
    }

    /**
     * Gets the value of the catalogValue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCatalogValue() {
        return catalogValue;
    }

    /**
     * Sets the value of the catalogValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCatalogValue(String value) {
        this.catalogValue = value;
    }

    /**
     * Gets the value of the catalogValueId property.
     * 
     */
    public int getCatalogValueId() {
        return catalogValueId;
    }

    /**
     * Sets the value of the catalogValueId property.
     * 
     */
    public void setCatalogValueId(int value) {
        this.catalogValueId = value;
    }

    /**
     * Gets the value of the contactId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContactId() {
        return contactId;
    }

    /**
     * Sets the value of the contactId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContactId(String value) {
        this.contactId = value;
    }

}
