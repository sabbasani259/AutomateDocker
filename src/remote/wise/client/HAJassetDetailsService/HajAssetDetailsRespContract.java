
package remote.wise.client.HAJassetDetailsService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for hajAssetDetailsRespContract complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="hajAssetDetailsRespContract">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="accountid" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="modelcode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="profilecode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="vin" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "hajAssetDetailsRespContract", propOrder = {
    "accountid",
    "modelcode",
    "profilecode",
    "vin"
})
public class HajAssetDetailsRespContract {

    protected int accountid;
    protected String modelcode;
    protected String profilecode;
    protected String vin;

    /**
     * Gets the value of the accountid property.
     * 
     */
    public int getAccountid() {
        return accountid;
    }

    /**
     * Sets the value of the accountid property.
     * 
     */
    public void setAccountid(int value) {
        this.accountid = value;
    }

    /**
     * Gets the value of the modelcode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getModelcode() {
        return modelcode;
    }

    /**
     * Sets the value of the modelcode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setModelcode(String value) {
        this.modelcode = value;
    }

    /**
     * Gets the value of the profilecode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProfilecode() {
        return profilecode;
    }

    /**
     * Sets the value of the profilecode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProfilecode(String value) {
        this.profilecode = value;
    }

    /**
     * Gets the value of the vin property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVin() {
        return vin;
    }

    /**
     * Sets the value of the vin property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVin(String value) {
        this.vin = value;
    }

}
