
package remote.wise.client.EAInterfaceService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for eaInterfaceDetailRespContract complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="eaInterfaceDetailRespContract">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="failureForRejection" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="reProcessCount" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="record" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "eaInterfaceDetailRespContract", propOrder = {
    "failureForRejection",
    "reProcessCount",
    "record"
})
public class EaInterfaceDetailRespContract {

    protected String failureForRejection;
    protected int reProcessCount;
    protected String record;

    /**
     * Gets the value of the failureForRejection property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFailureForRejection() {
        return failureForRejection;
    }

    /**
     * Sets the value of the failureForRejection property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFailureForRejection(String value) {
        this.failureForRejection = value;
    }

    /**
     * Gets the value of the reProcessCount property.
     * 
     */
    public int getReProcessCount() {
        return reProcessCount;
    }

    /**
     * Sets the value of the reProcessCount property.
     * 
     */
    public void setReProcessCount(int value) {
        this.reProcessCount = value;
    }

    /**
     * Gets the value of the record property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRecord() {
        return record;
    }

    /**
     * Sets the value of the record property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRecord(String value) {
        this.record = value;
    }

}
