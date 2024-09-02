
package remote.wise.client.UserAuthenticationService;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for userAuthenticationRespContract complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="userAuthenticationRespContract">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="isTenancyAdmin" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="last_login_date" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="loginId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="map" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="roleId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="role_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SMS" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="sysGeneratedPassword" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="tenancyNameIDProxyUser" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="user_name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "userAuthenticationRespContract", propOrder = {
    "isTenancyAdmin",
    "lastLoginDate",
    "loginId",
    "map",
    "roleId",
    "roleName",
    "sms",
    "sysGeneratedPassword",
    "tenancyNameIDProxyUser",
    "userName"
})
public class UserAuthenticationRespContract {

    protected int isTenancyAdmin;
    @XmlElement(name = "last_login_date")
    protected String lastLoginDate;
    protected String loginId;
    protected boolean map;
    protected int roleId;
    @XmlElement(name = "role_name")
    protected String roleName;
    @XmlElement(name = "SMS")
    protected boolean sms;
    protected int sysGeneratedPassword;
    @XmlElement(nillable = true)
    protected List<String> tenancyNameIDProxyUser;
    @XmlElement(name = "user_name")
    protected String userName;

    /**
     * Gets the value of the isTenancyAdmin property.
     * 
     */
    public int getIsTenancyAdmin() {
        return isTenancyAdmin;
    }

    /**
     * Sets the value of the isTenancyAdmin property.
     * 
     */
    public void setIsTenancyAdmin(int value) {
        this.isTenancyAdmin = value;
    }

    /**
     * Gets the value of the lastLoginDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLastLoginDate() {
        return lastLoginDate;
    }

    /**
     * Sets the value of the lastLoginDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLastLoginDate(String value) {
        this.lastLoginDate = value;
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
     * Gets the value of the map property.
     * 
     */
    public boolean isMap() {
        return map;
    }

    /**
     * Sets the value of the map property.
     * 
     */
    public void setMap(boolean value) {
        this.map = value;
    }

    /**
     * Gets the value of the roleId property.
     * 
     */
    public int getRoleId() {
        return roleId;
    }

    /**
     * Sets the value of the roleId property.
     * 
     */
    public void setRoleId(int value) {
        this.roleId = value;
    }

    /**
     * Gets the value of the roleName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRoleName() {
        return roleName;
    }

    /**
     * Sets the value of the roleName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRoleName(String value) {
        this.roleName = value;
    }

    /**
     * Gets the value of the sms property.
     * 
     */
    public boolean isSMS() {
        return sms;
    }

    /**
     * Sets the value of the sms property.
     * 
     */
    public void setSMS(boolean value) {
        this.sms = value;
    }

    /**
     * Gets the value of the sysGeneratedPassword property.
     * 
     */
    public int getSysGeneratedPassword() {
        return sysGeneratedPassword;
    }

    /**
     * Sets the value of the sysGeneratedPassword property.
     * 
     */
    public void setSysGeneratedPassword(int value) {
        this.sysGeneratedPassword = value;
    }

	/**
     * Gets the value of the tenancyNameIDProxyUser property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tenancyNameIDProxyUser property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTenancyNameIDProxyUser().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getTenancyNameIDProxyUser() {
        if (tenancyNameIDProxyUser == null) {
            tenancyNameIDProxyUser = new ArrayList<String>();
        }
        return this.tenancyNameIDProxyUser;
    }

    /**
     * Gets the value of the userName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets the value of the userName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUserName(String value) {
        this.userName = value;
    }

}
