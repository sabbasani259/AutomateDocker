
package remote.wise.client.VinProcessingService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for vinSearchRespContract complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="vinSearchRespContract">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="decodefailed" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="parsefailed" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="processingfailed" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="region" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="successprocessed" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="timeStamp" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="VIN" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="validationfailed" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "vinSearchRespContract", propOrder = {
    "decodefailed",
    "parsefailed",
    "processingfailed",
    "region",
    "successprocessed",
    "timeStamp",
    "vin",
    "validationfailed"
})
public class VinSearchRespContract {

    protected String decodefailed;
    protected String parsefailed;
    protected String processingfailed;
    protected String region;
    protected String successprocessed;
    protected String timeStamp;
    @XmlElement(name = "VIN")
    protected String vin;
    protected String validationfailed;

    /**
     * Gets the value of the decodefailed property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDecodefailed() {
        return decodefailed;
    }

    /**
     * Sets the value of the decodefailed property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDecodefailed(String value) {
        this.decodefailed = value;
    }

    /**
     * Gets the value of the parsefailed property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParsefailed() {
        return parsefailed;
    }

    /**
     * Sets the value of the parsefailed property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParsefailed(String value) {
        this.parsefailed = value;
    }

    /**
     * Gets the value of the processingfailed property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProcessingfailed() {
        return processingfailed;
    }

    /**
     * Sets the value of the processingfailed property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProcessingfailed(String value) {
        this.processingfailed = value;
    }

    /**
     * Gets the value of the region property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRegion() {
        return region;
    }

    /**
     * Sets the value of the region property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRegion(String value) {
        this.region = value;
    }

    /**
     * Gets the value of the successprocessed property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSuccessprocessed() {
        return successprocessed;
    }

    /**
     * Sets the value of the successprocessed property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSuccessprocessed(String value) {
        this.successprocessed = value;
    }

    /**
     * Gets the value of the timeStamp property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTimeStamp() {
        return timeStamp;
    }

    /**
     * Sets the value of the timeStamp property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTimeStamp(String value) {
        this.timeStamp = value;
    }

    /**
     * Gets the value of the vin property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVIN() {
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
    public void setVIN(String value) {
        this.vin = value;
    }

    /**
     * Gets the value of the validationfailed property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValidationfailed() {
        return validationfailed;
    }

    /**
     * Sets the value of the validationfailed property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValidationfailed(String value) {
        this.validationfailed = value;
    }

}
