
package remote.wise.client.AssetGroupService;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for assetGroupRespContract complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="assetGroupRespContract">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="assetGroupDescription" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="assetGroupId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="assetGroupName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="assetGroupTypeId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="assetGroupTypeName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="clientId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="loginId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="serialNumberList" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "assetGroupRespContract", propOrder = {
    "assetGroupDescription",
    "assetGroupId",
    "assetGroupName",
    "assetGroupTypeId",
    "assetGroupTypeName",
    "clientId",
    "loginId",
    "serialNumberList",
    "tenancyId"
})
public class AssetGroupRespContract {

    protected String assetGroupDescription;
    protected int assetGroupId;
    protected String assetGroupName;
    protected int assetGroupTypeId;
    protected String assetGroupTypeName;
    protected int clientId;
    protected String loginId;
    @XmlElement(nillable = true)
    protected List<String> serialNumberList;
    protected int tenancyId;

    /**
     * Gets the value of the assetGroupDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAssetGroupDescription() {
        return assetGroupDescription;
    }

    /**
     * Sets the value of the assetGroupDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAssetGroupDescription(String value) {
        this.assetGroupDescription = value;
    }

    /**
     * Gets the value of the assetGroupId property.
     * 
     */
    public int getAssetGroupId() {
        return assetGroupId;
    }

    /**
     * Sets the value of the assetGroupId property.
     * 
     */
    public void setAssetGroupId(int value) {
        this.assetGroupId = value;
    }

    /**
     * Gets the value of the assetGroupName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAssetGroupName() {
        return assetGroupName;
    }

    /**
     * Sets the value of the assetGroupName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAssetGroupName(String value) {
        this.assetGroupName = value;
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
     * Gets the value of the loginId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLoginId() {
        return loginId;
    }

    /**
     * Sets the value of the loginId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLoginId(String value) {
        this.loginId = value;
    }

    /**
     * Gets the value of the serialNumberList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the serialNumberList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSerialNumberList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getSerialNumberList() {
        if (serialNumberList == null) {
            serialNumberList = new ArrayList<String>();
        }
        return this.serialNumberList;
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
