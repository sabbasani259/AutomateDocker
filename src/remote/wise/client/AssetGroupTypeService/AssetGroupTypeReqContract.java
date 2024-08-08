
package remote.wise.client.AssetGroupTypeService;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for assetGroupTypeReqContract complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="assetGroupTypeReqContract">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="assetGroupTypeId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="loginId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="otherTenancy" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="tenancyIdList" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "assetGroupTypeReqContract", propOrder = {
    "assetGroupTypeId",
    "loginId",
    "otherTenancy",
    "tenancyIdList"
})
public class AssetGroupTypeReqContract {

    protected int assetGroupTypeId;
    protected String loginId;
    protected boolean otherTenancy;
    @XmlElement(nillable = true)
    protected List<Integer> tenancyIdList;

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
     * Gets the value of the otherTenancy property.
     * 
     */
    public boolean isOtherTenancy() {
        return otherTenancy;
    }

    /**
     * Sets the value of the otherTenancy property.
     * 
     */
    public void setOtherTenancy(boolean value) {
        this.otherTenancy = value;
    }

    /**
     * Gets the value of the tenancyIdList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tenancyIdList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTenancyIdList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Integer }
     * 
     * 
     */
    public List<Integer> getTenancyIdList() {
        if (tenancyIdList == null) {
            tenancyIdList = new ArrayList<Integer>();
        }
        return this.tenancyIdList;
    }

}
