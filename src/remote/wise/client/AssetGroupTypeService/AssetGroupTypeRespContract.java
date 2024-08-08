
package remote.wise.client.AssetGroupTypeService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for assetGroupTypeRespContract complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="assetGroupTypeRespContract">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="assetGroupTypeDescription" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="assetGroupTypeId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="assetGroupTypeName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="clientId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="tenancyId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "assetGroupTypeRespContract", propOrder = {
    "assetGroupTypeDescription",
    "assetGroupTypeId",
    "assetGroupTypeName",
    "clientId",
    "tenancyId"
})
public class AssetGroupTypeRespContract {

    protected String assetGroupTypeDescription;
    protected int assetGroupTypeId;
    protected String assetGroupTypeName;
    protected int clientId;
    protected int tenancyId;

    /**
     * Gets the value of the assetGroupTypeDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAssetGroupTypeDescription() {
        return assetGroupTypeDescription;
    }

    /**
     * Sets the value of the assetGroupTypeDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAssetGroupTypeDescription(String value) {
        this.assetGroupTypeDescription = value;
    }

    /**
     * Gets the value of the assetGroupTypeId property.
     * 
     */
    public int getAssetGroupTypeId() {
        return assetGroupTypeId;
    }

    /**
     * Sets the value of the assetGroupTypeId property.
     * 
     */
    public void setAssetGroupTypeId(int value) {
        this.assetGroupTypeId = value;
    }

    /**
     * Gets the value of the assetGroupTypeName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAssetGroupTypeName() {
        return assetGroupTypeName;
    }

    /**
     * Sets the value of the assetGroupTypeName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAssetGroupTypeName(String value) {
        this.assetGroupTypeName = value;
    }

    /**
     * Gets the value of the clientId property.
     * 
     */
    public int getClientId() {
        return clientId;
    }

    /**
     * Sets the value of the clientId property.
     * 
     */
    public void setClientId(int value) {
        this.clientId = value;
    }

    /**
     * Gets the value of the tenancyId property.
     * 
     */
    public int getTenancyId() {
        return tenancyId;
    }

    /**
     * Sets the value of the tenancyId property.
     * 
     */
    public void setTenancyId(int value) {
        this.tenancyId = value;
    }

}
