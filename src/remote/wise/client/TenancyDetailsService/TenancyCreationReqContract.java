
package remote.wise.client.TenancyDetailsService;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for tenancyCreationReqContract complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tenancyCreationReqContract">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="accountId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="childTenancyId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="childTenancyName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="countryCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="loginId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="operatingEndTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="operatingStartTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="overrideMachineOperatingHours" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="parentTenancyId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="parentTenancyUserIdList" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="tenancyAdminEmailId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tenancyAdminFirstName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tenancyAdminLastName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tenancyAdminPhoneNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tenancyAdminRoleId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tenancyCreationReqContract", propOrder = {
    "accountId",
    "childTenancyId",
    "childTenancyName",
    "countryCode",
    "loginId",
    "operatingEndTime",
    "operatingStartTime",
    "overrideMachineOperatingHours",
    "parentTenancyId",
    "parentTenancyUserIdList",
    "tenancyAdminEmailId",
    "tenancyAdminFirstName",
    "tenancyAdminLastName",
    "tenancyAdminPhoneNumber",
    "tenancyAdminRoleId"
})
public class TenancyCreationReqContract {

    protected int accountId;
    protected int childTenancyId;
    protected String childTenancyName;
    protected String countryCode;
    protected String loginId;
    protected String operatingEndTime;
    protected String operatingStartTime;
    protected boolean overrideMachineOperatingHours;
    protected int parentTenancyId;
    @XmlElement(nillable = true)
    protected List<String> parentTenancyUserIdList;
    protected String tenancyAdminEmailId;
    protected String tenancyAdminFirstName;
    protected String tenancyAdminLastName;
    protected String tenancyAdminPhoneNumber;
    protected int tenancyAdminRoleId;

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
     * Gets the value of the childTenancyId property.
     * 
     */
    public int getChildTenancyId() {
        return childTenancyId;
    }

    /**
     * Sets the value of the childTenancyId property.
     * 
     */
    public void setChildTenancyId(int value) {
        this.childTenancyId = value;
    }

    public void setParentTenancyUserIdList(List<String> parentTenancyUserIdList) {
		this.parentTenancyUserIdList = parentTenancyUserIdList;
	}

	/**
     * Gets the value of the childTenancyName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getChildTenancyName() {
        return childTenancyName;
    }

    /**
     * Sets the value of the childTenancyName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setChildTenancyName(String value) {
        this.childTenancyName = value;
    }

    /**
     * Gets the value of the countryCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * Sets the value of the countryCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCountryCode(String value) {
        this.countryCode = value;
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
     * Gets the value of the operatingEndTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOperatingEndTime() {
        return operatingEndTime;
    }

    /**
     * Sets the value of the operatingEndTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOperatingEndTime(String value) {
        this.operatingEndTime = value;
    }

    /**
     * Gets the value of the operatingStartTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOperatingStartTime() {
        return operatingStartTime;
    }

    /**
     * Sets the value of the operatingStartTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOperatingStartTime(String value) {
        this.operatingStartTime = value;
    }

    /**
     * Gets the value of the overrideMachineOperatingHours property.
     * 
     */
    public boolean isOverrideMachineOperatingHours() {
        return overrideMachineOperatingHours;
    }

    /**
     * Sets the value of the overrideMachineOperatingHours property.
     * 
     */
    public void setOverrideMachineOperatingHours(boolean value) {
        this.overrideMachineOperatingHours = value;
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
     * Gets the value of the parentTenancyUserIdList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the parentTenancyUserIdList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getParentTenancyUserIdList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getParentTenancyUserIdList() {
        if (parentTenancyUserIdList == null) {
            parentTenancyUserIdList = new ArrayList<String>();
        }
        return this.parentTenancyUserIdList;
    }

    /**
     * Gets the value of the tenancyAdminEmailId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTenancyAdminEmailId() {
        return tenancyAdminEmailId;
    }

    /**
     * Sets the value of the tenancyAdminEmailId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTenancyAdminEmailId(String value) {
        this.tenancyAdminEmailId = value;
    }

    /**
     * Gets the value of the tenancyAdminFirstName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTenancyAdminFirstName() {
        return tenancyAdminFirstName;
    }

    /**
     * Sets the value of the tenancyAdminFirstName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTenancyAdminFirstName(String value) {
        this.tenancyAdminFirstName = value;
    }

    /**
     * Gets the value of the tenancyAdminLastName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTenancyAdminLastName() {
        return tenancyAdminLastName;
    }

    /**
     * Sets the value of the tenancyAdminLastName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTenancyAdminLastName(String value) {
        this.tenancyAdminLastName = value;
    }

    /**
     * Gets the value of the tenancyAdminPhoneNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTenancyAdminPhoneNumber() {
        return tenancyAdminPhoneNumber;
    }

    /**
     * Sets the value of the tenancyAdminPhoneNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTenancyAdminPhoneNumber(String value) {
        this.tenancyAdminPhoneNumber = value;
    }

    /**
     * Gets the value of the tenancyAdminRoleId property.
     * 
     */
    public int getTenancyAdminRoleId() {
        return tenancyAdminRoleId;
    }

    /**
     * Sets the value of the tenancyAdminRoleId property.
     * 
     */
    public void setTenancyAdminRoleId(int value) {
        this.tenancyAdminRoleId = value;
    }

}
