
package remote.wise.client.FuelUtilizationDetailService;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GetFuelUtilizationDetail complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetFuelUtilizationDetail">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ReqObj" type="{http://webservice.service.wise.remote/}fuelUtilizationDetailReqContract" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetFuelUtilizationDetail", propOrder = {
    "reqObj"
})
public class GetFuelUtilizationDetail {

    @XmlElement(name = "ReqObj")
    protected FuelUtilizationDetailReqContract reqObj;

    /**
     * Gets the value of the reqObj property.
     * 
     * @return
     *     possible object is
     *     {@link FuelUtilizationDetailReqContract }
     *     
     */
    public FuelUtilizationDetailReqContract getReqObj() {
        return reqObj;
    }

    /**
     * Sets the value of the reqObj property.
     * 
     * @param value
     *     allowed object is
     *     {@link FuelUtilizationDetailReqContract }
     *     
     */
    public void setReqObj(FuelUtilizationDetailReqContract value) {
        this.reqObj = value;
    }

}
