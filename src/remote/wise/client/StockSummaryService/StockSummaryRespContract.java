
package remote.wise.client.StockSummaryService;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for stockSummaryRespContract complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="stockSummaryRespContract">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="dealerIdNameCountMap" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="zonalMachineCount" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="zonalTenancyId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="zonalTenancyName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "stockSummaryRespContract", propOrder = {
    "dealerIdNameCountMap",
    "zonalMachineCount",
    "zonalTenancyId",
    "zonalTenancyName"
})
public class StockSummaryRespContract {

    @XmlElement(nillable = true)
    protected List<String> dealerIdNameCountMap;
    protected long zonalMachineCount;
    protected int zonalTenancyId;
    protected String zonalTenancyName;

    /**
     * Gets the value of the dealerIdNameCountMap property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dealerIdNameCountMap property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDealerIdNameCountMap().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getDealerIdNameCountMap() {
        if (dealerIdNameCountMap == null) {
            dealerIdNameCountMap = new ArrayList<String>();
        }
        return this.dealerIdNameCountMap;
    }

    /**
     * Gets the value of the zonalMachineCount property.
     * 
     */
    public long getZonalMachineCount() {
        return zonalMachineCount;
    }

    /**
     * Sets the value of the zonalMachineCount property.
     * 
     */
    public void setZonalMachineCount(long value) {
        this.zonalMachineCount = value;
    }

    /**
     * Gets the value of the zonalTenancyId property.
     * 
     */
    public int getZonalTenancyId() {
        return zonalTenancyId;
    }

    /**
     * Sets the value of the zonalTenancyId property.
     * 
     */
    public void setZonalTenancyId(int value) {
        this.zonalTenancyId = value;
    }

    /**
     * Gets the value of the zonalTenancyName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getZonalTenancyName() {
        return zonalTenancyName;
    }

    /**
     * Sets the value of the zonalTenancyName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setZonalTenancyName(String value) {
        this.zonalTenancyName = value;
    }

	/**
	 * @param dealerIdNameCountMap the dealerIdNameCountMap to set
	 */
	public void setDealerIdNameCountMap(List<String> dealerIdNameCountMap) {
		this.dealerIdNameCountMap = dealerIdNameCountMap;
	}

}
