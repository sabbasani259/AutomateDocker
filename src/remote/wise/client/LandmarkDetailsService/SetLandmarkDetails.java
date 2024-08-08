
package remote.wise.client.LandmarkDetailsService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SetLandmarkDetails complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SetLandmarkDetails">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="RespObj" type="{http://webservice.service.wise.remote/}landmarkDetailsRespContract" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SetLandmarkDetails", propOrder = {
    "respObj"
})
public class SetLandmarkDetails {

    @XmlElement(name = "RespObj")
    protected LandmarkDetailsRespContract respObj;

    /**
     * Gets the value of the respObj property.
     * 
     * @return
     *     possible object is
     *     {@link LandmarkDetailsRespContract }
     *     
     */
    public LandmarkDetailsRespContract getRespObj() {
        return respObj;
    }

    /**
     * Sets the value of the respObj property.
     * 
     * @param value
     *     allowed object is
     *     {@link LandmarkDetailsRespContract }
     *     
     */
    public void setRespObj(LandmarkDetailsRespContract value) {
        this.respObj = value;
    }

}
